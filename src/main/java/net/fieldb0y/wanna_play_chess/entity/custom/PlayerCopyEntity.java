package net.fieldb0y.wanna_play_chess.entity.custom;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PlayerCopyEntity extends LivingEntity {
    private Identifier TEXTURE;

    public final AnimationState idleAnimationState = new AnimationState();

    private float clientAnimationProgress;

    public PlayerCopyEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.clientAnimationProgress = 0;
    }

    public void setSkin(PlayerEntity player){
        if (player != null){
            AbstractClientPlayerEntity clientPlayer = (AbstractClientPlayerEntity)player;
            TEXTURE = clientPlayer.getSkinTextures().texture();
        } else TEXTURE = null;
    }

    public Identifier getSkin() {
        return TEXTURE;
    }

    public static DefaultAttributeContainer.Builder createAttributes(){
        return MobEntity.createMobAttributes();
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    public void tickClient(){
        this.clientAnimationProgress += 0.1f;
    }

    public float getClientAnimationProgress() {
        return clientAnimationProgress;
    }
}
