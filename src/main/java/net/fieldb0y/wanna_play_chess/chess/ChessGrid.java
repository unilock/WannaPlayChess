package net.fieldb0y.wanna_play_chess.chess;

import net.fieldb0y.wanna_play_chess.block.custom.ChessBoardBlock;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.utils.ChessPieces;
import net.fieldb0y.wanna_play_chess.block.entity.renderer.PieceAnimator;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.fieldb0y.wanna_play_chess.chess.utils.PieceAction;
import net.fieldb0y.wanna_play_chess.item.custom.ChessPiece;
import net.fieldb0y.wanna_play_chess.sound.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.joml.Math;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.*;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.*;

public class ChessGrid {
    public static final int SIZE = 8;

    public ChessBoardBlockEntity blockEntity;
    public int[][] data;
    private Direction direction;

    public PieceAnimator pieceAnimator;

    public ChessGrid(ChessBoardBlockEntity blockEntity){
        this.data = new int[SIZE][SIZE];
        this.blockEntity = blockEntity;
        this.direction = blockEntity.getCachedState().get(ChessBoardBlock.FACING);
        this.pieceAnimator = new PieceAnimator(this);
    }

    public void genStartingPosition(){
        for (int i = 0; i < SIZE; i++){
            putPiece(ChessPieces.WHITE_PAWN.id, i, 6, false);
        }
        putPiece(ChessPieces.WHITE_ROOK.id, 0, 7, false);
        putPiece(ChessPieces.WHITE_KNIGHT.id, 1, 7, false);
        putPiece(ChessPieces.WHITE_BISHOP.id, 2, 7, false);
        putPiece(ChessPieces.WHITE_QUEEN.id, 3, 7, false);
        putPiece(ChessPieces.WHITE_KING.id, 4, 7, false);
        putPiece(ChessPieces.WHITE_BISHOP.id, 5, 7, false);
        putPiece(ChessPieces.WHITE_KNIGHT.id, 6, 7, false);
        putPiece(ChessPieces.WHITE_ROOK.id, 7, 7, false);


        for (int i = 0; i < SIZE; i++){
            putPiece(ChessPieces.BLACK_PAWN.id, i, 1, false);
        }
        putPiece(ChessPieces.BLACK_ROOK.id, 0, 0, false);
        putPiece(ChessPieces.BLACK_KNIGHT.id, 1, 0, false);
        putPiece(ChessPieces.BLACK_BISHOP.id, 2, 0, false);
        putPiece(ChessPieces.BLACK_QUEEN.id, 3, 0, false);
        putPiece(ChessPieces.BLACK_KING.id, 4, 0, false);
        putPiece(ChessPieces.BLACK_BISHOP.id, 5, 0, false);
        putPiece(ChessPieces.BLACK_KNIGHT.id, 6, 0, false);
        putPiece(ChessPieces.BLACK_ROOK.id, 7, 0, false);

        blockEntity.updateClient();
    }

    public void clearGrid(){
        this.data = new int[SIZE][SIZE];
        this.direction = blockEntity.getCachedState().get(ChessBoardBlock.FACING);
        this.pieceAnimator = new PieceAnimator(this);
        blockEntity.updateClient();
    }

    public void writeNbt(NbtCompound nbt){
        NbtList grid = new NbtList();
        for (int[] row : data) {
            grid.add(new NbtIntArray(row));
        }
        nbt.put("GridData", grid);

        nbt.putFloat("Direction", direction.asRotation());

        NbtCompound animData = new NbtCompound();
        pieceAnimator.writeNbt(animData);
        nbt.put("AnimData", animData);
    }

