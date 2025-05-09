package net.fieldb0y.wanna_play_chess.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.ModBlocks;
import net.fieldb0y.wanna_play_chess.item.custom.BoxForPieces;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.*;

public class ModItemGroups {
    public static final Text MOD_GROUP_TITLE = Text.translatable("itemgroup.wanna_play_chess.wanna_play_chess_itemgroup");

    public static final ItemGroup WANNA_PLAY_CHESS_ITEMGROUP = registerItemGroup("wanna_play_chess_itemgroup", FabricItemGroup.builder().displayName(MOD_GROUP_TITLE)
            .icon(()->new ItemStack(ModBlocks.CHESS_BOARD)).entries(((displayContext, entries) -> {
                ItemStack boardStack = ModItems.CHESS_BOARD.getDefaultStack();
                boardStack.set(ModComponents.INSERTED_PIECES_SETS, List.of(false, false));

                entries.add(boardStack);
                entries.add(ModItems.BLACK_BONE);
                entries.add(ModItems.BOX_FOR_CHESS_PIECES.getDefaultStack());
                entries.add(BoxForPieces.getFullBoxStack(WHITE));
                entries.add(BoxForPieces.getFullBoxStack(BLACK));

                entries.add(ModItems.WHITE_PAWN);
                entries.add(ModItems.WHITE_KNIGHT);
                entries.add(ModItems.WHITE_BISHOP);
                entries.add(ModItems.WHITE_ROOK);
                entries.add(ModItems.WHITE_QUEEN);
                entries.add(ModItems.WHITE_KING);

                entries.add(ModItems.BLACK_PAWN);
                entries.add(ModItems.BLACK_KNIGHT);
                entries.add(ModItems.BLACK_BISHOP);
                entries.add(ModItems.BLACK_ROOK);
                entries.add(ModItems.BLACK_QUEEN);
                entries.add(ModItems.BLACK_KING);
            })).build());

    private static ItemGroup registerItemGroup(String name, ItemGroup itemGroup){
        return Registry.register(Registries.ITEM_GROUP, Identifier.of(WannaPlayChess.MOD_ID, name), itemGroup);
    }

    public static void register(){}
}
