package net.fieldb0y.wanna_play_chess.network.c2sPayloads;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.PiecesData;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector2i;

public record TurnPawnIntoPayload(BlockPos blockEntityPos, int toPieceId) implements CustomPayload {
    public static final Id<TurnPawnIntoPayload> ID = new Id<>(Identifier.of(WannaPlayChess.MOD_ID, "turn_pawn_into"));
    public static final PacketCodec<RegistryByteBuf, TurnPawnIntoPayload> CODEC = PacketCodec.of(((payload, buf)->{
        buf.writeBlockPos(payload.blockEntityPos);
        buf.writeInt(payload.toPieceId);
    }), buf -> new TurnPawnIntoPayload(buf.readBlockPos(), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload payload, ServerPlayNetworking.Context context){
        ServerWorld world = context.player().getServerWorld();
        BlockEntity be = world.getBlockEntity(((TurnPawnIntoPayload)payload).blockEntityPos);
        if(be instanceof ChessBoardBlockEntity blockEntity && blockEntity.currentState instanceof ChessGameState state){
            Vector2i pawnCell = state.findPieceCellWithTag(PiecesData.DataTag.SHOULD_TURN);
            if (!pawnCell.equals(-1, -1)){
                state.turnPawnInto(pawnCell, ((TurnPawnIntoPayload)payload).toPieceId);
                state.piecesData.removeData(pawnCell.x, pawnCell.y, PiecesData.DataTag.SHOULD_TURN);
                state.updateAftermoveTags();
                state.checkForGameEnd();
                state.nextTurn();
            }
        }
    }
}
