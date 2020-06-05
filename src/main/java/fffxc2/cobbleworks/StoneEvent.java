package fffxc2.cobbleworks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod.EventBusSubscriber
public class StoneEvent {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void stoneGen(BlockEvent.FluidPlaceBlockEvent event) {
        if (event.getOriginalState().getBlock() == Blocks.FLOWING_LAVA) {
            BlockPos lavaPos = event.getLiquidPos(), waterPos = null;
            World world = event.getWorld();
            for (BlockPos pos : new BlockPos[]{lavaPos.east(), lavaPos.west(), lavaPos.north(), lavaPos.south()})
                if (world.getBlockState(pos).getBlock().getMaterial(world.getBlockState(pos)) == Material.WATER) {
                    waterPos = pos;
                    break;
                }
            IBlockState targetBlock = null;
            if (waterPos != null) {
                switch (world.getBlockState(waterPos).getBlock().getMetaFromState(world.getBlockState(waterPos))) {
                    case 2:
                    case 3:
                        targetBlock = getBlockForResorceName("minecraft:stone[variant=diorite]");
                        break;
                    case 4:
                    case 5:
                        targetBlock = getBlockForResorceName("minecraft:stone[variant=granite]");
                        break;
                    case 6:
                    case 7:
                        targetBlock = getBlockForResorceName("minecraft:stone[variant=andesite]");
                        break;
                    default:
                        targetBlock = getBlockForResorceName("cobblestone");
                }
            }
            event.setNewState(targetBlock);
        }
    }

    private IBlockState getBlockForResorceName(String resourceName) {
        try {
            String[] split = resourceName.split("\\[");
            for (String s : split){
                System.out.println("Split: "+s);
            }
            IBlockState target = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(split[0])).getDefaultState();

            if (split.length > 1) {
                // Strip tailing ], then split on equals
                String[] propertyInfo = split[1].substring(0,split[1].indexOf("]")).split("=");
                target = updatePropertyValue(target,propertyInfo[0],propertyInfo[1]);
            }

            return target;
        } catch(Exception e) {
            // If anything goes wrong, just return cobble
            System.out.println("Exception - Unable to find: "+resourceName);
            e.printStackTrace();
            return Blocks.COBBLESTONE.getDefaultState();
        }
    }

    private IBlockState updatePropertyValue(IBlockState blockState, String targetPropertyString, String targetValueString) {
        IProperty targetProperty = blockState.getBlock().getBlockState().getProperty(targetPropertyString);
        for (Object value : targetProperty.getAllowedValues()) {
            if (value.toString().equals(targetValueString)) {
                blockState = blockState.withProperty(targetProperty, blockState.getValue(targetProperty).getClass().cast(value));
            }
        }

        return blockState;
    }
}
