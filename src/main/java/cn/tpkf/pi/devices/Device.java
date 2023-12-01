package cn.tpkf.pi.devices;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
public interface Device {

    /**
     * 初始化设备
     */
    void init();

    /**
     * 销毁设备
     */
    void destroy();

    /**
     * 执行命令
     * @param command 命令
     * @return 返回结果
     * @param <T> 返回结果类型
     */
    <T> T execute(DeviceCommand<T> command);

    /**
     * 异步执行命令
     * @param command 命令
     * @return 返回结果
     * @param <T> 返回结果类型
     */
    <T> T asyncExecute(DeviceCommand<T> command);

    /**
     * 停止当前命令
     */
    void stopCurrentCommand();
}
