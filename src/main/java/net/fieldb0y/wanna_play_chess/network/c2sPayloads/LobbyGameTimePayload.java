package net.fieldb0y.wanna_play_chess.network.c2sPayloads;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessLobbyState;
import net.fieldb0y.wanna_play_chess.network.s2cPayloads.SetGameTimeTextFieldPayload;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record LobbyGameTimePayload(BlockPos blockEntityPos, int timeInSec) implements CustomPayload {
    public static final CustomPayload.Id<LobbyGameTimePayload> ID = new CustomPayload.Id<>(Identifier.of(WannaPlayChess.MOD_ID, "lobby_game_time"));
    public static final PacketCodec<RegistryByteBuf, LobbyGameTimePayload> CODEC = PacketCodec.of(((payload, buf) -> {
        buf.writeBlockPos(payload.blockEntityPos);
        buf.writeInt(payload.timeInSec);
    }), buf -> new LobbyGameTimePayload(buf.readBlockPos(), buf.readInt()));

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload payload, ServerPlayNetworking.Context context){
        LobbyGameTimePayload lobbyPayload = (LobbyGameTimePayload)payload;
        ServerWorld world = context.player().getServerWorld();
        BlockEntity be = world.getBlockEntity(lobbyPayload.blockEntityPos);
        if (be instanceof ChessBoardBlockEntity blockEntity && blockEntity.currentState instanceof ChessLobbyState lobbyState){
            lobbyState.setGameTimeInSec(lobbyPayload.timeInSec);
            for (ServerPlayerEntity player : context.server().getPlayerManager().getPlayerList()){
                ServerPlayNetworking.send(player, new SetGameTimeTextFieldPayload(blockEntity.getPos(), lobbyPayload.timeInSec));
            }
        }
    }
}
