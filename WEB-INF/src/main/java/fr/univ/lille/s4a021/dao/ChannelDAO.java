package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelCreationException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelUpdateException;

import java.util.List;

public interface ChannelDAO {


    public Channel createChannel(String name) throws ChannelCreationException, DataAccessException;

    public Channel getChannelByName(String name) throws ChannelNotFoundException, DataAccessException;

    public Channel getChannelById(int cid) throws ChannelNotFoundException, DataAccessException;

    public void deleteChannel(int cid) throws ChannelNotFoundException, DataAccessException;

    public void updateChannel(int cid, String newName) throws ChannelNotFoundException, ChannelUpdateException, DataAccessException;

    public List<Channel> getAllChannels() throws DataAccessException;

    public boolean channelExists(int cid) throws DataAccessException;

}
