package cn.evolvefield.mods.morechickens.client.render.item;


import cn.evolvefield.mods.morechickens.common.tile.base.FakeWorldTileEntity;
import cn.evolvefield.mods.morechickens.common.util.CachedMap;
import cn.evolvefield.mods.morechickens.common.util.render.ItemRenderer;
import cn.evolvefield.mods.morechickens.common.util.ItemUtils;
import cn.evolvefield.mods.morechickens.common.util.render.RendererProviders;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;


import java.util.function.Function;
import java.util.function.Supplier;


import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;



public class BlockItemRendererBase<T extends BlockEntityRenderer<U>, U extends FakeWorldTileEntity> extends ItemRenderer {

    private Function<BlockEntityRendererProvider.Context, T> rendererSupplier;
    private Supplier<U> tileEntitySupplier;
    private T renderer;
    private Minecraft minecraft;

    private CachedMap<ItemStack, U> cachedMap;

    public BlockItemRendererBase(Function<BlockEntityRendererProvider.Context, T> rendererSupplier, Supplier<U> tileentitySupplier) {
        this.rendererSupplier = rendererSupplier;
        this.tileEntitySupplier = tileentitySupplier;
        cachedMap = new CachedMap<>(10_000L, ItemUtils.ITEM_COMPARATOR);
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        if (renderer == null) {
            renderer = rendererSupplier.apply(RendererProviders.createBlockEntityRendererContext());
        }

        CompoundTag blockEntityTag = itemStack.getTagElement("BlockEntityTag");
        U tileEntity = cachedMap.get(itemStack, () -> {
            U te = tileEntitySupplier.get();
            te.setFakeWorld(minecraft.level);
            if (blockEntityTag != null) {
                te.load(blockEntityTag);
            }
            return te;
        });
        renderer.render(tileEntity, 0F, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
    }

}