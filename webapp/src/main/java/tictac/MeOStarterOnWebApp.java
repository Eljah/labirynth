package tictac;

import com.meo.MeOServer;
import edu.lmu.cs.networking.TicTacToeServer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Ilya Evlampiev on 14.12.2015.
 */
public class MeOStarterOnWebApp implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        Thread serverThread = new Thread() {
            public void run() {
                MeOServer server = new MeOServer();
                try

                {
                    server.main(null);
                } catch (
                        Exception e
                        )

                {
                    e.printStackTrace();
                }
            }
        };
        serverThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}

