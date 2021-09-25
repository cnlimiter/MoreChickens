package cn.evolvefield.mods.morechickens.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class DelegatedModel implements BakedModel {

    protected final BakedModel originalModel;

    public DelegatedModel(BakedModel originalModel) {
        this.originalModel = originalModel;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) {
        return originalModel.isAmbientOcclusion(state);
    }

    @Override
    public boolean isGui3d() {
        return originalModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return originalModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return originalModel.isCustomRenderer();
    }


    @Override
    public TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return originalModel.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return originalModel.getOverrides();
    }

    @Override
    public boolean doesHandlePerspectives() {
        return originalModel.doesHandlePerspectives();
    }

    @Override
    public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack) {
        return BakedModel.super.handlePerspective(cameraTransformType, poseStack);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@Nonnull IModelData data) {
        return originalModel.getParticleIcon(data);    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        return originalModel.getModelData(world, pos, state, tileData);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return originalModel.getQuads(state, side, rand, extraData);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState p_119123_, @Nullable Direction p_119124_, Random p_119125_) {
        return null;
    }










}
