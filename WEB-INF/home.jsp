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
<body class="container">
    <%@ page import="fr.univ.lille.s4a021.dto.Channel" %>
    <%@ page import="fr.univ.lille.s4a021.model.bdd.Util" %>
    <%@ page import="fr.univ.lille.s4a021.dto.Message" %>
    <%@ page import="fr.univ.lille.s4a021.dto.User" %>
    <%@ page import="fr.univ.lille.s4a021.dto.ImgMessage" %>
    <%@ page import="fr.univ.lille.s4a021.util.Pair" %>
    <%@ page import="java.util.*" %>
    <%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>
    <%@ page import="java.util.stream.Collectors" %>
    <%@ page import="fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException" %>
    <%@ page import="fr.univ.lille.s4a021.exception.dao.DataAccessException" %>
    <%@ page import="fr.univ.lille.s4a021.Config" %>
    <%@ page import="fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException" %>
    <%@ page import="fr.univ.lille.s4a021.dao.*" %>
    <%@ page import="fr.univ.lille.s4a021.exception.dao.message.MessageNotFoundException" %>
    <%@ page import="fr.univ.lille.s4a021.exception.ConfigErrorException" %>
    <%@ page import="fr.univ.lille.s4a021.exception.dao.reaction.ReactionNotFoundException" %>
    <%@ page import="fr.univ.lille.s4a021.exception.dao.NotFoundException" %>
    <%@ page import="fr.univ.lille.s4a021.exception.MyDiscordException" %>
    <%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>

    <div id="hover-div"
         class="popover bs-popover-top shadow bg-white rounded"
         style="display: none; position: absolute; z-index: 1000;">
    </div>


    <canvas style="display: none; position: absolute; top: 0; left: 0; height: 100vh; width: 100vw; z-index: 1000;"></canvas>

    <%!
        private String processMessages(List<? extends Message> messages, int uid, int channelID, boolean isAdmin, int editMid, UserDAO userDAO, AdminsDAO adminDAO, ReactionDAO reactionDAO, MessageDAO messageDAO) throws UserNotFoundException, DataAccessException, ChannelNotFoundException, MessageNotFoundException {
            StringBuilder sb = new StringBuilder();
            for (Message message : messages) {
                User user = userDAO.getUserById(message.getSenderId());
                sb.append("<div class=\"border p-3 mb-3 rounded\">");
                sb.append("<div class=\"d-flex justify-content-between align-items-center\">");
                sb.append("<div class=\"d-flex align-items-center justify-content-between\">");
                String imgBase64 = userDAO.getUserProfilePicture(user.getUid());
                sb.append("<img src=\"data:image/jpeg;base64,").append(imgBase64).append("\" alt=\"profile picture\" class=\"img-fluid rounded-circle\" style=\"width: 50px; height: 50px; object-fit: cover;\">");
                sb.append("<div class=\"ml-3\">");
                sb.append("<span class=\"font-weight-bold text-dark\">");
                if (user.getUid() == uid) {
                    sb.append("You");
                } else {
                    sb.append(user.getUsername());
                }
                if (adminDAO.userIsAdmin(user.getUid(), channelID)) {
                    sb.append("<span class=\"badge badge-warning ml-2\">Admin</span>");
                }
                sb.append("</span>");
                sb.append("<small class=\"text-muted d-block\">").append(message.getTimeAgo()).append("</small>");
                sb.append("</div>");
                sb.append("</div>");

                if (isAdmin || message.getSenderId() == uid) {
                    sb.append("<div class=\"d-flex\">");
                    if (message.getImg() == null && message.getSenderId() == uid) {
                        sb.append("<a href=\"?channelID=").append(channelID).append("&editMid=").append(message.getMid()).append("\" class=\"btn btn-link p-0\"><i class=\"bi bi-pencil\"></i></a>");
                    }
                    appendDeleteForm(sb, message.getMid());
                    sb.append("</div>");
                }
                sb.append("</div>");



                if (message.getImg() != null) {
                    sb.append("<img src=\"data:image/jpeg;base64,").append(message.getImg()).append("\" class=\"img-fluid my-2\">");
                } else {
                    if (editMid == message.getMid() && (message.getSenderId() == uid || isAdmin)) {
                        sb.append("<form action=\"message\" method=\"POST\" class=\"mt-4\" >");
                            sb.append("<input type=\"hidden\" name=\"action\" value=\"edit\">");
                            sb.append("<input type=\"hidden\" name=\"mid\" value=\"").append(message.getMid()).append("\">");
                            sb.append("<input type=\"text\" class=\"form-control\" name=\"message\" value=\"").append(message.getContenu()).append("\">");
                        sb.append("</form>");
                    } else {
                        sb.append("<p class=\"my-2 text-muted\">").append(message.getContenu()).append("</p>");
                    }
                }
                appendLikeForm(sb, message.getMid(), uid, reactionDAO, messageDAO, userDAO);

                sb.append("</div>");
            }
            return sb.toString();
        }
        %>

        <%!
            private void appendLikeForm(StringBuilder sb, int mid, int uid, ReactionDAO reactionDAO, MessageDAO messageDAO, UserDAO userDAO) throws MessageNotFoundException, DataAccessException, UserNotFoundException {
                ReactionDAO.Reaction userReaction;
                try {
                    userReaction = reactionDAO.getUserReactionForMessage(mid, uid);
                } catch (ReactionNotFoundException e) {
                    userReaction = null;
                }

                sb.append("<div style=\"width: 100px;\" class=\"d-flex align-items-center justify-content-around likeForm rounded \">");
                sb.append("<div style=\"display: none\" id=\"userDiv\" class=\"popover bs-popover-top shadow bg-white rounded p-2\">");

                // Fetch all reactions and their associated user IDs at once
                Map<ReactionDAO.Reaction, Set<Integer>> whoLiked = reactionDAO.getReactionsForMessage(mid);
                appendWhoLiked(sb, whoLiked, userDAO);
                sb.append("</div>");


                // Append reactions
                if (whoLiked == null || whoLiked.isEmpty()) {
                    appendReaction(sb, mid, ReactionDAO.Reaction.HEART, new HashSet<>(), userDAO);
                } else {
                    appendReactions(sb, mid, userReaction, whoLiked, userDAO);
                }


                sb.append("<div class=\"d-flex align-items-center otherReact\">");
                sb.append("<a class=\"btn btn-link p-0 text-muted\">...</a>");
                appendOtherReactions(sb, mid, whoLiked);
                sb.append("</div>");

                sb.append("</div>");
            }

            // Modify to accept whoLiked as a parameter
            private void appendWhoLiked(StringBuilder sb, Map<ReactionDAO.Reaction, Set<Integer>> whoLiked, UserDAO userDAO) throws UserNotFoundException, DataAccessException {
                if (whoLiked != null && !whoLiked.isEmpty()) {
                    sb.append("<span class=\"text-muted\">Liked by:</span>");
                    // Store user IDs from whoLiked
                    Set<Integer> userIds = new HashSet<>();
                    for (Set<Integer> ids : whoLiked.values()) {
                        userIds.addAll(ids); // Collect all user IDs
                    }
                    List<User> users = userDAO.getUserByIds(userIds); // Fetch all users at once

                    // Create a map for quick look-up
                    Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(User::getUid, user -> user));
                    for (Map.Entry<ReactionDAO.Reaction, Set<Integer>> entry : whoLiked.entrySet()) {
                        for (int id : entry.getValue()) {
                            User user = userMap.get(id);
                            appendUserLike(sb, user, userDAO);
                        }
                    }
                } else {
                    sb.append("<span class=\"text-muted\">No likes yet</span>");
                }
            }

            private void appendUserLike(StringBuilder sb, User user, UserDAO userDAO) throws UserNotFoundException, DataAccessException {
                if (user != null) {
                    String imgBase64 = userDAO.getUserProfilePicture(user.getUid());
                    sb.append("<div class=\"d-flex align-items-center my-1\">")
                            .append("<img src=\"data:image/jpeg;base64,").append(imgBase64)
                            .append("\" alt=\"profile picture\" class=\"img-fluid rounded-circle\" style=\"width: 30px; height: 30px; object-fit: cover;\">")
                            .append("<span class=\"ml-2\">").append(user.getUsername()).append("</span>")
                            .append("</div>");
                }
            }

            // Update appendReactions to accept whoLiked as a parameter
            private void appendReactions(StringBuilder sb, int mid, ReactionDAO.Reaction userReaction,
                                         Map<ReactionDAO.Reaction, Set<Integer>> whoLiked, UserDAO userDAO) throws UserNotFoundException, DataAccessException {
                // Iterate through each reaction type
                for (ReactionDAO.Reaction reaction : ReactionDAO.Reaction.values()) {
                    if (shouldSkipReaction(whoLiked, reaction)) {
                        continue; // Skip if there are no likes
                    }
                    appendReaction(sb, mid, reaction, whoLiked.get(reaction), userDAO);
                }
            }

            private boolean shouldSkipReaction(Map<ReactionDAO.Reaction, Set<Integer>> whoLiked,
                                               ReactionDAO.Reaction reaction) {
                return whoLiked == null || whoLiked.getOrDefault(reaction, Collections.emptySet()).isEmpty();
            }

            private void appendReaction(StringBuilder sb, int mid, ReactionDAO.Reaction reaction,
                                        Set<Integer> userIds, UserDAO userDAO) throws UserNotFoundException, DataAccessException {
                int likeCount = userIds.size();
                sb.append("<span class=\"badge badge-").append(likeCount > 0 ? "primary" : "secondary").append(" mx-1 reactSpan\">")
                        .append(likeCount > 0 ? likeCount : "");
                appendLikeForm(sb, mid, reaction);
                appendUserDetails(sb, userIds, userDAO);
                sb.append("</span>"); // Close badge span
            }

            private void appendLikeForm(StringBuilder sb, int mid, ReactionDAO.Reaction reaction) {
                sb.append("<form action=\"message\" method=\"POST\" class=\"d-inline-block mx-1\">")
                        .append("<input type=\"hidden\" name=\"action\" value=\"like\">")
                        .append("<input type=\"hidden\" name=\"mid\" value=\"").append(mid).append("\">")
                        .append("<input type=\"hidden\" name=\"emoji\" value=\"").append(reaction.getEmoji()).append("\">")
                        .append("<div class=\"d-flex align-items-center rounded\">")
                        .append("<input type=\"submit\" class=\"btn btn-link p-0\" value=\"")
                        .append(reaction.getEmoji()).append("\" style=\"font-size: 0.9em;\">")
                        .append("</div>")
                        .append("</form>");
            }

            private void appendUserDetails(StringBuilder sb, Set<Integer> userIds, UserDAO userDAO) throws UserNotFoundException, DataAccessException {
                sb.append("<section class=\"d-none reactDetails\">");
                if (userIds.isEmpty()) {
                    sb.append("<span class=\"text-muted\">No likes yet</span>");
                    sb.append("</section>"); // Close user details section
                    return;
                }
                List<User> users = userDAO.getUserByIds(userIds);

                for (int i = 0; i < Math.min(4, users.size()); i++) {
                    appendUserLike(sb, users.get(i), userDAO);
                }

                if (users.size() > 4) {
                    sb.append("<span class=\"text-muted\">and ").append(users.size() - 4).append(" others</span>");
                }
                sb.append("</section>"); // Close user details section
            }

            // Update appendOtherReactions to accept whoLiked as a parameter
            private void appendOtherReactions(StringBuilder sb, int mid, Map<ReactionDAO.Reaction, Set<Integer>> whoLiked) {
                for (ReactionDAO.Reaction reaction : ReactionDAO.Reaction.values()) {
                    if (whoLiked != null && whoLiked.getOrDefault(reaction, Collections.emptySet()).isEmpty()) {

                        sb.append("<form action=\"message\" method=\"POST\" class=\"d-inline-block otherReactForm mx-1\">")
                                .append("<input type=\"hidden\" name=\"action\" value=\"like\">")
                                .append("<input type=\"hidden\" name=\"mid\" value=\"").append(mid).append("\">")
                                .append("<input type=\"hidden\" name=\"emoji\" value=\"").append(reaction.getEmoji()).append("\">")
                                .append("<div class=\"align-items-center rounded\" style=\"display: none\">")
                                .append("<input type=\"submit\" class=\"btn btn-link p-0 text-muted\" value=\"")
                                .append(reaction.getEmoji()).append("\" style=\"font-size: 0.8em;\">")
                                .append("</div>")
                                .append("</form>");
                    }
                }
            }
