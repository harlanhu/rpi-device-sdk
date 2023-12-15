package cn.tpkf.pi.exception;

/**
 * @author Harlan
 * @email isharlan.hu@gmail.com
 * @date 2023 12 15 上午 09:34
 */
public class DeviceException extends RuntimeException {

    public DeviceException(String message) {
        super(message);
    }

    public DeviceException(String message, Throwable e) {
        super(message, e);
    }
}
