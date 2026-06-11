package io.github.luoshenshi;

public class Main {
    public static void main(String[] args) {
        try (YTDL ytdl = YTDL.builder().build()) {
            ytdl.getVideoInfo("VZGt8DFyX6A").thenAccept(videoInfo -> {
                System.out.println("Title: " + videoInfo.availableCountries());
            }).exceptionally(ex -> {
                System.err.println("Extraction failed: " + ex.getMessage());
                return null;
            }).join();
        }
    }
}