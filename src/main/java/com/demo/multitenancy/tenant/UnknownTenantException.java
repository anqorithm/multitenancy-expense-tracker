package com.demo.multitenancy.tenant;

public class UnknownTenantException extends RuntimeException {

    private final String host;

    public UnknownTenantException(String host) {
        super("unknown tenant for host '" + host + "'");
        this.host = host;
    }

    public String getHost() {
        return host;
    }
}
