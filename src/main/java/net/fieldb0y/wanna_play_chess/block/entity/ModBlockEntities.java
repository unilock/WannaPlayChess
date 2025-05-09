package net.fieldb0y.wanna_play_chess.block.entity;

import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.ModBlocks;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<ChessBoardBlockEntity> CHESS_BOARD_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(WannaPlayChess.MOD_ID,"chess_board_block_entity"),
            BlockEntityType.Builder.create(ChessBoardBlockEntity::new, ModBlocks.CHESS_BOARD).build());

    public static void register(){}
}
