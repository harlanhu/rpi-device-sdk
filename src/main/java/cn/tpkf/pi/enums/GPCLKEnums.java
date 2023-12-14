package cn.tpkf.pi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * General Purpose CLock
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/14
 */
@Getter
@AllArgsConstructor
public enum GPCLKEnums implements IBCMEnums {

    GPCLK_4(4),

    GPCLK_5(5),

    GPCLK_6(6);

    private final Integer value;
}
