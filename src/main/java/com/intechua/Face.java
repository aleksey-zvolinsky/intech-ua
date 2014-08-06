package com.intechua;

import org.apache.velocity.app.Velocity;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import com.intechua.web.AfterFilter;
import com.intechua.web.AuthFilter;
import com.intechua.web.ConverterFilter;
import com.intechua.web.Graph;
import com.intechua.web.IndexData;
import com.intechua.web.IndexStatus;
import com.intechua.web.Input;
import com.intechua.web.Journal;
import com.intechua.web.Settings;
import com.intechua.web.SettingsEdit;

class Face
{
	static void init()
	{
		Velocity.setProperty("resource.loader", "class");
		Velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.setProperty("runtime.log.logsystem.log4j.logger", "org.apache.velocity.runtime.log.Log4JLogChute");
		Velocity.init();

		Spark.setIpAddress(Setup.get().getWebServerAddress());
		Spark.setPort(Setup.get().getWebServerPort());

		Spark.before(new AuthFilter("/op"));
		Spark.get(new Settings("/op/settings"));
		Spark.get(new SettingsEdit("/op/settings/edit"));
		
		Spark.before(new AuthFilter("/set"));
		Spark.get(new Settings("/set"));

		Spark.before(new ConverterFilter("/input"));
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
