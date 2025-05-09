package net.fieldb0y.wanna_play_chess.network.payloads;

import it.unimi.dsi.fastutil.ints.IntList;
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

public record CellClickPayload(BlockPos blockEntityPos, int cellX, int cellY, int playerRole) implements CustomPayload {
    public static final CustomPayload.Id<CellClickPayload> ID = new CustomPayload.Id<>(Identifier.of(WannaPlayChess.MOD_ID, "cell_click"));
    public static final PacketCodec<RegistryByteBuf, CellClickPayload> CODEC =
            PacketCodec.of(((payload, buf) -> {
                buf.writeBlockPos(payload.blockEntityPos);
                buf.writeInt(payload.cellX);
                buf.writeInt(payload.cellY);
                buf.writeInt(payload.playerRole);
            }), buf ->new CellClickPayload(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readInt()));

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload payload, ServerPlayNetworking.Context context){
        CellClickPayload cellClickPayload = (CellClickPayload)payload;
        ServerWorld world = context.player().getServerWorld();
        BlockEntity be = world.getBlockEntity(cellClickPayload.blockEntityPos);
        if(be instanceof ChessBoardBlockEntity blockEntity && blockEntity.currentState.equals(blockEntity.states.get(ChessBoardBlockEntity.GAME_STATE))){
            ChessGameState state = (ChessGameState)blockEntity.currentState;
            state.clickOnCell(cellClickPayload.cellX, cellClickPayload.cellY, cellClickPayload.playerRole);
        }
    }
}
