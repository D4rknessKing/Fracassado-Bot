package me.d4rk.fracassadobot.utils;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import java.util.concurrent.CompletableFuture;

public class Paste {

    public static CompletableFuture<HttpResponse<JsonNode>> toHastebinAsync(String send) {

        return Unirest.post("https://hastebin.com/documents")
                .header("User-Agent", "L1ght")
                .header("Content-Type", "text/plain")
                .body(send)
                .asJsonAsync();

    }

    public static String toHastebin(String toSend) {
        try {
            String pasteToken = Unirest.post("https://hastebin.com/documents")
                    .connectTimeout(5000).socketTimeout(5000)
                    .header("User-Agent", "L1ght")
                    .header("Content-Type", "text/plain")
                    .body(toSend)
                    .asJson()
                    .getBody()
                    .getObject()
                    .getString("key");
            return "https://hastebin.com/" + pasteToken;
        } catch (Exception e) {
            System.out.println(toSend);
            e.printStackTrace();
            return "Check logs";
        }
    }
}
