package cn.evolvefield.mods.morechickens.client.render.item;

import cn.evolvefield.mods.morechickens.common.tile.base.FakeWorldTileEntity;
import cn.evolvefield.mods.morechickens.common.util.ItemUtils;
import cn.evolvefield.mods.morechickens.common.util.math.CachedMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockItemRendererBase<T extends TileEntityRenderer<U>, U extends FakeWorldTileEntity> extends ItemStackTileEntityRenderer {

    private Function<TileEntityRendererDispatcher, T> rendererSupplier;
    private Supplier<U> tileEntitySupplier;
    private T renderer;
    private Minecraft minecraft;

    private CachedMap<ItemStack, U> cachedMap;

    public BlockItemRendererBase(Function<TileEntityRendererDispatcher, T> rendererSupplier, Supplier<U> tileentitySupplier) {
        this.rendererSupplier = rendererSupplier;
        this.tileEntitySupplier = tileentitySupplier;
        cachedMap = new CachedMap<>(10_000L, ItemUtils.ITEM_COMPARATOR);
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if (renderer == null) {
            renderer = rendererSupplier.apply(TileEntityRendererDispatcher.instance);
        }

        CompoundNBT blockEntityTag = itemStack.getTagElement("BlockEntityTag");
        U tileEntity = cachedMap.get(itemStack, () -> {
            U te = tileEntitySupplier.get();
            te.setFakeWorld(minecraft.level);
            if (blockEntityTag != null) {
                te.load(null, blockEntityTag);
            }
            return te;
        });
        renderer.render(tileEntity, 0F, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
    }

}
