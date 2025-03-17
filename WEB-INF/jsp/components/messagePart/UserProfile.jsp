<% boolean isFriend = friendsChannels.stream().anyMatch(p -> p.getFirst().getUid() == sender.getUid());
    boolean canSendFriendRequest = !isFriend && sender.getUid() != uid && friendRequests.stream().noneMatch(p -> p.getUid() == sender.getUid()) && pendingFriendRequests.stream().noneMatch(p -> p.getUid() == sender.getUid());
    boolean friendRequestSent = pendingFriendRequests.stream().anyMatch(p -> p.getUid() == sender.getUid());
%>

<div class="d-flex align-items-center justify-content-between">
    <% if (canSendFriendRequest) { %>
    <a href="user?action=sendFriendRequest&uid=<%=sender.getUid()%>">
        <% } %>
    <img src="data:image/jpeg;base64,<%=imgBase64%>" alt="profile picture"
         class="img-fluid rounded-circle"

        <% if (canSendFriendRequest) { %>
         data-bs-toggle="tooltip" data-bs-placement="top"
         data-bs-title="Add as friend"
         style="width: 50px; height: 50px; object-fit: cover;cursor: pointer;"
        <% }else { %>
         style="width: 50px; height: 50px; object-fit: cover;"
        <% } %>
    >
        <% if (canSendFriendRequest) { %>
    </a>
    <% } %>
    <div class="ml-3">
        <span class="font-weight-bold text-dark">
            <%=displayName%>
            <% if (senderIsAdmin) { %>
                <span class="badge text-bg-warning ml-2">Admin</span>
            <% } %>
            <%
                if (isFriend) { %>
                <span class="badge text-bg-success ml-2">Friend</span>
            <% } %>
            <%
                if (friendRequestSent) { %>
                <span class="badge text-bg-info ml-2">
                    <i class="bi bi-clock"></i> Pending
                </span>
            <% } %>
        </span>
        <small class="text-muted d-block timeAgo">
            <span style="display: none"><%= message.getDateSend().getTime() / 1000 %></span>
            <p><%=message.getTimeAgo()%><p>
        </small>
    </div>
</div>