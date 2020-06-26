package com.increasingly.recommender.server;


import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.EnumSet;
import java.util.Properties;

import javax.servlet.DispatcherType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.increasingly.recommender.impl.Configuration;
import com.increasingly.recommender.utils.AESBouncyCastle;


/**
 * Main Entry point for Embedded jetty server
 * 
 * Reads in the Application Context
 * Reads in Properties File
 * Auto setup of Log4j2 (through System Property set in run configuration -D param)
 * @author Shreehari Padaki
 *
 */
public class WebServer implements Runnable
{

	private static final Logger logger = LogManager.getLogger(WebServer.class.getName());
	private static Properties properties = null;
	private final String SERVERPROPSFILE = "webapp/WEB-INF/service.properties";
	private final String SERVERAPPLICATIONCONTEXT = "webapp/WEB-INF/applicationContext.xml";
	private final String KEYSTORE = "webapp/WEB-INF/increasingly.co.jks";
	private final String RESOURCEBASE = "webapp";
	private  FileSystemXmlApplicationContext applicationContext = null;
	private static Server server = null;	
	public static AESBouncyCastle aes;

		
	public static void main(String[] args)
	{
		WebServer webServer = new WebServer();
		webServer.run();
	}

	/**
	 * Run the Jetty Web Server configured with Jersey
	 */
	public void run()
	{
		try
		{
			aes = AESBouncyCastle.getInstance("AirFrameBegining");
			logger.info("Starting Spring..");			
			applicationContext = new FileSystemXmlApplicationContext(SERVERAPPLICATIONCONTEXT);
			readProperties(SERVERPROPSFILE);		
			
			Configuration.setConfiguration();
			
		}
		catch (Exception e)
		{
			logger.error("Error reading prop file " + e.getMessage(), e);
			return;
		}
		try
		{
			logger.info("Starting Web Server...");
			/**
			 *  Setup Threadpool for Jetty Server
			 */
			QueuedThreadPool threadPool = new QueuedThreadPool();
			threadPool.setMinThreads(Integer.parseInt(properties.getProperty("InitialThreadpoolSize")));
			threadPool.setMaxThreads(Integer.parseInt(properties.getProperty("MaxThreadpoolSize")));

			/**
			 *  Jetty Server
			 */
			server = new Server(threadPool);

			/**
			 *  setup http configuration
			 */
			HttpConfiguration http_config = new HttpConfiguration();
			http_config.setSecureScheme("https");
			http_config.setSecurePort(Integer.parseInt(properties.getProperty("WebServerSSLPort")));
			http_config.setOutputBufferSize(Integer.parseInt(properties.getProperty("OutputBufferSize")));
			http_config.setRequestHeaderSize(Integer.parseInt(properties.getProperty("RequestHeaderSize")));
			http_config.setResponseHeaderSize(Integer.parseInt(properties.getProperty("ResponseHeaderSize")));
			http_config.setSendServerVersion(Boolean.parseBoolean((properties.getProperty("SendServerVersion"))));
			http_config.setSendDateHeader(Boolean.parseBoolean((properties.getProperty("SendDateHeader"))));

			/**
			 * Http connection/context setup
			 */
			ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
			http.setPort(Integer.parseInt(properties.getProperty("WebServerPort")));
			http.setIdleTimeout(Integer.parseInt(properties.getProperty("IdleTimeoutMS")));
			server.addConnector(http);
			
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
			context.setResourceBase(RESOURCEBASE);
			context.setContextPath("/");			
					
			FilterHolder cors = context.addFilter(CrossOriginFilter.class,"/*",EnumSet.of(DispatcherType.INCLUDE,DispatcherType.REQUEST));
			cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "false");
			cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, properties.getProperty("AllowedOriginList"));
			//cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "null");
			cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD,OPTIONS");
			cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");	
			
			/**
			 * Static Content handler
			 */
		    ResourceHandler resource_handler = new ResourceHandler();              
            resource_handler.setResourceBase(".");   
           
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[] {resource_handler,context, new DefaultHandler() });
            server.setHandler(handlers);
            
			/** 
			 * Jersey Configuration
			 */
			ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
			jerseyServlet.setInitOrder(1);
			jerseyServlet.setInitParameter("jersey.config.server.provider.packages", properties.getProperty("ServicePackage") + ";" + properties.getProperty("JSONProvider"));
			jerseyServlet.setInitParameter("jersey.config.disableMoxyJson.server", "true");		
		   
			
			/**
			 *  Setup JMX data collection for Jetty
			 */
			MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
			server.addBean(mbContainer);

			/**
			 * SSL Context Factory for Jetty
			 */
			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStorePath(KEYSTORE);
			sslContextFactory.setKeyStorePassword(properties.getProperty("KeyStorePassword"));
			sslContextFactory.setKeyManagerPassword(properties.getProperty("KeyManagerPassword"));
					
			/**
			 * Add SSL HTTP Configuration to Jetty
			 */
			HttpConfiguration https_config = new HttpConfiguration(http_config);
			https_config.addCustomizer(new SecureRequestCustomizer());

			// SSL Connector
			ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https_config));
			sslConnector.setPort(Integer.parseInt(properties.getProperty("WebServerSSLPort")));
			server.addConnector(sslConnector);					
			
			/**
			 * Start Jetty Server
			 */
			server.start();            
		
			server.join();
		}
		catch (Throwable t)
		{
			t.printStackTrace(System.err);
		}
		
	}
		
	/**
	 * Read Properties file
	 * @param fileURL - location of file
	 * @return -success or failure
	 * @throws IOException
	 */
	private boolean readProperties(String fileURL) throws IOException
	{
		properties = new Properties();
		FileInputStream is = new FileInputStream(fileURL);
		properties.load(is);
		is.close();
		logger.info("Read properties file...");
		return true;
	}
	
	public static Properties getProperties()
	{
		return properties;
	}	
	
}