package com.voygar.companion;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WsClient {
    private WebSocket ws;
    private String url;

    public WsClient(String url) {
        this.url = url;
        connect();
    }

    public boolean isOpen() { return true; } // optimistic for MVP

    private void connect() {
        try {
            HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(URI.create(url), new WebSocket.Listener() {})
                .thenAccept(socket -> this.ws = socket)
                .join();
        } catch (Exception ignored) { }
    }

    public String requestPlan(String payload) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        StringBuilder buf = new StringBuilder();

        WebSocket.Listener listener = new WebSocket.Listener() {
            @Override public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                buf.append(data);
                if (last) latch.countDown();
                return CompletableFuture.completedFuture(null);
            }
        };

        WebSocket tmp = HttpClient.newHttpClient().newWebSocketBuilder()
            .buildAsync(URI.create(url), listener).join();
        tmp.sendText(payload, true);
        latch.await(3, TimeUnit.SECONDS);
        tmp.abort();
        return buf.toString();
    }
}
