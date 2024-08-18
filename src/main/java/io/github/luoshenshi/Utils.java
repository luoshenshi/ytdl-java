package io.github.luoshenshi;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.luoshenshi.YtdlConstants.client;

public class Utils {
    public static Integer parseAbbreviatedNumber(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        // Replace ',' with '.' and remove spaces
        String sanitizedInput = input.replace(",", ".").replace(" ", "");

        // Regular expression to match the number with optional 'M' or 'K'
        Pattern pattern = Pattern.compile("([\\d,.]+)([MK]?)");
        Matcher matcher = pattern.matcher(sanitizedInput);

        if (matcher.find()) {
            String numString = matcher.group(1);
            String multi = matcher.group(2);

            try {
                double num = Double.parseDouble(numString);
                if ("M".equals(multi)) {
                    return (int) Math.round(num * 1_000_000);
                } else if ("K".equals(multi)) {
                    return (int) Math.round(num * 1_000);
                } else {
                    return (int) Math.round(num);
                }
            } catch (NumberFormatException e) {
                // Handle the case where parsing fails
                return null;
            }
        }
        return null;
    }

    public static String findBetween(String string, String start, String end) {
        int startIndex = string.indexOf(start);
        if (startIndex == -1) return null;
        startIndex += start.length();
        int endIndex = string.indexOf(end, startIndex);
        if (endIndex == -1) return null;
        return string.substring(startIndex, endIndex);
    }

    public static String findBetween(String text) {
        String start = "var ytInitialPlayerResponse = ";
        int startPos = text.indexOf(start);
        if (startPos == -1) return "";
        int endPos = text.indexOf(";</script>", startPos + start.length());
        if (endPos == -1) return "";
        return text.substring(startPos + start.length(), endPos);
    }

    public static JSONObject parseJSON(String source, String varName, String json) throws Exception {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return new JSONObject(json);
        } catch (Exception e) {
            throw new Exception("Error parsing " + varName + " in " + source + ": " + e.getMessage());
        }
    }

    public static CompletableFuture<String> request(String url) {
        Request request = new Request.Builder().url(url).build();
        CompletableFuture<String> future = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
                client.dispatcher().executorService().shutdown();
                client.connectionPool().evictAll();
                if (client.cache() != null) {
                    try {
                        client.cache().close();
                    } catch (IOException ioException) {
                        throw new IllegalArgumentException(ioException.getLocalizedMessage());
                    }
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        future.complete(response.body().string());
                    } else {
                        future.completeExceptionally(new IOException("Request failed with status code: " + response.code()));
                    }
                } finally {
                    client.dispatcher().executorService().shutdown();
                    client.connectionPool().evictAll();
                    if (client.cache() != null) {
                        try {
                            client.cache().close();
                        } catch (IOException ioException) {
                            throw new Error(ioException);
                        }
                    }
                }
            }
        });

        return future;
    }

    public static JSONObject tryParseBetween(String body, String left, String right, String prepend, String append) {
        try {
            String data = findBetween(body, left, right);
            assert data != null;
            if (data.isEmpty()) return null;
            return new JSONObject(prepend + data + append);
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject findJSON(String source, String varName, String body, String left, String right, String prependJSON) throws Exception {
        String jsonStr = findBetween(body, left, right);
        assert jsonStr != null;
        if (jsonStr.isEmpty()) {
            throw new Exception("Could not find " + varName + " in " + source);
        }
        return parseJSON(source, varName, prependJSON + jsonStr);
    }
}
