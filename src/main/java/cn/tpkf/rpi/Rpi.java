package cn.tpkf.rpi;

import cn.tpkf.rpi.config.RpiConfig;
import cn.tpkf.rpi.devices.gpio.digital.in.Button;
import cn.tpkf.rpi.devices.gpio.digital.in.FlameSensor;
import cn.tpkf.rpi.devices.gpio.digital.in.HCSr501;
import cn.tpkf.rpi.devices.gpio.digital.in.LineTrackingSensor;
import cn.tpkf.rpi.devices.gpio.digital.in.SoundSensor;
import cn.tpkf.rpi.devices.gpio.digital.in.TouchSensor;
import cn.tpkf.rpi.devices.gpio.digital.in.VibrationSensor;
import cn.tpkf.rpi.devices.gpio.digital.out.ActiveBuzzer;
import cn.tpkf.rpi.devices.gpio.digital.out.Led;
import cn.tpkf.rpi.devices.gpio.digital.out.Relay;
import cn.tpkf.rpi.devices.gpio.pwm.PassiveBuzzer;
import cn.tpkf.rpi.devices.gpio.pwm.SG90Servo;
import cn.tpkf.rpi.devices.gpio.w1.DS18B20;
import cn.tpkf.rpi.devices.i2c.Lcd1602I2c;
import cn.tpkf.rpi.devices.i2c.Pcf8591;
import cn.tpkf.rpi.devices.other.HCSr04;
import cn.tpkf.rpi.devices.other.L298N;
import cn.tpkf.rpi.devices.other.ULN2003Stepper;
import cn.tpkf.rpi.enums.BCMEnums;
import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;

import java.util.Objects;

/**
 * Zero-setup facade for common Raspberry Pi device operations.
 *
 * @author Harlan
 */
public final class Rpi {

    private static volatile DeviceManager deviceManager;

    private static volatile RpiConfig config = RpiConfig.defaults();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(Rpi::shutdown, "rpi-device-sdk-shutdown"));
    }

    private Rpi() {
    }

    public static DeviceManager manager() {
        DeviceManager current = deviceManager;
        if (current != null && current.isRunning()) {
            return current;
        }
        synchronized (Rpi.class) {
            current = deviceManager;
            if (current == null || !current.isRunning()) {
                deviceManager = DeviceManager.create(config);
            }
            return deviceManager;
        }
    }

    public static void configure(RpiConfig newConfig) {
        Objects.requireNonNull(newConfig, "newConfig must not be null");
        synchronized (Rpi.class) {
            shutdown();
            config = newConfig;
        }
    }

    public static void use(DeviceManager manager) {
        Objects.requireNonNull(manager, "manager must not be null");
        synchronized (Rpi.class) {
            if (deviceManager != manager) {
                shutdown();
            }
            deviceManager = manager;
            config = manager.getConfig();
        }
    }

    public static void shutdown() {
        synchronized (Rpi.class) {
            DeviceManager current = deviceManager;
            if (current != null) {
                current.shutdown();
                deviceManager = null;
            }
        }
    }

    public static Led led(IBCMEnums address) {
        return manager().led(address);
    }

    public static Relay relay(IBCMEnums address) {
        return manager().relay(address);
    }

    public static ActiveBuzzer activeBuzzer(IBCMEnums address) {
        return manager().activeBuzzer(address);
    }

    public static PassiveBuzzer passiveBuzzer(IBCMEnums address, int frequency) {
        return manager().passiveBuzzer(address, frequency);
    }

    public static Button button(IBCMEnums address, Runnable onPressTask, Runnable onReleaseTask) {
        return manager().button(address, onPressTask, onReleaseTask);
    }

    public static HCSr501 pir(IBCMEnums address) {
        return manager().pir(address);
    }

    public static FlameSensor flameSensor(IBCMEnums address) {
        return manager().flameSensor(address);
    }

    public static SoundSensor soundSensor(IBCMEnums address) {
        return manager().soundSensor(address);
    }

    public static TouchSensor touchSensor(IBCMEnums address) {
        return manager().touchSensor(address);
    }

    public static VibrationSensor vibrationSensor(IBCMEnums address) {
        return manager().vibrationSensor(address);
    }

    public static LineTrackingSensor lineTrackingSensor(IBCMEnums address) {
        return manager().lineTrackingSensor(address);
    }

    public static SG90Servo servo(IBCMEnums address) {
        return manager().servo(address);
    }

    public static L298N l298n(String id, IBCMEnums in1Address, IBCMEnums in2Address) {
        return manager().l298n(id, in1Address, in2Address);
    }

    public static L298N l298n(String id, IBCMEnums in1Address, IBCMEnums in2Address, IBCMEnums enableAddress) {
        return manager().l298n(id, in1Address, in2Address, enableAddress);
    }

    public static ULN2003Stepper uln2003Stepper(String id, IBCMEnums in1Address, IBCMEnums in2Address,
                                                IBCMEnums in3Address, IBCMEnums in4Address) {
        return manager().uln2003Stepper(id, in1Address, in2Address, in3Address, in4Address);
    }

    public static HCSr04 hcsr04(BCMEnums triggerAddress, BCMEnums echoAddress) {
        return manager().hcsr04(triggerAddress, echoAddress);
    }

    public static DS18B20 ds18b20() {
        return manager().ds18b20();
    }

    public static DS18B20 ds18b20(String id, String sensorId) {
        return manager().ds18b20(id, sensorId);
    }

    public static Pcf8591 pcf8591(int bus) {
        return manager().pcf8591(bus);
    }

    public static Lcd1602I2c lcd1602(int bus) {
        return manager().lcd1602(bus);
    }

    public static Lcd1602I2c lcd1602(int bus, int device) {
        return manager().lcd1602(bus, device);
    }
}
