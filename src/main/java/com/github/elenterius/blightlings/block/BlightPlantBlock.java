package com.github.elenterius.blightlings.block;

import com.github.elenterius.blightlings.init.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;

public class BlightPlantBlock extends BushBlock
{
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);
    protected static final VoxelShape SHAPE_SMALL = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 8.0D, 14.0D);
    private final boolean isSmall;

    public BlightPlantBlock(boolean isSmall) {
        super(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.0F).sound(SoundType.PLANT));
        this.isSmall = isSmall;
    }

    public BlightPlantBlock(boolean isSmall, Properties properties) {
        super(properties);
        this.isSmall = isSmall;
    }

    public BlightPlantBlock() {
        this(false);
    }

    public BlightPlantBlock(Properties properties) {
        super(properties);
        this.isSmall = false;
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.isIn(ModBlocks.INFERTILE_SOIL.get()) || state.isIn(ModBlocks.LUMINOUS_SOIL.get()) || state.isIn(Blocks.GRASS_BLOCK);
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return ModBlocks.BLIGHT_PLANT_TYPE;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Vector3d vec = state.getOffset(worldIn, pos);
        return (isSmall ? SHAPE_SMALL : SHAPE).withOffset(vec.x, vec.y, vec.z);
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
}