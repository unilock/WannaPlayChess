package net.fieldb0y.wanna_play_chess.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ModComponents {
    public static final ComponentType<Map<Integer, Integer>> PIECES_IN_BOX_COMPONENT = register("pieces_in_box", builder -> builder.codec(
            Codec.unboundedMap(Codec.STRING, Codec.INT)
                            .xmap(map -> map.entrySet().stream()
                                            .collect(Collectors.toMap(e -> Integer.parseInt(e.getKey()), Map.Entry::getValue)),
                                    map -> map.entrySet().stream()
                                            .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)))));

    public static final ComponentType<List<Boolean>> INSERTED_PIECES_SETS = register("inserted_pieces_sets", builder -> builder.codec(Codec.BOOL.listOf()).packetCodec(PacketCodecs.codec(Codec.BOOL.listOf())));

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator){
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(WannaPlayChess.MOD_ID, name), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void register(){}
}
