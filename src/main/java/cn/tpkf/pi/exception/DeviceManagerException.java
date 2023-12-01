package cn.tpkf.pi.exception;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/21
 */
public class DeviceManagerException extends RuntimeException {

    public DeviceManagerException(String message) {
        super(message);
    }

    public DeviceManagerException(String message, Throwable e) {
        super(message, e);
    }
}
