package com.sammy.malum.blocks.machines.spiritfurnace;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

@OnlyIn(value = Dist.CLIENT)
public class SpiritFurnaceBottomRenderer extends TileEntityRenderer<SpiritFurnaceBottomTileEntity>
{
    
    public SpiritFurnaceBottomRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }
    
    @Override
    public void render(SpiritFurnaceBottomTileEntity blockEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int light, int overlay)
    {
        if (this.renderDispatcher.renderInfo != null)
        {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            ItemStack item = blockEntity.inventory.getStackInSlot(0);
            Vector3i direction = blockEntity.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING).getDirectionVec();
            if (!item.isEmpty())
            {
                matrixStack.push();
                matrixStack.translate(0.5 + direction.getX() * 0.2f, 0.28, 0.5 + direction.getZ() * 0.2f);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(blockEntity.getWorld().getGameTime() * 3));
                matrixStack.scale(0.35f, 0.35f, 0.35f);
                itemRenderer.renderItem(item, ItemCameraTransforms.TransformType.FIXED, light, NO_OVERLAY, matrixStack, iRenderTypeBuffer);
                matrixStack.pop();
            }
        }
    }
}