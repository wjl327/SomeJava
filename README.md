SomeJava
========

###技术笔记 慢慢整理成博客：

  - [分析虚拟机GC回收](http://note.youdao.com/share/?id=ca4dfc685ba138ed98e00c3c60f5342f&type=note)
  - [分析ActiveMQ负载均衡](http://note.youdao.com/share/?id=5f987bcf03ec0ee0f8a38e0c0e09a00e&type=note)
  - [深入分析内存溢出，记录踩坑和填坑的过程](http://note.youdao.com/share/?id=10732f869f507a3602dedfd7d07348b5&type=note)
  - [你真的了解 context:annotation-config、context:component-scan、context:property-placeholder？](http://note.youdao.com/share/?id=285418606bfa7b8ec6fcd63e56ed9d9f&type=note)
  - [理解线程锁概念](http://note.youdao.com/share/?id=1bd09890ea6d6bc2cfe59ac90332acf6&type=note)

###tomcat

* `Tomcat6`  6版本是个经典，自己整理可以跑起来的Tomcat代码，阅读学习。
* `tomcat-work` 是《How Tomcat Works》的代码，可惜是Jdk1.4的，也可以作为学习用。

###分布式小框架Demo

* `gh-soa`  作为服务端
* `gh-soa-remote`
* `gh-web`  作为客户端，通过hessian访问soa。
<br>
帮别人弄的一个框架Demo、基于maven构建、采用springMvc+springJdbc，特点是web层和service层分离，采用hessian实现远程交互，这样web和service可以分开部署，中间用代理服务器转发，实现一台web多个service节点提供服务。当做学习的Demo而已，其实想做这种淘宝的Dubbo才是不错的方案。

###Quartz定时器框架

* `timer` 环境Spring、Quertz、Logger 定时调度项目框架。拓展：通过Web界面启停定时器。

###SpringMVC框架

* `springmvc`  非web项目，但集成Jetty，可以通过Bootstrap直接跑起来提供web服务。Bootstrap相当于把web.xml文件代码话，所以可以用来学习springmvc，或者加深对“容器”和“Servlet(包括Filter、listener)”工作原理的理解。
* `springmvc2` web项目demo，集成Mongodb，可以作为学习，或者拓展作为项目开发框架。 特别在里面说明了applicationContext和DispatcherServlet的关系，见web.xml文件。

###cxf的使用demo

* `simple版`  直接通过Java使用Jetty提供服务。
* `spring版`  整合到Spring框架，适合web服务的使用情况。
* 以前抽取出来的Demo，有上传到Csdn，不过那个是非Maven版的。现在改成Maven项目整理到Github。另外，我建议快速开发webservice的时候可以考虑采用cxf，不用去了解细节；而如果要进行长期或深度开发，那建议去学一下wsdl协议相关、SoapUI测试、另外可以了解有wsdl2Java之类的工具。

###ActiveMQ的使用demo mq项目

* `com.wjl.simple` 下面有两个Bootstrap都是完整例子（不需要单独启动MQ服务器），只依赖一个Jar包。包括从代码中启动MQ服务器、设置KahaDB、创建连接、Session、设置生产者和设置消费者。这种用法一般不用于生产环境，但是很适合用来了解MQ的原理。

###Mongodb项目

* `mongodb-test` 主要是java驱动对mongodb的大部分操作。包括：比较复杂的操作group和MapReduce等。

###Netty项目

* `netty-3.x`基于netty3.x版本的入门代码，项目里分netty和nio分别实现echoServer的业务。可做对比，明白选择Netty的好处
* `netty-5.x`基于netty5.x版本的入门实用代码，大部分参考《Netty权威指南》。注:由于Netty4之后对整个项目进行分包，因此比Netty3入门相对难一点。当然5.x也延续4.x的分包，但常用大部分功能还是在netty-all包下。[Netty3和4的区别](http://www.oschina.net/translate/netty-4-0-new-and-noteworthy?print)。还有Netty+protobuf的Demo。
* netty-5.x 推荐阅读顺序：
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

###Thrift项目

* `thrift-hello-server` thrift服务端Demo，项目ReadMe文件夹下有thrift的编译工具。
* `thrift-hello-client` thrift客户端Demo。

####工具相关Weather Poi JFreechart JExcel

#### Mina

#### Redis 

#### HBase

####Solr
