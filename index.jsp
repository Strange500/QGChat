<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>

    <h1>Connection </h1>

    <form action="Auth" method="post">
        <input type="text" name="mail" placeholder="Mail">
        <input type="password" name="password" placeholder="Password">
        <input type="submit" value="Login">
    </form>

    <h1>Registration</h1>

    <form action="register" method="post">
        <input type="text" name="username" placeholder="Username">
        <input type="email" name="email" placeholder="Email">
        <input type="password" name="password" placeholder="Password">
        <input type="submit" value="Register">
    </form>

</body>
</html>