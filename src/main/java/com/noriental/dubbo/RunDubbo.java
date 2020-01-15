package com.noriental.dubbo;


import com.alibaba.fastjson.JSON;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/4/18.
 */
public class RunDubbo extends AbstractJavaSamplerClient {



    private long start = 0;//记录测试开始时间；
    private long end = 0;//记录测试结束时间；
    public String zk;//zk地址和端口
    public String intetfaceClassName;//接口类名
    public String methodName;//方法名
    public String paramType;//参数类型
    public String requestInfo;//请求数据
    public String expectedCode;//检验数据
    private DubboServiceFactory dubbo;

    //初始化操作
    @Override
    public void setupTest(JavaSamplerContext arg0) {
        System.out.println("test start ");
        dubbo = DubboServiceFactory.getInstance();


    }

    //设置默认值
    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("zk", "zookeeper://IP地址:2181");
        params.addArgument("intetfaceClassName", "Service名称");
        params.addArgument("paramType","参数类型");
        params.addArgument("methodName", "findFutureYear");
        params.addArgument("requestInfo", "{\"orgType\":2,\"orgId\":119,\"reqId\":\"123456789\"}");
        params.addArgument("expectedCode", "code=0");
        return params;
    }

    //获取jmeter输入的参数值
    public void setValues(JavaSamplerContext arg0) {
        zk = arg0.getParameter("zk");
        intetfaceClassName = arg0.getParameter("intetfaceClassName");
        paramType = arg0.getParameter("paramType");
        methodName = arg0.getParameter("methodName");
        requestInfo = arg0.getParameter("requestInfo");
        expectedCode = arg0.getParameter("expectedCode");
    }


    @Override
    public SampleResult runTest(JavaSamplerContext arg0) {
        SampleResult sr = new SampleResult();
        sr.setSampleLabel("dubbo_jmeter");
        setValues(arg0);
        //组装参数
        Map requestParams = JSON.parseObject(requestInfo, Map.class);
        Map map = new HashMap<String,Object>();
        map.put("ParamType", paramType);
        map.put("Object", requestParams);
        List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
        params.add(map);
        String headers = "zk=" + zk + " intetfaceClassName=" + intetfaceClassName + "  methodName=" + methodName + "  params=" + params;
        sr.setRequestHeaders(headers);
        //开始
        sr.sampleStart();
        start = System.currentTimeMillis();

        try {
            String result =  dubbo.genericInvoke(zk,intetfaceClassName,methodName,params).toString();

            if (result.contains(expectedCode)) {
                sr.setSuccessful(true);
                sr.setResponseData(result, null);
                sr.setDataType(SampleResult.TEXT);
            } else {
                sr.setSuccessful(false);

            }

        } catch (Exception e) {
            System.out.println("response  error : " + e.getMessage());
            sr.setSuccessful(false);

        } finally {
            sr.sampleEnd();

        }
        return sr;
    }


    @Override
    public void teardownTest(JavaSamplerContext arg) {
        end = System.currentTimeMillis();
        dubbo.destory();
        System.out.println("responst time:" + (end - start) + " ms");
        System.out.println("test end ");
    }


}
