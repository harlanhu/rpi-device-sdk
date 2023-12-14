package cn.tpkf.pi.devices.gpio.digital.in;

import cn.tpkf.pi.exception.enums.IBCMEnums;
import cn.tpkf.pi.manager.DeviceManager;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/12
 */
public class Button extends AbstractDIDevice {
    /**
     * Initializes an instance of the AbstractDIDevice class.
     *
     * @param deviceManager  The DeviceManager instance.
     * @param id             The ID of the device.
     * @param name           The name of the device.
     * @param address        The IBCMEnums address of the device.
     * @param inverted       Indicates whether the input signal is inverted.
     * @param debounceMicSec The debounce time in microseconds.
     * @param onUpTask       The task to be executed when the input signal goes from low to high.
     * @param onDownTask     The task to be executed when the input signal goes from high to low.
     */
    public Button(DeviceManager deviceManager, String id, String name, IBCMEnums address, boolean inverted, long debounceMicSec, Runnable onUpTask, Runnable onDownTask) {
        super(deviceManager, id, name, address, inverted, debounceMicSec, onUpTask, onDownTask);
    }
}
