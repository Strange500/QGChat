<%@ page import="fr.univ.lille.s4a021.Config" %>
<%@ page import="org.apache.tomcat.jakartaee.commons.lang3.exception.ExceptionUtils" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Error Page</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body {
            background-color: #f8f9fa;
        }

        .error-container {
            max-width: 900px;
            margin: 100px auto;
            text-align: center;
        }

        .error-icon {
            font-size: 100px;
            color: #dc3545;
        }

        .back-btn {
            margin-top: 20px;
        }

        .stacktrace {
            text-align: left;
            white-space: pre-wrap;
            background-color: #f1f1f1;
            border: 1px solid #dc3545;
            border-radius: 0.25rem;
            padding: 15px;
            margin-top: 20px;
            max-height: 400px;
            overflow-y: auto; /* Enable scrolling */
        }
    </style>
</head>
<body>
<%
    Integer errorCode = (Integer) request.getAttribute("errorCode");
    String message = (String) request.getAttribute("message");
    Throwable exception = (Throwable) request.getAttribute("exception");
    response.setStatus(errorCode);
%>
<div class="error-container">
    <i class="bi bi-exclamation-triangle error-icon"></i>
    <h1 class="display-4 text-danger">Error <%= errorCode %>
    </h1>
    <p class="lead"><%= message != null ? message : "An unknown error has occurred. Please try again later." %>
    </p>
    <a href="home" class="btn btn-primary btn-lg back-btn">Back to Homepage</a>

    <% if (Config.DEBUG) { %>
    <div class="stacktrace">
        <h5>Stack Trace:</h5>
        <pre><%= exception != null ? ExceptionUtils.getStackTrace(exception) : "No stack trace available." %></pre>
    </div>
    <% } %>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>