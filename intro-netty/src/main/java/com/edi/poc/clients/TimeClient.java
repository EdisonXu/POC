package com.edi.poc.clients;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.edi.poc.handlers.TimeClientHandler;
import com.edi.poc.handlers.TimeDecoder;

public class TimeClient {

	public static void main(String[] args) throws InterruptedException {
		String host = "localhost";
		int port = 8081;
		
		EventLoopGroup workerGroup= new NioEventLoopGroup();
		
		try{
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new TimeDecoder(), new TimeClientHandler());
				}
			});
			
			// Start the client.
			ChannelFuture f = b.connect(host, port);
			
			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		}finally
		{
			workerGroup.shutdownGracefully();
		}
	}
}
