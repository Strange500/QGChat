<%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <!-- import bootstreap -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body class="container mt-5">

<% if (request.getAttribute("id") != null) { %>
<jsp:forward page="home.jsp"/>
<% } %>

<%
    try {
        if (request.getAttribute("loginError") != null) {
%>
    <div class="alert alert-danger" role="alert">
        <%= request.getAttribute("loginError") %>
    </div>
<%
    }
%>


<h1 class="mb-4">Connection</h1>

<form action="user" method="post" class="mb-4">
    <input type="hidden" name="action" value="auth">
    <div class="form-group">
        <label for="MailAuth">Mail</label>
        <input type="text" class="form-control" id="MailAuth" name="mail" placeholder="Mail">
    </div>
    <div class="form-group">
        <label for="pwdAuth">Password</label>
        <input type="password" class="form-control"  id="pwdAuth" name="password" placeholder="Password">
    </div>
    <button type="submit" class="btn btn-primary">Login</button>
</form>

<%
    if (request.getAttribute("registerError") != null) {
%>
        <div class="alert alert-danger" role="alert">
            <%= request.getAttribute("registerError") %>
        </div>
<%
    }
%>

<h1 class="mb-4">Registration</h1>

<form action="user" method="post">
    <input type="hidden" name="action" value="register">
    <div class="form-group">
        <label for="username">Username</label>
        <input type="text" class="form-control" id="username" name="username" placeholder="Username">
    </div>
    <div class="form-group">
        <label for="mail">Mail</label>
        <input type="email" class="form-control" id="mail" name="mail" placeholder="Mail">
    </div>
    <div class="form-group">
        <label for="password">Password</label>
        <input type="password" class="form-control" id="password" name="password" placeholder="Password">
    </div>
    <button type="submit" class="btn btn-primary">Register</button>
</form>

</body>
<%
} catch (Exception e) {
    request.setAttribute("message", e.getMessage());
    request.setAttribute("errorCode", AbstractController.getErrorCode(e));
    request.setAttribute("exception", e);

    if (!response.isCommitted()) { %>

<jsp:forward page="error.jsp"/>

<% } else {
    e.printStackTrace();
    out.println("An error occurred: " + e.getMessage());
}
}
%>
</html>