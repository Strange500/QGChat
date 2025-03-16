<%@ page import="fr.univ.lille.s4a021.Config" %>
<%@ page import="org.apache.tomcat.jakartaee.commons.lang3.exception.ExceptionUtils" %>
<%
    Integer errorCode = (Integer) request.getAttribute("errorCode");
    String message = (String) request.getAttribute("message");
    Throwable exception = (Throwable) request.getAttribute("exception");
    response.setStatus(errorCode);

    String referer = (String) request.getAttribute("referer");
    String title = "Error " + errorCode;
%>
<!doctype html>
<html lang="en">
<head>
    <%@include file="components/head.jsp"%>
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

        .stacktrace {
            text-align: left;
            white-space: pre-wrap;
            background-color: #f1f1f1;
            border: 1px solid #dc3545;
            border-radius: 0.25rem;
            padding: 15px;
            margin-top: 20px;
            max-height: 400px;
            overflow-y: auto; 
        }
    </style>
</head>
<body>

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