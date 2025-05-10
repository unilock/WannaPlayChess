package net.fieldb0y.wanna_play_chess.network.c2sPayloads;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.utils.GameState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record BackToLobbyButtonPayload(BlockPos blockEntityPos) implements CustomPayload {
    public static final Id<BackToLobbyButtonPayload> ID = new Id<>(Identifier.of(WannaPlayChess.MOD_ID, "back_to_lobby_button"));
    public static final PacketCodec<RegistryByteBuf, BackToLobbyButtonPayload> CODEC = PacketCodec.of(((payload, buf) -> buf.writeBlockPos(payload.blockEntityPos)), buf -> new BackToLobbyButtonPayload(buf.readBlockPos()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload payload, ServerPlayNetworking.Context context){
        ServerWorld world = context.player().getServerWorld();
        BlockEntity be = world.getBlockEntity(((BackToLobbyButtonPayload)payload).blockEntityPos);
        if(be instanceof ChessBoardBlockEntity blockEntity){
            blockEntity.setGameState(GameState.NOT_READY_TO_PLAY);
        }
    }
}
