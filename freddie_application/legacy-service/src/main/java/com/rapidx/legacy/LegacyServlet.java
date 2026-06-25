package com.rapidx.legacy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LegacyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to index.jsp for GET request
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Process parameters
        String clientName = request.getParameter("clientName");
        String message = request.getParameter("message");

        // Simple validation
        if (clientName == null || clientName.trim().isEmpty()) {
            request.setAttribute("error", "Client Name is required!");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        // Set attributes
        request.setAttribute("clientName", clientName);
        request.setAttribute("message", message);
        request.setAttribute("timestamp", LocalDateTime.now().toString());

        // Generate some legacy logs simulation list
        List<String> systemLogs = new ArrayList<>();
        systemLogs.add("Initializing legacy request process context...");
        systemLogs.add("Client identity verified: " + clientName);
        systemLogs.add("Dispatching parameters to JSP view handler...");
        request.setAttribute("logs", systemLogs);

        // Forward to display.jsp
        request.getRequestDispatcher("/display.jsp").forward(request, response);
    }
}
