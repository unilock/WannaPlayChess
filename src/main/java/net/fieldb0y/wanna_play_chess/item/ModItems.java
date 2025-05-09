package net.fieldb0y.wanna_play_chess.item;

import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.ModBlocks;
import net.fieldb0y.wanna_play_chess.item.custom.*;
import net.fieldb0y.wanna_play_chess.utils.Role;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class ModItems {
    public static final Item CHESS_BOARD = registerItem("chess_board", new BlockItem(ModBlocks.CHESS_BOARD, new Item.Settings().maxCount(16)));

    public static final Item WHITE_PAWN = registerItem("white_pawn", new Pawn(new Item.Settings(), Role.WHITE));
    public static final Item WHITE_KNIGHT = registerItem("white_knight", new Knight(new Item.Settings(), Role.WHITE));
    public static final Item WHITE_BISHOP = registerItem("white_bishop", new Bishop(new Item.Settings(), Role.WHITE));
    public static final Item WHITE_ROOK = registerItem("white_rook", new Rook(new Item.Settings(), Role.WHITE));
    public static final Item WHITE_QUEEN = registerItem("white_queen", new Queen(new Item.Settings(), Role.WHITE));
    public static final Item WHITE_KING = registerItem("white_king", new King(new Item.Settings(), Role.WHITE));

    public static final Item BLACK_PAWN = registerItem("black_pawn", new Pawn(new Item.Settings(), Role.BLACK));
    public static final Item BLACK_KNIGHT = registerItem("black_knight", new Knight(new Item.Settings(), Role.BLACK));
    public static final Item BLACK_BISHOP = registerItem("black_bishop", new Bishop(new Item.Settings(), Role.BLACK));
    public static final Item BLACK_ROOK = registerItem("black_rook", new Rook(new Item.Settings(), Role.BLACK));
    public static final Item BLACK_QUEEN = registerItem("black_queen", new Queen(new Item.Settings(), Role.BLACK));
    public static final Item BLACK_KING = registerItem("black_king", new King(new Item.Settings(), Role.BLACK));

    public static final Item BLACK_BONE = registerItem("black_bone", new Item(new Item.Settings()));
    public static final Item BOX_FOR_CHESS_PIECES = registerItem("box_for_pieces", new BoxForPieces(new Item.Settings().maxCount(1)));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(WannaPlayChess.MOD_ID, name), item);
    }

    public static void register(){}
}
