package cn.tpkf.pi.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * BCM/GPIO 引脚编号
 * @see <a href="https://pinout.xyz/pinout">reference</a>
 * @author Harlan
 * @email isharlan.hu@gmail.com
 * @date 2023 12 02 下午 08:28
 */
@Getter
@AllArgsConstructor
public enum BCMEnums implements IBCMEnums {

    /**
     * I2C 1 SDA
     * Data
     */
    BCM_2(2),

    /**
     * I2C 1 SCL
     * Serial Clock
     */
    BCM_3(3),

    /**
     * BCM/GPIO 4
     * General Purpose CLock
     */
    BCM_4(4),

    /**
     * BCM/GPIO 17
     */
    BCM_17(17),

    /**
     * BCM/GPIO 27
     */
    BCM_27(27),

    /**
     * BCM/GPIO 22
     */
    BCM_22(22),

    /**
     * BCM/GPIO 10
     * Serial Peripheral Interface 0
     * Primary Out Alex In
     */
    BCM_10(10),

    /**
     * BCM/GPIO 9
     * Serial Peripheral Interface 0
     * Primary In Alex Out
     */
    BCM_9(9),

    /**
     * BCM/GPIO 11
     * Serial Peripheral Interface 0
     * Serial Clock
     */
    BCM_11(11),

    /**
     * BCM/GPIO 0
     * I2C 0 EEPROM
     * Data
     */
    BCM_0(0),

    /**
     * BCM/GPIO 5
     */
    BCM_5(5),

    /**
     * BCM/GPIO 6
     */
    BCM_6(6),

    /**
     * BCM/GPIO 13
     * Pulse-width Modulation
     */
    BCM_13(13),

    /**
     * BCM/GPIO 19
     * Pulse-width Modulation
     */
    BCM_19(19),

    /**
     * BCM/GPIO 26
     */
    BCM_26(26),

    /**
     * BCM/GPIO 14
     * Universal Asynchronous Transmitter
     */
    BCM_14(14),

    /**
     * BCM/GPIO 15
     * Universal Asynchronous Receiver
     */
    BCM_15(15),

    /**
     * BCM/GPIO 18
     * PCM Clock
     */
    BCM_18(18),

    /**
     * BCM/GPIO 23
     */
    BCM_23(23),

    /**
     * BCM/GPIO 24
     */
    BCM_24(24),

    /**
     * BCM/GPIO 25
     */
    BCM_25(25),

    /**
     * BCM/GPIO 8
     * SPI0 CE0
     */
    BCM_8(8),

    /**
     * BCM/GPIO 7
     * SPI0 CE1
     */
    BCM_7(7),

    /**
     * BCM/GPIO 1
     * I2C 0 EEPROM
     * Serial Clock
     */
    BCM_1(1),

    /**
     * BCM/GPIO 12
     * Pulse-width Modulation
     */
    BCM_12(12),

    /**
     * BCM/GPIO 16
     */
    BCM_16(16),

    /**
     * BCM/GPIO 20
     */
    BCM_20(20),

    /**
     * BCM/GPIO 21
     */
    BCM_21(21);

    private final int value;
}
