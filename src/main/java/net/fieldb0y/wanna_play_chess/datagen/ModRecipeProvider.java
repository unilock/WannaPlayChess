package net.fieldb0y.wanna_play_chess.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fieldb0y.wanna_play_chess.item.ModItems;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.BLACK_BONE, 1)
                .input(Items.BONE)
                .input(ModItemTagProvider.BLACK_BONE_COMPONENTS)
                .group("black_bone")
                .criterion(hasItem(Items.BONE), conditionsFromItem(Items.BONE))
                .criterion("has_bone_components", conditionsFromTag(ModItemTagProvider.BLACK_BONE_COMPONENTS))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CHESS_BOARD, 1)
                        .pattern("bwb")
                        .pattern("wbw")
                        .pattern("###")
                        .input('#', ItemTags.WOODEN_SLABS)
                        .input('w', Items.WHITE_DYE)
                        .input('b', Items.BLACK_DYE)
                        .group("chessboard")
                        .criterion("has_wooden_slabs", conditionsFromTag(ItemTags.WOODEN_SLABS))
                        .criterion(hasItem(Items.WHITE_DYE), conditionsFromItem(Items.WHITE_DYE))
                        .criterion(hasItem(Items.BLACK_DYE), conditionsFromItem(Items.BLACK_DYE))
                        .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.BOX_FOR_CHESS_PIECES, 1)
                        .pattern("b b")
                        .pattern("b#b")
                        .input('b', ItemTags.WOODEN_BUTTONS)
                        .input('#', ItemTags.WOODEN_SLABS)
                        .group("box_for_pieces")
                        .criterion("has_wooden_slabs", conditionsFromTag(ItemTags.WOODEN_SLABS))
                        .criterion("has_wooden_buttons", conditionsFromTag(ItemTags.WOODEN_BUTTONS))
                        .offerTo(recipeExporter);

        offerStonecuttingRecipe(recipeExporter, RecipeCategory.MISC, ModItems.WHITE_PAWN, Items.BONE);
        offerStonecuttingRecipe(recipeExporter, RecipeCategory.MISC, ModItems.WHITE_KNIGHT, Items.BONE);
        offerStonecuttingRecipe(recipeExporter, RecipeCategory.MISC, ModItems.WHITE_BISHOP, Items.BONE);
        offerStonecuttingRecipe(recipeExporter, RecipeCategory.MISC, ModItems.WHITE_ROOK, Items.BONE);
        offerStonecuttingRecipe(recipeExporter, RecipeCategory.MISC, ModItems.WHITE_QUEEN, Items.BONE);
        offerStonecuttingRecipe(recipeExporter, RecipeCategory.MISC, ModItems.WHITE_KING, Items.BONE);

        offerStonecuttingRecipe(recipeExporter, RecipeCategory.MISC, ModItems.BLACK_PAWN, ModItems.BLACK_BONE);
        offerStonecuttingRecipe(recipeExporter, RecipeCategory.MISC, ModItems.BLACK_KNIGHT, ModItems.BLACK_BONE);
        offerStonecuttingRecipe(recipeExporter, RecipeCategory.MISC, ModItems.BLACK_BISHOP, ModItems.BLACK_BONE);
        offerStonecuttingRecipe(recipeExporter, RecipeCategory.MISC, ModItems.BLACK_ROOK, ModItems.BLACK_BONE);
        offerStonecuttingRecipe(recipeExporter, RecipeCategory.MISC, ModItems.BLACK_QUEEN, ModItems.BLACK_BONE);
        offerStonecuttingRecipe(recipeExporter, RecipeCategory.MISC, ModItems.BLACK_KING, ModItems.BLACK_BONE);
    }
}
