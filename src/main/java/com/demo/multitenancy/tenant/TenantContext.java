package com.demo.multitenancy.tenant;

public final class TenantContext {

    public static final String NONE = "__none__";

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(String tenantSlug) {
        CURRENT.set(tenantSlug);
    }

    public static String get() {
        String tenant = CURRENT.get();
        return tenant == null ? NONE : tenant;
    }

    public static boolean isSet() {
        return CURRENT.get() != null;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
