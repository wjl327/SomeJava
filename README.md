SomeJava
========

###技术笔记 慢慢整理成博客：

  - [分析虚拟机GC回收](http://note.youdao.com/share/?id=ca4dfc685ba138ed98e00c3c60f5342f&type=note)
  - [分析ActiveMQ负载均衡](http://note.youdao.com/share/?id=5f987bcf03ec0ee0f8a38e0c0e09a00e&type=note)
  - [深入分析内存溢出，记录踩坑和填坑的过程](http://note.youdao.com/share/?id=10732f869f507a3602dedfd7d07348b5&type=note)

###tomcat

* `Tomcat6`  6版本是个经典，自己整理可以跑起来的Tomcat代码，阅读学习。
* `tomcat-work` 是《How Tomcat Works》的代码，可惜是Jdk1.4的，也可以作为学习用。

###简单定时器框架

* `timer` 环境Spring、Quertz、Logger 定时调度项目框架。

###分布式小框架Demo

* `gh-soa`  作为服务端
* `gh-soa-remote`
* `gh-web`  作为客户端，通过hessian访问soa。
<br>
帮别人弄的一个框架Demo、基于maven构建、采用springMvc+springJdbc，特点是web层和service层分离，采用hessian实现远程交互，这样web和service可以分开部署，中间用代理服务器转发，实现一台web多个service节点提供服务。当做学习的Demo而已，其实想做这种淘宝的Dubbo才是不错的方案。