    public void readNbt(NbtCompound nbt){
        if (nbt.contains("GridData")) {
            NbtList grid = (NbtList)nbt.get("GridData");

            for (int i = 0; i < SIZE; i++) {
                if (i < grid.size()) {
                    int[] savedRow = ((NbtIntArray) grid.get(i)).getIntArray();
                    System.arraycopy(savedRow, 0, data[i], 0, Math.min(savedRow.length, SIZE));
                }
            }
        }

        if (nbt.contains("Direction")){
            this.direction = Direction.fromRotation(nbt.getFloat("Direction"));
        }

        if (nbt.contains("AnimData")) {
            NbtCompound animData = nbt.getCompound("AnimData");
            pieceAnimator.readNbt(animData);
        }
    }

    public void putPiece(int piece, int x, int y){
        data[x][y] = piece;
        blockEntity.updateClient();
    }

    public void putPiece(int piece, int x, int y, boolean updateClient){
        data[x][y] = piece;
        if (updateClient)
            blockEntity.updateClient();
    }

    public void playPieceMovingAnimation(int fromX, int fromY, int toX, int toY){
        pieceAnimator.playAnimation(new Vector2i(fromX, fromY), new Vector2i(toX, toY));
    }

    public void movePiece(int fromX, int fromY, int toX, int toY){
        int pieceId = safeGetPieceId(fromX, fromY);
        if (pieceId != -1 && pieceId != ChessPieces.EMPTY.id){
            playPieceMovingAnimation(fromX, fromY, toX, toY);
            data[toX][toY] = pieceId;
            data[fromX][fromY] = ChessPieces.EMPTY.id;
            blockEntity.updateClient();
        }
    }

    public void removePiece(int x, int y){
        data[x][y] = ChessPieces.EMPTY.id;
    }

    public Map<PieceAction, List<Vector2i>> getPossibleMoves(int x, int y){
        int pieceId = data[x][y];
        if (pieceId == ChessPieces.EMPTY.id) return Map.of();

        ChessPiece piece = getPieceById(pieceId);
        int role = getPieceRole(pieceId);

        return piece.getPossibleMoves(x, y, role, this);
    }

