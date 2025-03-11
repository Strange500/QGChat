<div class="d-flex align-items-center justify-content-between">
    <img src="data:image/jpeg;base64,<%=imgBase64%>" alt="profile picture"
         class="img-fluid rounded-circle"
         style="width: 50px; height: 50px; object-fit: cover;">
    <div class="ml-3">
        <span class="font-weight-bold text-dark">
            <%=displayName%>
            <% if (senderIsAdmin) { %>
                <span class="badge badge-warning ml-2">Admin</span>
            <% } %>
        </span>
        <small class="text-muted d-block"><%=message.getTimeAgo()%>
        </small>
    </div>
</div>