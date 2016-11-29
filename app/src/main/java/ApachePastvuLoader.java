import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.postgresql.util.PGobject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Created by Ilya Evlampiev on 09.12.2015.
 */
public class ApachePastvuLoader extends Thread {

    final int MAXCOUNTER = 405000;//402000;

    private final String USER_AGENT = "Mozilla/5.0";

    public void run() {
        int id = 0;
        while (id < MAXCOUNTER) {
     //id++;
            id = ApachePastvuOrchestrator.getNext();
            String url = "https://pastvu.com/p/" + id;
            //String url = "https://pastvu.com/p/365037";
            //String url="https://ya.ru/";

            try {

                System.setProperty("https.protocols", "TLSv1.2,SSLv3,SSLv2Hello");
                //System.setProperty("javax.net.debug", "all");
/*
            SSLContext sc = null;
            try {
                sc = SSLContext.getInstance("TLSv1.2");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            // Init the SSLContext with a TrustManager[] and SecureRandom()
            sc.init(null, trustCerts, new java.security.SecureRandom());
*/


                URL obj = new URL(url);
                HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");

                //add request header
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Encoding", "identity");

                //System.out.println("\nSending 'GET' request to URL : " + url);
                //System.out.println("Response Code : " + responseCode);

                //InputStream inStream = new GZIPInputStream();
                StringBuffer response = new StringBuffer();
                try {
                    int responseCode = con.getResponseCode();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));


                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    con.disconnect();
                } catch (java.io.FileNotFoundException jiff) {
                    con.disconnect();
                    System.out.println("No resourse "+url);
                    continue;
                }
                catch (java.net.ConnectException jnse)
                {
                    con.disconnect();
                    System.out.println("Socket exeption for resourse "+url);
                    ApachePastvuOrchestrator.addFailed(id);
                    continue;
                }
                //System.out.println(response.toString());
                //System.out.println(matcher.group(1));
                if (response.toString().contains("Kazan, Tatarstan, Russia") || response.toString().contains("Tatarstan, Russia") || response.toString().contains("Tatarstan") ) {
//
                    Pattern pattern = Pattern.compile("\"geo\":\\[(.*?),(.*?)\\]");
                    Matcher matcher = pattern.matcher(response.toString());

                    Float longitude = null;
                    Float latitude = null;

                    if (matcher.find()) {
                        System.out.println(matcher.group(2));
                        System.out.println(matcher.group(1));
                        longitude = Float.parseFloat(matcher.group(2));
                        latitude = Float.parseFloat(matcher.group(1));

                        Pattern pattern2 = Pattern.compile("\"title\":\"(.*?)\"");
                        Matcher matcher2 = pattern2.matcher(response.toString());


                        String title = "";
                        if (matcher2.find()) {
                            title = matcher2.group(1);
                            System.out.println(title);
                        }

                        java.sql.Connection conpg;

                        try {
    /*
    * Load the JDBC driver and establish a connection.
    */

                            Class.forName("org.postgresql.Driver");
                            String urlp = "jdbc:postgresql://localhost:5432/postgis_22_sample";
                            conpg = DriverManager.getConnection(urlp, "postgres", "tatarstan");
    /*
    * Add the geometry types to the connection. Note that you
    * must cast the connection to the pgsql-specific connection
    * implementation before calling the addDataType() method.
    */
                            ((org.postgresql.PGConnection) conpg).addDataType("geometry", (Class<? extends PGobject>) Class.forName("org.postgis.PGgeometry"));
                            //((org.postgresql.PGConnection)conpg).addDataType("point",Class.forName("org.postgis.Point"));
    /*
    * Create a statement and execute a select query.
    */
                            conpg.setAutoCommit(false);

                            org.postgis.Point pointToAdd = new org.postgis.Point();
                            pointToAdd.setX(longitude);
                            pointToAdd.setY(latitude);

                            //Statement s = conn.createStatement();
                            //String geomsql = ;
                            PreparedStatement psSE = conpg.prepareStatement("INSERT INTO public.\"poi-point\" (name,geom,leisure) VALUES (?,?,?)");
                            psSE.setString(1, title);
                            psSE.setObject(2, new org.postgis.PGgeometry(pointToAdd));
                            psSE.setString(3, "pastvu");

                            psSE.execute();
                            conpg.commit();
                            conpg.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[])
    {
        (new ApachePastvuLoader()).run();
    }
}
