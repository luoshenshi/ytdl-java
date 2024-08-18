package io.github.luoshenshi;

import java.util.Map;
import java.util.concurrent.*;

public class Cache {
    private final Map<String, CompletableFuture<String>> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long timeout;

    public Cache(long timeout) {
        this.timeout = timeout;
    }

    public void set(String key, CompletableFuture<String> value) {
        ScheduledFuture<?> future = scheduler.schedule(() -> cache.remove(key), timeout, TimeUnit.MILLISECONDS);

        cache.put(key, value);

        value.whenComplete((result, error) -> {
            if (error != null) {
                cache.remove(key);
                future.cancel(false);
            }
        });
    }

    public CompletableFuture<String> getOrSet(String key, Callable<CompletableFuture<String>> fn) throws Exception {
        CompletableFuture<String> value = cache.computeIfAbsent(key, k -> {
            try {
                return fn.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
        set(key, value);
        return value;
    }

    public void clear() {
        cache.values().forEach(future -> future.cancel(false));
        cache.clear();
        scheduler.shutdown();
    }
}
