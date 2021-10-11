package com.sammy.malum.core.data;

import com.sammy.malum.MalumMod;
import com.sammy.malum.core.init.block.MalumBlocks;
import net.minecraft.block.*;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static com.sammy.malum.core.init.block.MalumBlocks.BLOCKS;
import static net.minecraft.tags.BlockTags.*;
import static net.minecraftforge.common.Tags.Blocks.DIRT;

public class MalumBlockTags extends BlockTagsProvider
{
    public MalumBlockTags(DataGenerator generatorIn, ExistingFileHelper existingFileHelper)
    {
        super(generatorIn, MalumMod.MODID, existingFileHelper);
    }
    
    @Override
    protected void registerTags() {
        getOrCreateBuilder(Tags.Blocks.ORES).add(MalumBlocks.SOULSTONE_ORE.get(), MalumBlocks.BLAZING_QUARTZ_ORE.get());

        getOrCreateBuilder(net.minecraft.tags.BlockTags.SLABS).add(getModBlocks(b -> b instanceof SlabBlock));
        getOrCreateBuilder(net.minecraft.tags.BlockTags.STAIRS).add(getModBlocks(b -> b instanceof StairsBlock));
        getOrCreateBuilder(net.minecraft.tags.BlockTags.WALLS).add(getModBlocks(b -> b instanceof WallBlock));
        getOrCreateBuilder(net.minecraft.tags.BlockTags.FENCES).add(getModBlocks(b -> b instanceof FenceBlock));
        getOrCreateBuilder(net.minecraft.tags.BlockTags.FENCE_GATES).add(getModBlocks(b -> b instanceof FenceGateBlock));
        getOrCreateBuilder(net.minecraft.tags.BlockTags.LEAVES).add(getModBlocks(b -> b instanceof LeavesBlock));
        getOrCreateBuilder(DOORS).add(getModBlocks(b -> b instanceof DoorBlock));
        getOrCreateBuilder(TRAPDOORS).add(getModBlocks(b -> b instanceof TrapDoorBlock));
        getOrCreateBuilder(BUTTONS).add(getModBlocks(b -> b instanceof AbstractButtonBlock));
        getOrCreateBuilder(WOODEN_BUTTONS).add(getModBlocks(b -> b instanceof WoodButtonBlock));
        getOrCreateBuilder(PRESSURE_PLATES).add(getModBlocks(b -> b instanceof AbstractPressurePlateBlock));
        getOrCreateBuilder(LOGS).add(MalumBlocks.RUNEWOOD_LOG.get(), MalumBlocks.RUNEWOOD.get(), MalumBlocks.SOULWOOD_LOG.get(), MalumBlocks.SOULWOOD.get());
        getOrCreateBuilder(DIRT).add(getModBlocks(b -> b instanceof GrassBlock || b instanceof FarmlandBlock));
        getOrCreateBuilder(SAPLINGS).add(getModBlocks(b -> b instanceof SaplingBlock));
        getOrCreateBuilder(PLANKS).add(MalumBlocks.RUNEWOOD_PLANKS.get(), MalumBlocks.SOULWOOD_PLANKS.get());
        getOrCreateBuilder(WOODEN_FENCES).add(MalumBlocks.RUNEWOOD_PLANKS_FENCE.get(), MalumBlocks.SOULWOOD_PLANKS_FENCE.get());
        getOrCreateBuilder(WOODEN_DOORS).add(MalumBlocks.RUNEWOOD_DOOR.get(), MalumBlocks.SOULWOOD_DOOR.get());
        getOrCreateBuilder(WOODEN_STAIRS).add(MalumBlocks.RUNEWOOD_PLANKS_STAIRS.get(), MalumBlocks.SOULWOOD_PLANKS_STAIRS.get());
        getOrCreateBuilder(WOODEN_SLABS).add(MalumBlocks.RUNEWOOD_PLANKS_SLAB.get(), MalumBlocks.SOULWOOD_PLANKS_SLAB.get());
        getOrCreateBuilder(WOODEN_TRAPDOORS).add(MalumBlocks.RUNEWOOD_TRAPDOOR.get(), MalumBlocks.SOLID_RUNEWOOD_TRAPDOOR.get(), MalumBlocks.SOULWOOD_TRAPDOOR.get(), MalumBlocks.SOLID_SOULWOOD_TRAPDOOR.get());
        getOrCreateBuilder(WOODEN_PRESSURE_PLATES).add(MalumBlocks.RUNEWOOD_PLANKS_PRESSURE_PLATE.get(), MalumBlocks.SOULWOOD_PLANKS_PRESSURE_PLATE.get());
    }
    
    @Override
    public String getName()
    {
        return "Malum Block Tags";
    }
    
    @Nonnull
    private Block[] getModBlocks(Predicate<Block> predicate)
    {
        List<Block> ret = new ArrayList<>(Collections.emptyList());
        BLOCKS.getEntries().stream()
                .filter(b -> predicate.test(b.get())).forEach(b -> ret.add(b.get()));
        return ret.toArray(new Block[0]);
    }
}