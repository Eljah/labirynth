package com.mkyong;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

public class HttpURLConnectionExample {

    private final String USER_AGENT = "Mozilla/5.0";
    private static final String LINE_FEED = "\r\n";
   String boundary;

    public static void main(String[] args) throws Exception {

        HttpURLConnectionExample http = new HttpURLConnectionExample();

        System.out.println("Testing 1 - Send Http GET request");
        http.sendGet();

        System.out.println("\nTesting 2 - Send Http POST request");
        http.sendPost();

        System.out.println("\nTesting 3 - Send Http POST request");
        http.sendPost1();

    }

    // HTTP GET request
    private void sendGet() throws Exception {

        String url = "http://www.artlebedev.ru/kovodstvo/idioteka/2015/12/02/";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Encoding", "identity");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        InputStream inStream = new GZIPInputStream(con.getInputStream());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(inStream));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    // HTTP POST request
    private void sendPost() throws Exception {

        String url = "http://www.artlebedev.ru/kovodstvo/idioteka/action/";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "------WebKitFormBoundaryN5kqkXobLz4u4mCk\n" +
                "Content-Disposition: form-data; name=\"name\"\n" +
                "\n" +
                "asdsad\n" +
                "------WebKitFormBoundaryN5kqkXobLz4u4mCk\n" +
                "Content-Disposition: form-data; name=\"email\"\n" +
                "\n" +
                "asd\n" +
                "------WebKitFormBoundaryN5kqkXobLz4u4mCk\n" +
                "Content-Disposition: form-data; name=\"userfile\"; filename=\"uv.jpg\"\n" +
                "Content-Type: image/jpeg\n" +
                "\n" +
                "\n" +
                "------WebKitFormBoundaryN5kqkXobLz4u4mCk\n" +
                "Content-Disposition: form-data; name=\"comment\"\n" +
                "\n" +
                "asda\n" +
                "------WebKitFormBoundaryN5kqkXobLz4u4mCk\n" +
                "Content-Disposition: form-data; name=\"submit\"\n" +
                "\n" +
                "Отправить\n" +
                "------WebKitFormBoundaryN5kqkXobLz4u4mCk--";// "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    private void sendPost1() throws Exception {

        String urlSt = "http://www.artlebedev.ru/kovodstvo/idioteka/action/";

        String filename = "gapple.png";
        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(urlSt);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
        httpConn.setRequestProperty("Test", "Bonjour");
        OutputStream outputStream = httpConn.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"),
                true);
        addFormField("comment","asda",writer);
        addFormField("email","as@as",writer);
        addFilePart("userfile",new File(filename),writer,outputStream);
        writer.close();
        int responseCode = httpConn .getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
       System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(httpConn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());


    }


    public void addFormField(String name, String value, PrintWriter writer) {

        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + "UTF-8").append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile, PrintWriter writer, OutputStream outputStream)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }


}