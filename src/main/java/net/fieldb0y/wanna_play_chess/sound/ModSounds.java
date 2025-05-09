package net.fieldb0y.wanna_play_chess.sound;

import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent MOVE_SELF = registerSound("move_self");
    public static final SoundEvent MOVE_CHECK = registerSound("move_check");
    public static final SoundEvent CAPTURE = registerSound("capture");
    public static final SoundEvent CASTLE = registerSound("castle");
    public static final SoundEvent PROMOTE = registerSound("promote");
    public static final SoundEvent NOTIFY = registerSound("notify");

    private static SoundEvent registerSound(String name){
        return Registry.register(Registries.SOUND_EVENT, Identifier.of(WannaPlayChess.MOD_ID, name), SoundEvent.of(Identifier.of(WannaPlayChess.MOD_ID, name)));
    }

    public static void register(){}
}
