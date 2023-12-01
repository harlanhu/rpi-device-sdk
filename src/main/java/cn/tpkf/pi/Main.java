package cn.tpkf.pi;

import cn.tpkf.pi.manager.DeviceManager;
import cn.tpkf.pi.pojo.PlatformInfo;
import com.alibaba.fastjson2.JSON;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;

/**
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/27
 */
public class Main {

    public static void main(String[] args) {
        Context context = Pi4J.newAutoContext();
        DeviceManager manager = new DeviceManager(context);
        PlatformInfo platformInfo = manager.getPlatformInfo();
        System.out.println(JSON.toJSONString(platformInfo));
    }
}
