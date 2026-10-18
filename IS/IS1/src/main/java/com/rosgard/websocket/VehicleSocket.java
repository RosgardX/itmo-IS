package com.rosgard.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/vehicles")
public class VehicleSocket {

    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void open(Session session) { SESSIONS.add(session); }

    @OnClose
    public void close(Session session) { SESSIONS.remove(session); }

    @OnError
    public void error(Session session, Throwable t) { SESSIONS.remove(session); }

    public static void broadcast(String message) {
        for (Session s : SESSIONS) {
            if (s.isOpen()) s.getAsyncRemote().sendText(message);
        }
    }
}