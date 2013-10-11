package com.edi.poc.client;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edi.poc.protobuf.data.CommonProtocol.CommonRequest;
import com.edi.poc.protobuf.data.CommonProtocol.CommonResponse;
import com.edi.poc.protobuf.data.CommonProtocol.CommonResponseData;

public class ServerResponseHandler extends SimpleChannelInboundHandler<CommonResponse> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerResponseHandler.class);
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
		CommonRequest.Builder request = CommonRequest.newBuilder();
		request.setUserIp("127.0.0.1");
		request.setUserName("test");
		request.setProductId(1);
		request.setVersion(1.23f);
		request.setCmd(1);
		request.setSequence(1);
		request.setPackage("com.edi.poc.WorkService");
		request.addArg("test");
		request.setFunction("print");
		ctx.write(request.build());
		ctx.flush();
		LOGGER.info("Send a request to server.");
    }
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
		LOGGER.error("Error occurs", cause);
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CommonResponse msg)
			throws Exception {
		LOGGER.info("Parse the response");
		List<String> responseInfo = new ArrayList<String>();
		responseInfo.add(String.valueOf(msg.getCmd()));
		responseInfo.add(String.valueOf(msg.getSequence()));
		responseInfo.add(String.valueOf(msg.getRet()));
		
		CommonResponseData data = msg.getData();
		responseInfo.add(String.valueOf(data.getType()));
		String info = new String(data.getData().toByteArray(),"UTF-8");
		responseInfo.add(info);
		LOGGER.info(responseInfo.toString());
	}
}
