package net.fieldb0y.wanna_play_chess.mixin;

import net.fieldb0y.wanna_play_chess.CameraAnimationPlayable;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.*;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraAnimationPlayable {
    @Unique private boolean isAnimationPlaying = false;
    @Unique private boolean focusOnBlockEntity = false;

    @Unique private BlockPos blockEntityPos;

    @Unique private Vec3d startPos;
    @Unique private float startYaw;
    @Unique private float startPitch;
    @Unique private Vec3d targetPos;
    @Unique private float targetYaw;
    @Unique private float targetPitch;

    @Shadow private Vec3d pos;
    @Shadow private float yaw;
    @Shadow private float pitch;

    @Unique private Direction gridDirection;
    @Unique private int playerRole;

    @Unique private float animationProgress = 0.0f;
    @Unique private float animationDuration = 0.5f;
    @Unique private long lastUpdateTime = 0;

    @Shadow protected abstract void setPos(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"), cancellable = true)
    private void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (focusOnBlockEntity){
            setPos(targetPos.x, targetPos.y, targetPos.z);
            setRotation(targetYaw, targetPitch);
            ci.cancel();
        }
        if (isAnimationPlaying) {
            updateCameraPosition();
            checkAnimationCompletion();
            ci.cancel();
        }
    }

    @Unique
    private void updateCameraPosition() {
        long currentTime = System.nanoTime();
        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
        }
        float deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0f;
        animationProgress += deltaTime;
        lastUpdateTime = currentTime;

        float progress = Math.min(animationProgress / animationDuration, 1.0f);
        float smoothProgress = smoothStep(progress);

        Vec3d newPos = startPos.lerp(targetPos, smoothProgress);
        float newPitch = startPitch + (targetPitch - startPitch) * smoothProgress;
        float newYaw = lerpAngle(startYaw, targetYaw, smoothProgress);

        this.setPos(newPos.x, newPos.y, newPos.z);
        this.setRotation(newYaw, newPitch);
    }


    @Unique
    private void initializeAnimation(){
        startPos = this.pos;
        startYaw = MathHelper.wrapDegrees(this.yaw);
        startPitch = MathHelper.wrapDegrees(this.pitch);
        targetPos = Vec3d.ofCenter(blockEntityPos).add(calculateOffset());
        calculateTargetRotation();
    }

    @Unique
    private void calculateTargetRotation(){
        Vec3d direction = Vec3d.ofCenter(blockEntityPos).subtract(targetPos).normalize();
        double dx = direction.x;
        double dy = direction.y;
        double dz = direction.z;

        float baseYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) + calculateYawOffset();
        float directionOffset = playerRole == WHITE ? gridDirection.getOpposite().asRotation() : gridDirection.asRotation();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        targetYaw = MathHelper.wrapDegrees(baseYaw + directionOffset);
        targetPitch = MathHelper.clamp((float) Math.toDegrees(Math.atan2(-dy, horizontalDistance) * 1.5f), -90f, 90f);
    }

    @Unique
    private float calculateYawOffset(){
        float offset;
        if (playerRole == WHITE){
            offset = gridDirection.equals(Direction.EAST) ? 180 : gridDirection.equals(Direction.SOUTH) ? 90f : gridDirection.equals(Direction.NORTH) ? -90 : 0;
        } else {
            offset = gridDirection.equals(Direction.EAST) ? 0 : gridDirection.equals(Direction.SOUTH) ? -90f : gridDirection.equals(Direction.NORTH) ? 90 : -180;
        }
        return offset;
    }

    @Unique
    private Vec3d calculateOffset(){
        float x;
        float y = 0.3f;
        float z;
        if (playerRole == WHITE){
            x = gridDirection == Direction.EAST ? -0.3f : gridDirection == Direction.WEST ? 0.3f : 0f;
            z = gridDirection == Direction.NORTH ? 0.3f : gridDirection == Direction.SOUTH ? -0.3f : 0f;
        } else {
            x = gridDirection == Direction.EAST ? 0.3f : gridDirection == Direction.WEST ? -0.3f : 0f;
            z = gridDirection == Direction.NORTH ? -0.3f : gridDirection == Direction.SOUTH ? 0.3f : 0f;
        }
        return new Vec3d(x, y, z);
    }

    @Unique
    private float lerpAngle(float start, float end, float progress) {
        start = MathHelper.wrapDegrees(start);
        end = MathHelper.wrapDegrees(end);
        float difference = end - start;
        if (difference > 180.0F) {
            difference -= 360.0F;
        } else if (difference < -180.0F) {
            difference += 360.0F;
        }
        return MathHelper.wrapDegrees(start + difference * progress);
    }

    @Unique
    private float smoothStep(float t) {
        return t * t * (3.0f - 2.0f * t);
    }

    @Unique
    private void checkAnimationCompletion() {
        if (animationProgress >= animationDuration) {
            isAnimationPlaying = false;
            animationProgress = 0.0f;
            lastUpdateTime = 0;
            this.focusOnBlockEntity = true;
        }
    }

    @Unique
    public void playAnimation(ChessBoardBlockEntity blockEntity) {
        this.blockEntityPos = blockEntity.getPos();
        this.isAnimationPlaying = true;
        this.animationProgress = 0.0f;
        this.lastUpdateTime = System.nanoTime();
        initializeAnimation();
    }


    @Override
    public void focusOnBlockEntity(ChessBoardBlockEntity blockEntity, PlayerEntity player) {
        ChessGameState chessGameState = (ChessGameState)blockEntity.states.get(ChessBoardBlockEntity.GAME_STATE);
        ChessGrid chessGrid = chessGameState.getGrid();
        this.playerRole = chessGameState.getPlayers()[WHITE].compareTo(player.getUuid()) == 0 ? WHITE : BLACK;
        this.gridDirection = chessGrid.getDirection();
        playAnimation(blockEntity);
    }

    @Override
    public void focusOnBlockEntity(ChessBoardBlockEntity blockEntity, int playerRole) {
        ChessGameState chessGameState = (ChessGameState)blockEntity.states.get(ChessBoardBlockEntity.GAME_STATE);
        ChessGrid chessGrid = chessGameState.getGrid();
        this.playerRole = playerRole;
        this.gridDirection = chessGrid.getDirection();
        playAnimation(blockEntity);
    }

    @Override
    public void stopFocusing() {
        this.focusOnBlockEntity = false;
        this.isAnimationPlaying = false;
    }

    @Override
    public boolean isCameraFocused() {
        return focusOnBlockEntity;
    }
}
