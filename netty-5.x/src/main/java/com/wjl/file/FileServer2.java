package com.wjl.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.RandomAccessFile;

/**
 * 参考netty官方主线分支的例子，简化去掉ssl的情况。 
 * https://github.com/netty/netty/blob/master/example/src/main/java/io/netty/example/file/FileServer.java
 * 通过StringEncoder和LineBasedFrameDecoder编码解码字符串来传输文件内容。
 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 * 
 */
public class FileServer2 {
	
	private int port = 8000;
	
	private void service() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			 .channel(NioServerSocketChannel.class)
			 .option(ChannelOption.SO_BACKLOG, 100)
			 .handler(new LoggingHandler(LogLevel.INFO))
			 .childHandler(new ChannelInitializer<SocketChannel>(){
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					p.addLast(
							new StringEncoder(CharsetUtil.UTF_8),
                            new LineBasedFrameDecoder(8192),
                            new StringDecoder(CharsetUtil.UTF_8),
                            new ChunkedWriteHandler(),   //文件传输
                            new FileServer2Handler());
				}
			 });
			ChannelFuture f = b.bind(port).sync();
			//等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
		
	public static void main(String[] args) throws Exception {
		new FileServer2().service();
	}
	
}

class FileServer2Handler extends SimpleChannelInboundHandler<String>{
	
	private static final String CR = System.getProperty("line.separator");
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg)
			throws Exception {
		String fileName = msg;
		System.out.println("客户端请求的文件：" + fileName);
		RandomAccessFile raf = null;
		long length = -1;
        try {
            raf = new RandomAccessFile("html/"+fileName, "r");
            length = raf.length();
        } catch (Exception e) {
            ctx.writeAndFlush("ERR: " + e.getClass().getSimpleName() + ": " + e.getMessage() + CR);
            return;
        } finally {
            if (length < 0 && raf != null) {
                raf.close();
            }
        }
        
        ctx.write("OK: The file length is : " + raf.length() + "." + CR);
        if (ctx.pipeline().get(SslHandler.class) == null) {
            //SSL没有启动。能使用零拷贝技术来进行文件传输。
            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, length));
        } else {
            //启动了SSL。不能使用零拷贝技术来进行文件传输。
            ctx.write(new ChunkedFile(raf));
        }
        ChannelFuture cf = ctx.writeAndFlush(CR);
        cf.addListener(ChannelFutureListener.CLOSE); //异步监听器：发送结束的时候关闭channel，即关闭与客户端的链接。
    }
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            ctx.writeAndFlush("ERR: " +
                    cause.getClass().getSimpleName() + ": " +
                    cause.getMessage() + '\n').addListener(ChannelFutureListener.CLOSE);
        }
    }
}

