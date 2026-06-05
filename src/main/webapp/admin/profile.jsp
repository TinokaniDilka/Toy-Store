<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.toystore.admin.model.Admin" %>
<%
    Admin admin = (Admin) session.getAttribute("admin");
    if (admin == null) {
        response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Profile - Toy Store</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #FF6B6B;
            --secondary-color: #4ECDC4;
            --accent-color: #FFE66D;
        }
        body {
            background: white;
            min-height: 100vh;
            font-family: 'Comic Sans MS', cursive, sans-serif;
        }
        .profile-container {
            max-width: 500px;
            margin: 50px auto;
            padding: 30px;
            background: white;
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            position: relative;
            overflow: hidden;
        }
        .profile-title {
            text-align: center;
            color: var(--primary-color);
            font-size: 2em;
            margin-bottom: 20px;
        }
        .profile-icon {
            font-size: 48px;
            color: var(--primary-color);
            margin-bottom: 20px;
            display: block;
            text-align: center;
        }
        .btn-save {
            background: linear-gradient(135deg, var(--primary-color) 0%, #FF8E8E 100%);
            border: none;
            border-radius: 15px;
            padding: 12px;
            color: white;
            font-weight: bold;
            width: 100%;
            margin-top: 10px;
            transition: all 0.3s ease;
        }
        .btn-save:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(255, 107, 107, 0.4);
            color: white;
        }
        .alert {
            border-radius: 15px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="profile-container">
        <div class="profile-icon">
            <i class="fas fa-user-circle"></i>
        </div>
        <div class="profile-title">My Profile</div>
        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("success") %></div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
        <% } %>
        <form action="profile" method="post">
            <div class="mb-3">
                <label class="form-label">Username</label>
                <input type="text" class="form-control" value="<%= admin.getUsername() %>" readonly>
            </div>
            <div class="mb-3">
                <label class="form-label">Full Name</label>
                <input type="text" class="form-control" name="fullName" value="<%= admin.getFullName() %>" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="email" class="form-control" name="email" value="<%= admin.getEmail() %>" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Role</label>
                <input type="text" class="form-control" value="<%= admin.getRole() %>" readonly>
            </div>
            <div class="mb-3">
                <label class="form-label">Change Password</label>
                <input type="password" class="form-control" name="password" placeholder="Leave blank to keep current">
            </div>
            <button type="submit" class="btn btn-save">
                <i class="fas fa-save"></i> Save Changes
            </button>
        </form>
        <!-- Go Back Button -->
        <div class="text-center mt-4">
            <a href="dashboard" class="btn btn-outline-secondary" style="background: linear-gradient(135deg, #3a7bd5 0%, #00d2ff 100%); color: white; border: none; border-radius: 15px; font-weight: bold; padding: 12px 20px; display: inline-block; text-decoration: none;">
                <i class="fas fa-arrow-left"></i> Go Back
            </a>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
