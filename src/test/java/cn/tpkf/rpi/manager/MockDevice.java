package cn.tpkf.rpi.manager;

import cn.tpkf.rpi.devices.Device;
import lombok.Getter;

/**
 * Simple mock Device implementation for local tests and CI.
 */
public class MockDevice implements Device {

    private final String id;
    private final String name;
    // Test helper
    @Getter
    private boolean shutdown = false;

    public MockDevice(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void shutdown() {
        this.shutdown = true;
    }

    @Override
    public String getDescription() {
        return name;
    }

}
