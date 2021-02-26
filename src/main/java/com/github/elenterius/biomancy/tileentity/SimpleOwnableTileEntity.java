package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import net.minecraft.util.text.ITextComponent;

public class SimpleOwnableTileEntity extends OwnableTileEntity {

	public SimpleOwnableTileEntity() {
		super(ModTileEntityTypes.SIMPLE_OWNABLE_TILE.get());
	}

	@Override
	protected ITextComponent getDefaultName() {
		return BiomancyMod.getTranslationText("tile", "ownable");
	}

}