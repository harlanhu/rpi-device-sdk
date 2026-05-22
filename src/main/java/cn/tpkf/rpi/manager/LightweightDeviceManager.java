package cn.tpkf.rpi.manager;

import cn.tpkf.rpi.devices.Device;
import cn.tpkf.rpi.exception.DeviceManagerException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight DeviceManager for tests and CI that does not depend on Pi4J.
 */
public class LightweightDeviceManager implements AutoCloseable {

    private final Map<String, Device> devices = new ConcurrentHashMap<>();

    public void addDevice(Device device) {
        if (device == null || device.getId() == null || device.getId().isBlank()) {
            throw new DeviceManagerException("Device id or name is blank");
        }
        Device previous = devices.putIfAbsent(device.getId(), device);
        if (previous != null) {
            throw new DeviceManagerException("Duplicate device id: " + device.getId());
        }
    }

    public void removeDevice(String id) {
        devices.remove(id);
    }

    public Device getDevice(String id) {
        return devices.get(id);
    }

    public <T extends Device> T getDevice(String id, Class<T> deviceType) {
        Device device = getDevice(id);
        if (device == null) {
            return null;
        }
        if (!deviceType.isInstance(device)) {
            throw new DeviceManagerException("Device type mismatch, id: " + id + ", expected: " + deviceType.getName());
        }
        return deviceType.cast(device);
    }

    public Map<String, Device> getDevices() {
        return Map.copyOf(devices);
    }

    public boolean containsDevice(String id) {
        return devices.containsKey(id);
    }

    public int getDeviceCount() {
        return devices.size();
    }

    public boolean shutdownDevice(String id) {
        Device device = devices.get(id);
        if (device == null) {
            return false;
        }
        device.shutdown();
        return true;
    }

    public boolean shutdown() {
        devices.values().forEach(device -> {
            try {
                device.shutdown();
            } catch (RuntimeException e) {
                // swallow in tests
            }
        });
        devices.clear();
        return true;
    }

    @Override
    public void close() {
        shutdown();
    }
}
