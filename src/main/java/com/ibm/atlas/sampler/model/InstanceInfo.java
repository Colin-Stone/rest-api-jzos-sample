package com.ibm.atlas.sampler.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "info")
public class InstanceInfo{
    private App app;
    private MFaasInfo mfaasInfo;

    public InstanceInfo(App app, MFaasInfo mfaaSInfo) {
        this.app = app;
        this.mfaasInfo = mfaaSInfo;
    }

    public App getApp() {
        return app;
    }

    public MFaasInfo getMFaaSInfo() {
        return mfaasInfo;
    }
}
