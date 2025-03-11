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

    <script defer src="${pageContext.request.contextPath}/scripts/home.js"></script>

</head>
<body class="container">
    <%@ page import="fr.univ.lille.s4a021.dto.Channel" %>
    <%@ page import="fr.univ.lille.s4a021.model.bdd.Util" %>
    <%@ page import="fr.univ.lille.s4a021.dto.Message" %>
    <%@ page import="fr.univ.lille.s4a021.dto.User" %>
    <%@ page import="java.util.*" %>
    <%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>
    <%@ page import="java.util.stream.Collectors" %>
    <%@ page import="fr.univ.lille.s4a021.Config" %>
    <%@ page import="fr.univ.lille.s4a021.dao.*" %>
    <%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>

    <div id="hover-div"
         class="popover bs-popover-top shadow bg-white rounded"
         style="display: none; position: absolute; z-index: 1000;">
    </div>


    <canvas style="display: none; position: absolute; top: 0; left: 0; height: 100vh; width: 100vw; z-index: 1000;"></canvas>

    <%
        try {
            int uid = (int) session.getAttribute("id");

            UserDAO userDAO = Config.getConfig().getUserDAO();
            SubscriptionDAO subscriptionDAO = Config.getConfig().getSubscriptionDAO();
            ChannelDAO channelDAO = Config.getConfig().getChannelDAO();
            AdminsDAO adminsDAO = Config.getConfig().getAdminsDAO();
            MessageDAO messageDAO = Config.getConfig().getMessageDAO();
            ReactionDAO reactionDAO = Config.getConfig().getReactionDAO();

            User currentUser = userDAO.getUserById(Util.getUid(session));
            String profilepicBase64 = userDAO.getUserProfilePicture(currentUser.getUid());
    %>


    <!-- PROFILE -->

    <a href="?action=logout" class="btn btn-danger mb-3">Logout</a>

    <section class="text-left">
        <div class="d-flex align-items-center">
            <a href="user?action=edit" class="d-inline-block position-relative" id="pofileLink">
                <img src="data:image/jpeg;base64,<%=profilepicBase64%>" alt="profile picture"
                     class="img-fluid rounded-circle" style="width: 80px; height: 80px; object-fit: cover;">
                <i class="bi bi-pencil"
                   style="position: absolute; bottom: 50%; right: 50%; transform: translate(50%, 50%); font-size: 3em; background: rgba(127,127,127,0.5); border-radius: 50%; height: 100%; width: 100%; padding: 5% 0 0 15%; display: none;"></i>
            </a>
            <p class="ml-3 mt-2 mb-0"><%=currentUser.getUsername()%>
            </p>
        </div>
    </section>

    <!-- CHANNELS -->

    <%
        List<Channel> subscribedChannels = subscriptionDAO.getSubscribedChannels(uid);
    %>


    <div class="row">
        <div class="col-md-4">
            <section id="channels">
                <h1 class="mt-4">Channels</h1>
                <a href="channel?action=createchannel" class="btn btn-primary mb-3"><i class="bi bi-plus"></i></a>
                <%
                    for (Channel channel : subscribedChannels) {
                %>
                <a href="?action=view&channelID=<%=channel.getCid()%>" class="list-group-item list-group-item-action">
                    <h2 class="h5"><%=channel.getName()%>
                    </h2>
                </a>
                <%
                    }
                %>
            </section>
        </div>

        <!-- CONVERSATION -->

        <%
            int channelToViewId = request.getParameter("channelID") == null ? -1 : Integer.parseInt(request.getParameter("channelID"));
            if (channelToViewId != -1 && subscribedChannels.stream().anyMatch(channel -> channel.getCid() == channelToViewId)) {
                List<User> listAdmins = adminsDAO.getAdmins(channelToViewId);
                boolean isAdmin = listAdmins.stream().anyMatch(user -> user.getUid() == uid);
                int editMid = request.getParameter("editMid") == null ? -1 : Integer.parseInt(request.getParameter("editMid"));
                String sendError = request.getAttribute("senderror") == null ? "" : request.getAttribute("senderror").toString();
                request.removeAttribute("senderror");
                Channel channel = channelDAO.getChannelById(channelToViewId);
                List<Message> messages = messageDAO.getMessagesAndImgMessagesByChannelId(channelToViewId);
        %>

        <div class="col-md-8">
            <section id="conversation">

                            <div class="d-flex justify-content-between align-items-center">
                                <div class="d-flex align-items-center">
                                    <h2 class="mb-4"><%=channel.getName()%></h2>
                                    <%
                                        if (isAdmin) {
                                    %>
                                    <a href="channel?action=delete&channelID=<%=channelToViewId%>"
                                       class="btn btn-danger mb-3">
                                                <i class="bi bi-trash"></i>
                                            </a>
                                    <%
                                        }
                                    %>
                                </div>


                                <div>
                                    <% if (isAdmin) { %>

                                    <a id="editLink" href="channel?action=modifchannel&channelID=<%=channelToViewId%>"
                                       class="btn btn-primary mb-3">
                                            <i class="bi bi-pencil-square"></i>
                                        </a>
                                    <% } %>
                                    <a id="shareLink" href="channel?action=share&channelID=<%=channelToViewId%>"
                                       class="btn btn-primary mb-3">
                                        <i class="bi bi-share"></i>
                                    </a>
                                    <a id="quitLink" href="channel?action=quit&channelID=<%=channelToViewId%>"
                                       class="btn btn-danger mb-3">
                                        <i class="bi bi-box-arrow-right"></i>
                                    </a>
                                </div>

                            </div>

                            <div id="messageList" class="overflow-auto" style="max-height: 400px;">
                                <%
                                    if (!messages.isEmpty()) {
                                        for (Message message : messages) {
                                            User sender = userDAO.getUserById(message.getSenderId());
                                            boolean userCanEdit = message.getSenderId() == uid || isAdmin;
                                            boolean messageRequireEdit = editMid == message.getMid();
                                            boolean isImgMessage = message.getImg() != null;
                                            boolean senderIsAdmin = listAdmins.stream().anyMatch(user -> user.getUid() == sender.getUid());
                                            String imgBase64 = userDAO.getUserProfilePicture(sender.getUid());
                                            String displayName = sender.getUid() == uid ? "You" : sender.getUsername();
                                            Map<ReactionDAO.Reaction, Set<Integer>> usersForReactionMap = reactionDAO.getReactionsForMessage(message.getMid());
                                            List<User> users = userDAO.getUserByIds(usersForReactionMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));
                                            Map<Integer, String> usersProfilePictures = userDAO.getUserProfilePictures(users.stream().map(User::getUid).collect(Collectors.toList()));
                                %>

                                <div class="border p-3 mb-3 rounded">
                                    <div class="d-flex justify-content-between align-items-center">
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
                                        <% if (userCanEdit) {
                                        %>
                                        <div class="d-flex">
                                            <% if (!isImgMessage) { %>
                                            <a href="?channelID=<%=message.getChannelId()%>&editMid=<%=message.getMid()%>"
                                               class="btn btn-link p-0"><i class="bi bi-pencil"></i></a>
                                            <% } %>
                                            <form action="message?action=delete" method="POST">
                                                <input type="hidden" name="mid" value="<%=message.getMid()%>">
                                                <button type="submit" class="btn btn-link p-0"><i
                                                        class="bi bi-trash"></i></button>
                                            </form>
                                        </div>
                                        <% } %>
                                    </div>


                                    <% if (isImgMessage) { %>
                                    <img src="data:image/jpeg;base64,<%=message.getImg()%>" class="img-fluid my-2"
                                         alt="img">
                                    <% } else {
                                        if (messageRequireEdit && userCanEdit) { %>
                                    <form action="message" method="POST" class="mt-4">
                                        <input type="hidden" name="action" value="edit">
                                        <input type="hidden" name="mid" value="<%=message.getMid()%>">
                                        <input type="text" class="form-control" name="message"
                                               value="<%=message.getContenu()%>">
                                    </form>
                                    <% } else { %>
                                    <p class="my-2 text-muted"><%=message.getContenu()%>
                                    </p>
                                    <% }
                                    }%>
                                    <div style="width: 100px;"
                                         class="d-flex align-items-center justify-content-around likeForm rounded ">
                                        <%
                                            boolean forceHeart = false;
                                            if (usersForReactionMap.isEmpty()) {
                                                usersForReactionMap.put(ReactionDAO.Reaction.HEART, Collections.emptySet());
                                                forceHeart = true;
                                            }
                                            for (ReactionDAO.Reaction reaction : ReactionDAO.Reaction.values()) {
                                                if (usersForReactionMap.getOrDefault(reaction, Collections.emptySet()).isEmpty() && (reaction != ReactionDAO.Reaction.HEART || !forceHeart)) {
                                                    continue;
                                                }
                                                int likeCount = usersForReactionMap.get(reaction).size();
                                        %>

                                        <span class="badge badge-<%=likeCount == 0 ? "secondary" : "primary"%> mx-1 reactSpan">
                                                        <%=likeCount %>
                                                        <form action="message" method="POST"
                                                              class="d-inline-block mx-1">
                                                            <input type="hidden" name="action" value="like">
                                                            <input type="hidden" name="mid"
                                                                   value="<%=message.getMid()%>">
                                                            <input type="hidden" name="emoji"
                                                                   value="<%=reaction.getEmoji()%>">
                                                            <div class="d-flex align-items-center rounded">
                                                            <input type="submit" class="btn btn-link p-0"
                                                                   value="<%=reaction.getEmoji()%>"
                                                                   style="font-size: 0.9em;">
                                                            </div>
                                                        </form>
                                                        <section class="d-none reactDetails">

                                                        <% if (usersForReactionMap.isEmpty() || likeCount == 0) { %>
                                                            <span class="text-muted">No likes yet</span>
                                                        <% } else {
                                                            for (int i = 0; i < Math.min(4, likeCount); i++) {
                                                                User reactionUser = users.get(i);
                                                        %>
                                                                <div class="d-flex align-items-center my-1">
                                                                    <img src="data:image/jpeg;base64,<%=usersProfilePictures.get(reactionUser.getUid())%>"
                                                                         alt="profile picture"
                                                                         class="img-fluid rounded-circle"
                                                                         style="width: 30px; height: 30px; object-fit: cover;">
                                                                    <span class="ml-2"><%=reactionUser.getUsername()%></span>
                                                                </div>
                                                           <% }
                                                               if (likeCount > 4) { %>
                                                                <span class="text-muted">and <%=likeCount - 4%> others</span>
                                                           <% }
                                                           } %>
                                                        </section>
                                                    </span>
                                        <% } %>


                                        <div class="d-flex align-items-center otherReact">
                                            <a class="btn btn-link p-0 text-muted">...</a>
                                            <% for (ReactionDAO.Reaction reaction : ReactionDAO.Reaction.values()) {
                                                if (usersForReactionMap.getOrDefault(reaction, Collections.emptySet()).isEmpty()) { %>

                                            <form action="message" method="POST"
                                                  class="d-inline-block otherReactForm mx-1">
                                                <input type="hidden" name="action" value="like">
                                                <input type="hidden" name="mid" value="<%=message.getMid()%>">
                                                <input type="hidden" name="emoji" value="<%=reaction.getEmoji()%>">
                                                <div class="align-items-center rounded" style="display: none">
                                                    <input type="submit" class="btn btn-link p-0 text-muted"
                                                           value="<%=reaction.getEmoji()%>" style="font-size: 0.8em;">
                                                </div>
                                            </form>
                                            <%
                                                    }
                                                }
                                            %>
                                        </div>

                                    </div>

                                </div>
                                <%
                                    }
                                } else {
                                %>
                                <div class="alert alert-info">No messages yet</div>
                                <%
                                    }
                    %>
                        </div>

                        <form action="message" method="POST" class="mt-4" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="send">
                            <input type="hidden" name="channelID" value="<%=channelToViewId%>">
                            <div class="form-group">
                                <div class="card" style="max-width: 100px; display: none" id="preview">
                                    <div class="card-body p-0">
                                        <a class="position-absolute top-0 end-0 p-1">
                                            <i class="bi bi-x-octagon"></i>
                                        </a>
                                        <img src="" alt="preview" class="img-fluid" >
                                    </div>
                                </div>

                                <input type="text" class="form-control" name="message" placeholder="Enter your message">
                            </div>
                            <input type="file" accept="image/jpeg" class="form-control-file" name="img" id="imgInput" style="display: none;">
                            <a class="btn btn-secondary" onclick="document.getElementById('imgInput').click();">
                                <i class="bi bi-paperclip"></i>
                            </a>
                            <button type="submit" class="btn btn-primary"><i class="bi bi-send"></i></button>
                        </form>

                <% if (!sendError.isEmpty()) { %>
                    <div class="alert alert-danger mt-3" role="alert">
                        <%= sendError %>
                    </div>
                <% }
                } %>

            </section>
        </div>

    </div>

    <%
    } catch (Exception e) {
        request.setAttribute("message", e.getMessage());
        request.setAttribute("errorCode", AbstractController.getErrorCode(e));
        request.setAttribute("exception", e);

        if (!response.isCommitted()) { %>

    <jsp:forward page="error.jsp"/>

    <% } else {
        out.println("An error occurred: " + e.getMessage());
    }
    }
    %>


</body>

