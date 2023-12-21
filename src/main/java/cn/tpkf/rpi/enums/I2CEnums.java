package cn.tpkf.rpi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Inter Integrated Circuit
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/14
 */
@Getter
@AllArgsConstructor
public enum I2CEnums implements IBCMEnums {

    SDA(2),

    SCL(3),

    EEPROM_SDA(0),

    EEPROM_SCL(1);

    private final Integer value;
}
