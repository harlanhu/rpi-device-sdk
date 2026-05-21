package cn.tpkf.rpi.manager;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.tpkf.rpi.config.RpiConfig;
import cn.tpkf.rpi.devices.Device;
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
import cn.tpkf.rpi.exception.DeviceManagerException;
import cn.tpkf.rpi.pojo.PlatformInfo;
import cn.tpkf.rpi.utils.SystemInfoUtils;
import com.pi4j.Pi4J;
import com.pi4j.common.Descriptor;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.pwm.PwmProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/21
 */
@Slf4j
public class DeviceManager implements AutoCloseable {

    /**
     * 上下文
     */
    private final Context context;

    /**
     * 获取锁超时时间
     */
    private final Long timeOutMillis;

    /**
     * 设备锁
     */
    private final ReentrantLock lock;

    private final RpiConfig config;

    private final Map<String, Device> devices = new ConcurrentHashMap<>();

    public static DeviceManager create() {
        return new DeviceManager();
    }

    public static DeviceManager create(RpiConfig config) {
        return new DeviceManager(config);
    }

    public static DeviceManager create(Context context) {
        return new DeviceManager(context);
    }

    public static DeviceManager create(Context context, RpiConfig config) {
        return new DeviceManager(context, config);
    }

    public DeviceManager() {
        this(RpiConfig.defaults());
    }

    public DeviceManager(RpiConfig config) {
        this(Pi4J.newAutoContext(), config);
    }

    public DeviceManager(Context context) {
        this(context, RpiConfig.defaults());
    }

    public DeviceManager(Context context, Long timeOutMillis) {
        this(context, RpiConfig.builder()
                .lockTimeoutMillis(Objects.requireNonNullElse(timeOutMillis, RpiConfig.DEFAULT_LOCK_TIMEOUT_MILLIS))
                .build());
    }

    public DeviceManager(Context context, RpiConfig config) {
        this.config = Objects.requireNonNullElseGet(config, RpiConfig::defaults);
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.timeOutMillis = this.config.getLockTimeoutMillis();
        this.lock = new ReentrantLock();
    }

    /**
     * 打印横幅
     */
    public String printBanner() {
        // 读取横幅文本
        String banner = ResourceUtil.readUtf8Str("static/banner.txt");
        // 获取当前上下文的平台描述
        Descriptor describe = context.platform().describe();
        // 打印横幅和平台描述信息
        String describeInfo = String.format("%s%nId: %s%nName: %s%nCategory: %s%nQuantity: %s%nParent: %s%nValue: %s%n",
                banner, describe.id(), describe.name(), describe.category(), describe.quantity(), describe.parent(),
                describe.value());
        log.info(describeInfo);
        return describeInfo;
    }

