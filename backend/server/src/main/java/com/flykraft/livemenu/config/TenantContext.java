package com.flykraft.livemenu.config;

public class TenantContext {
    private static final ThreadLocal<Long> currentKitchenId = new ThreadLocal<>();

    public static void setKitchenId(Long kitchenId) { currentKitchenId.set(kitchenId); }
    public static Long getKitchenId() { return currentKitchenId.get(); }
    public static void clear() { currentKitchenId.remove(); }
}
