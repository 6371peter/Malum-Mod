package com.sammy.malum.common.blocks.taint;

import com.sammy.malum.MalumMod;
import com.sammy.malum.core.init.MalumSounds;
import com.sammy.malum.core.recipes.TaintConversion;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;

import static net.minecraft.util.SoundCategory.BLOCKS;

public interface ITaintSpreader
{
    default void spread(ServerWorld worldIn, BlockPos pos)
    {
        Vector3i[] offsets = new Vector3i[]{
                new Vector3i(-1, 0, -1),
                new Vector3i(-1, 0, 0),
                new Vector3i(-1, 0, 1),
                new Vector3i(0, 0, 1),
                new Vector3i(1, 0, 1),
                new Vector3i(1, 0, 0),
                new Vector3i(1, 0, -1),
                new Vector3i(0, 0, -1),
        
                new Vector3i(-1, -1, -1),
                new Vector3i(-1, -1, 0),
                new Vector3i(-1, -1, 1),
                new Vector3i(0, -1, 1),
                new Vector3i(1, -1, 1),
                new Vector3i(1, -1, 0),
                new Vector3i(1, -1, -1),
                new Vector3i(0, -1, -1),
                new Vector3i(0, -1, 0),
        
                new Vector3i(-1, 1, -1),
                new Vector3i(-1, 1, 0),
                new Vector3i(-1, 1, 1),
                new Vector3i(0, 1, 1),
                new Vector3i(1, 1, 1),
                new Vector3i(1, 1, 0),
                new Vector3i(1, 1, -1),
                new Vector3i(0, 1, -1),
                new Vector3i(0, 1, 0)
        };
        boolean playSound = false;
        for (Vector3i offset : offsets)
        {
            BlockPos targetPos = pos.add(offset);
            Block targetBlock = worldIn.getBlockState(targetPos).getBlock();
            TaintConversion conversion = TaintConversion.getConversion(targetBlock);
            if (conversion != null)
            {
                playSound = true;
                TaintConversion.spread(worldIn,targetPos,conversion);
                if (MalumMod.RANDOM.nextFloat() <= 0.5f)
                {
                    break;
                }
            }
        }
        if (playSound)
        {
            worldIn.playSound(null, pos, MalumSounds.TAINT_SPREAD,BLOCKS,0.5f,1f + MalumMod.RANDOM.nextFloat() * 0.5f);
        }
    }
}