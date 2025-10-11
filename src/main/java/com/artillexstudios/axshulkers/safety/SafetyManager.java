package com.artillexstudios.axshulkers.safety;

import com.artillexstudios.axshulkers.AxShulkers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;

public enum SafetyManager {
    INVENTORY_OPENING,
    CLICK_OPENING,
    PLACING,
    DISPENSE;

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final JavaPlugin instance = AxShulkers.getInstance();
    private static final Gson gson = new GsonBuilder().create();
    private static ScheduledExecutorService service = null;
    private static Notifier notifier = null;
    private static boolean safe = true;
    private boolean enabled = true;

    public void set(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean get() {
        return enabled;
    }

    public static boolean isSafe() {
        return safe;
    }

    private static void check() {
        String str = "https://api.artillex-studios.com/safety/?plugin=%s&version=%s&mc=%s".formatted(instance.getName(), instance.getDescription().getVersion(), Bukkit.getBukkitVersion());
        String body;
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(str)).GET().build();
            body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception ignored) {
            return;
        }

        JsonArray disabled = gson.fromJson(body, JsonArray.class);
        for (SafetyManager value : SafetyManager.values()) {
            value.set(true);
        }
        safe = true;
        for (JsonElement jsonElement : disabled) {
            try {
                SafetyManager.valueOf(jsonElement.getAsString()).set(false);
            } catch (Exception ignored) {}
            safe = false;
        }
        if (!safe) notifier.sendAlert();
    }

    public static void start() {
        if (!CONFIG.getBoolean("enable-safety", true)) return;
        if (service != null) return;
        notifier = new Notifier();
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(SafetyManager::check, 0, 3, TimeUnit.MINUTES);
    }

    public static void stop() {
        if (service == null) return;
        service.shutdownNow();
    }
}
