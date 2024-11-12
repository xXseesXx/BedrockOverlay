package org.xXseesXx.bedrockoverlay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("bedrock_overlay.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private boolean enabled = true;
    private int renderDistance = 10;

    public static Config INSTANCE = load();

    private final List<ConfigChangeListener> listeners = new ArrayList<>();

    public interface ConfigChangeListener {
        void onConfigChanged();
    }

    public void addListener(ConfigChangeListener listener) {
        listeners.add(listener);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
        notifyListeners();
    }

    public int getRenderDistance() {
        return renderDistance;
    }

    public void setRenderDistance(int renderDistance) {
        this.renderDistance = Math.max(1, Math.min(32, renderDistance));
        save();
        notifyListeners();
    }

    private void notifyListeners() {
        for (ConfigChangeListener listener : listeners) {
            listener.onConfigChanged();
        }
    }

    public static Config load() {
        if (CONFIG_FILE.exists()) {
            try (Reader reader = new FileReader(CONFIG_FILE)) {
                return GSON.fromJson(reader, Config.class);
            } catch (Exception e) {
                System.err.println("Failed to load config: " + e.getMessage());
            }
        }
        Config config = new Config();
        config.save();
        return config;
    }

    public void save() {
        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
}