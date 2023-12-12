package cn.tpkf.pi.manager;

import cn.tpkf.pi.pojo.PlatformInfo;
import com.pi4j.Pi4J;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/12
 */
class DeviceManagerTest {

    private final DeviceManager DEVICE_MANAGER = new DeviceManager(Pi4J.newAutoContext());

    @Test
    void printBanner() {
        String banner = DEVICE_MANAGER.printBanner();
        assertNotNull(banner);
    }

    @Test
    void getPlatformInfo() {
        PlatformInfo platformInfo = DEVICE_MANAGER.getPlatformInfo();
        assertNotNull(platformInfo);
    }
}