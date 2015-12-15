package com.meo;

/**
 * Created by Ilya Evlampiev on 15.12.2015.
 */
public class ModemEmulatorOrchistrator {
    public static void main(String[] args)
    {
        ModemEmulator me3=new ModemEmulator("COM3");
        me3.start();
    }
}
