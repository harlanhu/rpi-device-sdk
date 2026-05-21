package cn.tpkf.rpi.devices.i2c;

import cn.tpkf.rpi.exception.DeviceException;
import cn.tpkf.rpi.manager.DeviceManager;

import java.util.concurrent.TimeUnit;

/**
 * LCD1602/HD44780 display with a PCF8574 I2C backpack.
 *
 * @author Harlan
 */
public class Lcd1602I2c extends AbstractI2cDevice {

    public static final int DEFAULT_ADDRESS = 0x27;

    private static final int LCD_CLEARDISPLAY = 0x01;

    private static final int LCD_RETURNHOME = 0x02;

    private static final int LCD_ENTRYMODESET = 0x04;

    private static final int LCD_DISPLAYCONTROL = 0x08;

    private static final int LCD_FUNCTIONSET = 0x20;

    private static final int LCD_SETDDRAMADDR = 0x80;

    private static final int LCD_ENTRYLEFT = 0x02;

    private static final int LCD_DISPLAYON = 0x04;

    private static final int LCD_2LINE = 0x08;

    private static final int LCD_5X8DOTS = 0x00;

    private static final int LCD_4BITMODE = 0x00;

    private static final int ENABLE = 0x04;

    private static final int REGISTER_SELECT = 0x01;

    private static final int BACKLIGHT = 0x08;

    private static final int NO_BACKLIGHT = 0x00;

    private static final int[] ROW_OFFSETS = {0x00, 0x40, 0x14, 0x54};

    private final int columns;

    private final int rows;

    private boolean backlightEnabled = true;

    public Lcd1602I2c(DeviceManager deviceManager, String id, String name, Integer bus) {
        this(deviceManager, id, name, bus, DEFAULT_ADDRESS, 16, 2);
    }

    public Lcd1602I2c(DeviceManager deviceManager, String id, String name,
                      Integer bus, Integer device, int columns, int rows) {
        super(deviceManager, id, name, bus, device);
        this.columns = columns;
        this.rows = rows;
        initialize();
    }

    public void clear() {
        command(LCD_CLEARDISPLAY);
        sleepMillis(2);
    }

    public void home() {
        command(LCD_RETURNHOME);
        sleepMillis(2);
    }

    public void setCursor(int column, int row) {
        int maxRow = Math.min(rows, ROW_OFFSETS.length) - 1;
        int safeRow = Math.max(0, Math.min(row, maxRow));
        int safeColumn = Math.max(0, Math.min(column, columns - 1));
        command(LCD_SETDDRAMADDR | (safeColumn + ROW_OFFSETS[safeRow]));
    }

    public void print(String text) {
        if (text == null) {
            return;
        }
        for (int i = 0; i < text.length(); i++) {
            writeChar(text.charAt(i));
        }
    }

    public void printLine(int row, String text) {
        setCursor(0, row);
        String safeText = text == null ? "" : text;
        print(safeText.length() > columns ? safeText.substring(0, columns) : safeText);
    }

    public void backlightOn() {
        backlightEnabled = true;
        expanderWrite(0);
    }

    public void backlightOff() {
        backlightEnabled = false;
        expanderWrite(0);
    }

    public boolean isBacklightEnabled() {
        return backlightEnabled;
    }

    private void initialize() {
        sleepMillis(50);
        expanderWrite(0);
        sleepMillis(1);
        write4Bits(0x03 << 4);
        sleepMillis(5);
        write4Bits(0x03 << 4);
        sleepMicros(150);
        write4Bits(0x03 << 4);
        write4Bits(0x02 << 4);
        command(LCD_FUNCTIONSET | LCD_4BITMODE | LCD_2LINE | LCD_5X8DOTS);
        command(LCD_DISPLAYCONTROL | LCD_DISPLAYON);
        clear();
        command(LCD_ENTRYMODESET | LCD_ENTRYLEFT);
        home();
    }

    private void command(int value) {
        send(value, 0);
    }

    private void writeChar(char value) {
        send(value, REGISTER_SELECT);
    }

    private void send(int value, int mode) {
        int highNibble = value & 0xF0;
        int lowNibble = (value << 4) & 0xF0;
        write4Bits(highNibble | mode);
        write4Bits(lowNibble | mode);
    }

    private void write4Bits(int value) {
        expanderWrite(value);
        pulseEnable(value);
    }

    private void pulseEnable(int value) {
        expanderWrite(value | ENABLE);
        sleepMicros(1);
        expanderWrite(value & ~ENABLE);
        sleepMicros(50);
    }

    private void expanderWrite(int value) {
        i2C.write(value | (backlightEnabled ? BACKLIGHT : NO_BACKLIGHT));
    }

    private void sleepMillis(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DeviceException("LCD1602 sleep interrupted", e);
        }
    }

    private void sleepMicros(long micros) {
        try {
            TimeUnit.MICROSECONDS.sleep(micros);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DeviceException("LCD1602 sleep interrupted", e);
        }
    }
}
