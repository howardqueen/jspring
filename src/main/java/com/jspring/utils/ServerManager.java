package com.jspring.utils;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.jspring.Strings;
import com.jspring.Exceptions;

@Component
public class ServerManager {
	public static class ServerConf {
		public String host;
		public int port;
		public String user;
		public String password;
		//
		public String proxyHost;
		public int proxyPort;
		//
		public String keyFile;
		public String keyFilePassword;
		//
		public String localTempPath;
		public String remoteTempPath;
	}

	@Autowired
	Environment environment;

	private final ArrayList<ServerProxy> items = new ArrayList<>();

	public ServerProxy getServerProxy(String serverName) {
		if (Strings.isNullOrEmpty(serverName)) {
			serverName = "default";
		}
		for (ServerProxy d : items) {
			if (d.getConfig().host.equals(serverName)) {
				return d;
			}
		}
		synchronized (this) {
			for (ServerProxy d : items) {
				if (d.getConfig().host.equals(serverName)) {
					return d;
				}
			}
			ServerConf conf = new ServerConf();
			conf.host = environment.getProperty("server." + serverName + ".host");
			if (Strings.isNullOrEmpty(conf.host)) {
				throw Exceptions.newNullArgumentException("[Properties]server." + serverName + ".host");
			}
			conf.port = Strings.parseInt(environment.getProperty("server." + serverName + ".host"), 0);
			conf.user = environment.getProperty("server." + serverName + ".user");
			conf.password = environment.getProperty("server." + serverName + ".password");
			//
			conf.proxyHost = environment.getProperty("server." + serverName + ".proxy.host");
			conf.proxyPort = Strings.parseInt(environment.getProperty("server." + serverName + ".proxy.port"), 0);
			//
			conf.keyFile = environment.getProperty("server." + serverName + ".key_file");
			conf.keyFilePassword = environment.getProperty("server." + serverName + ".key_file.password");
			//
			conf.localTempPath = environment.getProperty("server." + serverName + ".local_temp_path");
			conf.remoteTempPath = environment.getProperty("server." + serverName + ".remote_temp_path");
			ServerProxy proxy = ServerProxy.newInstance(conf);
			items.add(proxy);
			return proxy;
		}
	}

	public ServerProxy getServerProxy() {
		return getServerProxy("default");
	}

}
