package net.fieldb0y.wanna_play_chess.block.entity.renderer;

import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.minecraft.nbt.NbtCompound;
import org.joml.Vector2i;

public class PieceAnimator {
    public ChessGrid grid;

    public boolean isAnimationPlaying = false;
    public Vector2i fromCell = new Vector2i(-1, -1);
    public Vector2i toCell = new Vector2i(-1, -1);
    private long animationStartTime;
    private static final long ANIMATION_DURATION = 100;

    public PieceAnimator(ChessGrid grid){
        this.grid = grid;
    }

    public void playAnimation(Vector2i fromCell, Vector2i toCell) {
        this.fromCell = fromCell;
        this.toCell = toCell;
        this.isAnimationPlaying = true;
        this.animationStartTime = System.currentTimeMillis();
    }

    public float getProgress() {
        if (!isAnimationPlaying) return 1.0f;
        long elapsed = System.currentTimeMillis() - animationStartTime;
        float progress = (float) elapsed / ANIMATION_DURATION;
        if (progress >= 1.0f) {
            stopAnimation();
            return 1.0f;
        }
        return progress;
    }

    public void stopAnimation() {
        isAnimationPlaying = false;
        grid.blockEntity.updateClient();
    }

    public void readNbt(NbtCompound nbt) {
        isAnimationPlaying = nbt.getBoolean("IsAnimPlaying");
        fromCell = new Vector2i(nbt.getIntArray("FromCell"));
        toCell = new Vector2i(nbt.getIntArray("ToCell"));
        if (isAnimationPlaying) {
            animationStartTime = System.currentTimeMillis();
        }
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("IsAnimPlaying", isAnimationPlaying);
        nbt.putIntArray("FromCell", new int[]{fromCell.x, fromCell.y});
        nbt.putIntArray("ToCell", new int[]{toCell.x, toCell.y});
    }
}