    /**
     * 执行ContextCommand命令
     *
     * @param command 要执行的ContextCommand命令
     * @return 执行结果，成功返回true，失败返回false
     */
    public <T> T execute(ContextCommand<T> command) {
        if (!isRunning()) {
            throw new DeviceManagerException("Context is not running");
        }
        try {
            // 尝试获取锁
            boolean isLocked = lock.tryLock(timeOutMillis, TimeUnit.MILLISECONDS);
            if (isLocked) {
                // 获取锁后执行命令
                return command.execute(context);
            }
            throw new DeviceManagerException("Acquire lock timeout");
        } catch (InterruptedException e) {
            // 被中断时设置当前线程状态为中断，并抛出DeviceManagerException异常
            Thread.currentThread().interrupt();
            throw new DeviceManagerException("Fail to acquire lock", e);
        } finally {
            // 如果当前线程持有锁，则释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 添加设备
     * @param device 设备
     */
    public void addDevice(Device device) {
        if (Objects.isNull(device) || StringUtils.isBlank(device.getId())) {
            throw new DeviceManagerException("Device id or name is blank");
        }
        Device previous = devices.putIfAbsent(device.getId(), device);
        if (Objects.nonNull(previous)) {
            throw new DeviceManagerException("Duplicate device id: " + device.getId());
        }
    }

    /**
     * 移除设备
     * @param id 设备id
     */
    public void removeDevice(String id) {
        devices.remove(id);
    }

    public Device getDevice(String id) {
        Objects.requireNonNull(id, "id must not be null");
        return devices.get(id);
    }

    public <T extends Device> T getDevice(String id, Class<T> deviceType) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(deviceType, "deviceType must not be null");
        Device device = getDevice(id);
        if (Objects.isNull(device)) {
            return null;
        }
        if (!deviceType.isInstance(device)) {
            throw new DeviceManagerException("Device type mismatch, id: " + id + ", expected: " + deviceType.getName());
        }
        return deviceType.cast(device);
    }

    public <T extends Device> Optional<T> findDevice(String id, Class<T> deviceType) {
        return Optional.ofNullable(getDevice(id, deviceType));
    }

    public Map<String, Device> getDevices() {
        return Map.copyOf(devices);
    }

    public boolean containsDevice(String id) {
        Objects.requireNonNull(id, "id must not be null");
        return devices.containsKey(id);
    }

    public int getDeviceCount() {
        return devices.size();
    }

    public boolean shutdownDevice(String id) {
        Objects.requireNonNull(id, "id must not be null");
        Device device = devices.get(id);
        if (Objects.isNull(device)) {
            return false;
        }
        device.shutdown();
        return true;
    }

    public Context getContext() {
        return context;
    }

    public RpiConfig getConfig() {
        return config;
    }

    public Class<? extends DigitalInputProvider> getDigitalInputProvider() {
        return config.getDigitalInputProvider();
    }

    public Class<? extends DigitalOutputProvider> getDigitalOutputProvider() {
        return config.getDigitalOutputProvider();
    }

    public Class<? extends PwmProvider> getPwmProvider() {
        return config.getPwmProvider();
    }

    public Class<? extends I2CProvider> getI2cProvider() {
        return config.getI2cProvider();
    }

    public Led led(IBCMEnums address) {
        return led(defaultGpioId("led", address), "LED", address);
    }

    public Led led(String id, String name, IBCMEnums address) {
        return getOrCreate(id, Led.class, () -> new Led(this, id, name, address));
    }

    public Relay relay(IBCMEnums address) {
        return relay(defaultGpioId("relay", address), "Relay", address);
    }

    public Relay relay(String id, String name, IBCMEnums address) {
        return getOrCreate(id, Relay.class, () -> new Relay(this, id, name, address));
    }

    public Relay relay(String id, String name, IBCMEnums address, boolean activeHigh) {
        return getOrCreate(id, Relay.class, () -> new Relay(this, id, name, address, activeHigh));
    }

    public ActiveBuzzer activeBuzzer(IBCMEnums address) {
        return activeBuzzer(defaultGpioId("active-buzzer", address), "Active Buzzer", address);
    }

    public ActiveBuzzer activeBuzzer(String id, String name, IBCMEnums address) {
        return getOrCreate(id, ActiveBuzzer.class, () -> new ActiveBuzzer(this, id, name, address));
    }

    public PassiveBuzzer passiveBuzzer(IBCMEnums address, int frequency) {
        return passiveBuzzer(defaultGpioId("passive-buzzer", address), "Passive Buzzer", address, frequency);
    }

    public PassiveBuzzer passiveBuzzer(String id, String name, IBCMEnums address, int frequency) {
        return getOrCreate(id, PassiveBuzzer.class, () -> new PassiveBuzzer(this, id, name, address, frequency));
    }

    public Button button(IBCMEnums address, Runnable onPressTask, Runnable onReleaseTask) {
        return button(defaultGpioId("button", address), "Button", address, false,
                config.getDebounceMicros(), onPressTask, onReleaseTask);
    }

    public Button button(String id, String name, IBCMEnums address, boolean inverted, long debounceMicSec,
                         Runnable onUpTask, Runnable onDownTask) {
        return getOrCreate(id, Button.class, () -> new Button(this, id, name, address, inverted, debounceMicSec, onUpTask, onDownTask));
    }

    public HCSr501 pir(IBCMEnums address) {
        return pir(defaultGpioId("pir", address), "PIR", address);
    }

    public HCSr501 pir(String id, String name, IBCMEnums address) {
        return getOrCreate(id, HCSr501.class, () -> new HCSr501(this, id, name, address));
    }

    public FlameSensor flameSensor(IBCMEnums address) {
        String id = defaultGpioId("flame", address);
        return getOrCreate(id, FlameSensor.class, () -> new FlameSensor(this, id, "Flame Sensor", address));
    }

    public SoundSensor soundSensor(IBCMEnums address) {
        String id = defaultGpioId("sound", address);
        return getOrCreate(id, SoundSensor.class, () -> new SoundSensor(this, id, "Sound Sensor", address));
    }

    public TouchSensor touchSensor(IBCMEnums address) {
        String id = defaultGpioId("touch", address);
        return getOrCreate(id, TouchSensor.class, () -> new TouchSensor(this, id, "Touch Sensor", address));
    }

    public VibrationSensor vibrationSensor(IBCMEnums address) {
        String id = defaultGpioId("vibration", address);
        return getOrCreate(id, VibrationSensor.class, () -> new VibrationSensor(this, id, "Vibration Sensor", address));
    }

    public LineTrackingSensor lineTrackingSensor(IBCMEnums address) {
        String id = defaultGpioId("line", address);
        return getOrCreate(id, LineTrackingSensor.class, () -> new LineTrackingSensor(this, id, "Line Tracking Sensor", address));
    }

    public SG90Servo servo(IBCMEnums address) {
        return servo(defaultGpioId("servo", address), "Servo", address);
    }

    public SG90Servo servo(String id, String name, IBCMEnums address) {
        return getOrCreate(id, SG90Servo.class, () -> new SG90Servo(this, id, name, address));
    }

    public L298N l298n(String id, IBCMEnums in1Address, IBCMEnums in2Address) {
        return getOrCreate(id, L298N.class, () -> new L298N(this, id, "L298N", in1Address, in2Address));
    }

    public L298N l298n(String id, IBCMEnums in1Address, IBCMEnums in2Address, IBCMEnums enableAddress) {
        return getOrCreate(id, L298N.class, () -> new L298N(this, id, "L298N", in1Address, in2Address, enableAddress));
    }

    public ULN2003Stepper uln2003Stepper(String id, IBCMEnums in1Address, IBCMEnums in2Address,
                                         IBCMEnums in3Address, IBCMEnums in4Address) {
        return getOrCreate(id, ULN2003Stepper.class,
                () -> new ULN2003Stepper(this, id, "ULN2003 Stepper", in1Address, in2Address, in3Address, in4Address));
    }

    public HCSr04 hcsr04(BCMEnums triggerAddress, BCMEnums echoAddress) {
        Objects.requireNonNull(triggerAddress, "triggerAddress must not be null");
        Objects.requireNonNull(echoAddress, "echoAddress must not be null");
        String id = "hcsr04-" + triggerAddress.getValue() + "-" + echoAddress.getValue();
        return getOrCreate(id, HCSr04.class, () -> new HCSr04(this, id, "HC-SR04", triggerAddress, echoAddress));
    }

    public DS18B20 ds18b20() {
        return getOrCreate("ds18b20", DS18B20.class, () -> new DS18B20(this, "ds18b20", "DS18B20"));
    }

    public DS18B20 ds18b20(String id, String sensorId) {
        return getOrCreate(id, DS18B20.class, () -> new DS18B20(this, id, "DS18B20", sensorId));
    }

    public Pcf8591 pcf8591(int bus) {
        String id = defaultI2cId("pcf8591", bus, Pcf8591.DEFAULT_ADDRESS);
        return getOrCreate(id, Pcf8591.class, () -> new Pcf8591(this, id, "PCF8591", bus));
    }

    public Lcd1602I2c lcd1602(int bus) {
        String id = defaultI2cId("lcd1602", bus, Lcd1602I2c.DEFAULT_ADDRESS);
        return getOrCreate(id, Lcd1602I2c.class, () -> new Lcd1602I2c(this, id, "LCD1602", bus));
    }

    public Lcd1602I2c lcd1602(int bus, int device) {
        String id = defaultI2cId("lcd1602", bus, device);
        return getOrCreate(id, Lcd1602I2c.class, () -> new Lcd1602I2c(this, id, "LCD1602", bus, device, 16, 2));
    }

    /**
     * 判断应用程序是否正在运行中
     *
     * @return 如果应用程序正在运行中则返回true，否则返回false
     */
    public boolean isRunning() {
        return Objects.nonNull(context) && !context.isShutdown();
    }

    /**
     * 关闭方法
     *
     * @return 关闭状态，如果成功返回true，否则返回false
     */
    public boolean shutdown() {
        devices.values().forEach(device -> {
            try {
                device.shutdown();
            } catch (RuntimeException e) {
                log.warn("Shutdown device failed, device id: {}", device.getId(), e);
            }
        });
        devices.clear();
        if (isRunning()) {
            context.shutdown();
        }
        return true;
    }

    @Override
    public void close() {
        shutdown();
    }

    /**
     * 获取平台信息
     *
     * @return 平台信息
     */
    public PlatformInfo getPlatformInfo() {
        return SystemInfoUtils.getPlatformInfo();
    }

    private static String defaultGpioId(String prefix, IBCMEnums address) {
        Objects.requireNonNull(address, "address must not be null");
        return prefix + "-" + address.getValue();
    }

    private static String defaultI2cId(String prefix, int bus, int device) {
        if (bus < 0) {
            throw new DeviceManagerException("I2C bus must be greater than or equal to 0");
        }
        if (device < 0) {
            throw new DeviceManagerException("I2C device address must be greater than or equal to 0");
        }
        return prefix + "-" + bus + "-" + device;
    }

    private synchronized <T extends Device> T getOrCreate(String id, Class<T> deviceType, Supplier<T> factory) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(deviceType, "deviceType must not be null");
        Objects.requireNonNull(factory, "factory must not be null");
        T existing = getDevice(id, deviceType);
        if (Objects.nonNull(existing)) {
            return existing;
        }
        return factory.get();
    }
}
