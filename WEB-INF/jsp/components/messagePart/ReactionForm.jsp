<div style="width: 100px;" class="d-flex align-items-center justify-content-around likeForm rounded">
    <%
        boolean forceHeart = false;
        if (usersForReactionMap.isEmpty()) {
            usersForReactionMap.put(ReactionDAO.Reaction.HEART, Collections.emptySet());
            forceHeart = true; // Force the heart reaction to be displayed if no reactions exist
        }

        for (ReactionDAO.Reaction reaction : ReactionDAO.Reaction.values()) {
            // Skip reactions with no likes and handle the heart case
            if (usersForReactionMap.getOrDefault(reaction, Collections.emptySet()).isEmpty()
                    && (reaction != ReactionDAO.Reaction.HEART || !forceHeart)) {
                continue;
            }

            int likeCount = usersForReactionMap.get(reaction).size();
    %>
    <span class="">

        <form action="message" method="POST" class="d-inline-block mx-1">
            <input type="hidden" name="action" value="like">
            <input type="hidden" name="mid" value="<%= message.getMid() %>">
            <input type="hidden" name="emoji" value="<%= reaction.getEmoji() %>">
            <div class="d-flex align-items-center rounded">
                <input type="submit" class="btn btn-link p-0"
                       value="<%= reaction.getEmoji() %>" style="display: none;">
                <button onclick="this.form.submit()" class="btn btn-<%= (likeCount == 0) ? "secondary" : "primary" %> position-relative p-1"
                        style="font-size: 0.9em;"
                        data-bs-toggle="tooltip" data-bs-html="true" data-bs-placement="bottom" data-bs-title='
                        <% if (usersForReactionMap.isEmpty() || likeCount == 0) { %>
                            <span class="text-muted">No likes yet</span>
                        <% } else {
                            for (int i = 0; i < Math.min(4, likeCount); i++) {
                                User reactionUser = users.get(i);
                        %>
                        <div class="d-flex align-items-center my-1" style="width: 30px; height: 30px; ">
                            <img src="data:image/jpeg;base64,<%= usersProfilePictures.get(reactionUser.getUid()) %>"
                                 alt="profile picture"
                                 class="img-fluid rounded-circle popupImage"
                                 >
                            <span class="ml-2"><%= reactionUser.getUsername() %></span>
                        </div>
                        <% }
                            if (likeCount > 4) { %>
                        <span class="text-muted">and <%= likeCount - 4 %> others</span>
                        <% }
                        } %>
                        '>
                    <%= reaction.getEmoji() %>
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                    <%= likeCount %>
                  </span>
                </button>
            </div>
        </form>
            </span>
    <% } %>

    <div class="d-flex align-items-center otherReact">
        <a class="btn btn-link p-0 text-muted">...</a>
        <%
            for (ReactionDAO.Reaction reaction : ReactionDAO.Reaction.values()) {
                if (usersForReactionMap.getOrDefault(reaction, Collections.emptySet()).isEmpty()) { %>
        <form action="message" method="POST" class="d-inline-block otherReactForm mx-1">
            <input type="hidden" name="action" value="like">
            <input type="hidden" name="mid" value="<%= message.getMid() %>">
            <input type="hidden" name="emoji" value="<%= reaction.getEmoji() %>">
            <div class="align-items-center rounded" style="display: none">
                <input type="submit" class="btn btn-link p-0 text-muted"
                       value="<%= reaction.getEmoji() %>" style="font-size: 0.8em;">
            </div>
        </form>
        <%
                }
            }
        %>
    </div>
    <style>
        .popupImage {
            height: 30px;
            width: 30px;
            object-fit: cover;
        }
    </style>
</div>