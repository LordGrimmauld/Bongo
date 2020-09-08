package io.github.noeppi_noeppi.mods.bongo.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class DummyGui {
   public static void blit(MatrixStack matrixStack, int p_238466_1_, int p_238466_2_, int p_238466_3_, int p_238466_4_, float p_238466_5_, float p_238466_6_, int p_238466_7_, int p_238466_8_, int p_238466_9_, int p_238466_10_) {
      innerBlit(matrixStack, p_238466_1_, p_238466_1_ + p_238466_3_, p_238466_2_, p_238466_2_ + p_238466_4_, p_238466_7_, p_238466_8_, p_238466_5_, p_238466_6_, p_238466_9_, p_238466_10_);
   }

   public static void blit(MatrixStack matrixStack, int p_238463_1_, int p_238463_2_, float p_238463_3_, float p_238463_4_, int p_238463_5_, int p_238463_6_, int p_238463_7_, int p_238463_8_) {
      blit(matrixStack, p_238463_1_, p_238463_2_, p_238463_5_, p_238463_6_, p_238463_3_, p_238463_4_, p_238463_5_, p_238463_6_, p_238463_7_, p_238463_8_);
   }

   private static void innerBlit(MatrixStack matrixStack, int x1, int x2, int y1, int y2, int p_238469_6_, int p_238469_7_, float p_238469_8_, float p_238469_9_, int p_238469_10_, int p_238469_11_) {
      innerBlit(matrixStack.getLast().getMatrix(), x1, x2, y1, y2, (p_238469_8_ + 0.0F) / (float)p_238469_10_, (p_238469_8_ + (float)p_238469_6_) / (float)p_238469_10_, (p_238469_9_ + 0.0F) / (float)p_238469_11_, (p_238469_9_ + (float)p_238469_7_) / (float)p_238469_11_);
   }

   private static void innerBlit(Matrix4f matrix, int x1, int x2, int y1, int y2, float minU, float maxU, float minV, float maxV) {
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos(matrix, (float)x1, (float)y2, (float) 0).tex(minU, maxV).endVertex();
      bufferbuilder.pos(matrix, (float)x2, (float)y2, (float) 0).tex(maxU, maxV).endVertex();
      bufferbuilder.pos(matrix, (float)x2, (float)y1, (float) 0).tex(maxU, minV).endVertex();
      bufferbuilder.pos(matrix, (float)x1, (float)y1, (float) 0).tex(minU, minV).endVertex();
      bufferbuilder.finishDrawing();
      RenderSystem.enableAlphaTest();
      WorldVertexBufferUploader.draw(bufferbuilder);
   }
}