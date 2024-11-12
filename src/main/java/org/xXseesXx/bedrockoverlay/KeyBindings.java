package org.xXseesXx.bedrockoverlay;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static KeyBinding TOGGLE_OVERLAY;

    public static void register() {
        TOGGLE_OVERLAY = new KeyBinding(
                "key.bedrockoverlay.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "category.bedrockoverlay.general"
        ) {
            @Override
            public boolean isUnbound() {
                return false; // This makes it not show up in the main controls menu
            }
        };
        KeyBindingHelper.registerKeyBinding(TOGGLE_OVERLAY);
    }
}