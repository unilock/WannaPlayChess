package net.fieldb0y.wanna_play_chess.network.s2cPayloads;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessGameRenderingState;
import net.fieldb0y.wanna_play_chess.screen.ChessBoardScreen;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record TimerUpdatePayload(BlockPos blockEntityPos, int whiteTimeLeftInSec, int blackTimeLeftInSec) implements CustomPayload {
    public static final Id<TimerUpdatePayload> ID = new Id<>(Identifier.of(WannaPlayChess.MOD_ID, "timer_update"));
    public static final PacketCodec<RegistryByteBuf, TimerUpdatePayload> CODEC = PacketCodec.of(((payload, buf) -> {
        buf.writeBlockPos(payload.blockEntityPos);
        buf.writeInt(payload.whiteTimeLeftInSec);
        buf.writeInt(payload.blackTimeLeftInSec);
    }), buf -> new TimerUpdatePayload(buf.readBlockPos(), buf.readInt(), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload customPayload, ClientPlayNetworking.Context context){
        TimerUpdatePayload payload = (TimerUpdatePayload)customPayload;
        if (context.client().currentScreen instanceof ChessBoardScreen screen && screen.currentRenderingState instanceof ChessGameRenderingState renderingState){
            if (screen.handler.blockEntity.getPos().equals(payload.blockEntityPos))
                renderingState.updateTimer(payload.whiteTimeLeftInSec, payload.blackTimeLeftInSec);
        }
    }
}
