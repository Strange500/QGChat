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
<%@ page import="java.util.*" %>
    <%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>
    <%@ page import="java.util.stream.Collectors" %>
    <%@ page import="fr.univ.lille.s4a021.dao.*" %>
    <%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>
    <%@ page import="fr.univ.lille.s4a021.util.Pair" %>
<%@ page import="fr.univ.lille.s4a021.dto.*" %>

<div id="hover-div"
         class="popover bs-popover-top shadow bg-white rounded"
         style="display: none; position: absolute; z-index: 1000;">
    </div>


    <canvas style="display: none; position: absolute; top: 0; left: 0; height: 100vh; width: 100vw; z-index: 1000;"></canvas>

    <%
        try {
            int uid = (Integer) request.getAttribute("id");
            List<Channel> subscribedChannels = (List<Channel>) request.getAttribute("subscribedChannels");

            List<Pair<User, Channel>> friendsChannels = (List<Pair<User, Channel>>) request.getAttribute("friendChannels");
            List<User> friendRequests = (List<User>) request.getAttribute("friendRequests");

            Boolean friendChannel = (Boolean) request.getAttribute("friendChannel");

    %>


    <!-- TOP BAR -->

    <%@ include file="components/TopBar.jsp" %>

    <!-- CHANNELS -->

    <div class="row">
        <div class="col-md-4">
            <ul class="nav nav-tabs nav-fill mb-4">
                <li class="nav-item">
                    <a class="nav-link <%= !friendChannel ? "active" : "" %>" aria-current="page" href="#"
                       id="channelTab">Channels</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= friendChannel ? "active" : "" %>" href="#" id="friendTab">Friends</a>
                </li>
            </ul>

            <section id="channels" class="mb-4">
                <a href="channel?action=createchannel" class="btn btn-primary mb-3">Create New Channel</a>
                <div class="list-group">
                    <% for (Channel channel : subscribedChannels) { %>
                    <a href="?action=view&channelID=<%=channel.getCid()%>"
                       class="list-group-item list-group-item-action">
                        <h5 class="mb-1"><%=channel.getName()%>
                        </h5>
                    </a>
                    <% } %>
                </div>
            </section>

            <section id="friendsChannel" style="display: none">
                <a href="user?action=addFriend" class="btn btn-primary mb-3">Add Friend</a>
                <div class="list-group">
                    <% for (User user : friendRequests) { %>
                    <div class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                        <h5 class="mb-0"><%=user.getUsername()%>
                        </h5>
                        <div>
                            <a href="user?action=acceptFriendRequest&uid=<%=user.getUid()%>"
                               class="btn btn-success btn-sm mx-1">
                                <i class="bi bi-check"></i>
                            </a>
                            <a href="user?action=declineFriendRequest&uid=<%=user.getUid()%>"
                               class="btn btn-danger btn-sm mx-1">
                                <i class="bi bi-x"></i>
                            </a>
                        </div>
                    </div>
                    <% } %>
                </div>

                <div class="list-group">
                    <% for (Pair<User, Channel> pair : friendsChannels) {
                        Channel channel = pair.getSecond();
                        User friend = pair.getFirst(); %>
                    <a href="?action=viewFriend&channelID=<%=channel.getCid()%>"
                       class="list-group-item list-group-item-action">
                        <h5 class="mb-1"><%=friend.getUsername()%>
                        </h5>
                    </a>
                    <% } %>
                </div>
            </section>
        </div>

        <!-- CONVERSATION -->

        <%
            Boolean showChannel = (Boolean) request.getAttribute("showChannel");
            if (showChannel) {
                Integer channelToViewId = (Integer) request.getAttribute("channelToViewId");
                List<User> listAdmins = (List<User>) request.getAttribute("listAdmins");
                Boolean isAdmin = (Boolean) request.getAttribute("isAdmin");
                Integer editMid = (Integer) request.getAttribute("editMid");
                String sendError = (String) request.getAttribute("sendError");
                Channel channel = (Channel) request.getAttribute("channel");
                List<Message> messages = (List<Message>) request.getAttribute("messages");
                User friend = (User) request.getAttribute("friend");
                int minuteBeforeExpiration = channel.getMinuteBeforeExpiration();
        %>

        <div class="col-md-8">
            <section id="conversation">

                            <div class="d-flex justify-content-between align-items-center">
                                <div class="d-flex align-items-center">
                                    <% if (friendChannel) { %>
                                    <h2 class="mb-4">Conversation with <%= friend.getUsername() %>
                                    </h2>
                                    <% } else { %>
                                    <h2 class="mb-4"><%=channel.getName()%></h2>
                                    <% } %>
                                    <%
                                        if (!friendChannel && isAdmin) {
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
                                    <% if (!friendChannel && isAdmin) { %>

                                    <a id="editLink" href="channel?action=modifchannel&channelID=<%=channelToViewId%>"
                                       class="btn btn-primary mb-3">
                                            <i class="bi bi-pencil-square"></i>
                                        </a>
                                    <% } %>
                                    <% if (!friendChannel) { %>
                                    <a id="shareLink" href="channel?action=share&channelID=<%=channelToViewId%>"
                                       class="btn btn-primary mb-3">
                                        <i class="bi bi-share"></i>
                                    </a>
                                    <a id="quitLink" href="channel?action=quit&channelID=<%=channelToViewId%>"
                                       class="btn btn-danger mb-3">
                                        <i class="bi bi-box-arrow-right"></i>
                                    </a>
                                    <% } %>
                                </div>

                            </div>

                            <div id="messageList" class="overflow-auto" style="max-height: 400px;">
                                <%
                                    if (!messages.isEmpty()) {
                                        UserDAO userDAO = (UserDAO) request.getAttribute("userDAO");
                                        ReactionDAO reactionDAO = (ReactionDAO) request.getAttribute("reactionDAO");
                                        for (Message message : messages) {
                                            User sender = userDAO.getUserById(message.getSenderId());
                                            boolean userCanEdit = message.getSenderId() == uid || (!friendChannel && isAdmin);
                                            boolean messageRequireEdit = editMid == message.getMid();
                                            boolean isImgMessage = message.getType().equals(MsgType.IMAGE);
                                            boolean isVideoMessage = message.getType().equals(MsgType.VIDEO);
                                            boolean isAudioMessage = message.getType().equals(MsgType.AUDIO);
                                            boolean senderIsAdmin = !friendChannel && listAdmins.stream().anyMatch(user -> user.getUid() == sender.getUid());
                                            String imgBase64 = userDAO.getUserProfilePicture(sender.getUid());
                                            String displayName = sender.getUid() == uid ? "You" : sender.getUsername();
                                            Map<ReactionDAO.Reaction, Set<Integer>> usersForReactionMap = reactionDAO.getReactionsForMessage(message.getMid());
                                            List<User> users = userDAO.getUserByIds(usersForReactionMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));
                                            Map<Integer, String> usersProfilePictures = userDAO.getUserProfilePictures(users.stream().map(User::getUid).collect(Collectors.toList()));
                                %>

                                <%@include file="components/message.jsp" %>

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

                                </div>
                                <% if (minuteBeforeExpiration > 0) { %>
                                <label>Message (will expire in <%=minuteBeforeExpiration%> minutes)</label>
                                <% } %>
                                <input type="text" class="form-control" name="message" placeholder="Enter your message">
                            </div>
                            <input type="file" accept="image/*,video/*,audio/*" class="form-control-file" name="img"
                                   id="imgInput" style="display: none;">
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
        e.printStackTrace();
        out.println("An error occurred: " + e.getMessage());
    }
    }
    %>


</body>

