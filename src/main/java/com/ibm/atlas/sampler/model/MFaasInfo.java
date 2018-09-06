package com.ibm.atlas.sampler.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "mfaasInfo")
public class MFaasInfo {
    private DiscoveryInfo discoveryInfo;

    public MFaasInfo(DiscoveryInfo discoveryInfo) {
        this.discoveryInfo = discoveryInfo;
    }

    public DiscoveryInfo getDiscoveryInfo() {
        return discoveryInfo;
    }
}
