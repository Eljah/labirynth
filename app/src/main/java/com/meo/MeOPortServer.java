package com.meo;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayDeque;

/**
 * Created by Ilya Evlampiev on 13.12.2015.
 */
public class MeOPortServer extends Thread {
    public ArrayDeque<MeOSession> globalDeviceList;

    public void run() {
        NewModemDeviceListener newModemDeviceListener=new NewModemDeviceListener();
        newModemDeviceListener.start();
        try {
            System.out.println("MeO Port Listener is started");
            //debug
            //MeOTCPSession meOTCPSession2 = new MeOTCPSession(listener.accept());
            //meOTCPSession2.sendSMS("Test serverside send","79047640086");
            while (true) {
                System.out.println("Waiting for device started");
                MeOPortSession meOPortSession = new MeOPortSession(newModemDeviceListener.accept());
                System.out.println("Device added: "+meOPortSession.serialPort.getPortName());
                globalDeviceList.add(meOPortSession);
                meOPortSession.start();
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        finally {
        }
    }
}
