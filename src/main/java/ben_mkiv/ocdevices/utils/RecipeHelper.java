package ben_mkiv.ocdevices.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IShapedRecipe;

import java.util.ArrayList;

public class RecipeHelper{
    ArrayList<IRecipe> recipeCache = new ArrayList<>();
    ItemStack recipeOutput = ItemStack.EMPTY;

    public RecipeHelper(ItemStack output){
        recipeOutput = output.copy();

        for(IRecipe recipe : CraftingManager.REGISTRY){
            if(!recipe.getRecipeOutput().getItem().equals(recipeOutput.getItem()))
                continue;

            if(recipe.getRecipeOutput().getMetadata() != recipeOutput.getMetadata())
                continue;

            recipeCache.add(recipe);
        }
    }

    public int getCount(){
        return recipeCache.size();
    }

    public ArrayList<Object> getList(){
        boolean isShaped;
        ItemStack recipeOutput;
        int recipeWidth, recipeHeight;

        ArrayList<Object> recipes = new ArrayList<>();

        for(IRecipe recipe : recipeCache) {
            isShaped = recipe instanceof IShapedRecipe;
            recipeOutput = recipe.getRecipeOutput();
            recipeWidth = recipe instanceof IShapedRecipe ? ((IShapedRecipe) recipe).getRecipeWidth() : 3;
            recipeHeight = recipe instanceof IShapedRecipe ? ((IShapedRecipe) recipe).getRecipeHeight() : 3;
            int slot = 0;

            ArrayList<Object[]> retData = new ArrayList<>();

            for (Ingredient ingredient : recipe.getIngredients()) {
                ArrayList<Object> slotData = new ArrayList<>();

                for (ItemStack stack : ingredient.getMatchingStacks()) {
                    ArrayList<Object> itemData = new ArrayList<>();
                    //add item registry name
                    itemData.add(stack.getItem().getRegistryName().toString());
                    //add stack meta
                    itemData.add(stack.getMetadata());
                    //add stack nbt
                    if (stack.hasTagCompound())
                        itemData.add(stack.getTagCompound().toString());

                    slotData.add(itemData.toArray());
                }

                retData.add(slotData.toArray());

                //fill empty slots
                for (int column = recipeWidth; column < 3; column++)
                    retData.add(new Object[]{});

                slot++;
            }

            //fill empty slots
            for (int row = recipeHeight; row < 3; row++)
                for (int column = recipeWidth; column < 3; column++)
                    retData.add(new Object[]{});

            recipes.add(new Object[]{ recipeOutput.getItem().getRegistryName(), recipeOutput.getMetadata(), recipeOutput.getCount(), isShaped, retData.toArray() });
        }


        return recipes;
    }

}
