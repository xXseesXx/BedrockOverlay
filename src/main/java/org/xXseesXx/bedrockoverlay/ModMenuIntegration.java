package org.xXseesXx.bedrockoverlay;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigScreen(parent);
    }

    private static class ConfigScreen extends Screen {
        private final Screen parent;
        private static final int BUTTON_WIDTH = 200;
        private static final int BUTTON_HEIGHT = 20;
        private static final int PADDING = 10;

        protected ConfigScreen(Screen parent) {
            super(Text.literal("Bedrock Overlay Settings"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            int centerX = this.width / 2;
            int startY = this.height / 4;

            // Toggle button
            this.addDrawableChild(ButtonWidget.builder(
                            Text.literal("Enabled: " + (Config.INSTANCE.isEnabled() ? "Yes" : "No")),
                            button -> {
                                Config.INSTANCE.setEnabled(!Config.INSTANCE.isEnabled());
                                button.setMessage(Text.literal("Enabled: " + (Config.INSTANCE.isEnabled() ? "Yes" : "No")));
                            })
                    .dimensions(centerX - BUTTON_WIDTH/2, startY, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build());

            // Render distance slider
            this.addDrawableChild(new SliderWidget(
                    centerX - BUTTON_WIDTH/2, startY + BUTTON_HEIGHT + PADDING,
                    BUTTON_WIDTH, BUTTON_HEIGHT,
                    Text.literal("Render Distance: " + Config.INSTANCE.getRenderDistance()),
                    (Config.INSTANCE.getRenderDistance() - 1) / 31.0) {
                @Override
                protected void updateMessage() {
                    setMessage(Text.literal("Render Distance: " + Config.INSTANCE.getRenderDistance()));
                }

                @Override
                protected void applyValue() {
                    Config.INSTANCE.setRenderDistance((int) (value * 31 + 1));
                }
            });

            // Done button
            this.addDrawableChild(ButtonWidget.builder(
                            ScreenTexts.DONE,
                            button -> this.client.setScreen(this.parent))
                    .dimensions(centerX - BUTTON_WIDTH/2, startY + (BUTTON_HEIGHT + PADDING) * 2, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build());
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.renderBackground(context);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
            super.render(context, mouseX, mouseY, delta);
        }
    }
}