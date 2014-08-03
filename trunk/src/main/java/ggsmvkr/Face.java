package ggsmvkr;

import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.Velocity;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import com.intechua.web.AfterFilter;
import com.intechua.web.AuthFilter;
import com.intechua.web.Graph;
import com.intechua.web.IndexData;
import com.intechua.web.IndexStatus;
import com.intechua.web.Input;
import com.intechua.web.Journal;

class Face
{
	
	static void init(final DB db, final Processor processor, final RRDs rrds, final boolean noauth)
	{
		Velocity.setProperty("resource.loader", "class");
		Velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.setProperty("runtime.log.logsystem.log4j.logger", "org.apache.velocity.runtime.log.Log4JLogChute");
		Velocity.init();
		
		

		Spark.setPort(Setup.get().getWebServerPort());

		Spark.before(new AuthFilter("/op"));
		
		Spark.get(new Input("/input"));
		Spark.get(new IndexStatus("/indexstatus"));
		Spark.get(new IndexData("/indexdata"));
		
		Spark.get(new Journal("/journal"));

		Spark.get(new Graph("/graph"));
		
		Spark.get(new Route("/")
		{
			@Override
			public Object handle(Request request, Response response)
			{
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
		
		Spark.post(new Route("/journalize")
		{
			@Override
			public Object handle(Request request, Response response)
			{
				try
				{
					int id = Integer.parseInt(request.queryParams("id"));
					if (request.attribute("operator") != null)
					{
						processor.submit(id, (Operator) request.attribute("operator"));
					}
					ChannelInfo ci = db.getChannelInfo(id);
					response.redirect("/view-" + ci.getType().getName() + "?id=" + id);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				return null;
			}
		});
				
		Spark.get(new Route("/graphslevel")
		{
			@Override
			public Object handle(Request request, Response response)
			{
				String speriod = request.queryParams("period");
				char period = (speriod == null) ? 'h' : speriod.charAt(0);

				String strGraphImgInfo = rrds.makeLevel1Graph(period);

				request.attribute("attrGraphImgInfo", strGraphImgInfo);
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
				if (type == null)
				{
					type = "t";
				}
				request.attribute("type", type);
				String period = request.queryParams("period");
				if (period == null)
				{
					period = "d";
				}
				request.attribute("period", period);
				if ((type.equals("l")) || (type.equals("v")))
				{
					List<Integer> pumps = new ArrayList<Integer>();
					for (int p = 0; p < 4; p++)
					{
						if (rrds.isPumpExists(id, p))
						{
							pumps.add(Integer.valueOf(p));
						}
					}
					request.attribute("pumps", pumps);
				} else if (rrds.isTankExists(id))
				{
					request.attribute("tank", "yes");
				}
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
				if (alarm)
				{
					db.setSetting("alarm", "on");
				} else
				{
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
				for (int p = 0; p < 4; p++)
				{
					ci.setPumpState(p, ChannelInfo.PumpState.byId((byte) (request.queryParams("pump" + p).charAt(0) - '0')));
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
				ArrayList<ChannelInfo> al = new ArrayList<ChannelInfo>();
				for (int id = 1; id <= 24; id++)
				{
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
		Spark.after(new AfterFilter());
	}
}
