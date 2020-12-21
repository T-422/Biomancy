package com.github.elenterius.blightlings.world.gen.tree;

import com.github.elenterius.blightlings.init.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class LilyTreeFeature extends Feature<NoFeatureConfig> {
	public LilyTreeFeature(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		BlockState state = reader.getBlockState(pos.down());
		if (state.isIn(ModBlocks.INFERTILE_SOIL.get()) || state.isIn(ModBlocks.LUMINOUS_SOIL.get()))
			return generate(reader, rand, pos, state);
		return false;
	}

	public boolean generate(IWorld iWorld, Random rand, BlockPos pos, BlockState state) {
		TreeGeneratorUtil.generateLilyTree(iWorld, rand, pos, state);
		return true;
	}
}
