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
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body>

    <%
    Integer errorCode = (Integer) request.getAttribute("errorCode");
    String message = (String) request.getAttribute("message");
    if (errorCode != null && message != null) {
%>
    <div class="container mt-5">
        <div class="alert alert-danger" role="alert">
            <h4 class="alert-heading">Error <%= errorCode %></h4>
            <p><%= message %></p>
        </div>
    </div>
<%
    }
%>

<a href="home" class="btn btn-primary">Back</a>

</body>
</html>