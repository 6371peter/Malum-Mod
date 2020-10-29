package com.sammy.malum.blocks.machines.spiritfurnace;

import com.sammy.malum.ClientHandler;
import com.sammy.malum.blocks.utility.BasicTileEntity;
import com.sammy.malum.init.ModRecipes;
import com.sammy.malum.init.ModSounds;
import com.sammy.malum.init.ModTileEntities;
import com.sammy.malum.particles.spiritflame.SpiritFlameParticleData;
import com.sammy.malum.recipes.SpiritFurnaceFuelData;
import com.sammy.malum.recipes.SpiritFurnaceRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static com.sammy.malum.MalumHelper.randomVector;
import static com.sammy.malum.MalumHelper.vectorFromBlockPos;
import static com.sammy.malum.MalumMod.random;

public class SpiritFurnaceBottomTileEntity extends BasicTileEntity implements ITickableTileEntity
{
    public int burnTime;
    public int burnProgress;
    public boolean isSmelting;
    public Object sound;
    public ItemStackHandler inventory = new ItemStackHandler(1)
    {
        @Override
        public int getSlotLimit(int slot)
        {
            return 64;
        }
        
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack)
        {
            return ModRecipes.getSpiritFurnaceFuelData(stack) != null;
        }
        
        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return ItemStack.EMPTY;
        }
        
        @Override
        protected void onContentsChanged(int slot)
        {
            SpiritFurnaceBottomTileEntity.this.markDirty();
            if (!world.isRemote)
            {
                updateContainingBlockInfo();
                BlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
            }
        }
    };
    public final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(() -> inventory);
    public SpiritFurnaceBottomTileEntity()
    {
        super(ModTileEntities.spirit_furnace_bottom_tile_entity);
    }
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return lazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        compound.put("inventory", inventory.serializeNBT());
        compound.putInt("burnTime", burnTime);
        compound.putInt("burnProgress", burnProgress);
        compound.putBoolean("isSmelting", isSmelting);
        return compound;
    }
    
    @Override
    public void read(CompoundNBT compound)
    {
        super.read(compound);
        inventory.deserializeNBT((CompoundNBT) Objects.requireNonNull(compound.get("inventory")));
        burnTime = compound.getInt("burnTime");
        burnProgress = compound.getInt("burnProgress");
        isSmelting = compound.getBoolean("isSmelting");
    }
    
    @Override
    public void remove()
    {
        if (world.isRemote())
        {
            ClientHandler.spiritFurnaceStop(this);
        }
        super.remove();
    }
    
    @Override
    public void tick()
    {
        BlockPos topPos = pos.up();
        if (world.getTileEntity(topPos) instanceof SpiritFurnaceTopTileEntity)
        {
            SpiritFurnaceTopTileEntity topTileEntity = (SpiritFurnaceTopTileEntity) world.getTileEntity(topPos);
            ItemStack fuelItem = inventory.getStackInSlot(0);
            ItemStack inputItem = topTileEntity.inventory.getStackInSlot(0);
            
            if (isSmelting)
            {
                if (world.isRemote())
                {
                    ClientHandler.spiritFurnaceTick(this);
                }
                if (world.getGameTime() % 4L == 0L)
                {
                    spawnTopSmoke();
                }
                if (world.getGameTime() % 8L == 0L)
                {
                    spawnTopSpiritFlame();
                }
            }
            if (burnTime > 0)
            {
                if (world.getGameTime() % 3L == 0L)
                {
                    spawnSpiritFlame();
                }
            }
            if (ModRecipes.getSpiritFurnaceRecipe(inputItem) != null)
            {
                SpiritFurnaceRecipe recipe = ModRecipes.getSpiritFurnaceRecipe(inputItem);
                
                if (!fuelItem.isEmpty())
                {
                    SpiritFurnaceFuelData data = ModRecipes.getSpiritFurnaceFuelData(fuelItem);
                    if (data != null)
                    {
                        if (burnTime <= 0)
                        {
                            fuelItem.shrink(1);
                            burnTime = data.getFuelTime();
                        }
                    }
                }
                if (burnTime > 0)
                {
                    updateState(true);
                    burnProgress++;
                    if (burnProgress >= recipe.getBurnTime())
                    {
                        output(new ItemStack(recipe.getOutputItem()), pos.up());
                        if (recipe.getSideItem() != null)
                        {
                            if (random.nextInt(recipe.getSideItemChance()) == 0)
                            {
                                output(new ItemStack(recipe.getSideItem()), pos.up());
                            }
                        }
                        inputItem.shrink(1);
                        burnProgress = 0;
                    }
                }
                else
                {
                    burnProgress = 0;
                    updateState(false);
                }
            }
            else // reset if no valid item is present
            {
                burnProgress = 0;
                updateState(false);
            }
            burnTime--;
        }
    }
    
    public void updateState(boolean isSmelting)
    {
        if (this.isSmelting != isSmelting)
        {
            this.isSmelting = isSmelting;
            BlockState state = world.getBlockState(pos);
            BlockState topState = world.getBlockState(pos.up());
            if (state.getBlock() instanceof SpiritFurnaceBottomBlock && topState.getBlock() instanceof SpiritFurnaceTopBlock)
            {
                BlockState newState = state.with(BlockStateProperties.LIT, isSmelting);
                BlockState newTopState = topState.with(BlockStateProperties.LIT, isSmelting);
                
                world.notifyBlockUpdate(pos, state, newState, 3);
                world.setBlockState(pos, newState, 3);
                world.notifyBlockUpdate(pos.up(), topState, newTopState, 3);
                world.setBlockState(pos.up(), newTopState, 3);
            }
            if (isSmelting)
            {
                world.playSound(null, pos, ModSounds.furnace_start, SoundCategory.BLOCKS, 1, 1);
            }
            else
            {
                if (FMLEnvironment.dist == Dist.CLIENT)
                {
                    ClientHandler.spiritFurnaceStop(this);
                }
                world.playSound(null, pos, ModSounds.furnace_stop, SoundCategory.BLOCKS, 1, 1);
            }
        }
    }
    
    public void spawnSpiritFlame()
    {
        Vector3i direction = getBlockState().get(BlockStateProperties.HORIZONTAL_FACING).getDirectionVec();
        Vector3d velocity = randomVector(random, -0.02, 0.02);
        Vector3d particlePos = vectorFromBlockPos(pos).add(randomVector(random, 0.3, 0.7)).add(direction.getX() * 0.3f, 0, direction.getZ() * 0.3f);
        
        world.addParticle(new SpiritFlameParticleData(MathHelper.nextFloat(random, 0.25f, 0.4f)), particlePos.x, particlePos.y, particlePos.z, velocity.x, velocity.y, velocity.z);
    }
    
    public void spawnTopSpiritFlame()
    {
        Vector3i direction = world.getBlockState(pos.up()).get(BlockStateProperties.HORIZONTAL_FACING).getDirectionVec();
        Vector3d velocity = randomVector(random, -0.02, 0.02);
        Vector3d particlePos = vectorFromBlockPos(pos).add(randomVector(random, 0.2, 0.8)).add(direction.getX() * 0.2f, 1, direction.getZ() * 0.2f);
        
        world.addParticle(new SpiritFlameParticleData(MathHelper.nextFloat(random, 0.25f, 0.4f)), particlePos.x, particlePos.y, particlePos.z, velocity.x, velocity.y, velocity.z);
    }
    
    public void spawnTopSmoke()
    {
        Vector3i direction = world.getBlockState(pos.up()).get(BlockStateProperties.HORIZONTAL_FACING).getDirectionVec();
        Vector3d particlePos = vectorFromBlockPos(pos).add(0.5, 1.75, 0.5).add(randomVector(random, -0.3, 0.3));
        
        if (direction.getZ() != 0)
        {
            world.addParticle(ParticleTypes.SMOKE, particlePos.x, particlePos.y, particlePos.getZ() - direction.getZ() * 0.4f, 0, 0.06, 0);
        }
        if (direction.getX() != 0)
        {
            world.addParticle(ParticleTypes.SMOKE, particlePos.x - direction.getX() * 0.4d, particlePos.y, particlePos.getZ(), 0, 0.06, 0);
        }
    }
}