package net.fieldb0y.wanna_play_chess;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fieldb0y.wanna_play_chess.datagen.*;
import net.fieldb0y.wanna_play_chess.datagen.lang.ModEnglishLangProvider;
import net.fieldb0y.wanna_play_chess.datagen.lang.ModRussianLangProvider;

public class WannaPlayChessDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModModelsProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModBlockLootTableProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModEnglishLangProvider::new);
		pack.addProvider(ModRussianLangProvider::new);
	}
}
