package com.hlysine.create_power_loader;

public class CreatePowerLoader {
    public static final String MOD_ID = "create_power_loader";

    public static void init() {
        System.out.println(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
