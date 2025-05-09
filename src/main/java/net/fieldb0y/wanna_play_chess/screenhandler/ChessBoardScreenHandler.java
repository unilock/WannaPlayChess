package net.fieldb0y.wanna_play_chess.screenhandler;

import net.fieldb0y.wanna_play_chess.CameraAnimationPlayable;
import net.fieldb0y.wanna_play_chess.block.ModBlocks;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.fieldb0y.wanna_play_chess.network.payloads.BlockPosPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

import static net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity.*;

public class ChessBoardScreenHandler extends ScreenHandler {
    public final ChessBoardBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    public ChessBoardScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload blockPosPayload){
        this(syncId, playerInventory, (ChessBoardBlockEntity) playerInventory.player.getWorld().getBlockEntity(blockPosPayload.blockPos()));
    }

    public ChessBoardScreenHandler(int syncId, PlayerInventory playerInventory, ChessBoardBlockEntity blockEntity) {
        super(ModScreenHandlers.CHESS_BOARD_SCREEN_HANDLER, syncId);

        this.blockEntity = blockEntity;
        this.context = ScreenHandlerContext.create(this.blockEntity.getWorld(), this.blockEntity.getPos());
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        boolean canPlay = true;
        if (blockEntity.currentState == blockEntity.states.get(GAME_STATE)){
            ChessGameState state = (ChessGameState)blockEntity.currentState;
            canPlay = state.isPlayerInList(player.getUuid());
        }
        return canUse(context, player, ModBlocks.CHESS_BOARD) && canPlay;
    }


    @Override
    public void onClosed(PlayerEntity player) {
        if (player.getWorld().isClient()){
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            ((CameraAnimationPlayable)camera).stopFocusing();
            MinecraftClient.getInstance().options.hudHidden = false;
        }
        super.onClosed(player);
    }
}
