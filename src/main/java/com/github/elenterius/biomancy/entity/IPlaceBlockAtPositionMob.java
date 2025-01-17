package com.github.elenterius.biomancy.entity;

import com.github.elenterius.biomancy.util.BlockPlacementTarget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;

import javax.annotation.Nullable;

public interface IPlaceBlockAtPositionMob {

	boolean tryToPlaceBlockAtPosition(BlockRayTraceResult rayTraceResult, Direction horizontalFacing);

	boolean hasPlaceableBlock();

	void setPlacementBlock(ItemStack stack);

	ItemStack getPlacementBlock();

	@Nullable
	BlockPlacementTarget getBlockPlacementTarget();

	void setBlockPlacementTarget(@Nullable BlockPlacementTarget placementTarget);
}
