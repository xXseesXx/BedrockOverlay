package org.xXseesXx.bedrockoverlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class BedrockOverlayMod implements ModInitializer {
    public static final String MOD_ID = "bedrockoverlay";
    public static final BedrockOverlayMod INSTANCE = new BedrockOverlayMod();
    public static final Identifier BEDROCK_OVERLAY_TEXTURE = new Identifier("bedrockoverlay", "textures/overlay/bedrock_overlay.png");
    private static final float OFFSET = 0.001f;
    private static final int NETHER_CEILING_Y = 127;

    @Override
    public void onInitialize() {
        // Nothing needed here anymore
    }

    public void renderOverlay(MatrixStack matrixStack, Vec3d cameraPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        // Only render in the nether
        if (!client.world.getDimension().ultrawarm()) return;

        // Use configured render distance
        int renderDistance = Config.INSTANCE.getRenderDistance();
        BlockPos playerPos = new BlockPos((int)cameraPos.x, (int)cameraPos.y, (int)cameraPos.z);

        for (BlockPos pos : BlockPos.iterateOutwards(playerPos, renderDistance, renderDistance, renderDistance)) {
            if (pos.getY() == NETHER_CEILING_Y && isOneBlockThickCeiling(client, pos)) {
                renderBedrockOverlay(matrixStack, pos, cameraPos);
            }
        }
    }

    private boolean isOneBlockThickCeiling(MinecraftClient client, BlockPos pos) {
        if (!client.world.getBlockState(pos).isOf(Blocks.BEDROCK)) return false;
        if (client.world.getBlockState(pos.up()).isOf(Blocks.BEDROCK)) return false;

        for (int i = 1; i <= 5; i++) {
            if (client.world.getBlockState(pos.down(i)).isOf(Blocks.BEDROCK)) {
                return false;
            }
        }

        return true;
    }

    private void renderBedrockOverlay(MatrixStack matrixStack, BlockPos pos, Vec3d cameraPos) {
        // Save current render state
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, BEDROCK_OVERLAY_TEXTURE);

        matrixStack.push();
        matrixStack.translate(
                pos.getX() - cameraPos.x - OFFSET,
                pos.getY() - cameraPos.y - OFFSET,
                pos.getZ() - cameraPos.z - OFFSET
        );

        float scale = 1.002f;
        matrixStack.scale(scale, scale, scale);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        // Red color with 70% opacity
        float r = 1.0f, g = 0.0f, b = 0.0f, a = 0.7f;

        // Draw all faces
        // Bottom face
        bufferBuilder.vertex(positionMatrix, 0, 0, 0).texture(0, 1).color(r, g, b, a).next();
        bufferBuilder.vertex(positionMatrix, 1, 0, 0).texture(1, 1).color(r, g, b, a).next();
        bufferBuilder.vertex(positionMatrix, 1, 0, 1).texture(1, 0).color(r, g, b, a).next();
        bufferBuilder.vertex(positionMatrix, 0, 0, 1).texture(0, 0).color(r, g, b, a).next();

        // Top face
        bufferBuilder.vertex(positionMatrix, 0, 1, 0).texture(0, 1).color(r, g, b, a).next();
        bufferBuilder.vertex(positionMatrix, 0, 1, 1).texture(1, 1).color(r, g, b, a).next();
        bufferBuilder.vertex(positionMatrix, 1, 1, 1).texture(1, 0).color(r, g, b, a).next();
        bufferBuilder.vertex(positionMatrix, 1, 1, 0).texture(0, 0).color(r, g, b, a).next();

        tessellator.draw();

        // Restore render state
        matrixStack.pop();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }
}