package cn.tpkf.pi.devices;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
@FunctionalInterface
public interface DeviceCommand<T> {

    /**
     * 执行命令
     * @return 返回结果
     */
    T execute();
}
