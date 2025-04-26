package org.D7lan.noitemtooltips.client;

import com.google.gson.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;

public class NoItemTooltipsMod implements ClientModInitializer {
    public static boolean enabled = true;
    public static long hoverDelayMs = 500;

    private static final Path CONFIG_PATH =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("noitemtooltips.json");

    @Override
    public void onInitializeClient() {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            if (Files.notExists(CONFIG_PATH)) {
                writeDefaultConfig();
            }
            try (Reader r = Files.newBufferedReader(CONFIG_PATH)) {
                JsonObject o = JsonParser.parseReader(r).getAsJsonObject();
                enabled      = o.has("enabled")      && o.get("enabled").getAsBoolean();
                hoverDelayMs = o.has("hoverDelayMs") ? o.get("hoverDelayMs").getAsLong() : hoverDelayMs;
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load noitemtooltips.json", e);
        }
    }

    private static void writeDefaultConfig() throws IOException {
        JsonObject o = new JsonObject();
        o.addProperty("enabled", true);
        o.addProperty("hoverDelayMs", -1);
        Files.writeString(CONFIG_PATH,
                new GsonBuilder().setPrettyPrinting().create().toJson(o),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }
}
