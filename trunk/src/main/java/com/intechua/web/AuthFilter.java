package com.intechua.web;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import spark.Filter;
import spark.Request;
import spark.Response;

import com.intechua.db.OperatorTable;

public class AuthFilter extends Filter
{
	private static final String REALM = UUID.randomUUID().toString();
	
	public AuthFilter(String string)
	{
		super(string);
	}

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
			OperatorTable operators = new OperatorTable();
			if(operators.exist(user, passwd))
			{
				request.session(true).attribute("operator", user);
				request.attribute("operator", user);
				return;
			}
		}
		response.status(401);
		response.header("WWW-Authenticate", "BASIC realm=\"IntechUA"+REALM+"\"");
	}
}
