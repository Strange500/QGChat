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

    String referer = (String) request.getAttribute("referer");
%>
<div class="error-container">
    <i class="bi bi-exclamation-triangle error-icon"></i>
    <h1 class="display-4 text-danger">Error <%= errorCode %>
    </h1>
    <p class="lead"><%= message != null ? message : "An unknown error has occurred. Please try again later." %>
    </p>
    <% if (referer != null) { %>
    <a href="<%= referer %>" class="btn btn-primary mb-3">Back</a>
    <% } %>

    <% if (Config.DEBUG) {
        exception.printStackTrace();
    %>
    <div class="stacktrace">
        <h5>Stack Trace:</h5>
        <pre><%= ExceptionUtils.getStackTrace(exception) %></pre>
    </div>
    <% } %>
</div>

</body>
</html>