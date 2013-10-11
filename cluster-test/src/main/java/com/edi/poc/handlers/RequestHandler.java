package com.edi.poc.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edi.poc.protobuf.data.CommonProtocol.CommonRequest;
import com.edi.poc.protobuf.data.CommonProtocol.CommonResponse;
import com.edi.poc.protobuf.data.CommonProtocol.CommonResponseData;
import com.edi.poc.protobuf.data.CommonProtocol.CommonResponseData.ResponseType;
import com.edi.poc.utils.DateAndTimeUtil;
import com.google.protobuf.ByteString;

public class RequestHandler extends SimpleChannelInboundHandler<CommonRequest> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("Some dude is connected at " + DateAndTimeUtil.getCurrentTime());
		//ctx.fireChannelActive();
    }
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("That dude lost connection at " + DateAndTimeUtil.getCurrentTime());
		//ctx.fireChannelActive();
    }
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
		LOGGER.error("Error occurs", cause);
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CommonRequest msg)
			throws Exception {
		List<String> requiredNodeInfo = new ArrayList<String>();
		if(msg.getUserIp()!=null)
			requiredNodeInfo.add(String.valueOf(msg.getUserIp()));
	
		
		if(msg.getUserName()!=null)
			requiredNodeInfo.add(String.valueOf(msg.getUserName()));
		
		if(msg.getProductId() != 0)
			requiredNodeInfo.add(String.valueOf(msg.getProductId()));
		
		if(msg.getVersion()!=0F)
			requiredNodeInfo.add(String.valueOf(msg.getVersion()));
		
		LOGGER.info(requiredNodeInfo.toString());
		
		CommonResponse.Builder response = CommonResponse.newBuilder();
		CommonResponseData.Builder responseData = CommonResponseData.newBuilder();
		int ret = 305;
		int responseType = 1;
		
		response.setCmd(msg.getCmd()).setSequence(msg.getSequence()).setRet(ret);
		responseData.setType(CommonResponseData.ResponseType.valueOf(responseType)).setFrame(2);
		byte[] out = String.valueOf("200OK可以").getBytes("UTF-8");
		responseData.setType(ResponseType.JSON);
		responseData.setData(ByteString.copyFrom(out));
		response.setData(responseData);
		ctx.writeAndFlush(response);
	}
	
}
