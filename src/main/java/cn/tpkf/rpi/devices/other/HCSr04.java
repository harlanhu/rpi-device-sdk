package cn.tpkf.rpi.devices.other;

import cn.tpkf.rpi.devices.AbstractDevice;
import cn.tpkf.rpi.enums.BCMEnums;
import cn.tpkf.rpi.exception.DeviceException;
import cn.tpkf.rpi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalInputProvider;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;

import java.util.concurrent.TimeUnit;

/**
 * HC-SR04
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/22
 */
public class HCSr04 extends AbstractDevice {

    private static final long DEFAULT_ECHO_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(100);

    private static final double SPEED_OF_SOUND_METERS_PER_SECOND = 343.0;

    private final DigitalOutput trigger;

    private final DigitalInput echo;

    private final BCMEnums triggerAddress;

    private final BCMEnums echoAddress;

    private final long echoTimeoutNanos;

    public HCSr04(DeviceManager deviceManager, String id, String name, BCMEnums triggerAddress, BCMEnums echoAddress) {
        this(deviceManager, id, name, triggerAddress, echoAddress, DEFAULT_ECHO_TIMEOUT_NANOS);
    }

    public HCSr04(DeviceManager deviceManager, String id, String name, BCMEnums triggerAddress, BCMEnums echoAddress,
                  long echoTimeoutNanos) {
        super(deviceManager, id, name);
        this.triggerAddress = triggerAddress;
        this.echoAddress = echoAddress;
        this.echoTimeoutNanos = echoTimeoutNanos;
        trigger = deviceManager.execute(context -> {
            DigitalOutputConfig config = DigitalOutputConfigBuilder.newInstance(context)
                    .id(id + "TRIGGER")
                    .name(name)
                    .onState(DigitalState.HIGH)
                    .initial(DigitalState.LOW)
                    .shutdown(DigitalState.LOW)
                    .address(triggerAddress.getValue())
                    .provider(PiGpioDigitalOutputProvider.class)
                    .build();
            return context.create(config);
        });
        echo = deviceManager.execute(context -> {
            DigitalInputConfig config = DigitalInputConfig.newBuilder(context)
                    .id(id + "ECHO")
                    .name(name)
                    .pull(PullResistance.OFF)
                    .address(echoAddress.getValue())
                    .provider(PiGpioDigitalInputProvider.class)
                    .build();
            return context.create(config);
        });
        deviceManager.addDevice(this);
    }

    /**
     * Calculates the distance based on the HC-SR04 sensor readings.
     *
     * @return the distance calculated in meters
     * @throws DeviceException if an error occurs during the detection process
     */
    public double detect() {
        try {
            // keep critical section minimal: avoid holding lock during sleeps
            lock.lock();
            trigger.on();
        } finally {
            lock.unlock();
        }
        try {
            TimeUnit.MICROSECONDS.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DeviceException("HCSr04 detect error", e);
        }
        try {
            lock.lock();
            trigger.off();
            waitForEchoState(DigitalState.HIGH);
            long startNanoTime = System.nanoTime();
            waitForEchoState(DigitalState.LOW);
            long endNanoTime = System.nanoTime();
            return (endNanoTime - startNanoTime) / 1000000000.0 * SPEED_OF_SOUND_METERS_PER_SECOND / 2;
        } finally {
            lock.unlock();
        }
    }

    private void waitForEchoState(DigitalState expectedState) {
        long deadline = System.nanoTime() + echoTimeoutNanos;
        while (!expectedState.equals(echo.state())) {
            if (System.nanoTime() > deadline) {
                throw new DeviceException("HCSr04 echo wait timeout, expected state: " + expectedState);
            }
            Thread.onSpinWait();
        }
    }

    @Override
    public void shutdown() {
        // best-effort to shutdown GPIO resources
        try {
            deviceManager.execute(context -> {
                context.shutdown(trigger.id());
                return null;
            });
        } catch (Exception e) {
            // ignore - best effort
        }
        try {
            deviceManager.execute(context -> {
                context.shutdown(echo.id());
                return null;
            });
        } catch (Exception e) {
            // ignore - best effort
        }
        deviceManager.removeDevice(id);
    }

    @Override
    public String getDescription() {
        return id + "-HCSR04-BCM " + triggerAddress + echoAddress + "-" + name;
    }
}
