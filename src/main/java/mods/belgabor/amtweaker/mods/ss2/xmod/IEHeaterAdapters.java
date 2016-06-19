package mods.belgabor.amtweaker.mods.ss2.xmod;

import blusunrize.immersiveengineering.api.tool.ExternalHeaterHandler;
import net.minecraft.item.ItemStack;
import shift.sextiarysector.recipe.FurnaceCraftingManager;
import shift.sextiarysector.tileentity.TileEntityFluidMachineBase;
import shift.sextiarysector.tileentity.TileEntityLargeFurnace;

/**
 * Created by Belgabor on 19.06.2016.
 * Based on the vanilla furnace handler by BluSunrize
 */
public class IEHeaterAdapters {
    public static class LiquidFurnaceAdapter extends ExternalHeaterHandler.HeatableAdapter<TileEntityFluidMachineBase> {
        public LiquidFurnaceAdapter() {
        }
        
        @Override
        public int doHeatTick(TileEntityFluidMachineBase tileEntity, int energyAvailable, boolean redstone) {
            int energyConsumed = 0;
            boolean canCook = tileEntity.canWork();
            if(canCook || redstone) {
                boolean burning = tileEntity.on;
                if(tileEntity.fuel < 200) {
                    tileEntity.fuelMax = 200;
                    byte energyToUse = 4;
                    int heatEnergyRatio = Math.max(1, ExternalHeaterHandler.defaultFurnaceEnergyCost);
                    int energyToUse1 = Math.min(energyAvailable, energyToUse * heatEnergyRatio);
                    int heat = energyToUse1 / heatEnergyRatio;
                    if(heat > 0) {
                        tileEntity.fuel += heat;
                        energyConsumed += heat * heatEnergyRatio;
                        if(!burning) {
                            tileEntity.markDirty();
                        }
                    }
                }

                if(canCook && tileEntity.fuel >= 200 && tileEntity.machineWorkProgressTime < 199) {
                    int var11 = ExternalHeaterHandler.defaultFurnaceSpeedupCost;
                    if(energyAvailable - energyConsumed > var11) {
                        energyConsumed += var11;
                        ++tileEntity.machineWorkProgressTime;
                    }
                }
            }

            return energyConsumed;
        }
    }
    
    public static class LargeFurnaceAdapter extends ExternalHeaterHandler.HeatableAdapter<TileEntityLargeFurnace> {
        public LargeFurnaceAdapter() {
        }

        private boolean canSmelt(TileEntityLargeFurnace tileEntity) {
            int j = 0;

            for(int itemstack = 0; itemstack < tileEntity.craftMatrix.getSizeInventory(); ++itemstack) {
                if(tileEntity.craftMatrix.getStackInSlot(itemstack) != null) {
                    ++j;
                }
            }

            if(j == 0) {
                return false;
            } else {
                ItemStack var4 = FurnaceCraftingManager.getInstance().findMatchingRecipe(tileEntity.craftMatrix, tileEntity.getWorldObj());
                if(var4 == null) {
                    return false;
                } else if(tileEntity.getStackInSlot(19) == null) {
                    return true;
                } else if(!tileEntity.getStackInSlot(19).isItemEqual(var4)) {
                    return false;
                } else {
                    int result = tileEntity.getStackInSlot(19).stackSize + var4.stackSize;
                    return result <= tileEntity.getInventoryStackLimit() && result <= var4.getMaxStackSize();
                }
            }
        }

        @Override
        public int doHeatTick(TileEntityLargeFurnace tileEntity, int energyAvailable, boolean redstone) {
            int energyConsumed = 0;
            boolean canCook = canSmelt(tileEntity);
            if(canCook || redstone) {
                boolean burning = tileEntity.isBurning();
                if(tileEntity.furnaceBurnTime < 200) {
                    tileEntity.currentItemBurnTime = 200;
                    byte energyToUse = 4;
                    int heatEnergyRatio = Math.max(1, ExternalHeaterHandler.defaultFurnaceEnergyCost);
                    int energyToUse1 = Math.min(energyAvailable, energyToUse * heatEnergyRatio);
                    int heat = energyToUse1 / heatEnergyRatio;
                    if(heat > 0) {
                        tileEntity.furnaceBurnTime += heat;
                        energyConsumed += heat * heatEnergyRatio;
                        
                        if(!burning) {
                            tileEntity.markDirty();
                        }
                    }
                }

                if(canCook && tileEntity.furnaceBurnTime >= 200 && tileEntity.furnaceCookTime < 199) {
                    int var11 = ExternalHeaterHandler.defaultFurnaceSpeedupCost;
                    if(energyAvailable - energyConsumed > var11) {
                        energyConsumed += var11;
                        ++tileEntity.furnaceCookTime;
                    }
                }
            }

            return energyConsumed;
        }
    }
}
