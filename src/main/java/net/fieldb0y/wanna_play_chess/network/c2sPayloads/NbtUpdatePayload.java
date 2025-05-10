package net.fieldb0y.wanna_play_chess.network.c2sPayloads;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record NbtUpdatePayload(BlockPos blockEntityPos) implements CustomPayload {
    public static final Id<NbtUpdatePayload> ID = new Id<>(Identifier.of(WannaPlayChess.MOD_ID, "nbt_update"));
    public static final PacketCodec<RegistryByteBuf, NbtUpdatePayload> CODEC = PacketCodec.of((payload, buf) -> buf.writeBlockPos(payload.blockEntityPos), buf -> new NbtUpdatePayload(buf.readBlockPos()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload payload, ServerPlayNetworking.Context context){
        NbtUpdatePayload nbtUpdatePayload = (NbtUpdatePayload)payload;
        World world = context.player().getServerWorld();
        if (world.getBlockEntity(nbtUpdatePayload.blockEntityPos) instanceof ChessBoardBlockEntity blockEntity){
            blockEntity.updateClient();
        }
    }
}
