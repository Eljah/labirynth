package com.meo;

/**
 * Created by Ilya Evlampiev on 14.12.2015.
 */
public abstract class AbstractMeOSession extends Thread implements MeOSession {
    public abstract boolean sendSMS(String text, String number);

    public abstract void releaseConnectionResource();

    public void run() {

        while (true) {
            Message me = MeOServer.getMessage();
            if (me != null) {
                boolean deviceWorks = sendSMS(me.text, me.phoneNumber);
                if (!deviceWorks) {
                    MeOServer.removeSession(this);
                    this.releaseConnectionResource();
                    MeOServer.addMessage(me);
                    break;
                }
            }
        }
    }

}
