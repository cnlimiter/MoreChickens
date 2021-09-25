package cn.evolvefield.mods.morechickens.common.util.render;

import cn.evolvefield.mods.morechickens.MoreChickens;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import org.lwjgl.opengl.GL11;

public class RenderTypes extends RenderStateShard {

    public static RenderType getOutlineTranslucent(ResourceLocation texture, boolean cull) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                //.setDiffuseLightingState(DIFFUSE_LIGHTING)
                //.setAlphaState(DEFAULT_ALPHA)
                .setCullState(cull ? CULL : NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return RenderType.create(createLayerName("outline_translucent" + (cull ? "_cull" : "")),
                DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state);
    }

    private static final RenderType OUTLINE_SOLID =
            RenderType.create(createLayerName("outline_solid"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true,
                    false, RenderType.CompositeState.builder()
                            .setTextureState(new RenderStateShard.TextureStateShard(SpecialTextures.BLANK.getLocation(), false, false))
                            //.setDiffuseLightingState(DIFFUSE_LIGHTING)
                            .setLightmapState(LIGHTMAP)
                            .setOverlayState(OVERLAY)
                            .createCompositeState(true));

    public static RenderType getGlowingSolid(ResourceLocation texture) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return RenderType.create(createLayerName("glowing_solid"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
                true, false, rendertype$state);
    }

    public static RenderType getGlowingTranslucent(ResourceLocation texture) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                //.setAlphaState(DEFAULT_ALPHA)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return RenderType.create(createLayerName("glowing_translucent"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS,
                256, true, true, rendertype$state);
    }

    private static final RenderType GLOWING_SOLID = getGlowingSolid(InventoryMenu.BLOCK_ATLAS);
    private static final RenderType GLOWING_TRANSLUCENT = getGlowingTranslucent(InventoryMenu.BLOCK_ATLAS);

    private static final RenderType ITEM_PARTIAL_SOLID =
            RenderType.create(createLayerName("item_solid"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true,
                    false, RenderType.CompositeState.builder()
                            .setTextureState(BLOCK_SHEET)
                            .setTransparencyState(NO_TRANSPARENCY)
                            //.setDiffuseLightingState(DIFFUSE_LIGHTING)
                            .setLightmapState(LIGHTMAP)
                            .setOverlayState(OVERLAY)
                            .createCompositeState(true));

    private static final RenderType ITEM_PARTIAL_TRANSLUCENT = RenderType.create(createLayerName("item_translucent"),
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
                    .setTextureState(BLOCK_SHEET)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    //.setDiffuseLightingState(DIFFUSE_LIGHTING)
                    //.setAlphaState(DEFAULT_ALPHA)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true));

    private static final RenderType FLUID = RenderType.create(createLayerName("fluid"),
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_SMOOTH_CUTOUT_SHADER)
                    //.setAlphaState(DEFAULT_ALPHA)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true));

    private static String createLayerName(String name) {
        return MoreChickens.MODID + ":" + name;
    }

    public static RenderType getItemPartialSolid() {
        return ITEM_PARTIAL_SOLID;
    }

    public static RenderType getItemPartialTranslucent() {
        return ITEM_PARTIAL_TRANSLUCENT;
    }

    public static RenderType getOutlineSolid() {
        return OUTLINE_SOLID;
    }

    public static RenderType getGlowingSolid() {
        return GLOWING_SOLID;
    }

    public static RenderType getGlowingTranslucent() {
        return GLOWING_TRANSLUCENT;
    }

    public static RenderType getFluid() {
        return FLUID;
    }

    // Mmm gimme those protected fields
    public RenderTypes() {
        super(null, null, null);
    }
}
