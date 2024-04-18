package com.grabbler.grabblerapi.config;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import org.springframework.web.reactive.socket.WebSocketMessage;

@Component
public class ChatWebSocketHandler implements WebSocketHandler{

    // Sink that will multicast messages to subscribers
    private final Many<String> messageSink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String path = session.getHandshakeInfo().getUri().getPath();
        String chatId = path.substring(path.lastIndexOf('/') + 1);

        // Use the sink to broadcast messages to all subscriber
        session.receive()
            .map(msg -> msg.getPayloadAsText())
            .subscribe(messageSink::tryEmitNext); // Subscribing to the flux does not block
        
        // Use the sink's asFlux method to broadcast messages to all subscribers
        Flux<WebSocketMessage> messageFlux = messageSink.asFlux().map(session::textMessage);
        
        return session.send(messageFlux);
    }
}
