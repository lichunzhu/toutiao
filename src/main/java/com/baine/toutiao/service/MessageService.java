package com.baine.toutiao.service;

import com.baine.toutiao.dao.MessageDAO;
import com.baine.toutiao.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageDAO messageDAO;

    public int addMessage(Message message) {
        return messageDAO.addMessage(message);
    }

    public int updateHasRead(int id, int hasRead) {
        return messageDAO.updateHasRead(id, hasRead);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        // conversation的总条数存在id里
        return messageDAO.getConversationList(userId, offset, limit);
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    public int getUnreadCount(int userId, String conversationId) {
        return messageDAO.getConversationUnReadCount(userId, conversationId);
    }

    public String getConversationId(int fromId, int toId) {
        return fromId < toId ? String.format("%d_%d", fromId, toId): String.format("%d_%d", toId, fromId);
    }
}
