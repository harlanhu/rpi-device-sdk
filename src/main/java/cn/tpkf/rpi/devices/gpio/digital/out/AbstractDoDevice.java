package cn.tpkf.rpi.devices.gpio.digital.out;

import cn.tpkf.rpi.devices.gpio.digital.AbstractDigitalDevice;
import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.exception.DeviceException;
import cn.tpkf.rpi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 数字信号输出设备
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/6
 */
public class AbstractDoDevice extends AbstractDigitalDevice {

    /**
     * The DigitalOutput instance.
     */
    private final DigitalOutput digitalOutput;

    /**
     * The on state.
     */
    @Getter
    private final DigitalState onState;

    /**
     * The off state.
     */
    @Getter
    private final DigitalState offState;

    /**
     * Constructor for AbstractDoDevice.
     *
     * @param deviceManager The DeviceManager instance.
     * @param id The unique identifier for the device.
     * @param name The name of the device.
     * @param address The address of the device.
     * @param initial The initial state of the device.
     * @param shutdown The shutdown state of the device.
     */
    public AbstractDoDevice(DeviceManager deviceManager, String id, String name, IBCMEnums address, DigitalState initial, DigitalState shutdown) {
        super(deviceManager, id, name, address);
        Objects.requireNonNull(initial, "initial state must not be null");
        Objects.requireNonNull(shutdown, "shutdown state must not be null");
        digitalOutput = deviceManager.execute(c -> {
            DigitalOutputConfig config = DigitalOutputConfigBuilder.newInstance(c)
                    .id(id)
                    .name(name)
                    .address(address.getValue())
                    .initial(initial)
                    .shutdown(shutdown)
                    .description(getDescription())
                    .provider(PiGpioDigitalOutputProvider.class)
                    .build();
            return c.create(config);
        });
        if (DigitalState.LOW.equals(shutdown)) {
            onState = DigitalState.HIGH;
            offState = DigitalState.LOW;
        } else {
            onState = DigitalState.LOW;
            offState = DigitalState.HIGH;
        }
        deviceManager.addDevice(this);
    }

    /**
     * 开启
     */
    public void on() {
        try {
            lock.lock();
            digitalOutput.state(onState);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 关闭
     */
    public void off() {
        try {
            lock.lock();
            digitalOutput.state(offState);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 切换
     */
    public void toggle() {
        try {
            lock.lock();
            digitalOutput.toggle();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 单次开启
     *
     * @param duration     持续时间
     * @param timeUnit     时间单位
     * @param digitalState 开启状态
     */
    public void pulse(int duration, TimeUnit timeUnit, DigitalState digitalState) {
        if (duration < 0) {
            throw new DeviceException("Pulse duration must be greater than or equal to 0");
        }
        Objects.requireNonNull(timeUnit, "timeUnit must not be null");
        Objects.requireNonNull(digitalState, "digitalState must not be null");
        try {
            lock.lock();
            digitalOutput.pulse(duration, timeUnit, digitalState);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 单次开启 onState
     *
     * @param duration 持续时间
     * @param timeUnit 时间单位
     */
    public void pulse(int duration, TimeUnit timeUnit) {
        pulse(duration, timeUnit, onState);
    }

    /**
     * 闪烁开启
     *
     * @param duration     一次闪烁持续时间
     * @param cycle        闪烁次数
     * @param timeUnit     时间单位
     * @param digitalState 闪烁状态
     */
    public void blink(int duration, int cycle, TimeUnit timeUnit, DigitalState digitalState) {
        if (duration < 0) {
            throw new DeviceException("Blink duration must be greater than or equal to 0");
        }
        if (cycle < 0) {
            throw new DeviceException("Blink cycle must be greater than or equal to 0");
        }
        Objects.requireNonNull(timeUnit, "timeUnit must not be null");
        Objects.requireNonNull(digitalState, "digitalState must not be null");
        try {
            lock.lock();
            digitalOutput.blink(duration, cycle, timeUnit, digitalState);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 闪烁开启 onState
     *
     * @param duration 一次闪烁持续时间
     * @param cycle    闪烁次数
     * @param timeUnit 时间单位
     */
    public void blink(int duration, int cycle, TimeUnit timeUnit) {
        blink(duration, cycle, timeUnit, onState);
    }

    /**
     * 循环开启
     *
     * @param times        循环次数
     * @param interval     每次循环间隔
     * @param duration     单次闪烁持续时间
     * @param cycle        单次循环闪烁次数
     * @param timeUnit     时间单位
     * @param digitalState 闪烁状态
     */
    public void cycle(int times, long interval, int duration, int cycle, TimeUnit timeUnit, DigitalState digitalState) {
        if (times < 0 || interval < 0) {
            throw new DeviceException("Cycle times and interval must be greater than or equal to 0");
        }
        if (duration < 0 || cycle < 0) {
            throw new DeviceException("Cycle duration and blink cycle must be greater than or equal to 0");
        }
        Objects.requireNonNull(timeUnit, "timeUnit must not be null");
        Objects.requireNonNull(digitalState, "digitalState must not be null");
        try {
            while (times >= 1) {
                try {
                    lock.lock();
                    digitalOutput.blink(duration, cycle, timeUnit, digitalState);
                } finally {
                    lock.unlock();
                }
                times--;
                try {
                    timeUnit.sleep(interval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new DeviceException("DigitalOutput cycle interrupted", e);
                }
            }
        } finally {
            // nothing to unlock here because locks are handled per-iteration
        }
    }

    /**
     * 循环开启 onState
     *
     * @param times    循环次数
     * @param interval 每次循环间隔
     * @param duration 单次闪烁持续时间
     * @param cycle    单次循环闪烁次数
     * @param timeUnit 时间单位
     */
    public void cycle(int times, long interval, int duration, int cycle, TimeUnit timeUnit) {
        cycle(times, interval, duration, cycle, timeUnit, onState);
    }

    @Override
    protected DigitalState getState() {
        return digitalOutput.state();
    }

    @Override
    protected boolean isHigh() {
        return digitalOutput.isHigh();
    }

    @Override
    protected boolean isLow() {
        return digitalOutput.isLow();
    }
}
