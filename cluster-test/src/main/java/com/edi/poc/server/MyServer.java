package com.edi.poc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edi.poc.common.config.MyConfig;
import com.edi.poc.utils.DateAndTimeUtil;

public class MyServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyServer.class);
	
	public void start() {
		// load config
		if(!MyConfig.isLoaded())
		{
			LOGGER.error("System will shutdown: failed to load config files.");
			System.exit(-1);
		}
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()+1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()+1);
		
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ServerInitializer())
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.bind(MyConfig.ip, MyConfig.port);
		
		LOGGER.info(buildDebugOutput());
		LOGGER.info("Server is started at " + DateAndTimeUtil.getCurrentTime());
		
	}
	private String buildDebugOutput()
	{
		StringBuffer sb = new StringBuffer("JVM working environment:");
        sb.append("\r\n");
        sb.append("os.name: " + System.getProperty("os.name"));
        sb.append("\r\n");
        sb.append("os.arch: " + System.getProperty("os.arch"));
        sb.append("\r\n");
        sb.append("os.version: " + System.getProperty("os.version"));
        sb.append("\r\n");
        sb.append("sun.arch.data.model: " + System.getProperty("sun.arch.data.model"));
        sb.append("\r\n");
        sb.append("sun.cpu.endian:  " + System.getProperty("sun.cpu.endian"));
        sb.append("\r\n");
        sb.append("sun.io.unicode.encoding: " + System.getProperty("sun.io.unicode.encoding"));
        sb.append("\r\n");
        sb.append("sun.management.compiler: " + System.getProperty("sun.management.compiler"));
        sb.append("\r\n");


        sb.append("java.version: " + System.getProperty("java.version"));
        sb.append("\r\n");
        sb.append("java.vendor: " + System.getProperty("java.vendor"));
        sb.append("\r\n");
        sb.append("java.home: " + System.getProperty("java.home"));
        sb.append("\r\n");
        sb.append("java.specification.version: " + System.getProperty("java.specification.version"));
        sb.append("\r\n");
        sb.append("java.specification.vendor: " + System.getProperty("java.specification.vendor"));
        sb.append("\r\n");
        sb.append("java.specification.name: " + System.getProperty("java.specification.name"));
        sb.append("\r\n");

        sb.append("java.vm.version: " + System.getProperty("java.vm.version"));
        sb.append("\r\n");
        sb.append("java.vm.vendor: " + System.getProperty("java.vm.vendor"));
        sb.append("\r\n");
        sb.append("java.vm.name: " + System.getProperty("java.vm.name"));
        sb.append("\r\n");
        sb.append("java.vm.specification.version: " + System.getProperty("java.vm.specification.version"));
        sb.append("\r\n");
        sb.append("java.vm.specification.vendor: " + System.getProperty("java.vm.specification.vendor"));
        sb.append("\r\n");
        sb.append("java.vm.specification.name: " + System.getProperty("java.vm.specification.name"));
        sb.append("\r\n");

        sb.append("java.class.version: " + System.getProperty("java.class.version"));
        sb.append("\r\n");
        sb.append("java.class.path: " + System.getProperty("java.class.path"));
        sb.append("\r\n");
        sb.append("java.library.path: " + System.getProperty("java.library.path"));
		sb.append("\r\n");
		return sb.toString();
	}

	public static void main(String[] args) {
		
		MyServer server = new MyServer();
		server.start();
		
	}
}
