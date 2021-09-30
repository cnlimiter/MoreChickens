package cn.evolvefield.mods.morechickens.integrations.jei;

import cn.evolvefield.mods.morechickens.MoreChickens;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class BaseCategory<T> implements IRecipeCategory<T> {

    private static final ResourceLocation ICONS = new ResourceLocation(MoreChickens.MODID, "textures/gui/jei/icons.png");

    private final ResourceLocation categoryId;
    private final String localizedName;
    private final IDrawable background;
    private final IDrawable icon;
    private final Class<? extends T> recipeClass;

    public final IDrawable info;

    public BaseCategory(IGuiHelper guiHelper, ResourceLocation categoryId, String localizedName, IDrawable background, IDrawable icon, Class<? extends T> recipeClass) {
        this.categoryId = categoryId;
        this.localizedName = localizedName;
        this.background = background;
        this.icon = icon;
        this.recipeClass = recipeClass;


        this.info = guiHelper.createDrawable(ICONS, 16, 0, 9, 9);
    }

    @Override
    public @NotNull ResourceLocation getUid() {
        return categoryId;
    }

    @Override
    public @NotNull Class<? extends T> getRecipeClass() {
        return recipeClass;
    }

    @Override
    public @NotNull String getTitle() {
        return localizedName;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }
}
