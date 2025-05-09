package net.fieldb0y.wanna_play_chess.network.payloads;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessLobbyRenderingState;
import net.fieldb0y.wanna_play_chess.screen.ChessBoardScreen;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SetGameTimeTextFieldPayload(int timeInSec) implements CustomPayload {
    public static final Id<SetGameTimeTextFieldPayload> ID = new Id<>(Identifier.of(WannaPlayChess.MOD_ID, "set_time_text_field"));
    public static final PacketCodec<RegistryByteBuf, SetGameTimeTextFieldPayload> CODEC = PacketCodec.of(((payload, buf) -> buf.writeInt(payload.timeInSec)), buf -> new SetGameTimeTextFieldPayload(buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload payload, ClientPlayNetworking.Context context){
        if (context.client().currentScreen instanceof ChessBoardScreen screen && screen.currentRenderingState instanceof ChessLobbyRenderingState renderingState){
            renderingState.setGameTimeTextFields(((SetGameTimeTextFieldPayload)payload).timeInSec);
        }
    }
}
