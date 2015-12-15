package com.meo;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Ilya Evlampiev on 14.12.2015.
 */
public class MeOClient {
    private JFrame frame = new JFrame("Tic Tac Toe");
    private JLabel messageLabel = new JLabel("");

    private static int PORT = 8902;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public MeOClient(String serverAddress) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(240, 160);
        frame.setVisible(true);
        frame.setResizable(false);
        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, "South");
        //SMSSender.smsSend("Test","79047640086");

        try {
            socket = new Socket(serverAddress, PORT);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loop() throws IOException {
        String response;
        String number;
        String text;
        try {
            response = in.readLine();
            if (response.startsWith("WELCOME")) {
                messageLabel.setText("Client connected to the server; waiting for the connection");
            }

            while (true) {
                response = in.readLine();
                if (response.startsWith("MESSAGE")) {
                    messageLabel.setText("Message sending is started");
                    number = in.readLine();
                    text = in.readLine();
                    boolean resultFlag = SMSSender.smsSend(text, number);
                    if (resultFlag) {
                        messageLabel.setText("Message " + text + " has been sent to " + number + "!");
                        System.out.print("Message result: "+SMSSender.getResut());
                        out.println("OK");
                    } else {
                        messageLabel.setText("There was an error while sending message " + text + " has been sent to " + number + "!");
                        out.println("ERROR");
                    }

                } else if (response.startsWith("SERVER_SHUTDOWN")) {
                    messageLabel.setText("Server shutdown");
                    break;
                }
            }
            out.println("QUIT");
        } finally {
            socket.close();
        }
    }


    public static void main(String[] args) throws Exception {
        //SMSSender.smsSend("Game started","79047640086");
        String serverAddress = (args.length == 0) ? "localhost" : args[1];
        MeOClient client = new MeOClient(serverAddress);
        client.loop();

    }

}
