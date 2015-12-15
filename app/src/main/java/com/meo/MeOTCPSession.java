package com.meo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Ilya Evlampiev on 14.12.2015.
 */
public class MeOTCPSession extends AbstractMeOSession {
    Socket socket;
    BufferedReader input;
    PrintWriter output;

    MeOTCPSession(Socket sock) {
        this.socket = sock;
        try {
            input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println("WELCOME");
            //output.println("MESSAGE Waiting for opponent to connect");
        } catch (IOException e) {
            System.out.println("Player died: " + e);
        }
    }

    public boolean sendSMS(String text, String number) {
        System.out.println("Sending message "+text+" to number "+number);
        output.println("MESSAGE");
        output.println(number);
        output.println(text);
        try {
            String responce = input.readLine();
            System.out.println(responce);
            if (!responce.contains("ERROR")) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public void releaseConnectionResource() {
        System.out.println("Releasing resources");
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
