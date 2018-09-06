package com.ibm.atlas.sampler.model;

import com.fasterxml.jackson.annotation.JsonTypeName;


@JsonTypeName(value = "discoveryInfo")
public class DiscoveryInfo{
    private String hostName;
    private Boolean secure;
    private String serviceName;

    private Integer port;
    private String serviceType;
    private String serviceTitle;
    private Boolean enableApiDoc;
    private String description;

    public DiscoveryInfo(String hostName, Boolean secure, String serviceName,
                         Integer port, String serviceType, String serviceTitle,
                         Boolean enableApiDoc, String description
                         ) {
        this.hostName = hostName;
        this.secure = secure;
        this.serviceName = serviceName;

        this.port = port;
        this.serviceType = serviceType;
        this.serviceTitle = serviceTitle;
        this.enableApiDoc = enableApiDoc;
        this.description = description;

    }

    public String getHostName () {
        return hostName;
    }

    public Boolean getSecure() {
        return secure;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Integer getPort () {
        return port;
    }

    public String getServiceType () {
        return serviceType;
    }

    public String getServiceTitle () { return serviceTitle; }

    public Boolean getEnableApiDoc () {
        return enableApiDoc;
    }

    public String getDescription () {
        return description;
    }


}
