package cn.tpkf.pi.devices.gpio.digital.out;

import cn.tpkf.pi.devices.gpio.digital.AbstractDigitalDevice;
import cn.tpkf.pi.enums.BCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.linuxfs.provider.gpio.digital.LinuxFsDigitalInputProvider;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

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
                    .provider(LinuxFsDigitalInputProvider.class)
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

    public void on() {
        try {
            lock.lock();
            digitalOutput.state(onState);
        } finally {
            lock.unlock();
        }
    }

    public void off() {
        try {
            lock.lock();
            digitalOutput.state(offState);
        } finally {
            lock.unlock();
        }
    }

    public void toggle() {
        try {
            lock.lock();
            digitalOutput.toggle();
        } finally {
            lock.unlock();
        }
    }

    public void pulse(int interval, TimeUnit timeUnit, DigitalState digitalState) {
        try {
            lock.lock();
            digitalOutput.pulse(interval, timeUnit, digitalState);
        } finally {
            lock.unlock();
        }
    }

    public void blink(int delay, int duration, TimeUnit timeUnit, DigitalState digitalState) {
        try {
            lock.lock();
            digitalOutput.blink(delay, duration, timeUnit, digitalState);
        } finally {
            lock.unlock();
        }
    }

    public void cycle(int cycle, int delay, int duration, TimeUnit timeUnit, DigitalState digitalState) {
        try {
            lock.lock();
            while (cycle >= 1) {
                digitalOutput.blink(delay, duration, timeUnit, digitalState);
                cycle--;
            }
        } finally {
            lock.unlock();
        }
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
