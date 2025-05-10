package net.fieldb0y.wanna_play_chess.network.c2sPayloads;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessLobbyState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record LeaveLobbyPayload(BlockPos blockEntityPos) implements CustomPayload {
    public static final Id<LeaveLobbyPayload> ID = new Id<>(Identifier.of(WannaPlayChess.MOD_ID, "leave_lobby_button"));
    public static final PacketCodec<RegistryByteBuf, LeaveLobbyPayload> CODEC = PacketCodec.of(((payload, buf) -> buf.writeBlockPos(payload.blockEntityPos)), buf -> new LeaveLobbyPayload(buf.readBlockPos()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload payload, ServerPlayNetworking.Context context){
        BlockEntity be = context.player().getServerWorld().getBlockEntity(((LeaveLobbyPayload)payload).blockEntityPos);
        if (be instanceof ChessBoardBlockEntity blockEntity){
            ((ChessLobbyState)blockEntity.states.getFirst()).removePlayerFromLobby(context.player());
        }
    }
}
