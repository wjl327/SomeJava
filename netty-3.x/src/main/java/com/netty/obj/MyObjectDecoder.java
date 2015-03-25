package com.netty.obj;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DefaultExceptionEvent;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

/**
 * 简单的Object Decoder 
 * 
 */
public class MyObjectDecoder extends OneToOneDecoder{

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (msg instanceof ChannelBuffer) {
			ChannelBuffer buffer = (ChannelBuffer)msg;
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
			Object obj = ois.readObject();
			return obj;
		}
		return msg;
	}

}
