package net.fieldb0y.wanna_play_chess.item.custom;

import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.fieldb0y.wanna_play_chess.chess.utils.ChessPieces;
import net.fieldb0y.wanna_play_chess.item.ModComponents;
import net.fieldb0y.wanna_play_chess.item.ModItems;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.*;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.*;

public class BoxForPieces extends Item {
    public static final Text SET_ALREADY_INSERTED_MESSAGE = Text.translatable(WannaPlayChess.MOD_ID + ".box_for_pieces.set_already_inserted");
    public static final Text BOX_IS_NOT_FULL_MESSAGE = Text.translatable(WannaPlayChess.MOD_ID + ".box_for_pieces.box_is_not_full");
    public static final Text SUCCESSFUL_SET_INSERT = Text.translatable(WannaPlayChess.MOD_ID + ".box_for_pieces.successful_set_insert");
    public static final Text BOX_IS_EMPTY_TOOLTIP = Text.translatable(WannaPlayChess.MOD_ID + ".box_for_pieces.box_is_empty_tooltip");
    public static final Text EMPTY_BOX_SHIFT_TOOLTIP = Text.translatable(WannaPlayChess.MOD_ID + ".box_for_pieces.empty_box_shift_tooltip");
    public static final Text BOX_SHIFT_TOOLTIP = Text.translatable(WannaPlayChess.MOD_ID + ".box_for_pieces.box_shift_tooltip");
    public static final Text PRESS_SHIFT_TOOLTIP = Text.translatable(WannaPlayChess.MOD_ID + ".box_for_pieces.press_shift_tooltip");

    private static final int PAWNS_MAX_COUNT = 8;
    private static final int BISHOPS_KNIGHTS_ROOKS_MAX_COUNT = 2;
    private static final int DEFAULT_MAX_COUNT = 1;

