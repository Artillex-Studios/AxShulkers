package com.artillexstudios.axshulkers.utils;

import com.artillexstudios.axshulkers.AxShulkers;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class UpdateNotifier implements Listener {
    private static YamlDocument config;
    private static YamlDocument lang;
    private static boolean onJoin;
    private static String prefix;
    private static String updateNotifier;

    private final String current;
    private String latest = null;
    private boolean newest = true;

    public static void init(YamlDocument config, YamlDocument lang) {
        UpdateNotifier.config = config;
        UpdateNotifier.lang = lang;
        reload();
    }

    public static void reload() {
        onJoin = config.getBoolean("update-notifier.on-join", true);
        prefix = config.getString("prefix");
        updateNotifier = lang.getString("update-notifier");
    }

    public UpdateNotifier() {
        this.current = AxShulkers.getInstance().getDescription().getVersion();

        AxShulkers.getInstance().getServer().getPluginManager().registerEvents(this, AxShulkers.getInstance());

        long time = 30L * 60L * 20L;
        AxShulkers.getScheduler().runTimerAsync(t -> {
            this.latest = readVersion();
            this.newest = !isOutdated(current);

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
        if (!onJoin) return;
        if (!event.getPlayer().hasPermission(AxShulkers.getInstance().getName().toLowerCase() + ".update-notify")) return;
        AxShulkers.getScheduler().runLaterAsync(t -> {
            event.getPlayer().sendMessage(getMessage());
        }, 50L);
    }

    private String getMessage() {
        return ColorUtils.format(String.format("%s %s", prefix, updateNotifier)
                .replace("%current%", current)
                .replace("%latest%", latest)
        );
    }

    @Nullable
    private String readVersion() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://www.artillex-studios.com/api/v1/resource/%s/latest-version".formatted(AxShulkers.getInstance().getName())))
                    .timeout(Duration.of(10, SECONDS))
                    .GET()
                    .build();

            HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return null;
            return response.body().toString();
        } catch (Exception ex) {
            return null;
        }
    }

    public String getLatest() {
        return latest;
    }

    public boolean isOutdated(String current) {
        if (latest == null) return false;
        String[] parts1 = latest.split("\\.");
        String[] parts2 = current.split("\\.");
        for (int i = 0; i < 3; i++) {
            int num1 = Integer.parseInt(parts1[i]);
            int num2 = Integer.parseInt(parts2[i]);
            if (num1 > num2) {
                return true;
            } else if (num1 < num2) {
                return false;
            }
        }
        return false;
    }
}