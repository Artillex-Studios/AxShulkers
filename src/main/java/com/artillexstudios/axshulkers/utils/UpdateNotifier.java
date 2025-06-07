package com.artillexstudios.axshulkers.utils;

import com.artillexstudios.axshulkers.AxShulkers;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;
import static com.artillexstudios.axshulkers.AxShulkers.MESSAGES;
import static java.time.temporal.ChronoUnit.SECONDS;

public class UpdateNotifier implements Listener {
    private final int id;
    private final String current;
    private final JavaPlugin instance;
    private String latest = null;
    private boolean newest = true;

    public UpdateNotifier(JavaPlugin instance, int id) {
        this.id = id;
        this.current = instance.getDescription().getVersion();
        this.instance = instance;

        instance.getServer().getPluginManager().registerEvents(this, instance);

        long time = 30L * 60L * 20L;
        AxShulkers.getScheduler().runTimerAsync(t -> {
            this.latest = readVersion();
            this.newest = isLatest(current);

            if (latest == null || newest) return;
            AxShulkers.getScheduler().runLaterAsync(t2 -> {
                Bukkit.getConsoleSender().sendMessage(getMessage());
            }, 50L);
            t.cancel();
        }, 0, time);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (latest == null || newest) return;
        if (!CONFIG.getBoolean("update-notifier.on-join", true)) return;
        if (!event.getPlayer().hasPermission(instance.getName().toLowerCase() + ".update-notify")) return;
        AxShulkers.getScheduler().runLaterAsync(t -> {
            event.getPlayer().sendMessage(getMessage());
        }, 50L);
    }

    private String getMessage() {
        return ColorUtils.format(CONFIG.getString("prefix") + MESSAGES.getString("update-notifier").replace("%current%", current).replace("%latest%", latest));
    }

    @Nullable
    private String readVersion() {
        try {
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.polymart.org/v1/getResourceInfoSimple/?resource_id=" + id + "&key=version"))
                    .timeout(Duration.of(10, SECONDS))
                    .GET()
                    .build();

            final HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body().toString();
        } catch (Exception ex) {
            return null;
        }
    }

    public String getLatest() {
        return latest;
    }

    public boolean isLatest(String current) {
        return getWeight(latest) <= getWeight(current);
    }

    private int getWeight(String version) {
        if (version == null) return 0;
        String[] s = version.split("\\.");
        if (!isInt(s[0]) || !isInt(s[1]) || !isInt(s[2])) return 0;
        int res = 0;
        res += Integer.parseInt(s[0]) * 1000000;
        res += Integer.parseInt(s[1]) * 1000;
        res += Integer.parseInt(s[2]);
        return res;
    }

    private boolean isInt(@Nullable String value) {
        if (value == null) {
            return false;
        } else {
            try {
                Integer.parseInt(value);
                return true;
            } catch (Exception var2) {
                return false;
            }
        }
    }
}