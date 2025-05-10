package net.fieldb0y.wanna_play_chess.chess.gameStates;

import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.utils.GameState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.List;

public abstract class ChessState {
    protected List<GameState> states;
    protected ChessBoardBlockEntity blockEntity;

    public ChessState(ChessBoardBlockEntity blockEntity, List<GameState> states){
        this.states = states;
        this.blockEntity = blockEntity;
    }

    public void tick(){

    }

    public boolean shouldUse(){
        return this.states.contains(blockEntity.getGameState());
    }

    public void writeNbt(NbtCompound nbt){}

    public void readNbt(NbtCompound nbt){}

    public void updateClientAndServer(){
        this.blockEntity.updateClientAndServer();
    }

    public void updateClientOnly(){
        this.blockEntity.updateClientOnly();
    }

    public abstract void afterSwitch();
    public abstract void clear();

    public World getWorld(){
        return blockEntity.getWorld();
    }

    public ChessBoardBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
