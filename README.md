# teletgram-robot
一个学习项目，学习teltegram机器人的使用

功能列表：  
1.入群欢迎语(已完成)  
2.监控群成员改名行为，给出提示(已完成)  
3.群内关键词回复(已完成)  
4.群内收到非客服人员消息后，进行特殊提示(开发中)  
5.群内定时消息推送(未开始)  
6.提供一些常用小工具(待设计)  
7.支持使用机器人进行功能配置(未开始)  
8.提供人性化的功能配置界面(未开始)  
9.提供使用文档(未开始)  

如有问题，欢迎交流  
联系方式：  
  &emsp;&emsp;telegram: https://t.me/hahakayaa

使用教程： 仔细阅读，一定要按步骤一步一步操作  
  
安装步骤  
	&emsp;&emsp;1.[点击此处下载压缩包](https://github.com/one-robot-dev/teletgram-robot/releases/download/1.0.0/telegram-robot.zip)  
	&emsp;&emsp;2.压缩包放在一个空间比较大的磁盘上，然后解压  
	&emsp;&emsp;3.进入解压好的文件夹内  
	&emsp;&emsp;4.用记事本打开application.properties文件  
	&emsp;&emsp;5.在application.properties文件中找到 robot.token=6249872568:AAHmG3y0xHjGQrB49tbkTexdes8ZhgtgA1s 将6249872568:AAHmG3y0xHjGQrB49tbkTexdes8ZhgtgA1s改为自己机器人的token  
	&emsp;&emsp;6.双击start.bat进行启动  
    
      
获取群id  
	&emsp;&emsp;建一个测试群，把机器人添加进去，在群里发送/getGroupId，机器人会返回群id，记录一下，下面配置会用到  

群功能配置：命令都是直接发送给机器人的  
1.获取所有配置过的群  
&emsp;&emsp;命令：{"masterCmd":"groupFunction", "subCmd":"getAllGroup", "param":{}}  
2.克隆群组功能，以fromGroupId群为模板，克隆一个相同群功能到newGroupId  
	&emsp;&emsp;以-1001651704858为模板克隆功能到测试群id上（上面在测试群里的得到的id）  
	&emsp;&emsp;后面的步骤都以测试群为基础进行操作  
&emsp;&emsp;命令：{"masterCmd":"groupFunction", "subCmd":"cloneFromOld", "param":{"newGroupId":测试群的id, "fromGroupId":-1001651704858}}  
3.获取指定群组的功能信息  
	&emsp;&emsp;关注一些返回的结果中的result,下一步会用到  
&emsp;&emsp;命令：{"masterCmd":"groupFunction", "subCmd":"getByGroupId", "param":{"groupId":测试群的id}}  

现在可以在测试群已经有了 改名提示，关键词回复(你好, 验证)，入群欢迎的功能了，可以体验一下  
非客服发言提示，需要修改一下功能配置中的receiveId(步骤4是修改配置的教学)，当有非客服发言时，会发送提示到receiveId对应的群或人  
  
接下来是对功能进行修改  
4.修改或添加群功能  
	&emsp;&emsp;可以使用步骤3里克隆的测试群进行配置测试，不要修改-1001651704858群的配置，因为-1001651704858是配置模板供参考学习用的  
	&emsp;&emsp;functions的结构 与 上一步得到的返回结果中的result的结构一样，可以直接将result复制过来，也可以挑选几个复制过来，修改各种回复内容后发送命令进行保存  
&emsp;&emsp;命令：{"masterCmd":"groupFunction", "subCmd":"save", "param":{"functions":[{}]}}  
5.删除群指定功能
&emsp;&emsp;命令：{"masterCmd":"groupFunction", "subCmd":"deleteOne", "param":{"groupId":测试群的id, "type":"keywordReply"}}  
6.删除群所有功能  
&emsp;&emsp;命令：{"masterCmd":"groupFunction", "subCmd":"deleteById", "param":{"groupId":测试群的id}}  
  
配置设置：  
&emsp;&emsp;1.查看所有配置  
&emsp;&emsp;命令：{"masterCmd":"config", "subCmd":"getAll", "param":{}}  
&emsp;&emsp;2.设置配置  
&emsp;&emsp;命令：{"masterCmd":"config", "subCmd":"set", "param":{"key":"checkInterval","value":"6"}}  

其他问题：  
&emsp;&emsp;当出现下面报错时，说明连接不到api.telegram.org网址，检查一下网络或vpn  
&emsp;&emsp;&emsp;&emsp;Unexpected error occurred in scheduled task java.net.UnknownHostException: api.telegram.org
