package com.toy.servlet;

import com.toy.dao.ToyDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.File;

@WebServlet("/list-toys")
public class ListToysServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ListToysServlet.class.getName());
    private ToyDAO toyDAO;
    private static final String LIST_TOYS_JSP = "/list-toys.jsp";

    @Override
    public void init() {
        toyDAO = new ToyDAO();
        LOGGER.info("ListToysServlet initialized");

        String uploadPath = getServletContext().getRealPath("/images");
        if (uploadPath != null) {
            File imagesDir = new File(uploadPath);
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
                LOGGER.info("Created images directory: " + imagesDir.getAbsolutePath());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            LOGGER.info("Processing list-toys request");
            String searchQuery = request.getParameter("search");
            
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                LOGGER.info("Searching for toys with query: " + searchQuery);
                request.setAttribute("toys", toyDAO.searchToys(searchQuery));
            } else {
                LOGGER.info("Getting all toys");
                request.setAttribute("toys", toyDAO.getAllToys());
            }
            
            // Set images path for JSP fallback (uses web-accessible URL in practice)
            String imagesPath = getServletContext().getRealPath("/images");
            request.setAttribute("imagesPath", imagesPath != null ? imagesPath : "");
            
            LOGGER.info("Forwarding to " + LIST_TOYS_JSP);
            request.getRequestDispatcher(LIST_TOYS_JSP).forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ListToysServlet", e);
            throw e;
        }
    }
} 