    public boolean isCellUnderAttack(int x, int y, int role){
        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                int pieceId = safeGetPieceId(i, j);
                if (pieceId != ChessPieces.EMPTY.id && pieceId != -1){
                    if (getPieceRole(pieceId) == role) continue;
                    ChessPiece piece = ChessGrid.getPieceById(pieceId);
                    Map<PieceAction, List<Vector2i>> possibleMoves = piece.getPossibleMoves(i, j, ChessGrid.getPieceRole(pieceId), this);
                    for (PieceAction action : possibleMoves.keySet()){
                        if (pieceId == ChessPieces.WHITE_PAWN.id || pieceId == ChessPieces.BLACK_PAWN.id){
                            if (action == PieceAction.MOVE) continue;
                        }
                        if (possibleMoves.get(action).contains(new Vector2i(x, y)))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCellInArea(Vector2i cell, Vector2i areaCenter, int areaRadius){
        int areaStartX = areaCenter.x - areaRadius;
        int areaStartY = areaCenter.y - areaRadius;
        int areaDiameter = areaRadius * 2 + 1;

        for (int i = 0; i < areaDiameter; i++){
            for (int j = 0; j < areaDiameter; j++){
                int x = areaStartX + i;
                int y = areaStartY + j;
                int pieceId = safeGetPieceId(x, y);
                if (pieceId != -1 && cell.equals(x, y))
                    return true;
            }
        }
        return false;
    }

    public List<Vector2i> getPiecesCellsByRole(int role){
        List<Vector2i> cells = new ArrayList<>();
        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                int pieceId = safeGetPieceId(i, j);
                if (ChessGrid.getPieceRole(pieceId) == role)
                    cells.add(new Vector2i(i, j));
            }
        }
        return cells;
    }

    public static ChessPiece getPieceById(int id){
        return Arrays.stream(ChessPieces.values()).filter((value)->value.id == id).toList().getFirst().item;
    }

    public boolean isAnyOfPiecesOnBoard(List<Integer> pieces){
        for (int pieceId : pieces){
            if (!findPiecePos(pieceId).equals(-1, -1)){
                return true;
            }
        }
        return false;
    }

    public int safeGetPieceId(int x, int y){
        if (x >= SIZE || y >= SIZE || x < 0 || y < 0) return -1;
        return data[x][y];
    }

    public static int getPieceRole(int pieceId){
        return pieceId > 0 && pieceId <= 6 ? WHITE : pieceId != 0 ? BLACK : -1;
    }

    public void rotateToPlayer(UUID[] playersUUIDs){
        World world = blockEntity.getWorld();
        PlayerEntity whitePlayer = world.getPlayerByUuid(playersUUIDs[WHITE]);
        PlayerEntity blackPlayer = world.getPlayerByUuid(playersUUIDs[BLACK]);

        int orientationPlayer = whitePlayer != null ? WHITE : blackPlayer != null ? BLACK : -1;

        if (orientationPlayer == WHITE){
            this.direction = getDirectionToPlayer(whitePlayer).getOpposite();
        } else if (orientationPlayer == BLACK){
            this.direction = getDirectionToPlayer(blackPlayer);
        }
        blockEntity.updateClient();
    }

    public void tick(){
        pieceAnimator.getProgress();
    }

    private Direction getDirectionToPlayer(PlayerEntity player){
        Vector3f playerOffset = new Vector3f(blockEntity.getPos().getX() - player.getBlockX(), blockEntity.getPos().getY() - player.getBlockY(), blockEntity.getPos().getZ() - player.getBlockZ());
        Direction directionToPlayer = Direction.NORTH;

        if (maxComponentXZ(playerOffset) == 0){
            directionToPlayer = playerOffset.x > 0 ? Direction.WEST : Direction.EAST;
        } else if (maxComponentXZ(playerOffset) == 1) {
            directionToPlayer = playerOffset.z > 0 ? Direction.NORTH : Direction.SOUTH;
        }
        return directionToPlayer;
    }

    public int maxComponentXZ(Vector3f vector3f) {
        float absX = Math.abs(vector3f.x);
        float absZ = Math.abs(vector3f.z);
        if (absX >= absZ) {
            return 0;
        }
        return 1;
    }

    public List<Vector2i> getCheckingPiecesCells(){
        List<Vector2i> checkingPiecesCells = new ArrayList<>();
        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                if (getGameState().piecesData.hasDataTag(i, j, PiecesData.DataTag.GIVES_CHECK))
                    checkingPiecesCells.add(new Vector2i(i, j));
            }
        }
        return checkingPiecesCells;
    }

    public Vector2i findPiecePos(int pieceId){
        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                int id = safeGetPieceId(i, j);
                if (id == pieceId)
                    return new Vector2i(i, j);
            }
        }
        return new Vector2i(-1, -1);
    }

    public List<Vector2i> findPiecesPos(int pieceId){
        List<Vector2i> positions = new ArrayList<>();

        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                int id = safeGetPieceId(i, j);
                if (id == pieceId)
                    positions.add(new Vector2i(i, j));
            }
        }
        return positions;
    }

    public ChessPiece getPieceAt(int x, int y){
        return ChessGrid.getPieceById(safeGetPieceId(x, y));
    }

    public ChessGameState getGameState(){
        if (blockEntity.currentState == blockEntity.states.get(ChessBoardBlockEntity.GAME_STATE)){
            return (ChessGameState)blockEntity.currentState;
        }
        return null;
    }

    public Direction getDirection() {
        return direction;
    }

    public void turnPawnInto(Vector2i cell, int turnToId) {
        int pieceId = safeGetPieceId(cell.x, cell.y);
        if (pieceId == ChessPieces.WHITE_PAWN.id || pieceId == ChessPieces.BLACK_PAWN.id){
            putPiece(turnToId, cell.x, cell.y);
            ((ChessGameState)blockEntity.currentState).playSound(ModSounds.PROMOTE);
        }
    }

    @Override
    public String toString() {
        return Arrays.deepToString(data);
    }
}
