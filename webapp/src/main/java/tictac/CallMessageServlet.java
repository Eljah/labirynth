package tictac;

import com.meo.MeOServer;
import com.meo.Message;
import edu.lmu.cs.networking.Game;
import edu.lmu.cs.networking.TicTacToeServer;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Ilya Evlampiev on 14.12.2015.
 */
@WebServlet("/new")
public class CallMessageServlet  extends HttpServlet {
    static Logger log = Logger.getLogger(ScoreServlet.class);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        log.debug("User list is got from the db");
        //req.setAttribute("users",userList);
        //req.setAttribute("sessionUser",req.getSession().getAttribute("user"));
        //getServletContext().getRequestDispatcher("/users.jsp").forward(req, resp);    }
        MeOServer.addMessage(new Message("New servlet call","79047640086"));

    }
}
