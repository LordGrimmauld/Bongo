package io.github.noeppi_noeppi.mods.bongo.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderHelper {

    public static final ResourceLocation WHITE_TEXTURE = new ResourceLocation(BongoMod.MODID, "textures/white.png");

    public static void renderItemGui(MatrixStack matrixStack, IRenderTypeBuffer buffer, ItemStack stack, int x, int y, int size) {
        if (!stack.isEmpty()) {
            IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, Minecraft.getInstance().player);

            matrixStack.push();
            Minecraft.getInstance().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
            //noinspection ConstantConditions
            Minecraft.getInstance().getTextureManager().getTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);

            //noinspection deprecation
            RenderSystem.enableAlphaTest();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            //noinspection deprecation
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            matrixStack.translate(x, y, 150);
            matrixStack.scale(size / 16f, size / 16f, 1);
            matrixStack.translate(8.0F, 8.0F, 0.0F);
            matrixStack.scale(1.0F, -1.0F, 1.0F);
            matrixStack.scale(16.0F, 16.0F, 16.0F);

            if (!model.func_230044_c_()) {
                net.minecraft.client.renderer.RenderHelper.setupGuiFlatDiffuseLighting();
            }

            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GUI, false, matrixStack, buffer, 15728880, OverlayTexture.NO_OVERLAY, model);
            ((IRenderTypeBuffer.Impl) buffer).finish();

            RenderSystem.enableDepthTest();

            if (!model.func_230044_c_()) {
                net.minecraft.client.renderer.RenderHelper.setupGui3DDiffuseLighting();
            }

            //noinspection deprecation
            RenderSystem.disableAlphaTest();
            //noinspection deprecation
            RenderSystem.disableRescaleNormal();

            matrixStack.pop();
        }
    }

    public static void renderText(String text, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        float widthHalf = Minecraft.getInstance().fontRenderer.getStringWidth(text) / 2f;
        float heightHalf = Minecraft.getInstance().fontRenderer.FONT_HEIGHT / 2f;

        matrixStack.push();
        matrixStack.translate(-(widthHalf + 2), -(heightHalf + 2), 0);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //noinspection deprecation
        GlStateManager.color4f(0.2f, 0.2f, 0.2f, 0.8f);
        Minecraft.getInstance().getTextureManager().bindTexture(WHITE_TEXTURE);

        DummyGui.blit(matrixStack, 0, 0, 0, 0, (int) (2 * widthHalf) + 4, (int) (2 * heightHalf) + 4, 256, 256);

        //noinspection deprecation
        GlStateManager.color4f(1, 1, 1, 1);
        RenderSystem.disableBlend();
        matrixStack.translate(widthHalf + 2, heightHalf + 2, 10);

        Vector4f vector4f = new Vector4f(-widthHalf, -heightHalf, 0, 1.0F);
        vector4f.transform(matrixStack.getLast().getMatrix());
        RenderSystem.enableAlphaTest();

        Minecraft.getInstance().fontRenderer.renderString(text, -widthHalf, -heightHalf, 0xFFFFFF, matrixStack.getLast().getMatrix(), false);

        matrixStack.pop();
    }
}
