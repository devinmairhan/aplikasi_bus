package controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.User;

@WebServlet(name = "UsersController", urlPatterns = {"/users"})
public class UsersController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String auth = request.getParameter("auth");

        if (auth == null) {
            request.setAttribute("title", "Register");
            request.getRequestDispatcher("users/signup.jsp").forward(request, response);
        } else if ("login".equals(auth)) {
            request.setAttribute("title", "Login");
            request.getRequestDispatcher("users/login.jsp").forward(request, response);
        } else if ("logout".equals(auth)) {
            HttpSession session = request.getSession();
            session.invalidate();
            response.sendRedirect("users?auth=login");
        } else if ("view_customer".equals(auth)) {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                response.sendRedirect("users?auth=login");
            } else {
                request.setAttribute("title", "Customer Dashboard");
                request.setAttribute("user", user);
                request.getRequestDispatcher("users/view_customer.jsp").forward(request, response);
            }
        } else {
            response.sendRedirect("users?auth=login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        System.out.println("Action: " + action);
        if ("login".equals(action)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);

            if (user.validate()) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                response.sendRedirect("users?auth=view_customer"); // Redirect ke view_customer.jsp
            } else {
                request.getSession().setAttribute("msg", "Login failed! Check your username or password.");
                response.sendRedirect("users?auth=login");
            }

        } else if ("signup".equals(action)) {
            System.out.println("Processing signup request"); 
            String userIDParam = request.getParameter("userID");
            if (userIDParam == null || userIDParam.isEmpty()) {
                request.getSession().setAttribute("msg", "User ID is required.");
                response.sendRedirect("users?auth=signup");
                return;
            }
            try {
                int userID = Integer.parseInt(userIDParam);
                String username = request.getParameter("username");
                String email = request.getParameter("email");
                String password = request.getParameter("password");

                if (username == null || username.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
                    request.getSession().setAttribute("msg", "All fields are required.");
                    response.sendRedirect("users?auth=signup");
                    return;
                }

                User user = new User();
                user.setUserID(userID);
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                
                user.insert(); // Simpan user baru
                System.out.println("Before redirect to: users?auth=login");
                response.sendRedirect("users?auth=login");
                System.out.println("After redirect (will not be shown if redirect works).");
                //request.getSession().setAttribute("msg", "Signup successful!");
                //response.sendRedirect("users?auth=login");
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("msg", "Invalid User ID.");
                response.sendRedirect("users?auth=signup");
            }
        }
    }
}
