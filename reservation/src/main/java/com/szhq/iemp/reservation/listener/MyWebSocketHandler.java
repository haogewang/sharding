package com.szhq.iemp.reservation.listener;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.reservation.api.model.Tuser;
import com.szhq.iemp.reservation.api.service.OperatorService;
import com.szhq.iemp.reservation.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class MyWebSocketHandler implements WebSocketHandler {

    @Autowired
    private UserService userService;
    @Autowired
    private OperatorService operatorService;

    //在线用户列表
    private static final Map<String, WebSocketSession> users;

    static {
        users = new HashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("connect websocket successful!");
        String ID = session.getUri().toString().split("ID=")[1];
        if (ID != null) {
            users.put(ID, session);
            session.sendMessage(new TextMessage("{\"message\":\"socket successful connection!\"}"));
            log.info("id:" + ID + ",session:" + session + "");
        }
        log.info("current user number is:" + users.size());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        try {
            JSONObject jsonobject = JSONObject.parseObject((String) message.getPayload());
            log.info("receive message:" + jsonobject);
//			log.info(jsonobject.get("message")+":来自"+(String)session.getAttributes().get("WEBSOCKET_USERID")+"的消息");
            //jsonobject.get("id")
            sendMessageToUser(2 + "", new TextMessage("{\"message\":\"server received message,hello!\"}"));
        } catch (Exception e) {
            log.error("e", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        log.error("connect error", exception);
        users.remove(getClientId(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("connection closed: " + closeStatus);
        users.remove(getClientId(session));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 发送信息给指定用户
     */
    public boolean sendMessageToUser(String clientId, TextMessage message) {
        if (users.get(clientId) == null) return false;
        WebSocketSession session = users.get(clientId);
        log.info("sendMessage:" + message);
        if (!session.isOpen()) return false;
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            log.error("e", e);
            return false;
        }
        return true;
    }



    /**
     * 广播信息
     */
    public boolean sendMessageToAllUsers(TextMessage message) {
        boolean allSendSuccess = true;
        Set<String> clientIds = users.keySet();
        WebSocketSession session = null;
        for (String clientId : clientIds) {
            try {
                Tuser user = userService.findById(clientId);
                if (user == null) {
                    log.error("user is not find!userId:" + clientId);
                    return false;
                }
                List<Integer> ids = operatorService.findAllChildIds(user.getOperatorId());
                JSONObject alarm = JSONObject.parseObject(message.getPayload());
                log.info("user operatorIds:{}, alarm operatorId:{}", ids, alarm.getInteger("operatorId"));
                if (ids != null && (ids.contains(alarm.getInteger("operatorId")) || ids.get(0) == 0)) {
                    session = users.get(clientId);
                    if (session.isOpen()) {
                        session.sendMessage(message);
                    }
                }
            } catch (IOException e) {
                log.error("e", e);
                allSendSuccess = false;
            } catch (Exception e){
                log.error("e" + message.getPayload(), e);
            }
        }
        return allSendSuccess;
    }

    /**
     * 获取用户标识
     */
    private String getClientId(WebSocketSession session) {
        try {
            String clientId = (String) session.getAttributes().get("WEBSOCKET_USERID");
            return clientId;
        } catch (Exception e) {
            log.error("e", e);
            return null;
        }
    }

    /**
     * 获取在线人数
     */
    public static synchronized int getOnlineNum() {
        return users.size();
    }
}
