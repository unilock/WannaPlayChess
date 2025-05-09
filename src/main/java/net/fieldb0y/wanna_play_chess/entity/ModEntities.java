package net.fieldb0y.wanna_play_chess.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.entity.custom.PlayerCopyEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<PlayerCopyEntity> PLAYER_COPY = Registry.register(Registries.ENTITY_TYPE, Identifier.of(WannaPlayChess.MOD_ID, "player_copy"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, PlayerCopyEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.9f)).build());

    public static void register(){}
}
