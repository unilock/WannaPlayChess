package net.fieldb0y.wanna_play_chess.utils;

public enum GameState {
     NOT_READY_TO_PLAY(0), READY_FOR_SINGLEPLAYER_GAME(1), READY_TO_PLAY(2), PLAYING(3), GAME_OVER(4);

     public final int nbtValue;

     GameState(int nbtValue){
          this.nbtValue = nbtValue;
     }
}
