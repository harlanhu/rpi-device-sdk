package cn.tpkf.rpi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Universal Asynchronous Receiver/Transmitter
 * CTS (Clear To Send)
 * RTS (Request To Send)
 * TXD (Transmit Data)
 * RXD (Receive Data)
 * <p>
 * ttyAMA0 -> UART0
 * ttyS0(mini) -> UART1
 * ttyAMA1 -> UART2
 * ttyAMA2 -> UART3
 * ttyAMA3 -> UART4
 * ttyAMA4 -> UART5
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/14
 */
@Getter
@AllArgsConstructor
public enum UARTEnums implements IBCMEnums {


    CTS_2(2),

    RTS_2(3),

    TXD_3(4),

    RTS_5(17),

    CTS_4(10),

    RXD_4(9),

    RTS_4(11),

    TXD_2(0),

    RXD_3(5),

    CTS_3(6),

    RXD_5(13),

    TXD_0(14),

    RXD_0(15),

    TXD_1(14),

    RXD_1(15),

    TXD_4(8),

    RTS_3(7),

    RXD_2(1),

    TXD_5(12),

    CTS_5(16);

    private final Integer value;
}
