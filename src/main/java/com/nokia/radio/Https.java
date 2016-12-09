/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.nokia.radio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.pf.util.Base64Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;

/**
 * Visit https website
 * 
 *  @author <a href="laixiaoxuan@gmail.com">Adrian LAI</a>
 *
 */
public class Https
 {
 	/**
	 * 加载证书
	 * */
	static 
	{
		System.setProperty("javax.net.ssl.trustStore", "../conf/bts.keystore");
		System.setProperty("javax.net.ssl.trustStorePassword", "123456");
	}

    /**
	 * slf4j logger interface
	 */
	protected static final transient Logger LOG = LoggerFactory.getLogger(Https.class);

    /**
	 * Main.
	 */
	public static void main(String[] args) throws Exception
	{
		//enable SSH
		String html = visitUrl( new URL("https://"+args[0]+"/protected/sshservice.html"));
		String[] params = parseParams(html);
		visitUrl( new URL("https://"+args[0]+"/protected/enableSsh.cgi?stamp="+params[0]+"&token="+params[1]+"&frame="+params[2]));
			
		//enable R&D port
		html = visitUrl( new URL("https://"+args[0]+"/protected/RndPortsService.html"));
		params = parseParams(html);
		visitUrl( new URL("https://"+args[0]+"/protected/enableRndPorts.cgi?stamp="+params[0]+"&token="+params[1]+"&frame="+params[2]));
	}
	
	/**
	 * 解析参数
	 */
	private static String[] parseParams(String html) throws Exception
	{
		String[] params = new String[3];
		String[] htmlLines = html.split("\r\n");
		String stamp =null;
		String token =null;
		String frame =null;
		boolean isParsingSshCgiParamss = false;
		/*
		#html#
		<form ACTION="enableSsh.cgi" method="get">
		<b>
		<input type="SUBMIT" VALUE="Enable SSH Service">
		<input type=hidden name=stamp value="1481259636">
		<input type=hidden name=token value="1d1c545e35e6994ab63082456e0152bd5f7b125a739cc72b47eef8e37f0fef75">
		<input type=hidden name=frame value="sshservice">
		</b>
		</form>
        */
		for(String htmlLine:htmlLines) 
		{
			if(htmlLine.equals("<form ACTION=\"enableSsh.cgi\" method=\"get\">") || htmlLine.equals("<form ACTION=\"enableRndPorts.cgi\" method=\"get\">"))
			{
				//start parsing
				isParsingSshCgiParamss = true;
				continue;
			}
			if(isParsingSshCgiParamss&&htmlLine.equals("</form>"))
			{
				//finish parsing
				isParsingSshCgiParamss = false;
				break;
			}
			
			if(htmlLine.contains("name=stamp"))
			{
				String[] temp = htmlLine.split("value=\"");
				stamp = htmlLine.split("value=\"")[1].split("\">")[0];
			}
			
			if(htmlLine.contains("name=token"))
			{
				String[] temp = htmlLine.split("value=\"");
				token = htmlLine.split("value=\"")[1].split("\">")[0];
			}
			
			if(htmlLine.contains("name=frame"))
			{
				String[] temp = htmlLine.split("value=\"");
				frame = htmlLine.split("value=\"")[1].split("\">")[0];
			}
		}
		params[0]=stamp;
		params[1]=token;
		params[2]=frame;
		LOG.info(Arrays.toString(params));
		return params;
	}
	
	/**
	 * 访问URL
	 */
	private static String visitUrl(URL url) throws Exception
	{
		LOG.info(url.toString());
		HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);
		//打开连接
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		//用户名密码
		String credentials = "Nemuadmin" + ":" + "nemuuser";
		String encoding = new String(Base64Converter.encode(credentials.getBytes("UTF-8")));
		con.setRequestProperty("Authorization", String.format("Basic %s", encoding));
		//解析response
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		String line;
		StringBuffer responseText = new StringBuffer();
		while ((line = reader.readLine()) != null) 
		{
			responseText.append(line).append("\r\n");
		}
		reader.close();
		con.disconnect();
		LOG.info(responseText.toString());
		return responseText.toString();
	}

}
