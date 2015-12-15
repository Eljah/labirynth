package com.meo;

/**
 * Created by Ilya Evlampiev on 13.12.2015.
 */
public interface MeOSession {
    public boolean sendSMS(String text, String number);

    public void releaseConnectionResource();
}