//
        %>

    <%!
        private void appendDeleteForm(StringBuilder sb, int mid) {
        sb.append("<form action=\"message?action=delete\" method=\"POST\">");
        sb.append("<input type=\"hidden\" name=\"mid\" value=\"").append(mid).append("\">");
        sb.append("<button type=\"submit\" class=\"btn btn-link p-0\"><i class=\"bi bi-trash\"></i></button>");
        sb.append("</form>");
        }
    %>

    <a href="?action=logout" class="btn btn-danger mb-3">Logout</a>

    <section class="text-left">
    <%
        try {
            UserDAO userDAO = null;
            SubscriptionDAO subscriptionDAO = null;
            ChannelDAO channelDAO = null;
            AdminsDAO adminsDAO = null;
            MessageDAO messageDAO = null;
            ReactionDAO reactionDAO = null;

            try {
                userDAO = Config.getConfig().getUserDAO();
                subscriptionDAO = Config.getConfig().getSubscriptionDAO();
                channelDAO = Config.getConfig().getChannelDAO();
                adminsDAO = Config.getConfig().getAdminsDAO();
                messageDAO = Config.getConfig().getMessageDAO();
                reactionDAO = Config.getConfig().getReactionDAO();
            } catch (ConfigErrorException e) {
                AbstractController.handleError(e, request, response);
                return;
            }

            User currentUser = null;
            String profilepicBase64 = null;
            try {
                currentUser = userDAO.getUserById(Util.getUid(session));
                profilepicBase64 = userDAO.getUserProfilePicture(currentUser.getUid());
            } catch (UserNotFoundException e) {
                AbstractController.handleError(e, request, response);
                return;
            }


    %>
    <div class="d-flex align-items-center">
        <a href="user?action=edit" class="d-inline-block position-relative" id="pofileLink">
            <img src="data:image/jpeg;base64,<%=profilepicBase64%>" alt="profile picture" class="img-fluid rounded-circle" style="width: 80px; height: 80px; object-fit: cover;">
            <i class="bi bi-pencil" style="position: absolute; bottom: 50%; right: 50%; transform: translate(50%, 50%); font-size: 3em; background: rgba(127,127,127,0.5); border-radius: 50%; height: 100%; width: 100%; padding: 5% 0 0 15%; display: none;"></i>
        </a>
    <p class="ml-3 mt-2 mb-0"><%=currentUser.getUsername()%></p>
</div>
</section>

    <div class="row">
        <div class="col-md-4">
            <section id="channels">
                <h1 class="mt-4">Channels</h1>


                <a href="channel?action=createchannel" class="btn btn-primary mb-3"><i class="bi bi-plus"></i></a>
                <%
                    List<Channel> channels = channelDAO.getAllChannels();
                    if (channels != null) {
                    for (Channel channel : channels) {
                        boolean estAbonne = false;
                        try {
                            estAbonne = subscriptionDAO.isSubscribedTo((int) session.getAttribute("id"), channel.getCid());
                        } catch (UserNotFoundException | ChannelNotFoundException e) {
                            AbstractController.handleError(e, request, response);
                            return;
                        }
                        if (!estAbonne) {
                            continue;
                        }
                        %>
                            <a href="?action=view&channelID=<%=channel.getCid()%>" class="list-group-item list-group-item-action">
                                <h2 class="h5"><%=channel.getName()%></h2>
                            </a>
                        <%
                        }
                    }
                    %>
            </section>
        </div>

        <div class="col-md-8">
            <section id="conversation">
                <%
                    String channelIdParam = request.getParameter("channelID");
                    String editMid = request.getParameter("editMid");




                    if (channelIdParam != null) {
                        boolean estAbonne = false;
                        try {
                            estAbonne = subscriptionDAO.isSubscribedTo((int) session.getAttribute("id"), Integer.parseInt(channelIdParam));
                        } catch (UserNotFoundException | ChannelNotFoundException e) {
                            AbstractController.handleError(e, request, response);
                            return;
                        }
                        if (!estAbonne) {
                            return;
                        }
                        int channelID = Integer.parseInt(channelIdParam);
                        Channel channel = null;
                        try {
                            channel = channelDAO.getChannelById(channelID);
                        }catch (ChannelNotFoundException e) {
                            AbstractController.handleError(e, request, response);
                            return;
                        }

                        boolean isAdmin = false;


                        try {

                            isAdmin = adminsDAO.userIsAdmin(Util.getUid(session), channelID);

                        } catch (UserNotFoundException | ChannelNotFoundException e) {
                            AbstractController.handleError(e, request, response);
                            return;
                        }




                        if (channel != null) {
                            %>
                            <div class="d-flex justify-content-between align-items-center">
                                <div class="d-flex align-items-center">
                                    <h2 class="mb-4"><%=channel.getName()%></h2>
                                    <%
                                        if (isAdmin) {
                                    %>
                                    <a href="channel?action=delete&channelID=<%=channelID%>"
                                       class="btn btn-danger mb-3">
                                                <i class="bi bi-trash"></i>
                                            </a>

                                    <%
                                        }
                                    %>
                                </div>


                                <div>
                                    <% if (isAdmin) { %>

                                        <a id="editLink" href="channel?action=modifchannel&channelID=<%=channelID%>" class="btn btn-primary mb-3">
                                            <i class="bi bi-pencil-square"></i>
                                        </a>
                                    <% } %>
                                    <a id="shareLink" href="channel?action=share&channelID=<%=channelID%>" class="btn btn-primary mb-3">
                                        <i class="bi bi-share"></i>
                                    </a>
                                    <a id="quitLink" href="channel?action=quit&channelID=<%=channelID%>" class="btn btn-danger mb-3">
                                        <i class="bi bi-box-arrow-right"></i>
                                    </a>
                                </div>

                            </div>

                            <div id="messageList" class="overflow-auto" style="max-height: 400px;">
                                <%

                                    List<Message> messages = null;
                                    try {

                                        messages = messageDAO.getMessageByChannelId(channelID);
                                        System.out.println("LALALALALALLALA");


                                        Pair<List<ImgMessage>, List<Message>> pair = messageDAO.separateImgFromMessage(messages);
                                        List<Message> messagesList = new ArrayList<>(){{addAll(pair.getSecond()); addAll(pair.getFirst());}};
                                        messages.sort(Comparator.comparing(Message::getDateSend));

                                        if (!messages.isEmpty()) {
                                            int uid = (int) session.getAttribute("id");
                                            int editMidInt = editMid == null ? -1 : Integer.parseInt(editMid);
                                            out.print(processMessages(messagesList, uid, channelID, isAdmin, editMidInt, userDAO, adminsDAO, reactionDAO, messageDAO));
                                        }



                                    } catch (ChannelNotFoundException | UserNotFoundException | MessageNotFoundException  e) {
                                        AbstractController.handleError(e, request, response);
                                        return;
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
                            <input type="hidden" name="channelID" value="<%=channelIdParam%>">
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
                <%
                    }
                %>

                <% if (request.getAttribute("senderror") != null) { %>
                    <div class="alert alert-danger mt-3" role="alert">
                        <%= request.getAttribute("senderror") %>
                        <% request.removeAttribute("senderror"); %>
                    </div>
                <% } %>

            </section>
        </div>
    </div>


    <script defer>
        const messageList = document.getElementById('messageList');
        if (messageList) {

            messageList.scrollTop = messageList.scrollHeight;

            const reactSpan = document.querySelectorAll('.reactSpan');
            reactSpan.forEach(span => {
                span.addEventListener('mouseover', (event) => {
                    document.getElementById('hover-div').style.display = 'block';
                    document.getElementById('hover-div').style.position = 'absolute';

                    const rect = span.getBoundingClientRect();
                    document.getElementById('hover-div').style.top = rect.bottom + 'px';
                    document.getElementById('hover-div').style.left = rect.left + 'px';

                    const hoverDiv = document.getElementById('hover-div');
                    hoverDiv.innerHTML = span.querySelector('.reactDetails').innerHTML;

                });

                span.addEventListener('mouseleave', () => {
                    document.getElementById('hover-div').style.display = 'none';
                });

            });


            const imgInput = document.querySelector('input[type="file"]');
            imgInput.addEventListener('change', () => {
                const previewCard = document.getElementById('preview');
                preview.style.display = 'block';
                const previewImg = previewCard.querySelector('img');
                const reader = new FileReader();
                reader.onload = (e) => {
                    previewImg.src = e.target.result;
                    previewImg.style.display = 'block';
                };
                reader.readAsDataURL(imgInput.files[0]);
            });


        const previewCardCancelBtn = document.querySelector('#preview a');
        previewCardCancelBtn.addEventListener('click', () => {
            const previewCard = document.getElementById('preview');
            previewCard.style.display = 'none';
            const imgInput = document.querySelector('input[type="file"]');
            imgInput.value = '';
        });

        function displaySheep() {

            const canvas = document.querySelector('canvas');
            canvas.style.display = 'block';
            const ctx = canvas.getContext('2d');
            // draw image sheep.jpg
            const img = new Image();
            img.src = 'sheep.jpg';
            img.onload = () => {
                ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
            };
            setTimeout(() => {
                canvas.style.display = 'none';
            }, 500);

        }

        // check if one message contains the word sheep
            const messages = messageList.querySelectorAll('p');
            messages.forEach(message => {
                if (message.innerText.toLowerCase().includes('sheep')) {
                    displaySheep();
                }
            });
        }

        const profileLink = document.getElementById('pofileLink');
        profileLink.addEventListener('mouseenter', () => {
            profileLink.querySelector('i').style.display = 'block';
        });
        profileLink.addEventListener('mouseleave', () => {
            profileLink.querySelector('i').style.display = 'none';
        });

        const otherReacts = document.querySelectorAll('.otherReact');
        otherReacts.forEach(otherReact => {
            const aLink = otherReact.querySelector('a');
            const otherReactFormsDivs = otherReact.querySelectorAll('.otherReactForm > div');
            aLink.addEventListener('click', () => {
                otherReactFormsDivs.forEach(div => {
                    div.style.display = 'flex';
                });
            });

            otherReact.addEventListener('mouseleave', () => {
                otherReactFormsDivs.forEach(div => {
                    div.style.display = 'none';
                });
            });
        });





    </script>
</body>

<%
    } catch (MyDiscordException e) {
        AbstractController.handleError(e, request, response);
            return;
    }
%>

</html>