package com.meo;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya Evlampiev on 15.12.2015.
 */
public class NewModemDeviceListener extends Thread {
    final String PORT_PRFIX = "COM"; //todo make it cross-platform

    static List<SerialPort> possibleDevices = new ArrayList<SerialPort>() {
    };
    static List<SerialPort> knownDevices = new ArrayList<SerialPort>() {
    };

    SerialPort toBeReturnedOnFind=null;

    NewModemDeviceListener() {
        for (int i = 0; i < 20; i++)
        {
            possibleDevices.add(new SerialPort(PORT_PRFIX + i));
        }
    }

    public void run() {
        while (true) {
            try {
                for (SerialPort currentSerialPort : possibleDevices) {
                    //System.out.println("Exploring port "+currentSerialPort.getPortName());
                    try {
                        currentSerialPort.openPort();
                        currentSerialPort.setParams(SerialPort.BAUDRATE_9600,
                                SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE);
                        char c = 0x0D;//Символ перевода каретки CR
                        String str0 = "AT" + c;
                        currentSerialPort.writeString(str0);
                        Thread.sleep(500); // По-идее, здесь надо ждать ответ модема, но мы ограничимся просто ожиданием в полсекунды
                        //Очистим порт
                        String responce = currentSerialPort.readString(2, 500);
                        //System.out.println(currentSerialPort.getPortName()+" "+responce);
                        if (!responce.equals(null) && (toBeReturnedOnFind==null)) {
                            knownDevices.add(currentSerialPort);
                            //possibleDevices.remove(currentSerialPort);
                            toBeReturnedOnFind = currentSerialPort;
                      //      System.out.print("Modem is fount on " + currentSerialPort.getPortName());
                        }
                        currentSerialPort.purgePort(currentSerialPort.PURGE_RXCLEAR | currentSerialPort.PURGE_TXCLEAR);
                        currentSerialPort.closePort();
                    }
                    catch (SerialPortException e) {
                        //e.printStackTrace();
                    } catch (SerialPortTimeoutException e) {
                        //e.printStackTrace();
                    }
                }
                this.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized SerialPort accept() {
        while (true) {
            if (toBeReturnedOnFind != null) {
                SerialPort tbr = toBeReturnedOnFind;
                toBeReturnedOnFind = null;
                return tbr;
            }
        }
    }

}
