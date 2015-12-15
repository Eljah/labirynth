package com.meo;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by Ilya Evlampiev on 13.12.2015.
 */
public class MeOServer {

    static ArrayDeque<MeOSession> globalDeviceList=new ArrayDeque<MeOSession>(){};
    static ArrayDeque<Message> globalMessageList=new ArrayDeque<Message>(){};

    public static void main(String[] args) throws Exception {
        MeOTCPServer meOTCPServer = new MeOTCPServer();
        meOTCPServer.globalDeviceList = globalDeviceList;
        MeOPortServer meOPortServer = new MeOPortServer();
        meOPortServer.globalDeviceList = globalDeviceList;
        meOTCPServer.start();
        meOPortServer.start();
    }

    public static synchronized Message getMessage() {

        if (!globalMessageList.isEmpty()) {
            return globalMessageList.poll();
        }
        return null;
    }

    public static synchronized void addMessage(Message m)
    {
        globalMessageList.add(m);
    }

    public static synchronized void removeSession(MeOSession me) {
        globalDeviceList.remove(me);
    }
}
