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

<%
    if (request.getAttribute("error") != null) {
%>

    <div class="alert alert-danger" role="alert">
        <%= request.getAttribute("error") %>
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
    if (request.getAttribute("registererror") != null) {
%>

        <div class="alert alert-danger" role="alert">
            <%= request.getAttribute("registererror") %>
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
</html>