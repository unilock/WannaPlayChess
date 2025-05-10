package net.fieldb0y.wanna_play_chess;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fieldb0y.wanna_play_chess.block.ModBlocks;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.block.entity.ModBlockEntities;
import net.fieldb0y.wanna_play_chess.entity.ModEntities;
import net.fieldb0y.wanna_play_chess.entity.custom.PlayerCopyEntity;
import net.fieldb0y.wanna_play_chess.item.ModComponents;
import net.fieldb0y.wanna_play_chess.item.ModItemGroups;
import net.fieldb0y.wanna_play_chess.item.ModItems;
import net.fieldb0y.wanna_play_chess.item.custom.BoxForPieces;
import net.fieldb0y.wanna_play_chess.network.c2sPayloads.*;
import net.fieldb0y.wanna_play_chess.network.s2cPayloads.SetGameTimeTextFieldPayload;
import net.fieldb0y.wanna_play_chess.screenhandler.ModScreenHandlers;
import net.fieldb0y.wanna_play_chess.sound.ModSounds;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.BLACK;
import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.WHITE;

public class WannaPlayChess implements ModInitializer {
	public static final String MOD_ID = "wanna_play_chess";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.register();
		ModBlocks.register();
		ModBlockEntities.register();
		ModScreenHandlers.register();
		ModItemGroups.register();
		ModComponents.register();
		ModSounds.register();

		ModEntities.register();
		FabricDefaultAttributeRegistry.register(ModEntities.PLAYER_COPY, PlayerCopyEntity.createAttributes());

		registerC2SPackets();
		registerS2CPackets();
		registerEvents();
	}

	private void registerC2SPackets(){
		PayloadTypeRegistry.playC2S().register(JoinLobbyButtonPayload.ID, JoinLobbyButtonPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(JoinLobbyButtonPayload.ID, JoinLobbyButtonPayload::receive);

		PayloadTypeRegistry.playC2S().register(LeaveLobbyPayload.ID, LeaveLobbyPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(LeaveLobbyPayload.ID, LeaveLobbyPayload::receive);

		PayloadTypeRegistry.playC2S().register(StartGameButtonPayload.ID, StartGameButtonPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(StartGameButtonPayload.ID, StartGameButtonPayload::receive);

		PayloadTypeRegistry.playC2S().register(CellClickPayload.ID, CellClickPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(CellClickPayload.ID, CellClickPayload::receive);

		PayloadTypeRegistry.playC2S().register(BackToLobbyButtonPayload.ID, BackToLobbyButtonPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(BackToLobbyButtonPayload.ID, BackToLobbyButtonPayload::receive);

		PayloadTypeRegistry.playC2S().register(TurnPawnIntoPayload.ID, TurnPawnIntoPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(TurnPawnIntoPayload.ID, TurnPawnIntoPayload::receive);

		PayloadTypeRegistry.playC2S().register(LobbyGameTimePayload.ID, LobbyGameTimePayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(LobbyGameTimePayload.ID, LobbyGameTimePayload::receive);

		PayloadTypeRegistry.playC2S().register(LobbyNoTimeControlCheckboxPayload.ID, LobbyNoTimeControlCheckboxPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(LobbyNoTimeControlCheckboxPayload.ID, LobbyNoTimeControlCheckboxPayload::receive);

		PayloadTypeRegistry.playC2S().register(LobbyFirstPlayerRolePayload.ID, LobbyFirstPlayerRolePayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(LobbyFirstPlayerRolePayload.ID, LobbyFirstPlayerRolePayload::receive);

		PayloadTypeRegistry.playC2S().register(DrawOfferButtonPayload.ID, DrawOfferButtonPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(DrawOfferButtonPayload.ID, DrawOfferButtonPayload::receive);

		PayloadTypeRegistry.playC2S().register(ResignButtonPayload.ID, ResignButtonPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ResignButtonPayload.ID, ResignButtonPayload::receive);

		PayloadTypeRegistry.playC2S().register(YesButtonPayload.ID, YesButtonPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(YesButtonPayload.ID, YesButtonPayload::receive);

		PayloadTypeRegistry.playC2S().register(NoButtonPayload.ID, NoButtonPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(NoButtonPayload.ID, NoButtonPayload::receive);
	}

	private void registerS2CPackets(){
		PayloadTypeRegistry.playS2C().register(SetGameTimeTextFieldPayload.ID, SetGameTimeTextFieldPayload.CODEC);
	}

	public void registerEvents(){
		UseBlockCallback.EVENT.register(((player, world, hand, blockHitResult) -> {
			if (!world.isClient() && world.getBlockEntity(blockHitResult.getBlockPos()) instanceof ChessBoardBlockEntity blockEntity && player.isSneaking()){
				if (blockEntity.whiteSetInsereted || blockEntity.blackSetInserted){
					if (blockEntity.whiteSetInsereted){
						blockEntity.removePiecesSet(WHITE);
						player.giveItemStack(BoxForPieces.getFullBoxStack(WHITE));
					}
					if (blockEntity.blackSetInserted){
						blockEntity.removePiecesSet(BLACK);
						player.giveItemStack(BoxForPieces.getFullBoxStack(BLACK));
					}
				}
				return ActionResult.SUCCESS;
			}

			return ActionResult.PASS;
		}));
	}
}