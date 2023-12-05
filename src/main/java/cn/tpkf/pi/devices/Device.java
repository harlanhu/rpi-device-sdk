package cn.tpkf.pi.devices;

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
     * 执行命令
     * @param command 命令
     * @return 命令执行结果
     * @param <T> 命令执行结果类型
     */
    <T> T execute(DeviceCommand<T> command);
}
