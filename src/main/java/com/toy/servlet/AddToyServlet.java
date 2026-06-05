package com.toy.servlet;

import com.toy.dao.ToyDAO;
import com.toy.model.Toy;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/add-toy")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class AddToyServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AddToyServlet.class.getName());
    private static final String ADD_TOY_JSP = "/add-toy.jsp";

    // ✅ FINAL FIX: Use external folder (no Jetty conflict)
    private static final String UPLOAD_DIR = "C:\\toy-store-uploads";

    private ToyDAO toyDAO;

    @Override
    public void init() {
        toyDAO = new ToyDAO();

        // ✅ Create upload directory
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
            LOGGER.info("Created upload directory: " + UPLOAD_DIR);
        } else {
            LOGGER.info("Upload directory exists: " + UPLOAD_DIR);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(ADD_TOY_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // ✅ Get form data
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            double price = Double.parseDouble(request.getParameter("price"));

            // ✅ Get file
            Part filePart = request.getPart("image");
            String originalFileName = filePart.getSubmittedFileName();

            if (originalFileName != null && !originalFileName.isEmpty()) {

                // ✅ Clean filename (remove full path if exists)
                String cleanFileName = new File(originalFileName).getName();

                // ✅ Extract extension
                String extension = "";
                int dotIndex = cleanFileName.lastIndexOf(".");
                if (dotIndex >= 0) {
                    extension = cleanFileName.substring(dotIndex);
                }

                // ✅ Unique file name
                String fileName = System.currentTimeMillis() + extension;

                String filePath = UPLOAD_DIR + File.separator + fileName;

                // ✅ ✅ SAFE SAVE (no Part.write)
                try (InputStream input = filePart.getInputStream()) {
                    Files.copy(input, Paths.get(filePath));
                }

                LOGGER.info("File saved successfully at: " + filePath);

                // ✅ Save to database
                Toy toy = new Toy(fileName, name, description, price);
                toyDAO.addToy(toy);

                response.sendRedirect(request.getContextPath() + "/list-toys");

            } else {
                throw new ServletException("No image selected");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding toy", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error adding toy: " + e.getMessage()
            );
        }
    }
}
