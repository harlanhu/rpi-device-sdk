package cn.tpkf.pi.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Serial Peripheral Interface
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/14
 */
@Getter
@AllArgsConstructor
public enum SPIEnums implements IBCMEnums {

    SPI1_CE1(17),

    SPI0_MOSI(10),

    SPI0_MISO(9),

    SPI0_SCLK(11),

    SPI1_MISO(19),

    SPI1_CE0(18),

    SPI0_CE0(8),

    SPI0_CE1(7),

    SPI1_CE2(16),

    SPI1_MOSI(20),

    SPI1_SCLK(21);

    private final Integer value;
}
