package com.wjl.hello.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThreadLocalRandom;

/**
 * Netty UDP Demo
 * 
 * @author jl.wu
 *
 */
public class JokeServer {
	
	private int port = 8999;
	
	private void service() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			 .channel(NioDatagramChannel.class)
			 .option(ChannelOption.SO_BROADCAST, true)
			 .handler(new JokeUDPServerHandler());
			 
			b.bind(port).sync().channel().closeFuture().await();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally{
			group.shutdownGracefully();
		}
	}
	
	private static class JokeUDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket>{
		private String[] jokes = {"四只老鼠吹牛：甲：我每天都拿鼠药当糖吃；乙：我一天不踩老鼠夹脚发痒；丙：我每天不过几次大街不踏实；丁：时间不早了，回家抱猫去咯。", 
				"一只小狗爬上你的餐桌，向一只烧鸡爬去，你大怒道：你敢对那只烧鸡怎样，我就敢对你怎样，结果小狗舔了一下鸡屁股，你昏倒，小狗乐道：小样看谁狠。", 
				"如果每个人都不丢垃圾，清洁工不就失业了吗？想到这里，我又继续扫了下去。", 
				"地铁上，旁边的一个孕妇眼看就要生了。我作为一名医生，此刻容不得半点迟疑，立即递给她我们医院的宣传单。"};
		public void messageReceived(ChannelHandlerContext ctx,
				DatagramPacket msg) throws Exception {
			String req = msg.content().toString(CharsetUtil.UTF_8);
			System.out.println("收到请求命令：" + req);
			if(req != null && req.equalsIgnoreCase("joke")){
				ctx.writeAndFlush(getRespPacket(msg));
			}
		}
		
		private DatagramPacket getRespPacket(DatagramPacket reqPackage){
			ByteBuf resp = Unpooled.copiedBuffer(getJoke(), CharsetUtil.UTF_8);
			DatagramPacket packet = new DatagramPacket(resp, reqPackage.sender());
			return packet;
		}
		
		private String getJoke(){
			return jokes[ThreadLocalRandom.current().nextInt(jokes.length)];
		}
		
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			ctx.close();
		}
	}
	
	public static void main(String[] args) {
		new JokeServer().service();
	}

}
