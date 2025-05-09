package net.fieldb0y.wanna_play_chess.block;

import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.custom.ChessBoardBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block CHESS_BOARD = Registry.register(Registries.BLOCK, Identifier.of(WannaPlayChess.MOD_ID, "chess_board"), new ChessBoardBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD).strength(0.3f).nonOpaque()));


    private static Block registerBlock(String name, Block block){
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(WannaPlayChess.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block){
        Registry.register(Registries.ITEM, Identifier.of(WannaPlayChess.MOD_ID, name), new BlockItem(block, new Item.Settings()));
    }

    public static void register(){}
}
