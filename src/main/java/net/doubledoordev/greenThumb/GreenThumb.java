package net.doubledoordev.greenThumb;

import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static net.doubledoordev.greenThumb.util.Constants.MODID;


@Mod(modid = MODID, canBeDeactivated = false)
public class GreenThumb
{
    @Mod.Instance(MODID)
    public static GreenThumb instance;

    private Configuration configuration;
    boolean checkGrowth = true;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);

        configuration = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
    }


    @SubscribeEvent
    public void rightClickEvent(PlayerInteractEvent event) throws InvocationTargetException, IllegalAccessException
    {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        if (event.entityPlayer.getHeldItem() != null) return;

        IBlockState blockState = event.world.getBlockState(event.pos);
        Block block = blockState.getBlock();
        
        if (block instanceof BlockCrops)
        {
            BlockCrops crop = ((BlockCrops) block);
            if (checkGrowth && (Integer) crop.getMetaFromState(blockState) != 7) return;
            java.util.List<ItemStack> drops = crop.getDrops(event.world, event.pos, blockState, 0);
            event.world.setBlockState(event.pos, blockState.withProperty(BlockCrops.AGE, 0), 3);
            for (ItemStack itemStack : drops)
            {
                event.world.spawnEntityInWorld(new EntityItem(event.world, event.entityPlayer.posX, event.entityPlayer.posY, event.entityPlayer.posZ, itemStack));
            }
        }
        else if (block instanceof BlockNetherWart)
        {
            BlockNetherWart crop = ((BlockNetherWart) block);
            if (checkGrowth && crop.getMetaFromState(blockState) != 3) return;
            java.util.List<ItemStack> drops = crop.getDrops(event.world, event.pos, blockState, 0);
            event.world.setBlockState(event.pos, blockState.withProperty(BlockCrops.AGE, 0), 3);
            for (ItemStack itemStack : drops)
            {
                event.world.spawnEntityInWorld(new EntityItem(event.world, event.entityPlayer.posX, event.entityPlayer.posY, event.entityPlayer.posZ, itemStack));
            }
        }
    }


    public void syncConfig()
    {
        configuration.setCategoryLanguageKey(MODID, "d3.greenThumb.config.greenThumb");

        checkGrowth = configuration.getBoolean("checkGrowth", MODID, checkGrowth, "Check to see if the crop is grown 100% and only harvest then.", "d3.greenThumb.config.checkGrowth");

        if (configuration.hasChanged())
        {
            configuration.save();
        }
    }


    public void addConfigElements(List<IConfigElement> configElements)
    {
        configElements.add(new ConfigElement(configuration.getCategory(MODID.toLowerCase())));
    }
}
