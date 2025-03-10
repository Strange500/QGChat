package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.message.MessageNotFoundException;
import fr.univ.lille.s4a021.exception.dao.reaction.ReactionCreationException;
import fr.univ.lille.s4a021.exception.dao.reaction.ReactionNotFoundException;
import fr.univ.lille.s4a021.exception.dao.reaction.ReactionUpdateException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface ReactionDAO {

    Map<Reaction, Set<Integer>> getReactionsForMessage(int mid) throws MessageNotFoundException, DataAccessException;

    Reaction getUserReactionForMessage(int mid, int uid) throws MessageNotFoundException, UserNotFoundException, ReactionNotFoundException, DataAccessException;

    void updateUserReactionForMessage(int mid, int uid, ReactionDAO.Reaction emoji) throws MessageNotFoundException, UserNotFoundException, ReactionUpdateException, ReactionNotFoundException, DataAccessException;

    void createReactionForMessage(int mid, int uid, ReactionDAO.Reaction emoji) throws MessageNotFoundException, UserNotFoundException, ReactionCreationException, DataAccessException;

    void deleteReactionForMessage(int mid, int uid) throws MessageNotFoundException, UserNotFoundException, ReactionNotFoundException, DataAccessException;

    enum Reaction {
        EMPTY(""),
        FIRE("🔥"),
        HEART("❤️"),
        LAUGH("😂"),
        SAD("😢"),
        ANGRY("😡");


        private final String emoji;

        Reaction(String emoji) {
            this.emoji = emoji;
        }

        public String getEmoji() {
            return emoji;
        }

        public static Reaction getReactionFromEmoji(String emoji) {
            for (Reaction r : Reaction.values()) {
                if (r.emoji.equals(emoji)) {
                    return r;
                }
            }
            return EMPTY;
        }

    }
}
