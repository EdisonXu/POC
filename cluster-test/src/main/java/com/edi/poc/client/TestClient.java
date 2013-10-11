package com.edi.poc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.edi.poc.common.config.MyConfig;

public class TestClient {

	public static void main(String[] args) throws InterruptedException {
		
		if (!MyConfig.isLoaded()) {
			System.exit(-1);
		}
		
		EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()+1);
		try{
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ClientChannelInitializer());
			
			// Start the client.
			ChannelFuture f = b.connect(MyConfig.ip, MyConfig.port);
			
			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		}finally
		{
			workerGroup.shutdownGracefully();
		}
	}
}
