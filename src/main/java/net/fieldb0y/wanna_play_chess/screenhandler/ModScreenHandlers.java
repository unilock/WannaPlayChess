package net.fieldb0y.wanna_play_chess.screenhandler;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.network.payloads.BlockPosPayload;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<ChessBoardScreenHandler> CHESS_BOARD_SCREEN_HANDLER = registerScreenHandler("chess_board_screen_handler", ChessBoardScreenHandler::new, BlockPosPayload.PACKET_CODEC);

    private static <T extends ScreenHandler, D extends CustomPayload> ExtendedScreenHandlerType<T, D> registerScreenHandler(String name, ExtendedScreenHandlerType.ExtendedFactory<T, D> factory, PacketCodec<? super RegistryByteBuf, D> codec){
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(WannaPlayChess.MOD_ID, name), new ExtendedScreenHandlerType<>(factory, codec));
    }

    public static void register(){}
}
