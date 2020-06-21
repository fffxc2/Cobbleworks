package fffxc2.cobbleworks;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class Util {
    // Consider doing this via Block.getStateFromMeta
    //Eg: <modid>:<blockname>@<meta>
    // Downside, less human readable, upside code is less messy
    public static IBlockState getBlockForResorceName(String resourceName) {
        try {
            String[] split = resourceName.split("\\[");
            IBlockState target = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(split[0])).getDefaultState();

            if (split.length > 1) {
                // Strip tailing ], then split on equals
                String[] propertyInfo = split[1].substring(0,split[1].indexOf("]")).split("=");
                target = updatePropertyValue(target,propertyInfo[0],propertyInfo[1]);
            }

            return target;
        } catch(Exception e) {
            // If anything goes wrong, just return cobble
            System.out.println("Invalid replacement definition: "+resourceName+", replacing with cobblestone");
            return Blocks.COBBLESTONE.getDefaultState();
        }
    }

    private static IBlockState updatePropertyValue(IBlockState blockState, String targetPropertyString, String targetValueString) {
        IProperty targetProperty = blockState.getBlock().getBlockState().getProperty(targetPropertyString);
        for (Object value : targetProperty.getAllowedValues()) {
            if (value.toString().equalsIgnoreCase(targetValueString)) {
                blockState = blockState.withProperty(targetProperty, blockState.getValue(targetProperty).getClass().cast(value));
            }
        }

        return blockState;
    }
}
