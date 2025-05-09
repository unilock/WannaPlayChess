package net.fieldb0y.wanna_play_chess.network.payloads;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessLobbyState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record JoinLobbyButtonPayload(BlockPos blockEntityPos) implements CustomPayload {
    public static final Id<JoinLobbyButtonPayload> ID = new Id<>(Identifier.of(WannaPlayChess.MOD_ID, "join_game_button"));
    public static final PacketCodec<RegistryByteBuf, JoinLobbyButtonPayload> CODEC = PacketCodec.of(((payload, buf) -> buf.writeBlockPos(payload.blockEntityPos)), buf -> new JoinLobbyButtonPayload(buf.readBlockPos()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload payload, ServerPlayNetworking.Context context){
        ServerWorld world = context.player().getServerWorld();
        BlockEntity be = world.getBlockEntity(((JoinLobbyButtonPayload)payload).blockEntityPos);
        if(be instanceof ChessBoardBlockEntity blockEntity){
            ((ChessLobbyState)blockEntity.states.getFirst()).addPlayer(context.player());
        }
    }
}
