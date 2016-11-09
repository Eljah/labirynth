package com.meo;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.io.IOException;

public class SMSSender {
    public SMSSender() {}

    private static SerialPort serialPort;


    public static void main(String args[])
    {
        try {
            smsSend("Test","79656020089");//"79047640086");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String model;
    static String latsSendResult="";

    static String getResut()
    {
        return model+" "+latsSendResult;
    }


    //Функция разворачивания номера в нужном формате
    //Телефон в международном формате имеет 11 символов (79231111111)
    //11-Нечётное число, поэтому в конце добавляем F
    //И переставляем попарно цифры местами. Этого требует PDU-формат
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

    //Получить длину сообщения
    private static int getSMSLength(String sms) {
        return (sms.length()/2 - 1);
    }

    public static boolean smsSend(String sms,String phone) throws IOException {

        //Передаём в конструктор имя порта
        serialPort = new SerialPort("COM37");
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
            String message = "0011000B91"+reversePhone(phone)+"0008A7"+StringToUSC2(sms);


            char c = 0x0D;//Символ перевода каретки CR
            String str0 = "AT+CGMM"+c;
            serialPort.writeString(str0);
            Thread.sleep(500); // По-идее, здесь надо ждать ответ модема, но мы ограничимся просто ожиданием в полсекунды
            //Очистим порт
            String responce=serialPort.readString();
            model=responce;
            System.out.println(responce);

            //time
            serialPort.purgePort(serialPort.PURGE_RXCLEAR | serialPort.PURGE_TXCLEAR);
            //out.println(str);
            String strt = "AT#NITZ"+c;
            //str = "AT+CMGS="+getSMSLength(message)+c;
            serialPort.writeString(strt);
            Thread.sleep(2000);
            responce=serialPort.readString();
            System.out.println(responce);
            //responce=serialPort.readString();
            //System.out.println("Clock resp: "+responce);

            //time/




            //Отправляем запрос устройству
            //String str = "AT+CMGF=0"+c;
            String str = "AT+CMGF=1"+c;
            serialPort.writeString(str);
            Thread.sleep(2000); // По-идее, здесь надо ждать ответ модема, но мы ограничимся просто ожиданием в полсекунды
            //Очистим порт
            responce=serialPort.readString();
            System.out.println(responce);
            serialPort.purgePort(serialPort.PURGE_RXCLEAR | serialPort.PURGE_TXCLEAR);
            //out.println(str);
            str = "AT+CMGS=\"+"+phone+"\""+c;
            //str = "AT+CMGS="+getSMSLength(message)+c;
            serialPort.writeString(str);
            Thread.sleep(2000);
            responce=serialPort.readString();
            System.out.println(responce);

            serialPort.purgePort(serialPort.PURGE_RXCLEAR | serialPort.PURGE_TXCLEAR);
            //out.println(str);
            c = 26;//Символ CTRL+Z
            //serialPort.writeString(message+c);
            serialPort.writeString(sms+c);
            Thread.sleep(2000);
            responce=serialPort.readString();
            latsSendResult=responce;
            System.out.println("Send mesage result: "+responce);
            serialPort.purgePort(serialPort.PURGE_RXCLEAR | serialPort.PURGE_TXCLEAR);
            serialPort.closePort();

            return true;
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
            return false;
        } catch (InterruptedException e) {
            //out.println(e);
            return false;
        }

    }

    //Класс считывания ответов. Я решил обойтись без него, но в документации к JSSC всё есть :)
    /*private static class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    //Получаем ответ от устройства, обрабатываем данные и т.д.
                    String data = serialPort.readString(event.getEventValue());
                    //И снова отправляем запрос
                    System.out.println("response: " + data);
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
    }*/
}