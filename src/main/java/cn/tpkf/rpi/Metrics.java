package cn.tpkf.rpi;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lightweight metrics for observability (counts only).
 */
public final class Metrics {

    private final AtomicInteger registeredDevices = new AtomicInteger(0);
    private final AtomicLong totalDevicesRegistered = new AtomicLong(0);
    private final AtomicLong shutdownOperations = new AtomicLong(0);

    public void onDeviceAdded() {
        registeredDevices.incrementAndGet();
        totalDevicesRegistered.incrementAndGet();
    }

    public void onDeviceRemoved() {
        registeredDevices.updateAndGet(v -> v > 0 ? v - 1 : 0);
    }

    public void onDeviceShutdown() {
        shutdownOperations.incrementAndGet();
    }

    public int getRegisteredDevices() {
        return registeredDevices.get();
    }

    public long getTotalDevicesRegistered() {
        return totalDevicesRegistered.get();
    }

    public long getShutdownOperations() {
        return shutdownOperations.get();
    }

    public MetricSnapshot snapshot() {
        return new MetricSnapshot(getRegisteredDevices(), getTotalDevicesRegistered(), getShutdownOperations());
    }

    public static final class MetricSnapshot {
        private final int registeredDevices;
        private final long totalDevicesRegistered;
        private final long shutdownOperations;

        public MetricSnapshot(int registeredDevices, long totalDevicesRegistered, long shutdownOperations) {
            this.registeredDevices = registeredDevices;
            this.totalDevicesRegistered = totalDevicesRegistered;
            this.shutdownOperations = shutdownOperations;
        }

        public int getRegisteredDevices() {
            return registeredDevices;
        }

        public long getTotalDevicesRegistered() {
            return totalDevicesRegistered;
        }

        public long getShutdownOperations() {
            return shutdownOperations;
        }
    }
}
