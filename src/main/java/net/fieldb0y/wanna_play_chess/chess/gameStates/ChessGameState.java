package net.fieldb0y.wanna_play_chess.chess.gameStates;

import net.fieldb0y.wanna_play_chess.CameraAnimationPlayable;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.fieldb0y.wanna_play_chess.chess.PiecesData;
import net.fieldb0y.wanna_play_chess.chess.utils.ChessGameOverReason;
import net.fieldb0y.wanna_play_chess.chess.utils.ChessPieces;
import net.fieldb0y.wanna_play_chess.chess.utils.PieceAction;
import net.fieldb0y.wanna_play_chess.item.custom.ChessPiece;
import net.fieldb0y.wanna_play_chess.sound.ModSounds;
import net.fieldb0y.wanna_play_chess.utils.GameState;
import net.fieldb0y.wanna_play_chess.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.joml.Vector2i;
import net.fieldb0y.wanna_play_chess.utils.Timer;

import java.util.*;

import static net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessLobbyRenderingState.FIRST;
import static net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessLobbyRenderingState.SECOND;
import static net.fieldb0y.wanna_play_chess.chess.utils.ChessPieces.*;

public class ChessGameState extends ChessState {
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    private UUID[] players = new UUID[2];
    private Map<Integer, List<Integer>> playersTakenPieces = new HashMap<>();
    private ChessGrid grid;
    public PiecesData piecesData;

    public Timer[] gameTimers;
    public int whiteTimeLeft = 0, blackTimeLeft = 0;

    public int currentChosenPiece = EMPTY.id;
    public Vector2i currentChosenCell = new Vector2i(-1, -1);

    public int currentTurn = 0;
    public boolean isSingleplayer = false;
    private ChessGameOverReason gameOverReason = ChessGameOverReason.NONE;
    private int winnerRole = -1;

    public int drawOfferRole = -1, resignOfferRole = -1;

    public ChessGameState(ChessBoardBlockEntity blockEntity) {
        super(blockEntity, List.of(GameState.PLAYING));
        grid = new ChessGrid(blockEntity);
        piecesData = new PiecesData(this);
        gameTimers = new Timer[]{new Timer(this::whiteTimerTick, 1), new Timer(this::blackTimerTick, 1)};

        if (players.length >= 2){
            gameTimers[currentTurn].start();
        }
    }

    public void startGame(UUID[] playersInLobby, int gameTime, boolean noTimeControl, int firstPlayerRole, boolean isSingleplayer){
        Random random = new Random();
        this.isSingleplayer = isSingleplayer;

        int whiteId = firstPlayerRole == 2 ? random.nextInt(0, 1) : firstPlayerRole;
        if (isSingleplayer){
            UUID playerUUID = playersInLobby[playersInLobby[FIRST] == null ? SECOND : FIRST];
            players[WHITE] = playerUUID;
            players[BLACK] = playerUUID;
        } else {
            players[WHITE] = playersInLobby[whiteId];
            players[BLACK] = playersInLobby[whiteId == 0 ? 1 : 0];
        }
        grid.rotateToPlayer(players);
        grid.genStartingPosition();

        this.whiteTimeLeft = noTimeControl ? -1 : gameTime;
        this.blackTimeLeft = noTimeControl ? -1 : gameTime;

        gameTimers = new Timer[]{new Timer(this::whiteTimerTick, 1), new Timer(this::blackTimerTick, 1)};
        updateClient();

        if (blockEntity.getWorld().isClient()) {
            MinecraftClient client = MinecraftClient.getInstance();
            Camera camera = client.gameRenderer.getCamera();

            int role = client.player.getUuid().compareTo(players[WHITE]) == 0 ? WHITE : client.player.getUuid().compareTo(players[BLACK]) == 0 ? BLACK : -1;
            if (role != -1)
                ((CameraAnimationPlayable) camera).focusOnBlockEntity(blockEntity, role);
        }
    }

