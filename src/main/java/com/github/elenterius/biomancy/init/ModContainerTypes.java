package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.*;
import com.github.elenterius.biomancy.inventory.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModContainerTypes {
	private ModContainerTypes() {}

	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BiomancyMod.MOD_ID);

	public static final RegistryObject<ContainerType<GulgeContainer>> GULGE = CONTAINERS.register("gulge", () -> IForgeContainerType.create(GulgeContainer::createClientContainer));
	public static final RegistryObject<ContainerType<FleshChestContainer>> FLESHBORN_CHEST = CONTAINERS.register("fleshborn_chest", () -> IForgeContainerType.create(FleshChestContainer::createClientContainer));
	public static final RegistryObject<ContainerType<DecomposerContainer>> DECOMPOSER = CONTAINERS.register("decomposer", () -> IForgeContainerType.create(DecomposerContainer::createClientContainer));
	public static final RegistryObject<ContainerType<ChewerContainer>> CHEWER = CONTAINERS.register("chewer", () -> IForgeContainerType.create(ChewerContainer::createClientContainer));
	public static final RegistryObject<ContainerType<DigesterContainer>> DIGESTER = CONTAINERS.register("digester", () -> IForgeContainerType.create(DigesterContainer::createClientContainer));
	public static final RegistryObject<ContainerType<SolidifierContainer>> SOLIDIFIER = CONTAINERS.register("solidifier", () -> IForgeContainerType.create(SolidifierContainer::createClientContainer));
	public static final RegistryObject<ContainerType<EvolutionPoolContainer>> EVOLUTION_POOL = CONTAINERS.register("evolution_pool", () -> IForgeContainerType.create(EvolutionPoolContainer::createClientContainer));

	@OnlyIn(Dist.CLIENT)
	protected static void registerContainerScreens() {
		ScreenManager.registerFactory(GULGE.get(), GulgeContainerScreen::new);
		ScreenManager.registerFactory(FLESHBORN_CHEST.get(), FleshChestContainerScreen::new);
		ScreenManager.registerFactory(CHEWER.get(), ChewerContainerScreen::new);
		ScreenManager.registerFactory(DIGESTER.get(), DigesterContainerScreen::new);
		ScreenManager.registerFactory(SOLIDIFIER.get(), SolidifierContainerScreen::new);
		ScreenManager.registerFactory(DECOMPOSER.get(), DecomposerContainerScreen::new);
		ScreenManager.registerFactory(EVOLUTION_POOL.get(), EvolutionPoolContainerScreen::new);
	}

}
