package com.example.websocketclienttest.handler;

import com.example.websocketclienttest.model.ClientMessage;
import com.example.websocketclienttest.model.ServerMessage;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    private String userId;

    public MyStompSessionHandler(String userId)
    {
        this.userId = userId;
    }

    private void showHeaders(StompHeaders headers)
    {
        for (Map.Entry<String, List<String>> e:headers.entrySet()) {
            System.err.print("  " + e.getKey() + ": ");
            boolean first = true;
            for (String v : e.getValue()) {
                if ( ! first ) System.err.print(", ");
                System.err.print(v);
                first = false;
            }
            System.err.println();
        }
    }

    private void sendJsonMessage(StompSession session)
    {
        ClientMessage msg = new ClientMessage(userId,
                "hello from spring");
        session.send("/app/chat/java", msg);
    }

    private void subscribeTopic(String topic,StompSession session)
    {
        session.subscribe(topic, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ServerMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers,
                                    Object payload)
            {
                System.err.println(payload.toString());
            }
        });
    }

    @Override
    public void afterConnected(StompSession session,
                               StompHeaders connectedHeaders)
    {
        System.err.println("Connected! Headers:");
        showHeaders(connectedHeaders);

        subscribeTopic("/topic/messages", session);
        sendJsonMessage(session);
    }
}