    public void clickOnCell(int cellX, int cellY, int playerRole){
        if (!findPieceCellWithTag(PiecesData.DataTag.SHOULD_TURN).equals(-1, -1)) return;
        if(resignOfferRole != -1 || drawOfferRole != -1) return;

        if (currentTurn == playerRole) {
            Vector2i prevChosenCell = new Vector2i(currentChosenCell.x, currentChosenCell.y);
            currentChosenCell = new Vector2i(cellX, cellY);
            int id = grid.safeGetPieceId(cellX, cellY);
            if (id != -1 && id != EMPTY.id && ChessGrid.getPieceRole(id) == playerRole){
                currentChosenPiece = grid.safeGetPieceId(cellX, cellY);
            } else if (currentChosenPiece != EMPTY.id && ChessGrid.getPieceRole(currentChosenPiece) == playerRole) {
                ChessPiece piece = ChessGrid.getPieceById(currentChosenPiece);
                Map<PieceAction, List<Vector2i>> possibleMoves = piece.getPossibleMoves(prevChosenCell.x, prevChosenCell.y, playerRole, grid);

                for (PieceAction action : possibleMoves.keySet()){
                    for (Vector2i move : possibleMoves.get(action)) {
                        if (move.equals(cellX, cellY)) {
                            if (grid.safeGetPieceId(cellX, cellY) != WHITE_KING.id && grid.safeGetPieceId(cellX, cellY) != BLACK_KING.id && action != PieceAction.NONE && action != PieceAction.OTHER){
                                if (action == PieceAction.TAKE) {
                                    if (playersTakenPieces.containsKey(playerRole)){
                                        playersTakenPieces.get(playerRole).add(grid.safeGetPieceId(cellX, cellY));
                                    } else playersTakenPieces.put(playerRole, new ArrayList<>(List.of(grid.safeGetPieceId(cellX, cellY))));
                                    playSound(ModSounds.CAPTURE);
                                } else if(action == PieceAction.EN_PASSANT){
                                    if (playersTakenPieces.containsKey(playerRole)){
                                        playersTakenPieces.get(playerRole).add(playerRole == WHITE ? BLACK_PAWN.id : WHITE_PAWN.id);
                                    } else playersTakenPieces.put(playerRole, new ArrayList<>(List.of(playerRole == WHITE ? BLACK_PAWN.id : WHITE_PAWN.id)));
                                    grid.removePiece(cellX, cellY + (playerRole == WHITE ? 1 : -1));
                                    playSound(ModSounds.CAPTURE);
                                } else if (action == PieceAction.CASTLE){
                                    int pieceRole = ChessGrid.getPieceRole(grid.safeGetPieceId(prevChosenCell.x, prevChosenCell.y));
                                    int kingY = pieceRole == WHITE ? 7 : 0;
                                    Vector2i scrPos = new Vector2i(7, kingY);
                                    Vector2i lcrPos = new Vector2i(0, kingY);
                                    if (cellX > 4)
                                        grid.movePiece(scrPos.x, scrPos.y, scrPos.x - 2, scrPos.y);
                                    else grid.movePiece(lcrPos.x, lcrPos.y, lcrPos.x + 3, lcrPos.y);
                                    playSound(ModSounds.CASTLE);
                                }
                                if (action == PieceAction.MOVE) playSound(ModSounds.MOVE_SELF);
                                updatePremoveTags();
                                ChessGrid.getPieceById(grid.safeGetPieceId(prevChosenCell.x, prevChosenCell.y)).updatePieceData(new Vector2i(prevChosenCell.x, prevChosenCell.y), new Vector2i(cellX, cellY), piecesData);
                                grid.movePiece(prevChosenCell.x, prevChosenCell.y, cellX, cellY);
                                if (currentChosenPiece == WHITE_KING.id || currentChosenPiece == BLACK_KING.id || currentChosenPiece == WHITE_ROOK.id || currentChosenPiece == BLACK_ROOK.id){
                                    piecesData.removeData(prevChosenCell.x, prevChosenCell.y, PiecesData.DataTag.ALREADY_MOVED);
                                    piecesData.putData(cellX, cellY, List.of(PiecesData.DataTag.ALREADY_MOVED));
                                }
                                updateAftermoveTags();
                                if (!piecesData.hasDataTag(currentChosenCell.x, currentChosenCell.y, PiecesData.DataTag.SHOULD_TURN)){
                                    checkForGameEnd();
                                    nextTurn();
                                }
                                this.currentChosenCell = new Vector2i(-1, -1);
                                this.currentChosenPiece = EMPTY.id;
                            }
                        } else {
                            if (ChessGrid.getPieceRole(id) != playerRole)
                                this.currentChosenPiece = EMPTY.id;
                        }
                    }
                }
            }
        }
        updateClient();
    }

