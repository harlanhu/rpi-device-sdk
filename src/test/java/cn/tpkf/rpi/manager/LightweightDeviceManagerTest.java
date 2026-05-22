package cn.tpkf.rpi.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LightweightDeviceManagerTest {

    @Test
    public void testAddGetShutdown() {
        LightweightDeviceManager manager = new LightweightDeviceManager();
        MockDevice device = new MockDevice("mock-1", "Mock Device");

        manager.addDevice(device);
        assertTrue(manager.containsDevice("mock-1"));
        assertEquals(1, manager.getDeviceCount());
        assertSame(device, manager.getDevice("mock-1"));

        assertTrue(manager.shutdownDevice("mock-1"));
        assertTrue(device.isShutdown());

        manager.shutdown();
        assertEquals(0, manager.getDeviceCount());
    }
}
