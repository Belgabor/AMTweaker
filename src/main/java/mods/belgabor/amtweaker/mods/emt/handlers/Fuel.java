/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Belgabor
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mods.belgabor.amtweaker.mods.emt.handlers;

import defeatedcrow.addonforamt.economy.api.RecipeManagerEMT;
import defeatedcrow.addonforamt.economy.api.energy.IFuelFluid;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraftforge.fluids.Fluid;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.toFluid;

/**
 * Created by Belgabor on 13.05.2015.
 */
@ZenClass("mods.emt.Fuel")
public class Fuel {
    // Adding a new barrel brewing recipe
    @ZenMethod
    public static void add(ILiquidStack fuel, int amount) {
        Fluid cfuel = toFluid(fuel).getFluid();
        if (cfuel == null) {
            MineTweakerAPI.getLogger().logError("Fluid Fuel: Fuel may be null!");
            return;
        }
        if (findFuel(cfuel) != null) {
            MineTweakerAPI.getLogger().logError("Fluid Fuel: Fuel " + cfuel.toString() + " already registered");
            return;
        }
        MineTweakerAPI.apply(new FuelAdd(cfuel, amount));
    }

    private static IFuelFluid findFuel(Fluid fuel) {
        for(IFuelFluid f : RecipeManagerEMT.fuelRegister.getRecipes()) {
            if (fuel.getName().equals(f.getInput().getName()))
                return f;
        }
        return null;
    }

    private static class FuelAdd implements IUndoableAction {
        private final Fluid fuel;
        private final int amount;
        private Boolean applied;

        public FuelAdd(Fluid fuel, int amount)  {
            this.fuel = fuel;
            this.amount = amount;
            this.applied = false;
        }

        @Override
        public void apply() {
            if (!applied) {
                RecipeManagerEMT.fuelRegister.addFuel(fuel, amount);
                applied = true;
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            if (applied) {
                IFuelFluid f = findFuel(fuel);
                if (f == null) {
                    MineTweakerAPI.getLogger().logError("Fluid Fuel: Entry unexpectedly not found!");
                } else {
                    RecipeManagerEMT.fuelRegister.getRecipes().remove(f);
                    applied = false;
                }
            }
        }

        @Override
        public String describe() {
            return String.format("Registering fluid fuel %s, amount %d", fuel.getName(), amount);
        }

        @Override
        public String describeUndo() {
            return String.format("Removing registered fluid fuel %s, amount %d", fuel.getName(), amount);
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Removing a clay pan recipe
    @ZenMethod
    public static void remove(ILiquidStack fuel) {
        Fluid cfuel = toFluid(fuel).getFluid();
        if (cfuel == null) {
            MineTweakerAPI.getLogger().logError("Fluid fuel removal: Input may not be null!");
            return;
        }
        if (findFuel(cfuel) == null) {
            MineTweakerAPI.getLogger().logError("Fluid fuel removal: Fuel " + cfuel.toString() + " isn't registered");
            return;
        }
        MineTweakerAPI.apply(new FuelRemove(cfuel));
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class FuelRemove implements IUndoableAction {
        private Integer amount = null;
        private final Fluid fuel;
        private Boolean applied = false;

        public FuelRemove(Fluid fuel) {
            this.fuel = fuel;
        }

        @Override
        public void apply() {
            if (!applied) {
                IFuelFluid f = findFuel(fuel);
                if (f == null) {
                    MineTweakerAPI.getLogger().logError("Fluid fuel removal: Couldn't apply removal (fuel unexpectedly not found)");
                    return;
                }
                amount = f.getGenerateAmount();
                RecipeManagerEMT.fuelRegister.getRecipes().remove(f);
                applied = true;
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            if (applied) {
                RecipeManagerEMT.fuelRegister.addFuel(fuel, amount);
                applied = false;
            }
        }

        @Override
        public String describeUndo() {
            return String.format("Restoring fluid fuel %s, amount %d", fuel.getName(), amount);
        }

        @Override
        public String describe() {
            return String.format("Removing registered fluid fuel %s", fuel.getName());
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }

    }

}
