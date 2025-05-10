package net.fieldb0y.wanna_play_chess.network.c2sPayloads;

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

public record LobbyNoTimeControlCheckboxPayload(BlockPos blockEntityPos, boolean checked) implements CustomPayload {
    public static final CustomPayload.Id<LobbyNoTimeControlCheckboxPayload> ID = new CustomPayload.Id<>(Identifier.of(WannaPlayChess.MOD_ID, "lobby_no_time_control_checkbox"));
    public static final PacketCodec<RegistryByteBuf, LobbyNoTimeControlCheckboxPayload> CODEC = PacketCodec.of(((payload, buf) -> {
        buf.writeBlockPos(payload.blockEntityPos);
        buf.writeBoolean(payload.checked);
    }), buf -> new LobbyNoTimeControlCheckboxPayload(buf.readBlockPos(), buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload payload, ServerPlayNetworking.Context context){
        LobbyNoTimeControlCheckboxPayload lobbyPayload = (LobbyNoTimeControlCheckboxPayload)payload;
        ServerWorld world = context.player().getServerWorld();
        BlockEntity be = world.getBlockEntity(lobbyPayload.blockEntityPos);
        if (be instanceof ChessBoardBlockEntity blockEntity && blockEntity.currentState instanceof ChessLobbyState lobbyState){
            lobbyState.setNoTimeControl(lobbyPayload.checked);
        }
    }
}
