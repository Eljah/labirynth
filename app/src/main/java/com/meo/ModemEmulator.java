package com.meo;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Created by Ilya Evlampiev on 15.12.2015.
 */
public class ModemEmulator extends Thread {
    private String portname;
    private SerialPort serialPort;

    ModemEmulator(String portname) {
        portname = portname;
        serialPort = new SerialPort(portname);
        try {
            serialPort.openPort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                String input = serialPort.readString();
                if (input != null) {
                    System.out.println(input);
                }
                serialPort.writeString("OK");
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }
}
