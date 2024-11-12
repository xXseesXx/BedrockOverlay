package org.xXseesXx.bedrockoverlay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.text.Text;

public class BedrockOverlayClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register key binding
        KeyBindings.register();

        // Register render event
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if (Config.INSTANCE.isEnabled()) {
                BedrockOverlayMod.INSTANCE.renderOverlay(context.matrixStack(), context.camera().getPos());
            }
        });

        // Register key binding handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (KeyBindings.TOGGLE_OVERLAY.wasPressed()) {
                Config.INSTANCE.setEnabled(!Config.INSTANCE.isEnabled());
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Bedrock Overlay: " +
                            (Config.INSTANCE.isEnabled() ? "Enabled" : "Disabled")), true);
                }
            }
        });
    }
}