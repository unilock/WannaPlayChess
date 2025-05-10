package net.fieldb0y.wanna_play_chess.network.c2sPayloads;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record ResignButtonPayload(BlockPos blockEntityPos, int role) implements CustomPayload {
    public static final Id<ResignButtonPayload> ID = new Id<>(Identifier.of(WannaPlayChess.MOD_ID, "resign_button_payload"));
    public static final PacketCodec<RegistryByteBuf, ResignButtonPayload> CODEC = PacketCodec.of(((payload, buf) -> {
        buf.writeBlockPos(payload.blockEntityPos);
        buf.writeInt(payload.role);
    }), buf -> new ResignButtonPayload(buf.readBlockPos(), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload payload, ServerPlayNetworking.Context context){
        ServerWorld world = context.player().getServerWorld();
        BlockEntity be = world.getBlockEntity(((ResignButtonPayload)payload).blockEntityPos);
        if(be instanceof ChessBoardBlockEntity blockEntity){
            ((ChessGameState)blockEntity.currentState).setResignOfferRole(((ResignButtonPayload) payload).role);
        }
    }
}
