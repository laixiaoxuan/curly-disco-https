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
		System.setProperty("javax.net.ssl.trustStore", "../conf/vagustour.keystore");
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
		//构建请求
		URL postUrl = new URL("https://vagustour.com/svn/WAM");
		HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);
		//打开连接
		HttpsURLConnection con = (HttpsURLConnection) postUrl.openConnection();
		//用户名密码
		String credentials = "laixx" + ":" + "lxx@2013";
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
	}

}
