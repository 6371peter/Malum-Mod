package com.kittykitcatcat.malum.blocks.machines.spiritbellows;

import com.kittykitcatcat.malum.blocks.machines.spiritfurnace.SpiritFurnaceBottomTileEntity;
import com.kittykitcatcat.malum.init.ModBlocks;
import com.kittykitcatcat.malum.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

@Mod.EventBusSubscriber
public class SpiritBellowsTileEntity extends TileEntity implements ITickableTileEntity
{
    public SpiritBellowsTileEntity()
    {
        super(ModTileEntities.spirit_bellows_tile_entity);
    }
    public boolean active;
    public boolean stretching;
    public float stretch;
    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        compound.putBoolean("active", active);
        compound.putBoolean("stretching",stretching);
        compound.putFloat("stretch",stretch);
        return compound;
    }

    @Override
    public void read(CompoundNBT compound)
    {
        super.read(compound);
        active = compound.getBoolean("active");
        stretching = compound.getBoolean("stretching");
        stretch = compound.getFloat("stretch");
    }
    @Override
    public void tick()
    {
        if (stretch > 1f)
        {
            stretching = true;
            stretch = 1f;
        }
        if (stretch < 0.56f)
        {
            stretching = false;
        }
        if (stretching)
        {
            if (active)
            {
                stretch -= 0.04f;
            }
        }
        else
        {
            stretch +=0.02f;
        }
        Direction direction = getBlockState().get(HORIZONTAL_FACING);
        BlockPos furnacePos = pos.add(direction.getDirectionVec());
        if (world.getBlockState(furnacePos).getBlock().equals(ModBlocks.spirit_furnace))
        {
            BlockState state = world.getBlockState(furnacePos);
            if (!state.get(HORIZONTAL_FACING).equals(direction.getOpposite()) && !state.get(HORIZONTAL_FACING).equals(direction))
            {
                if (world.getTileEntity(furnacePos) instanceof SpiritFurnaceBottomTileEntity)
                {
                    SpiritFurnaceBottomTileEntity furnaceEntity = (SpiritFurnaceBottomTileEntity) world.getTileEntity(furnacePos);
                    if (furnaceEntity.isSmelting)
                    {
                        active = true;
                        if (world.getGameTime() % 4 == 0)
                        {
                            furnaceEntity.burnProgress++;
                            furnaceEntity.markDirty();
                        }
                    }
                    else
                    {
                        active = false;
                    }
                }
            }
        }
        else
        {
            active = false;
        }
    }
    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.write(new CompoundNBT());
    }
    @Override
    public void handleUpdateTag(CompoundNBT tag)
    {
        read(tag);
    }
    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
    }
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        handleUpdateTag(pkt.getNbtCompound());
    }
}