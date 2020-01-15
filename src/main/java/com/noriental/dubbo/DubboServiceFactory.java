package com.noriental.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class DubboServiceFactory {

    private ApplicationConfig application;
    private RegistryConfig registry;
    private String version;
    private  ReferenceConfigCache cache;
    private  ReferenceConfig<GenericService> reference;
    private  ClassLoader loader;

    private static class SingletonHolder {
        private static DubboServiceFactory INSTANCE = new DubboServiceFactory();
    }

    private DubboServiceFactory() {
    }

    public static DubboServiceFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Object genericInvoke(String registryAddress, String interfaceClass, String methodName, List<Map<String, Object>> parameters) {

        Properties prop = new Properties();
        loader = DubboServiceFactory.class.getClassLoader();
        try {
            prop.load(loader.getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(prop.getProperty("application.name"));
        //这里配置了dubbo的application信息*(demo只配置了name)*，因此demo没有额外的dubbo.xml配置文件
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(registryAddress);
        //这里配置dubbo的注册中心信息，因此demo没有额外的dubbo.xml配置文件

        this.application = applicationConfig;
        this.registry = registryConfig;
        this.version = prop.getProperty("version");

        reference = new ReferenceConfig<GenericService>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setVersion(version);
        reference.setInterface(interfaceClass); // 接口名
        reference.setGeneric(true); // 声明为泛化接口
        /*ReferenceConfig实例很重，封装了与注册中心的连接以及与提供者的连接，
        需要缓存，否则重复生成ReferenceConfig可能造成性能问题并且会有内存和连接泄漏。
        API方式编程时，容易忽略此问题。
        这里使用dubbo内置的简单缓存工具类进行缓存*/
        cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference);
        // 用com.alibaba.dubbo.rpc.service.GenericService可以替代所有接口引用

        int len = parameters.size();
        String[] invokeParamTyeps = new String[len];
        Object[] invokeParams = new Object[len];
        for (int i = 0; i < len; i++) {
            invokeParamTyeps[i] = parameters.get(i).get("ParamType") + "";
            invokeParams[i] = parameters.get(i).get("Object");
        }

        Object result = genericService.$invoke(methodName, invokeParamTyeps, invokeParams);

// 关闭注册服务,断开与dubbo的连接
//        cache.destroyAll();
//        reference.destroy();
//        loader.clearAssertionStatus();

        return result;

    }

    public void destory(){
        //关闭注册服务,断开与dubbo的连接
        cache.destroyAll();
        reference.destroy();
        loader.clearAssertionStatus();
    }

}