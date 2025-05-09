package net.fieldb0y.wanna_play_chess.chess.renderingStates;

public class RenderingProperties {
    public boolean renderInGameBackground;
    public boolean renderScreenTitle;

    public RenderingProperties(boolean renderInGameBackground, boolean renderScreenTitle){
        this.renderInGameBackground = renderInGameBackground;
        this.renderScreenTitle = renderScreenTitle;
    }

    public static RenderingProperties createDefault(){
        return new RenderingProperties(true, true);
    }
}
