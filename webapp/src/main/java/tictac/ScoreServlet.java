package tictac;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import edu.lmu.cs.networking.TicTacToeServer;
import edu.lmu.cs.networking.Game;

import org.apache.log4j.Logger;


/**
 * Created by Ilya Evlampiev on 13.12.2015.
 */

@WebServlet("/score")
public class ScoreServlet extends HttpServlet {
    static Logger log = Logger.getLogger(ScoreServlet.class);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        log.debug("User list is got from the db");
        //req.setAttribute("users",userList);
        //req.setAttribute("sessionUser",req.getSession().getAttribute("user"));
        //getServletContext().getRequestDispatcher("/users.jsp").forward(req, resp);    }
        for (Game game : TicTacToeServer.gamesArchive)
        {
            resp.getWriter().println(game.toString()+"<br>");
        }

    }
}
