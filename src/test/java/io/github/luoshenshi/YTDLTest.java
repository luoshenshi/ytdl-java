package io.github.luoshenshi;

import org.junit.jupiter.api.Test;

class YTDLTest {

    @Test
    void shouldCreateInstance() {

        try (YTDL ytdl = YTDL.builder().build()) {
            ytdl.getVideoInfo("VZGt8DFyX6A").thenAccept(videoInfo -> {
                System.out.println("Title: " + videoInfo.title());
            }).exceptionally(ex -> {
                System.err.println("Extraction failed: " + ex.getMessage());
                return null;
            }).join();
        }
    }
}