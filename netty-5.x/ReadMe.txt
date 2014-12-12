Netty5.x版本的入门Demo。
阅读顺序：
com.wjl.hello包：了解Netty服务端和客户端如何建立和关闭连接，以及编写业务handler，另外明白Netty异步操作结果监听。
com.wjl.hello2包:通过连续发送100个字符串的方式，展示了TCP拆包和粘包的问题。
com.wjl.hello3包：通过判断长度的方式解决TCP拆包和粘包的问题。顺便学习下如何自己写编解码器。
com.wjl.decoder.fixedlen包：展示定长解码器的使用。
com.wjl.decoder.line包：展示换行解码器的使用，通信时，默认带上回车换行表示一行结束。
com.wjl.decoder.string包：展示通过字符串编解码器来解放对ByteBuf的操作。
com.wjl.decoder.delimit包:展示可以指定字符的编解码器。补充的。
com.wjl.http:了解Netty如何开发Http服务。
com.wjl.file:参照官方的例子整理出来的对文件的操作和传输大文件时候使用ChunkedFile。
com.wjl.serialize.java:了解Netty如何通过序列化传输对象。
com.wjl.serialize.pb:了解Google ProtoBuf技术。
com.wjl.serialize.protobuf:了解Netty如何通过Google Protobuf序列化传输对象。
com.wjl.websocket:了解Netty跟html5如何通过websocket通讯。
com.wjl.udp:了解Netty如何使用UDP。