    private void updatePremoveTags(){
        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                if (piecesData.hasDataTag(i, j, PiecesData.DataTag.ONE_TURN))
                    piecesData.clearData(i, j);
            }
        }
    }

    public void updateAftermoveTags(){
        updateDataForKings();
        updateCantMoveTag();
        updateShouldTurnTag();
    }

    public void checkForGameEnd(){
        if (getCheckedKing() != -1){
            Vector2i cell = grid.findPiecePos(getCheckedKing());
            if (isKingStalemated(cell))
                gameOver(ChessGameOverReason.CHECKMATE, getCheckedKingRole() == WHITE ? BLACK : WHITE);
        } else {
            Vector2i whiteKingPos = grid.findPiecePos(WHITE_KING.id);
            Vector2i blackPiecePos = grid.findPiecePos(BLACK_KING.id);

            if(isKingStalemated(whiteKingPos)) gameOver(ChessGameOverReason.STALEMATE, -1);
            if (isKingStalemated(blackPiecePos)) gameOver(ChessGameOverReason.STALEMATE, -1);
            if(!isPossibleToWin(whiteKingPos, blackPiecePos)) gameOver(ChessGameOverReason.IMPOSSIBLE_TO_WIN, -1);
        }
    }

    private boolean isPossibleToWin(Vector2i whiteKingPos, Vector2i blackKingPos){
        if (whiteKingPos.equals(-1, -1) || blackKingPos.equals(-1, -1)) return true;
        List<Vector2i > whitePiecesCells = grid.getPiecesCellsByRole(WHITE);
        List<Vector2i > blackPiecesCells = grid.getPiecesCellsByRole(BLACK);

        if (whitePiecesCells.size() == 1){
            if (blackPiecesCells.size() == 1) return false;
            if (getGrid().isAnyOfPiecesOnBoard(getAllIdsWithRoleExcept(BLACK, List.of(BLACK_KNIGHT.id, BLACK_BISHOP.id, BLACK_KING.id)))) return true;
            if (blackPiecesCells.size() > 3) return true;

            if (blackPiecesCells.size() == 2) return false;
            else return !grid.findPiecesPos(BLACK_BISHOP.id).isEmpty();
        }

        if (blackPiecesCells.size() == 1){
            if (getGrid().isAnyOfPiecesOnBoard(getAllIdsWithRoleExcept(WHITE, List.of(WHITE_KNIGHT.id, WHITE_BISHOP.id, WHITE_KING.id)))) return true;
            if (whitePiecesCells.size() > 3) return true;

            if (whitePiecesCells.size() == 2) return false;
            else return !grid.findPiecesPos(WHITE_BISHOP.id).isEmpty();
        }
        return true;
    }

    private boolean isKingStalemated(Vector2i kingPos){
        if (kingPos.equals(-1, -1)) return false;
        int kingId = grid.safeGetPieceId(kingPos.x, kingPos.y);

        int role = ChessGrid.getPieceRole(kingId);
        ChessPiece kingPiece = ChessGrid.getPieceById(kingId);
        if(!kingPiece.isAbleToMove(kingPos.x, kingPos.y, role, grid)){
            List<Vector2i> piecesCellsOnBoard = grid.getPiecesCellsByRole(role);
            piecesCellsOnBoard.remove(kingPos);
            if (piecesCellsOnBoard.isEmpty()) return true;

            for (Vector2i cell : piecesCellsOnBoard){
                ChessPiece piece = grid.getPieceAt(cell.x, cell.y);
                if (piece.isAbleToMove(cell.x, cell.y, role, grid))
                    return false;
            }
            return true;
        }
        return false;
    }

    public void whiteTimerTick(){
        whiteTimeLeft--;
        if (whiteTimeLeft <= 0)
            gameOver(ChessGameOverReason.TIME_IS_UP, BLACK);
        updateClient();
    }

    public void blackTimerTick(){
        blackTimeLeft--;
        if (blackTimeLeft <= 0)
            gameOver(ChessGameOverReason.TIME_IS_UP, WHITE);
        updateClient();
    }

    private void updateShouldTurnTag(){
        clearTagOnBoard(PiecesData.DataTag.SHOULD_TURN);

        for (Vector2i cell : grid.findPiecesPos(WHITE_PAWN.id)){
            if (cell.y == 0) piecesData.putData(cell.x, cell.y, List.of(PiecesData.DataTag.SHOULD_TURN));
        }
        for (Vector2i cell : grid.findPiecesPos(BLACK_PAWN.id)){
            if (cell.y == 7) piecesData.putData(cell.x, cell.y, List.of(PiecesData.DataTag.SHOULD_TURN));
        }
    }

    public int getCheckedKing(){
        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                int pieceId = grid.safeGetPieceId(i, j);
                if (pieceId == WHITE_KING.id || pieceId == BLACK_KING.id){
                    if (piecesData.hasDataTag(i, j, PiecesData.DataTag.CHECKED))
                        return pieceId;
                }
            }
        }
        return -1;
    }

    public int getCheckedKingRole(){
        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                int pieceId = grid.safeGetPieceId(i, j);
                if (pieceId == WHITE_KING.id || pieceId == BLACK_KING.id){
                    if (piecesData.hasDataTag(i, j, PiecesData.DataTag.CHECKED))
                        return ChessGrid.getPieceRole(pieceId);
                }
            }
        }
        return -1;
    }

    private void updateDataForKings(){
        Vector2i whiteKingPos = grid.findPiecePos(WHITE_KING.id);
        Vector2i blackKingPos = grid.findPiecePos(BLACK_KING.id);

        piecesData.removeData(whiteKingPos.x, whiteKingPos.y, PiecesData.DataTag.CHECKED);
        piecesData.removeData(blackKingPos.x, blackKingPos.y, PiecesData.DataTag.CHECKED);

        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                int pieceId = grid.safeGetPieceId(i, j);
                if (pieceId != -1 && pieceId != EMPTY.id){
                    piecesData.removeData(i, j, PiecesData.DataTag.GIVES_CHECK);

                    ChessPiece piece = ChessGrid.getPieceById(pieceId);
                    List<Vector2i> takeCells = piece.getPossibleMoves(i, j, ChessGrid.getPieceRole(pieceId), grid).get(PieceAction.TAKE);
                    if (takeCells == null) continue;

                    if(!whiteKingPos.equals(-1, -1) && takeCells.contains(whiteKingPos)) {
                        piecesData.putData(whiteKingPos.x, whiteKingPos.y, List.of(PiecesData.DataTag.CHECKED));
                        piecesData.putData(i, j, List.of(PiecesData.DataTag.GIVES_CHECK));
                        playSound(ModSounds.MOVE_CHECK);
                    }
                    if(!blackKingPos.equals(-1, -1) && takeCells.contains(blackKingPos)) {
                        piecesData.putData(blackKingPos.x, blackKingPos.y, List.of(PiecesData.DataTag.CHECKED));
                        piecesData.putData(i, j, List.of(PiecesData.DataTag.GIVES_CHECK));
                        playSound(ModSounds.MOVE_CHECK);
                    }
                }
            }
        }
        updateCastleTags(whiteKingPos, blackKingPos);
        updateClient();
    }

    private void updateCastleTags(Vector2i whiteKingPos, Vector2i blackKingPos){
        List<Vector2i> whiteRooksPositions = grid.findPiecesPos(WHITE_ROOK.id);
        List<Vector2i> blackRooksPositions = grid.findPiecesPos(BLACK_ROOK.id);

        if (blackRooksPositions.isEmpty() || whiteRooksPositions.isEmpty()) return;

        clearTagOnBoard(PiecesData.DataTag.CAN_CASTLE_SHORT);
        clearTagOnBoard(PiecesData.DataTag.CAN_CASTLE_LONG);

        if (whiteKingPos.equals(4, 7) && !piecesData.hasDataTag(whiteKingPos.x, whiteKingPos.y, PiecesData.DataTag.CHECKED) && !piecesData.hasDataTag(whiteKingPos.x, whiteKingPos.y, PiecesData.DataTag.ALREADY_MOVED)){
            Vector2i scRook = whiteRooksPositions.getLast();
            Vector2i lcRook = whiteRooksPositions.getFirst();

            if (lcRook.equals(0, 7) && !piecesData.hasDataTag(lcRook.x, lcRook.y, PiecesData.DataTag.ALREADY_MOVED)
                    && !grid.isCellUnderAttack(whiteKingPos.x - 1, whiteKingPos.y, WHITE) && !grid.isCellUnderAttack(whiteKingPos.x - 2, whiteKingPos.y, WHITE)){
                List<Vector2i> path = new ArrayList<>();
                ChessPiece.addStraightPath(lcRook, whiteKingPos, path, false);
                if (Utils.isPathHasNoPieces(path, grid)) piecesData.putData(whiteKingPos.x, whiteKingPos.y, List.of(PiecesData.DataTag.CAN_CASTLE_SHORT));
            }

            if (scRook.equals(7, 7) && !piecesData.hasDataTag(scRook.x, scRook.y, PiecesData.DataTag.ALREADY_MOVED)
                    && !grid.isCellUnderAttack(whiteKingPos.x + 1, whiteKingPos.y, WHITE) && !grid.isCellUnderAttack(whiteKingPos.x + 2, whiteKingPos.y, WHITE)){
                List<Vector2i> path = new ArrayList<>();
                ChessPiece.addStraightPath(scRook, whiteKingPos, path, false);
                if (Utils.isPathHasNoPieces(path, grid)) piecesData.putData(whiteKingPos.x, whiteKingPos.y, List.of(PiecesData.DataTag.CAN_CASTLE_LONG));
            }
        }

        if (blackKingPos.equals(4, 0) && !piecesData.hasDataTag(blackKingPos.x, blackKingPos.y, PiecesData.DataTag.CHECKED)  && !piecesData.hasDataTag(blackKingPos.x, blackKingPos.y, PiecesData.DataTag.ALREADY_MOVED)){
            Vector2i scRook = blackRooksPositions.getLast();
            Vector2i lcRook = blackRooksPositions.getFirst();

            if (lcRook.equals(0, 0) && !piecesData.hasDataTag(lcRook.x, lcRook.y, PiecesData.DataTag.ALREADY_MOVED)
                    && !grid.isCellUnderAttack(blackKingPos.x - 1, blackKingPos.y, BLACK) && !grid.isCellUnderAttack(blackKingPos.x - 2, blackKingPos.y, BLACK)){
                List<Vector2i> path = new ArrayList<>();
                ChessPiece.addStraightPath(lcRook, blackKingPos, path, false);
                if (Utils.isPathHasNoPieces(path, grid)) piecesData.putData(blackKingPos.x, blackKingPos.y, List.of(PiecesData.DataTag.CAN_CASTLE_SHORT));
            }

            if (scRook.equals(7, 0) && !piecesData.hasDataTag(scRook.x, scRook.y, PiecesData.DataTag.ALREADY_MOVED)
                    && !grid.isCellUnderAttack(blackKingPos.x + 1, blackKingPos.y, BLACK) && !grid.isCellUnderAttack(blackKingPos.x + 2, blackKingPos.y, BLACK)){
                List<Vector2i> path = new ArrayList<>();
                ChessPiece.addStraightPath(scRook, blackKingPos, path, false);
                if (Utils.isPathHasNoPieces(path, grid)) piecesData.putData(blackKingPos.x, blackKingPos.y, List.of(PiecesData.DataTag.CAN_CASTLE_LONG));
            }
        }
    }

    private void updateCantMoveTag(){
        clearTagOnBoard(PiecesData.DataTag.CANT_MOVE);

        Vector2i whiteKingPos = grid.findPiecePos(WHITE_KING.id);
        Vector2i blackKingPos = grid.findPiecePos(BLACK_KING.id);

        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                int pieceId = grid.safeGetPieceId(i, j);
                if (pieceId != WHITE_KING.id && pieceId != BLACK_KING.id && pieceId != EMPTY.id && pieceId != -1) {
                    ChessPiece piece = ChessGrid.getPieceById(pieceId);

                    List<Vector2i> pathToKing = ChessPiece.getAttackPath(ChessGrid.getPieceRole(pieceId) == WHITE ? blackKingPos : whiteKingPos, new Vector2i(i, j), piece, false);
                    int piecesOnPathCount = 0;
                    Vector2i tiedPiece = new Vector2i(-1, -1);
                    if (!pathToKing.isEmpty()){
                        for (Vector2i pathCell : pathToKing){
                            int pieceOnPath = grid.safeGetPieceId(pathCell.x, pathCell.y);
                            if (ChessGrid.getPieceRole(pieceOnPath) != ChessGrid.getPieceRole(pieceId) && pieceOnPath != -1 && pieceOnPath != EMPTY.id){
                                piecesOnPathCount++;
                                if (piecesOnPathCount <= 1) tiedPiece = new Vector2i(pathCell.x, pathCell.y);
                                else tiedPiece = new Vector2i(-1, -1);
                            }
                        }
                    }
                    if (!tiedPiece.equals(-1, -1))
                        piecesData.putData(tiedPiece.x, tiedPiece.y, List.of(PiecesData.DataTag.CANT_MOVE));
                }
            }
        }
    }

    public Vector2i findPieceCellWithTag(PiecesData.DataTag tag){
        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                if (piecesData.hasDataTag(i, j, tag))
                    return new Vector2i(i, j);
            }
        }
        return new Vector2i(-1, -1);
    }

    private void clearTagOnBoard(PiecesData.DataTag tag){
        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                piecesData.removeData(i, j, tag);
            }
        }
    }

    public void nextTurn(){
        currentTurn = currentTurn == WHITE ? BLACK : WHITE;
        updateClient();
    }

    public Map<PieceAction, List<Vector2i>> getPossibleMoves(){
        if (!currentChosenCell.equals(-1, -1)){
            return grid.getPossibleMoves(currentChosenCell.x, currentChosenCell.y);
        }
        return Map.of();
    }

    public void gameOver(ChessGameOverReason gameOverReason, int winnerRole){
        this.gameOverReason = gameOverReason;
        this.winnerRole = winnerRole;
        this.blockEntity.setGameState(GameState.GAME_OVER);
    }

    public void turnPawnInto(Vector2i pawnCell, int toPieceId){
        grid.turnPawnInto(pawnCell, toPieceId);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (int i = 0; i < players.length; i++) {
            UUID uuid = players[i];
            if (uuid != null) {
                list.add(i, NbtHelper.fromUuid(uuid));
            }
        }
        nbt.put("PlayingPlayers", list);
        nbt.putInt("CurrentTurn", currentTurn);
        nbt.putIntArray("CurrentChosenCellXY", List.of(currentChosenCell.x, currentChosenCell.y));
        nbt.putInt("CurrentChosenPiece", currentChosenPiece);

        writeTakenPiecesToNbt(nbt);
        grid.writeNbt(nbt);
        piecesData.saveData(nbt);
        nbt.putIntArray("TimeLeft", List.of(whiteTimeLeft, blackTimeLeft));
        nbt.putInt("DrawOfferRole", drawOfferRole);
        nbt.putInt("ResignOfferRole", resignOfferRole);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("PlayingPlayers")){
            NbtList list = (NbtList) nbt.get("PlayingPlayers");
            for (int i = 0; i < 2; i++) {
                if (i < list.size()){
                    players[i] = NbtHelper.toUuid(list.get(i));
                }
            }
        }

        if (nbt.contains("CurrentChosenCellXY")){
            int[] xy = nbt.getIntArray("CurrentChosenCellXY");
            currentChosenCell.set(xy[0], xy[1]);
        }
        if (nbt.contains("CurrentChosenPiece"))
            this.currentChosenPiece = nbt.getInt("CurrentChosenPiece");

        if (nbt.contains("CurrentTurn")){
            this.currentTurn = nbt.getInt("CurrentTurn");
        }

        readTakenPiecesFromNbt(nbt);
        grid.readNbt(nbt);
        piecesData.readData(nbt);

        if (nbt.contains("TimeLeft")){
            int[] array = nbt.getIntArray("TimeLeft");
            this.whiteTimeLeft = array[WHITE];
            this.blackTimeLeft = array[BLACK];
        }

        if (nbt.contains("DrawOfferRole"))
            this.drawOfferRole = nbt.getInt("DrawOfferRole");
        if (nbt.contains("ResignOfferRole"))
            this.resignOfferRole = nbt.getInt("ResignOfferRole");
    }

    private void writeTakenPiecesToNbt(NbtCompound nbt){
        NbtList takenList = new NbtList();

        for (int role : new int[]{WHITE, BLACK}) {
            NbtList roleList = new NbtList();
            for (Integer pieceId : playersTakenPieces.getOrDefault(role, Collections.emptyList())) {
                roleList.add(NbtInt.of(pieceId));
            }
            takenList.add(roleList);
        }

        nbt.put("TakenPieces", takenList);
    }

    private void readTakenPiecesFromNbt(NbtCompound nbt){
        List<Integer> wtpl = new ArrayList<>();
        List<Integer> btpl = new ArrayList<>();
        if (nbt.contains("TakenPieces")){
            NbtList list = (NbtList) nbt.get("TakenPieces");
            NbtList whiteList = list.getList(WHITE);
            NbtList blackList = list.getList(BLACK);

            for (int i = 0; i < whiteList.size(); i++){
                wtpl.add(whiteList.getInt(i));
            }
            for (int i = 0; i < blackList.size(); i++){
                btpl.add(blackList.getInt(i));
            }
        }
        this.playersTakenPieces.put(WHITE, wtpl);
        this.playersTakenPieces.put(BLACK, btpl);
    }

    public boolean isPlayerInList(UUID uuid){
        return players[WHITE].compareTo(uuid) == 0 || players[BLACK].compareTo(uuid) == 0;
    }

    @Override
    public void tick() {
        grid.tick();
        if (whiteTimeLeft != -1 && blackTimeLeft != -1){
            if (gameTimers[WHITE] != null && gameTimers[BLACK] != null){
                gameTimers[WHITE].tick(currentTurn == WHITE);
                gameTimers[BLACK].tick(currentTurn == BLACK);
            }
        }
    }

    public void playSound(SoundEvent sound){
        getWorld().playSound(null, blockEntity.getPos(), sound, SoundCategory.BLOCKS, 1, 1);
    }

    public void setResignOfferRole(int role){
        this.resignOfferRole = role;
        if (role != -1){
            PlayerEntity player = getWorld().getPlayerByUuid(players[role]);
            if (player != null)
                getWorld().getPlayerByUuid(players[role]).playSoundToPlayer(ModSounds.NOTIFY, SoundCategory.BLOCKS,1, 1);
        }

        updateClient();
    }

    public void setDrawOfferRole(int role){
        this.drawOfferRole = role;

        if (role != -1){
            PlayerEntity player = getWorld().getPlayerByUuid(players[role == WHITE ? BLACK : WHITE]);
            if (player != null)
                player.playSoundToPlayer(ModSounds.NOTIFY, SoundCategory.BLOCKS,1, 1);
        }
        updateClient();
    }

    @Override
    public void afterSwitch() {
        ((ChessGameOverState)blockEntity.states.get(ChessBoardBlockEntity.GAME_OVER_STATE)).transferData(this, players, gameOverReason, winnerRole);
    }

    @Override
    public void clear() {
        this.players = new UUID[2];
        this.playersTakenPieces = new HashMap<>();
        this.piecesData = new PiecesData(this);
        this.gameOverReason = ChessGameOverReason.NONE;
        this.winnerRole = -1;
        this.currentTurn = 0;
        this.currentChosenCell = new Vector2i(-1, -1);
        this.currentChosenPiece = EMPTY.id;
        this.gameTimers = new Timer[2];
        this.resignOfferRole = -1;
        this.drawOfferRole = -1;
        updateClient();
    }

    public ChessGrid getGrid() {
        return grid;
    }

    public UUID[] getPlayers() {
        return players;
    }

    public Map<Integer, List<Integer>> getTakenPieces(){
        return playersTakenPieces;
    }
}
