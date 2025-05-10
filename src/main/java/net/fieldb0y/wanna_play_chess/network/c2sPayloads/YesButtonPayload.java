package net.fieldb0y.wanna_play_chess.network.c2sPayloads;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.fieldb0y.wanna_play_chess.chess.utils.ChessGameOverReason;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.*;

public record YesButtonPayload(BlockPos blockEntityPos) implements CustomPayload {
    public static final Id<YesButtonPayload> ID = new Id<>(Identifier.of(WannaPlayChess.MOD_ID, "yes_button_payload"));
    public static final PacketCodec<RegistryByteBuf, YesButtonPayload> CODEC = PacketCodec.of(((payload, buf) -> {
        buf.writeBlockPos(payload.blockEntityPos);
    }), buf -> new YesButtonPayload(buf.readBlockPos()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload payload, ServerPlayNetworking.Context context){
        ServerWorld world = context.player().getServerWorld();
        BlockEntity be = world.getBlockEntity(((YesButtonPayload)payload).blockEntityPos);
        if(be instanceof ChessBoardBlockEntity blockEntity && blockEntity.currentState instanceof ChessGameState gameState){
            if (gameState.resignOfferRole != -1){
                gameState.gameOver(ChessGameOverReason.RESIGN, gameState.resignOfferRole == WHITE ? BLACK : WHITE);
            } else if(gameState.drawOfferRole != -1) gameState.gameOver(ChessGameOverReason.AGREED_DRAW, -1);
        }
    }
}
