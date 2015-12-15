package tictac;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.lmu.cs.networking.TicTacToeServer;

/**
 * Created by Ilya Evlampiev on 13.12.2015.
 */
public class TicTacToeStarterOnWebApp implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        Thread serverThread = new Thread() {
            public void run() {
                TicTacToeServer server = new TicTacToeServer();
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
