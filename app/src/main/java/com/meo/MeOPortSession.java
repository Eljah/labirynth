package com.meo;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Ilya Evlampiev on 15.12.2015.
 */
public class MeOPortSession extends AbstractMeOSession {
    SerialPort serialPort;

    MeOPortSession(SerialPort sock) {
        this.serialPort = sock;
    }

    public boolean sendSMS(String text, String number) {

        try {
            //Открываем порт
            serialPort.openPort();
            //Выставляем параметры
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            //serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);

            //Формируем сообщение
            //try {
            //    String message = "0011000B91" + reversePhone(number) + "0008A7" + StringToUSC2(text);
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}


            char c = 0x0D;//Символ перевода каретки CR
            String str0 = "AT+CGMM" + c;
            serialPort.writeString(str0);
            Thread.sleep(500); // По-идее, здесь надо ждать ответ модема, но мы ограничимся просто ожиданием в полсекунды
            //Очистим порт
            String responce = serialPort.readString();
            System.out.println(responce);


            //Отправляем запрос устройству
            //String str = "AT+CMGF=0"+c;
            String str = "AT+CMGF=1" + c;
            serialPort.writeString(str);
            Thread.sleep(2000); // По-идее, здесь надо ждать ответ модема, но мы ограничимся просто ожиданием в полсекунды
            //Очистим порт
            responce = serialPort.readString();
            System.out.println(responce);
            serialPort.purgePort(serialPort.PURGE_RXCLEAR | serialPort.PURGE_TXCLEAR);
            //out.println(str);
            str = "AT+CMGS=\"+" + number + "\"" + c;
            //str = "AT+CMGS="+getSMSLength(message)+c;
            serialPort.writeString(str);
            Thread.sleep(2000);
            responce = serialPort.readString();
            System.out.println(responce);
            serialPort.purgePort(serialPort.PURGE_RXCLEAR | serialPort.PURGE_TXCLEAR);
            //out.println(str);
            c = 26;//Символ CTRL+Z
            //serialPort.writeString(message+c);
            serialPort.writeString(text + c);
            Thread.sleep(2000);
            responce = serialPort.readString();
            System.out.println("Send mesage result: " + responce);
            serialPort.purgePort(serialPort.PURGE_RXCLEAR | serialPort.PURGE_TXCLEAR);
            serialPort.closePort();

            return true;
        } catch (SerialPortException ex) {
            System.out.println(ex);
            return false;
        } catch (InterruptedException e) {
            //out.println(e);
            return false;
        }

    }


    public void releaseConnectionResource() {
        System.out.println("Releasing resources");
        try {
            NewModemDeviceListener.knownDevices.remove(serialPort);
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private static String reversePhone(String phone) {
        phone += "F";
        String phoneRev = "";
        phoneRev += phone.charAt(1);
        phoneRev += phone.charAt(0);
        phoneRev += phone.charAt(3);
        phoneRev += phone.charAt(2);
        phoneRev += phone.charAt(5);
        phoneRev += phone.charAt(4);
        phoneRev += phone.charAt(7);
        phoneRev += phone.charAt(6);
        phoneRev += phone.charAt(9);
        phoneRev += phone.charAt(8);
        phoneRev += phone.charAt(11);
        phoneRev += phone.charAt(10);

        return phoneRev;
    }

    //Функция конвертации текста СМС-ки в USC2 формат вместе с длиной сообщения (Возвращаемое значение <длина пакета><пакет>)

    private static String StringToUSC2(String text) throws IOException {
        String str = "";

        byte[] msgb = text.getBytes("UTF-16");
        //Конвертация самой СМС
        String msgPacked = "";
        for (int i = 2; i < msgb.length; i++) {
            String b = Integer.toHexString((int) msgb[i]);
            if (b.length() < 2) msgPacked += "0";
            msgPacked += b;
        }

        //Длина получившегося пакета в нужном формате
        String msglenPacked = Integer.toHexString(msgPacked.length() / 2);
        //Если длина нечётная - добавляем в конце 0
        if (msglenPacked.length() < 2) str += "0";

        //Формируем строку из длины и самого тела пакета
        str += msglenPacked;
        str += msgPacked;

        str = str.toUpperCase();

        return str;

    }


}
