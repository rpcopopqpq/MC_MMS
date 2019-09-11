package kr.ac.kaist.mns_interaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import kr.ac.kaist.mns_interaction.MIH_MessageOutputChannel.MessageBlockingQue;

public class MnsClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private static final Logger logger = LoggerFactory.getLogger(MnsClientHandler.class);
	
	private String req;
	
	MnsClientHandler(String req) {
		this.req = req;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		logger.debug("============req\r{}\r",req);
		ctx.writeAndFlush(Unpooled.copiedBuffer(req, CharsetUtil.UTF_8));

	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
		String res = "";
		
		res = in.toString(CharsetUtil.UTF_8);
		logger.debug("\r\r\rClient received: {}\r\r", res);

		try {
			MessageBlockingQue.getQueue().put(res);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.debug("\rlength {}", res.length());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
