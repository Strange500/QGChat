<div class="d-flex align-items-center justify-content-between">
    <img src="data:image/jpeg;base64,<%=imgBase64%>" alt="profile picture"
         class="img-fluid rounded-circle"
         style="width: 50px; height: 50px; object-fit: cover;">
    <div class="ml-3">
        <span class="font-weight-bold text-dark">
            <%=displayName%>
            <% if (senderIsAdmin) { %>
                <span class="badge text-bg-warning ml-2">Admin</span>
            <% } %>
            <%
                if (friendsChannels.stream().anyMatch(p -> p.getFirst().getUid() == sender.getUid())) { %>
                <span class="badge text-bg-success ml-2">Friend</span>
            <% } %>
        </span>
        <small class="text-muted d-block timeAgo">
            <span style="display: none"><%= message.getDateSend().getTime() / 1000 %></span>
            <p><%=message.getTimeAgo()%><p>
        </small>
    </div>
</div>