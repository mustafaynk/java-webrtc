package com.github.joelbars.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;

import com.github.joelbars.Room;

@WebServlet(urlPatterns = "/app")
public class ApplicationServlet extends HttpServlet {

    private static final long serialVersionUID = 3987695371953543306L;

    private ConcurrentMap<String, Set<Session>> rooms = Room.INSTANCE.map();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        
        PrintWriter w = resp.getWriter();
        w.write("<!DOCTYPE html>\n"
                + "<html lang=\"tr\">\n"
                + "<head>"
                + "  <!-- Basic Page Needs --------->\n"
                + "  <meta charset=\"utf-8\">\n"
                + "  <title>WebRTC Java Experiment</title>\n"
                + "  <meta name=\"description\" content=\"\">\n"
                + "  <meta name=\"author\" content=\"\">\n"
                + "  <!-- Mobile Specific Metas ---->\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                + "  <!-- FONT -->\n"
                + "  <link href=\"//fonts.googleapis.com/css?family=Raleway:400,300,600\" rel=\"stylesheet\" type=\"text/css\">\n"
                + "  <!-- CSS --->\n"
                + "  <link rel=\"stylesheet\" href=\"css/normalize.css\">\n"
                + "  <link rel=\"stylesheet\" href=\"css/skeleton.css\">\n"
                + "  <link rel=\"stylesheet\" href=\"css/custom.css\">\n"
                + "  <!-- Favicon -->\n"
                + "  <link rel=\"icon\" type=\"image/png\" href=\"images/favicon.png\">\n"
                + "</head>\n"
                + "<body>\n");
        if (!req.getParameterMap().containsKey("r")) {
            w.write("<div class=\"section container\">\n"
                    + "    <h3>WebRTC</h3>\n"
                    + "    <form action=\"app\">\n"
                    + "        <label for=\"r\">NUMARA GIRINIZ</label>\n"
                    + "        <input type=\"number\" name=\"r\" id=\"r\" />\n"
                    + "        <p>"
                    + "        <input type=\"submit\" value='ARA' />\n"
                    + "        </p>"
                    + "    </form>\n"
                    + "</div>\n");
        } else {
            String room = req.getParameter("r");
            w.write("<div class=\"section container\"> "
                    + "Vid 1:<br /><video width=\"30%\" height=\"50%\" id=\"vid1\"></video>\n"
                    + "<br />Vid 2:<br /><video id=\"vid2\" width=\"30%\" height=\"50%\"></video>\n"
                    + "</div>"
                    + "<script type=\"text/javascript\">\n"
                    + "    var room = \"" + req.getContextPath() + "/server/" + room + "\";\n"
                    + "    console.log(room);"
                    + "    console.log(" + rooms.containsKey(room) + ");"
                    + "    var initCall = " + rooms.containsKey(room) + ";\n"
                    + "</script>\n"
                    + "<script src=\"js/app.js\" type=\"text/javascript\"></script>\n");
            
        }

        w.write("<!-- End Document -->\n"
                + "</body>\n"
                + "</html>");
    }

}
