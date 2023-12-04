package cn.tpkf.pi.devices;

import cn.tpkf.pi.manager.DeviceManager;

import java.util.List;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
public interface Device {

    /**
     * 获取设备id
     * @return 设备id
     */
    String getId();

    /**
     * 获取设备名称
     * @return 设备名称
     */
    String getName();

    /**
     * 初始化设备
     */
    void init();

    /**
     * 销毁设备
     */
    void destroy();

    /**
     * 获取设备管理器
     * @return 设备管理器
     */
    DeviceManager getDeviceManager();
}
