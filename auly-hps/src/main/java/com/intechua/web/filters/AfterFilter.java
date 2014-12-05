package com.intechua.web.filters;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import org.apache.log4j.lf5.util.StreamUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import spark.Filter;
import spark.Request;
import spark.Response;

import com.intechua.db.jooq.tables.records.SettingsRecord;
import com.intechua.db.managers.SettingsTable;

public class AfterFilter extends Filter
{
	private static final String PATH_TO_FACE = "intechua/face/";
	private static final String ENCODING = StandardCharsets.UTF_8.name();
	private static final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	private static final SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
	private static final SimpleDateFormat dtf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private static final SettingsTable SETTINGS = new SettingsTable();
	
	
	@Override
	public void handle(Request request, Response response)
	{
	
		String path = request.pathInfo();
		if (path.startsWith("/"))
		{
			path = path.substring(1);
		}
		String vmName = null;
		String mime = "text/html; charset=utf-8";
		if (path.endsWith(".ttf"))
		{
			mime = "application/octet-stream";
		}
		else if (path.startsWith("css/"))
		{
			vmName = path.substring(4);
			mime = "text/css";
		}
		else if (path.startsWith("js/"))
		{
			// vmName = path;
			mime = "application/javascript; charset=utf-8";
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
			if (path.length() == 0)
			{
				vmName = "index";
			}
			else
			{
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
			if (vmName.endsWith(".vm"))
			{
				VelocityContext context = new VelocityContext();
				//include settings
				for(SettingsRecord setting : SETTINGS.getList())
				{
					context.put(setting.getName(), setting.getValue());
				}
				Velocity.getTemplate(PATH_TO_FACE + "_head.vm", ENCODING).merge(context, sw);
			}
			Template t = Velocity.getTemplate(PATH_TO_FACE + vmName, ENCODING);
			VelocityContext context = new VelocityContext();
			//include settings
			for(SettingsRecord setting : SETTINGS.getList())
			{
				context.put(setting.getName(), setting.getValue());
			}
			for (String attr : request.attributes())
			{
				context.put(attr, request.attribute(attr));
			}
			context.put("_dtf", dtf);
			context.put("_df", df);
			context.put("_tf", tf);
			context.put("_ff1", new FixedFormat1());
			t.merge(context, sw);
			if (vmName.endsWith(".vm"))
			{
				Velocity.getTemplate(PATH_TO_FACE + "_tail.vm", ENCODING).merge(new VelocityContext(), sw);
			}
			response.body(sw.toString());
		}
		else
		{
			try
			{
				response.type(mime);
				byte[] png = (byte[]) request.attribute("png");
				if (png == null)
				{
					InputStream fis = this.getClass().getResourceAsStream("/" + PATH_TO_FACE + path);

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
				System.out.println("Failed to read file: /"+PATH_TO_FACE + path);
				e.printStackTrace(System.out);
				halt(404);
			}
		}
		response.type(mime);
	}
	
	

	private static class FixedFormat1
	{
		@SuppressWarnings("unused")
		public String format(int l)
		{
			return "" + (l / 10) + '.' + (l % 10);
		}
	}

}
