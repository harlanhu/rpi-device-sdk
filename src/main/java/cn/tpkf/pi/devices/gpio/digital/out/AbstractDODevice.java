package cn.tpkf.pi.devices.gpio.digital.out;

import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;

import cn.tpkf.pi.devices.gpio.digital.AbstractDigitalDevice;
import cn.tpkf.pi.enums.BCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import lombok.Getter;

/**
 * 数字信号输出设备
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/6
 */
public class AbstractDODevice extends AbstractDigitalDevice {

    private final DigitalOutput digitalOutput;

    @Getter
    private final DigitalState onState;

    @Getter
    private final DigitalState offState;

    public AbstractDODevice(DeviceManager deviceManager, String id, String name, BCMEnums address, DigitalState initial, DigitalState shutdown) {
        super(deviceManager, id, name, address);
        digitalOutput = deviceManager.execute(c -> {
            DigitalOutputConfig config = DigitalOutputConfigBuilder.newInstance(c)
                    .id(id)
                    .name(name)
                    .address(address.getVale())
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
     * @param duration 持续时间
     * @param timeUnit 时间单位
     * @param digitalState 开启状态
     */
    public void pulse(int duration, TimeUnit timeUnit, DigitalState digitalState) {
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
     * @param duration 一次闪烁持续时间
     * @param cycle 闪烁次数
     * @param timeUnit 时间单位
     * @param digitalState 闪烁状态
     */
    public void blink(int duration, int cycle, TimeUnit timeUnit, DigitalState digitalState) {
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
     * @param cycle 闪烁次数
     * @param timeUnit 时间单位
     */
    public void blink(int duration, int cycle, TimeUnit timeUnit) {
        blink(duration, cycle, timeUnit, onState);
    }

    /**
     * 循环开启
     * 
     * @param times 循环次数
     * @param interval 每次循环间隔
     * @param duration 单次闪烁持续时间
     * @param cycle 单次循环闪烁次数
     * @param timeUnit 时间单位
     * @param digitalState 闪烁状态
     * @throws InterruptedException 中断异常
     */
    public void cycle(int times, long interval, int duration, int cycle, TimeUnit timeUnit, DigitalState digitalState) throws InterruptedException {
        try {
            lock.lock();
            while (times >= 1) {
                digitalOutput.blink(duration, cycle, timeUnit, digitalState);
                times--;
                timeUnit.sleep(interval);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 循环开启 onState
     * 
     * @param times 循环次数
     * @param interval 每次循环间隔
     * @param duration 单次闪烁持续时间
     * @param cycle 单次循环闪烁次数
     * @param timeUnit 时间单位
     * @throws InterruptedException 中断异常
     */
    public void cycle(int times, long interval, int duration, int cycle, TimeUnit timeUnit) throws InterruptedException {
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