    public BoxForPieces(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        if (!world.isClient()){
            if (world.getBlockEntity(context.getBlockPos()) instanceof ChessBoardBlockEntity blockEntity){
                ItemStack stack = player.getStackInHand(context.getHand());
                if (!player.isSneaking()){
                    if (isFull(stack)){
                        int role = getPiecesInBoxRole(stack);

                        if (!blockEntity.isSetInserted(role)){
                            blockEntity.insertPiecesSet(role);
                            if (!player.getMainHandStack().equals(stack))
                                player.getInventory().removeStack(PlayerInventory.OFF_HAND_SLOT);
                            else player.getInventory().removeStack(player.getInventory().selectedSlot);
                            return ActionResult.SUCCESS;
                        } else player.sendMessage(SET_ALREADY_INSERTED_MESSAGE.copy().formatted(Formatting.RED));
                    } else player.sendMessage(BOX_IS_NOT_FULL_MESSAGE.copy().formatted(Formatting.RED));
                }
            }
        }

        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient()) {
            ItemStack stack = player.getStackInHand(hand);
            ItemStack otherHandStack = player.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
            Map<Integer, Integer> piecesInBox = new HashMap<>(stack.getOrDefault(ModComponents.PIECES_IN_BOX_COMPONENT, Collections.emptyMap()));

            if (player.isSneaking()){
                for (int pieceId : piecesInBox.keySet()){
                    ChessPiece piece = ChessGrid.getPieceById(pieceId);
                    player.giveItemStack(ChessGrid.getPieceRole(pieceId) == WHITE ? piece.getWhiteStack().copyWithCount(piecesInBox.get(pieceId)) : piece.getBlackStack().copyWithCount(piecesInBox.get(pieceId)));
                }

                piecesInBox.clear();
                piecesInBox = new HashMap<>();
                stack.set(ModComponents.PIECES_IN_BOX_COMPONENT, piecesInBox);
            } else {
                if (otherHandStack.getItem() instanceof ChessPiece piece){
                    int pieceId = ChessPieces.getId(piece);
                    int stackCount = otherHandStack.getCount();

                    if (piecesInBox.containsKey(pieceId)){
                        int countToPut = getCountToPut(pieceId, piecesInBox.get(pieceId), stackCount);

                        if (countToPut > 0){
                            piecesInBox.put(pieceId, piecesInBox.get(pieceId) + countToPut);
                            otherHandStack.decrement(countToPut);
                        }
                    } else {
                        int neededRole = piecesInBox.isEmpty() ? -1 : ChessGrid.getPieceRole((Integer) piecesInBox.keySet().toArray()[0]);
                        int countToPut = getCountToPut(pieceId, 0, stackCount);

                        if (countToPut > 0){
                            if (neededRole == -1 || ChessGrid.getPieceRole(pieceId) == neededRole){
                                piecesInBox.put(pieceId, countToPut);
                                otherHandStack.decrement(countToPut);
                            }
                        }
                    }
                }
                stack.set(ModComponents.PIECES_IN_BOX_COMPONENT, piecesInBox);
            }
        }
        return TypedActionResult.consume(player.getStackInHand(hand));
    }

    private int getCountToPut(int pieceId, int countOfPiecesInBox, int otherHandStackCount){
        int countToPut = DEFAULT_MAX_COUNT - countOfPiecesInBox;
        if (pieceId == ChessPieces.WHITE_PAWN.id || pieceId == ChessPieces.BLACK_PAWN.id) countToPut = PAWNS_MAX_COUNT - countOfPiecesInBox;
        if (pieceId == ChessPieces.WHITE_KNIGHT.id || pieceId == ChessPieces.BLACK_KNIGHT.id
                || pieceId == ChessPieces.WHITE_BISHOP.id || pieceId == ChessPieces.BLACK_BISHOP.id
                || pieceId == ChessPieces.WHITE_ROOK.id || pieceId == ChessPieces.BLACK_ROOK.id) countToPut = BISHOPS_KNIGHTS_ROOKS_MAX_COUNT - countOfPiecesInBox;

        return Math.min(countToPut, otherHandStackCount);
    }

    public static boolean isFull(ItemStack stack){
        if (stack.getItem() instanceof BoxForPieces item){
            Map<Integer, Integer> piecesInBox = new HashMap<>(stack.getOrDefault(ModComponents.PIECES_IN_BOX_COMPONENT, Collections.emptyMap()));
            if (piecesInBox.size() < 6) return false;
            int role = getPiecesInBoxRole(stack);
            int indexOffset = role == WHITE ? 0 : 6;

            return piecesInBox.get(ChessPieces.WHITE_PAWN.id + indexOffset) >= 8
                    && piecesInBox.get(ChessPieces.WHITE_KNIGHT.id + indexOffset) >= 2
                    && piecesInBox.get(ChessPieces.WHITE_BISHOP.id + indexOffset) >= 2
                    && piecesInBox.get(ChessPieces.WHITE_ROOK.id + indexOffset) >= 2
                    && piecesInBox.get(ChessPieces.WHITE_QUEEN.id + indexOffset) >= 1
                    && piecesInBox.get(ChessPieces.WHITE_KING.id + indexOffset) >= 1;
        }
        return false;
    }

    public boolean isEnoughPieces(int pieceId, int count){
        return getFullBoxMap(ChessGrid.getPieceRole(pieceId)).get(pieceId) <= count;
    }

    public static ItemStack getFullBoxStack(int role){
        ItemStack stack = ModItems.BOX_FOR_CHESS_PIECES.getDefaultStack();
        stack.set(ModComponents.PIECES_IN_BOX_COMPONENT, getFullBoxMap(role));
        return stack;
    }

    public static Map<Integer, Integer> getFullBoxMap(int role){
        int indexOffset = role == WHITE ? 0 : 6;
        return Map.of(ChessPieces.WHITE_PAWN.id + indexOffset, 8,
                ChessPieces.WHITE_KNIGHT.id + indexOffset, 2,
                ChessPieces.WHITE_BISHOP.id + indexOffset, 2,
                ChessPieces.WHITE_ROOK.id + indexOffset, 2,
                ChessPieces.WHITE_QUEEN.id + indexOffset, 1,
                ChessPieces.WHITE_KING.id + indexOffset, 1);
    }

    public static int getPiecesInBoxRole(ItemStack stack){
        if (stack.getItem() instanceof BoxForPieces item) {
            Map<Integer, Integer> piecesInBox = new HashMap<>(stack.getOrDefault(ModComponents.PIECES_IN_BOX_COMPONENT, Collections.emptyMap()));
            if (piecesInBox.isEmpty()) return -1;
            return ChessGrid.getPieceRole((int)piecesInBox.keySet().toArray()[0]);
        }
        return -1;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Map<Integer, Integer> pieces = stack.getOrDefault(ModComponents.PIECES_IN_BOX_COMPONENT, Collections.emptyMap());
        if (pieces.isEmpty()){
            tooltip.add(BOX_IS_EMPTY_TOOLTIP.copy().formatted(Formatting.RED));
        }

        if (Screen.hasShiftDown()){
            if (!pieces.isEmpty())
                pieces.forEach((id, count) -> tooltip.add(Text.translatable(ChessGrid.getPieceById(id).getTranslationKey()).formatted(Formatting.GRAY).append(": " + (isEnoughPieces(id, count) ? "§a" : "§c") + count + "§r")));
            else tooltip.add(EMPTY_BOX_SHIFT_TOOLTIP.copy().formatted(Formatting.DARK_GRAY));
            tooltip.add(BOX_SHIFT_TOOLTIP.copy().formatted(Formatting.DARK_GRAY));
        } else tooltip.add(PRESS_SHIFT_TOOLTIP);
    }
}
