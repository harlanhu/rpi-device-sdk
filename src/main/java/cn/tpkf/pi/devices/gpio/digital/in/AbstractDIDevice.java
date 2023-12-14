package cn.tpkf.pi.devices.gpio.digital.in;

import cn.tpkf.pi.devices.gpio.digital.AbstractDigitalDevice;
import cn.tpkf.pi.enums.IBCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalInputProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * 数字信号输入设备
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/6
 */
@Slf4j
public abstract class AbstractDIDevice extends AbstractDigitalDevice {

    /**
     * The DigitalInput instance.
     */
    protected final DigitalInput digitalInput;

    /**
     * The pull resistance.
     */
    protected final PullResistance pull;

    /**
     * The debounce time in microseconds.
     */
    protected final long debounceMicSec;

    /**
     * The task to be executed when the input signal goes from low to high.
     */
    protected final Runnable onUpTask;

    /**
     * The task to be executed when the input signal goes from high to low.
     */
    protected final Runnable onDownTask;

    /**
     *
     * Initializes an instance of the AbstractDIDevice class.
     *
     * @param deviceManager The DeviceManager instance.
     * @param id The ID of the device.
     * @param name The name of the device.
     * @param address The IBCMEnums address of the device.
     * @param inverted Indicates whether the input signal is inverted.
     * @param debounceMicSec The debounce time in microseconds.
     * @param onUpTask The task to be executed when the input signal goes from low to high.
     * @param onDownTask The task to be executed when the input signal goes from high to low.
     */
    protected AbstractDIDevice(DeviceManager deviceManager, String id, String name,
                               IBCMEnums address, boolean inverted, long debounceMicSec,
                               Runnable onUpTask, Runnable onDownTask) {
        super(deviceManager, id, name, address);
        this.pull = inverted ? PullResistance.PULL_UP : PullResistance.PULL_DOWN;
        this.debounceMicSec = debounceMicSec;
        this.onUpTask = onUpTask;
        this.onDownTask = onDownTask;
        digitalInput = deviceManager.execute(c -> {
            DigitalInputConfig config = DigitalInputConfigBuilder.newInstance(c)
                    .id(id)
                    .name(name)
                    .address(address.getValue())
                    .description(getDescription())
                    .pull(pull)
                    .debounce(debounceMicSec)
                    .provider(PiGpioDigitalInputProvider.class)
                    .build();
            return c.create(config);
        });
        digitalInput.addListener(event -> {
            DigitalState state = getState();
            switch (state) {
                case HIGH -> onUpTask.run();
                case LOW -> onDownTask.run();
                case UNKNOWN -> log.warn("Unknown state for device: {}", getDescription());
            }
        });
        deviceManager.addDevice(this);
    }

    @Override
    protected DigitalState getState() {
        return digitalInput.state();
    }

    @Override
    protected boolean isHigh() {
        return digitalInput.isHigh();
    }

    @Override
    protected boolean isLow() {
        return digitalInput.isLow();
    }
}
