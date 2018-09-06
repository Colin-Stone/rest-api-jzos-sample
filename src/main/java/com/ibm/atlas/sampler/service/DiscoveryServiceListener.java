package com.ibm.atlas.sampler.service;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.ResourceBundle;

@WebListener
public class DiscoveryServiceListener implements ServletContextListener {
    private static ResourceBundle eurekaProperties = ResourceBundle.getBundle("eureka-client");
    private static ApplicationInfoManager applicationInfoManager;
    private static EurekaClient eurekaClient;

    public static synchronized ApplicationInfoManager initializeApplicationInfoManager(EurekaInstanceConfig instanceConfig) {
        if (applicationInfoManager == null) {
            InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
            InstanceInfo.Builder builder = new InstanceInfo.Builder(instanceInfo);
            String instanceId = eurekaProperties.getString("eureka.service.hostname") + ":" +
                    eurekaProperties.getString("eureka.name") + ":" +
                    eurekaProperties.getString("eureka.port");


            InstanceInfo instance = builder.setInstanceId(instanceId)
                    .setHostName(eurekaProperties.getString("eureka.service.hostname"))
                    .setPort(Integer.parseInt(eurekaProperties.getString("eureka.port"))).build();
            applicationInfoManager = new ApplicationInfoManager(instanceConfig, instance);
        }
        return applicationInfoManager;
    }

    public static synchronized EurekaClient initializeEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig clientConfig) {
        if (eurekaClient == null) {
            eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
        }
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        return eurekaClient;
    }

    public void contextInitialized(ServletContextEvent sce) {
        // create the client
        ApplicationInfoManager infoManager = initializeApplicationInfoManager(new MyDataCenterInstanceConfig());
        initializeEurekaClient(infoManager, new DefaultEurekaClientConfig());
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // shutdown the client
        eurekaClient.shutdown();
    }
}
