package com.meo;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by Ilya Evlampiev on 13.12.2015.
 */
public class MeOTCPServer extends Thread {

    public ArrayDeque<MeOSession> globalDeviceList;

    public void run() {
        ServerSocket listener = null;
        try {
            listener = new ServerSocket(8902);
        System.out.println("MeO Server is started");
        //debug
        //MeOTCPSession meOTCPSession2 = new MeOTCPSession(listener.accept());
        //meOTCPSession2.sendSMS("Test serverside send","79047640086");
        while (true) {
                MeOTCPSession meOTCPSession = new MeOTCPSession(listener.accept());
               globalDeviceList.add(meOTCPSession);
               meOTCPSession.start();
                /*
                Game game = new Game();
                Game.Player playerX = game.new Player(listener.accept(), 'X');
                Game.Player playerO = game.new Player(listener.accept(), 'O');
                playerX.setOpponent(playerO);
                playerO.setOpponent(playerX);
                game.currentPlayer = playerX;
                playerX.start();
                playerO.start();
                */
            }

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        finally {
            try {
                listener.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
