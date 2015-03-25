package com.netty.obj;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * 简单的Object Encoder 
 * 
 */
public class MyObjectEncoder extends OneToOneEncoder{

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (msg instanceof Command) {
			Command command = (Command) msg;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(command);
			oos.flush();
			oos.close();
			ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
			buffer.writeBytes(baos.toByteArray());
			return buffer;
		}
		return msg;
	}

}
