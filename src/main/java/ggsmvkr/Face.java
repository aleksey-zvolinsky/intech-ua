package ggsmvkr;

import java.io.InputStream;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.lf5.util.StreamUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

class Face
{
  static void init(final DB db, final Processor processor, final RRDs rrds, boolean noauth)
  {
    Velocity.setProperty("resource.loader", "class");
    Velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    Velocity.setProperty("runtime.log.logsystem.log4j.logger", "org.apache.velocity.runtime.log.Log4JLogChute");
    Velocity.init();
    
    Spark.setPort(8030);
    if (!noauth) {
      Spark.before(new Filter()
      {
        @Override
		public void handle(Request request, Response response)
        {
          String authorization = request.headers("Authorization");
          if (authorization != null)
          {
            String userInfo = authorization.substring(6).trim();
            String nameAndPassword = new String(Base64.decodeBase64(userInfo));
            int index = nameAndPassword.indexOf(":");
            String user = nameAndPassword.substring(0, index);
            String passwd = nameAndPassword.substring(index + 1);
            //FIXME
            Operator oper = null;// = this.val$db.getOperator(user);
            if ((oper != null) && (oper.getPasswd() != null) && (oper.getPasswd().equals(passwd)))
            {
              request.attribute("operator", oper);
              return;
            }
          }
          response.status(401);
          response.header("WWW-Authenticate", "BASIC realm=\"GGSM vkr\"");
        }
      });
    }
    Spark.get(new Route("/")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        List<ChannelInfo> channels = new ArrayList();
        for (Iterator i$ = db.getWorkingChannels().iterator(); i$.hasNext();)
        {
          int cid = ((Integer)i$.next()).intValue();
          channels.add(db.getChannelInfo(cid));
        }
        request.attribute("channels", channels);
        return null;
      }
    });
    Spark.get(new Route("/view")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        int id = Integer.parseInt(request.queryParams("id"));
        
        ChannelInfo ci = db.getChannelInfo(id);
        request.attribute("ch", ci);
        return null;
      }
    });
    Spark.get(new Route("/view-zelio")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        int id = Integer.parseInt(request.queryParams("id"));
        
        ChannelInfo ci = db.getChannelInfo(id);
        request.attribute("ch", ci);
        return null;
      }
    });
    Spark.get(new Route("/view-kinco")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        int id = Integer.parseInt(request.queryParams("id"));
        ChannelInfo ci = db.getChannelInfo(id);
        request.attribute("ch", ci);
        return null;
      }
    });
    Spark.post(new Route("/journalize")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        try
        {
          int id = Integer.parseInt(request.queryParams("id"));
          if (request.attribute("operator") != null) {
            processor.submit(id, (Operator)request.attribute("operator"));
          }
          ChannelInfo ci = db.getChannelInfo(id);
          response.redirect("/view-" + ci.getType().getName() + "?id=" + id);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        return null;
      }
    });
    Spark.get(new Route("/status")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        try
        {
          int id = Integer.parseInt(request.queryParams("id"));
          ChannelInfo ci = db.getChannelInfo(id);
          Message lm = db.getLastMessage(id);
          if (lm != null) {
            for (int p = 0; p < 4; p++) {
              if (ci.getPumpState(p) != ChannelInfo.PumpState.On) {
                lm.pumps[p] = null;
              }
            }
          }
          request.attribute("lm", lm);
          




















          request.attribute("connected", Boolean.valueOf(processor.isConnected(id)));
          request.attribute("submitRequired", Boolean.valueOf(processor.isSubmitRequired(id)));
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        return null;
      }
    });
    Spark.get(new Route("/history")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        synchronized (this)
        {
          int id = Integer.parseInt(request.queryParams("id"));
          
          String sday = request.queryParams("day");
          Date day;
          try
          {
            day = Face.df.parse(sday);
          }
          catch (Exception e)
          {
            day = new Date();
          }
          request.attribute("id", Integer.valueOf(id));
          request.attribute("day", Face.df.format(day));
          
          ArrayList<String> days = new ArrayList();
          Calendar cal = new GregorianCalendar();
          cal.setTime(new Date());
          for (int i = 0; i < 18; i++)
          {
            days.add(Face.df.format(cal.getTime()));
            cal.add(5, -1);
          }
          request.attribute("days", days);
          request.attribute("list", db.getMessages(id, day));
          return null;
        }
      }
    });
	Spark.get(new Route("/packets")  //FIXME 
    {
      @Override
	public Object handle(Request request, Response response)
      {
        String sid = request.queryParams("id");
        int id = sid == null ? 0 : Integer.parseInt(sid);
        if (id > 0) {
          request.attribute("ch", db.getPacketList(id));
        }
        request.attribute("list", db.getPacketList(id));
        return null;
      }
    });   //FIXME
    Spark.get(new Route("/journal")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        String sid = request.queryParams("id");
        int id = sid == null ? 0 : Integer.parseInt(sid);
        if (id > 0) {
          request.attribute("ch", db.getChannelInfo(id));
        }
        request.attribute("list", db.getJournals(id));
        return null;
      }
    });
    Spark.get(new Route("/param")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        List<Object> channels = new ArrayList();
        for (Iterator i$ = db.getWorkingChannels().iterator(); i$.hasNext();)
        {
          int id = ((Integer)i$.next()).intValue();
          channels.add(new Face.CurrStatus(id, db.getLastMessage(id), processor.isConnected(id), processor.isSubmitRequired(id)));
        }
        request.attribute("channels", channels);
        request.attribute("alarm", Boolean.valueOf(db.getSetting("alarm") != null));
        return null;
      }
    });
    Spark.get(new Route("/connections")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        synchronized (this)
        {
          int id = Integer.parseInt(request.queryParams("id"));
          
          String sday = request.queryParams("day");
          Date day;
          try
          {
            day = Face.df.parse(sday);
          }
          catch (Exception e)
          {
            day = new Date();
          }
          request.attribute("id", Integer.valueOf(id));
          request.attribute("day", Face.df.format(day));
          ArrayList<String> days = new ArrayList();
          Calendar cal = new GregorianCalendar();
          cal.setTime(new Date());
          for (int i = 0; i < 18; i++)
          {
            days.add(Face.df.format(cal.getTime()));
            cal.add(5, -1);
          }
          request.attribute("days", days);
          request.attribute("list", db.getConnections(id, day));
          return null;
        }
      }
    });
    Spark.get(new Route("/rrd")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        int id = Integer.parseInt(request.queryParams("id"));
        String speriod = request.queryParams("period");
        char period = speriod == null ? 'h' : speriod.charAt(0);
        byte[] data = null;
        String spump = request.queryParams("pump");
        if (spump != null)
        {
          int pump = Integer.parseInt(spump);
          String type = request.queryParams("type");
          if ((type == null) || (type.equals("v")))
          {
            data = rrds.makePumpGraph(id, period, pump, true, new boolean[] { false, false, false });
          }
          else
          {
            boolean[] ls = new boolean[3];
            if (type.length() >= 3) {
              for (int l = 0; l < 3; l++) {
                ls[l] = (type.charAt(l) != '0' ? true : false);
              }
            }
            data = rrds.makePumpGraph(id, period, pump, false, ls);
          }
        }
        else
        {
          data = rrds.makeTankGraph(id, period);
        }
        request.attribute("png", data);
        return null;
      }
    });
    Spark.get(new Route("/graphs")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        int id = Integer.parseInt(request.queryParams("id"));
        ChannelInfo ci = db.getChannelInfo(id);
        request.attribute("ch", ci);
        request.attribute("channels", db.getWorkingChannels());
        String type = request.queryParams("type");
        if (type == null) {
          type = "t";
        }
        request.attribute("type", type);
        String period = request.queryParams("period");
        if (period == null) {
          period = "d";
        }
        request.attribute("period", period);
        if ((type.equals("l")) || (type.equals("v")))
        {
          List<Integer> pumps = new ArrayList();
          for (int p = 0; p < 4; p++) {
            if (rrds.isPumpExists(id, p)) {
              pumps.add(Integer.valueOf(p));
            }
          }
          request.attribute("pumps", pumps);
        }
        else if (rrds.isTankExists(id))
        {
          request.attribute("tank", "yes");
        }
        return null;
      }
    });
    Spark.get(new Route("/table")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        List<DB.ChannelAvgs> result = new ArrayList(30);
        for (Iterator i$ = db.getWorkingChannels().iterator(); i$.hasNext();)
        {
          int id = ((Integer)i$.next()).intValue();
          result.add(db.getChannelAvgs(id));
        }
        request.attribute("channels", result);
        return null;
      }
    });
    Spark.get(new Route("/sett/")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        String alarm = db.getSetting("alarm");
        request.attribute("alarm", Boolean.valueOf(alarm != null));
        return null;
      }
    });
    Spark.get(new Route("/sett/edit")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        String alarm = db.getSetting("alarm");
        request.attribute("alarm", Boolean.valueOf(alarm != null));
        return null;
      }
    });
    Spark.post(new Route("/sett/edit")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        boolean alarm = "true".equals(request.queryParams("alarm"));
        if (alarm) {
          db.setSetting("alarm", "on");
        } else {
          db.delSetting("alarm");
        }
        response.redirect("/sett/");
        return null;
      }
    });
    Spark.get(new Route("/sett/ch.edit")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        ChannelInfo ci = db.getChannelInfo(Integer.parseInt(request.queryParams("id")));
        request.attribute("ci", ci);
        return null;
      }
    });
    Spark.post(new Route("/sett/ch.edit")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        ChannelInfo ci = new ChannelInfo(Integer.parseInt(request.queryParams("id")));
        ci.setType(ChannelInfo.Type.byName(request.queryParams("type")));
        for (int p = 0; p < 4; p++) {
          ci.setPumpState(p, ChannelInfo.PumpState.byId((byte)(request.queryParams("pump" + p).charAt(0) - '0')));
        }
        db.putChannelInfo(ci);
        response.redirect("/sett/ch.list");
        return null;
      }
    });
    Spark.get(new Route("/sett/ch.list")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        ArrayList<ChannelInfo> al = new ArrayList();
        for (int id = 1; id <= 24; id++) {
          al.add(db.getChannelInfo(id));
        }
        request.attribute("cis", al);
        return null;
      }
    });
    Spark.get(new Route("/sett/op.list")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        request.attribute("ops", db.getOperators());
        return null;
      }
    });
    Spark.get(new Route("/sett/op.edit")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        Operator oper = db.getOperator(request.queryParams("login"));
        request.attribute("op", oper);
        return null;
      }
    });
    Spark.post(new Route("/sett/op.delete")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        db.delOperator(request.queryParams("login"));
        response.redirect("/sett/op.list");
        return null;
      }
    });
    Spark.post(new Route("/sett/op.edit")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        Operator oper = new Operator(request.queryParams("login"));
        oper.setName(request.queryParams("name"));
        oper.setPasswd(request.queryParams("passwd"));
        
        db.putOperator(oper);
        response.redirect("/sett/op.list");
        return null;
      }
    });
    Spark.get(new Route("/*")
    {
      @Override
	public Object handle(Request request, Response response)
      {
        return null;
      }
    });
    Spark.after(new Filter()
    {
      @Override
	public void handle(Request request, Response response)
      {
        String path = request.pathInfo();
        if (path.startsWith("/")) {
          path = path.substring(1);
        }
        String vmName = null;
        String mime = "text/html; charset=utf-8";
        if (path.startsWith("css/"))
        {
          vmName = path.substring(4);
          mime = "text/css";
        }
        else if (path.startsWith("js/"))
        {
          vmName = path.substring(3);
        }
        else if (path.startsWith("img/"))
        {
          mime = "image/jpeg";
        }
        else if (path.startsWith("rrd"))
        {
          mime = "image/png";
        }
        else if (path.startsWith("favicon.ico"))
        {
          mime = "mage/x-icon";
        }
        else if (path.startsWith("alarm.mp3"))
        {
          mime = "audio/mpeg";
        }
        else
        {
          if (path.length() == 0) {
            vmName = "index";
          } else {
            vmName = path.replace('/', '.');
          }
          if (request.queryParams("json") != null)
          {
            vmName = vmName + ".json";
            mime = "application/json";
          }
          else
          {
            vmName = vmName + ".vm";
          }
        }
        if (vmName != null)
        {
          StringWriter sw = new StringWriter();
          if (vmName.endsWith(".vm")) {
            Velocity.getTemplate("ggsmvkr/face/_head.vm", "koi8-r").merge(new VelocityContext(), sw);
          }
          Template t = Velocity.getTemplate("ggsmvkr/face/" + vmName, "koi8-r");
          VelocityContext context = new VelocityContext();
          for (String attr : request.attributes()) {
            context.put(attr, request.attribute(attr));
          }
          context.put("_dtf", Face.dtf);
          context.put("_df", Face.df);
          context.put("_tf", Face.tf);
          context.put("_ff1", new Face.FixedFormat1());
          t.merge(context, sw);
          if (vmName.endsWith(".vm")) {
            Velocity.getTemplate("ggsmvkr/face/_tail.vm", "koi8-r").merge(new VelocityContext(), sw);
          }
          response.body(sw.toString());
        }
        else
        {
          try
          {
            byte[] png = (byte[])request.attribute("png");
            if (png == null)
            {
              InputStream fis = Face.class.getResourceAsStream("/ggsmvkr/face/" + path);
                            
              StreamUtils.copy(fis, response.raw().getOutputStream());
              fis.close();
            }
            else
            {
              response.raw().getOutputStream().write(png);
            }
            response.body("");
          }
          catch (Exception e)
          {
            System.out.println(e);
            halt(404);
          }
        }
        response.type(mime);
      }
    });
  }
  
  public static class CurrStatus
  {
    int id;
    Message lm;
    boolean connected;
    boolean submitRequired;
    
    CurrStatus(int id, Message lm, boolean connected, boolean submitRequired)
    {
      this.id = id;
      this.lm = lm;
      this.connected = connected;
      this.submitRequired = submitRequired;
    }
    
    public int getId()
    {
      return this.id;
    }
    
    public Message getLastMessage()
    {
      return this.lm;
    }
    
    public boolean isObsolete()
    {
      return (this.lm == null) || (System.currentTimeMillis() - this.lm.getDate().getTime() > 300000L);
    }
    
    public boolean isConnected()
    {
      return this.connected;
    }
    
    public boolean isSubmitRequired()
    {
      return this.submitRequired;
    }
  }
  
  public static class FixedFormat1
  {
    public String format(int l)
    {
      return "" + (l / 10) + '.' + (l % 10);
    }
  }
  
  private static final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
  private static final SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
  private static final SimpleDateFormat dtf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  private static final NumberFormat nf2 = new DecimalFormat("00");
}
