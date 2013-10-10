package com.edi.poc.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

public class TimeClientHandler extends ChannelInboundHandlerAdapter  {

	/*private ByteBuf buf = null;
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx)
	{
		buf = ctx.alloc().buffer(4);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx)
	{
		buf.release();
		buf = null;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf m = (ByteBuf)msg;
		
		buf.writeBytes(m);
		m.release();
		
		if(buf.readableBytes()>=4)
		{
			long currentTimeMillis = (buf.readInt()-2208988800L)*1000L;
			System.out.println(new Date(currentTimeMillis));
			ctx.close();
		}
	}*/
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf buf = (ByteBuf)msg;
		
		try {
			long currentTimeMillis = (buf.readUnsignedInt()-2208988800L)*1000L;
			System.out.println(new Date(currentTimeMillis));
			ctx.close();
		}finally{
			buf.release();
		}
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
