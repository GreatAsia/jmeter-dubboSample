利用Jmeter工具使用泛化调用压测dubbo服务<br>
*使用条件 <br>
* apache-jmeter-5.1.1版本 <br>
*操作步骤 <br>
*1 使用maven的package打包，会在target目录下面生成lib目录和dubboGeneralize-jmeter-1.0-SNAPSHOT.jar <br>
*2 把lib目录下面的jar包拷贝到 apache-jmeter-5.1.1\lib 下面 <br>
*3 把dubboGeneralize-jmeter-1.0-SNAPSHOT.jar 拷贝到 D:\apache-jmeter-5.1.1\lib\ext  <br>
*4 在bin目录下面启动jmeter.bat  <br>
*5 创建线程组，创建JavaRequest, ClassName选择com。noriental.dubbo.RunDubbo   <br>
*6 根据下面的Name修改对应的Value值  <br>
*7 添加聚合报告和查看结构树  <br>
*8 线程组先使用1个用户运行1次（因为zkclient首次启动需要完成初始化，时间比较长） <br>
*9 线程组改成需要用户和运行规则,进行压测  <br>