package com.supermartijn642.scarecrowsterritory;

import com.supermartijn642.core.ToolType;
import com.supermartijn642.core.block.BaseBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;

/**
 * Created 11/30/2020 by SuperMartijn642
 */
public enum ScarecrowType {

    PRIMITIVE;

    private static final AxisAlignedBB[] PRIMITIVE_SHAPE = new AxisAlignedBB[]{
        new AxisAlignedBB(7.5 / 16d, 0, 7.5 / 16d, 8.5 / 16d, 26 / 16d, 8.5 / 16d),
        new AxisAlignedBB(4 / 16d, 9 / 16d, 6 / 16d, 12 / 16d, 22 / 16d, 10 / 16d),
        new AxisAlignedBB(4 / 16d, 21 / 16d, 4 / 16d, 12 / 16d, 29 / 16d, 12 / 16d)};

    private static final AxisAlignedBB[][] PRIMITIVE_SHAPES_BOTTOM = new AxisAlignedBB[4][];
    private static final AxisAlignedBB[][] PRIMITIVE_SHAPES_TOP = new AxisAlignedBB[4][];

    static{
        PRIMITIVE_SHAPES_BOTTOM[EnumFacing.NORTH.getHorizontalIndex()] = PRIMITIVE_SHAPE;
        PRIMITIVE_SHAPES_BOTTOM[EnumFacing.EAST.getHorizontalIndex()] = rotateShape(EnumFacing.NORTH, EnumFacing.EAST, PRIMITIVE_SHAPE);
        PRIMITIVE_SHAPES_BOTTOM[EnumFacing.SOUTH.getHorizontalIndex()] = rotateShape(EnumFacing.NORTH, EnumFacing.SOUTH, PRIMITIVE_SHAPE);
        PRIMITIVE_SHAPES_BOTTOM[EnumFacing.WEST.getHorizontalIndex()] = rotateShape(EnumFacing.NORTH, EnumFacing.WEST, PRIMITIVE_SHAPE);
        for(int i = 0; i < 4; i++){
            PRIMITIVE_SHAPES_TOP[i] = new AxisAlignedBB[PRIMITIVE_SHAPES_BOTTOM[i].length];
            for(int j = 0; j < PRIMITIVE_SHAPES_BOTTOM[i].length; j++)
                PRIMITIVE_SHAPES_TOP[i][j] = PRIMITIVE_SHAPES_BOTTOM[i][j].offset(0, -1, 0);
        }
    }

    public static AxisAlignedBB[] rotateShape(EnumFacing from, EnumFacing to, AxisAlignedBB[] shape){
        AxisAlignedBB[] result = new AxisAlignedBB[shape.length];
        System.arraycopy(shape, 0, result, 0, shape.length);

        int times = (to.getHorizontalIndex() - from.getHorizontalIndex() + 4) % 4;
        for(int i = 0; i < times; i++){
            for(int j = 0; j < result.length; j++){
                AxisAlignedBB box = result[j];
                result[j] = new AxisAlignedBB(1 - box.maxZ, box.minY, box.minX, 1 - box.minZ, box.maxY, box.maxX);
            }
        }

        return result;
    }

    public final EnumMap<EnumDyeColor,ScarecrowBlock> blocks = new EnumMap<>(EnumDyeColor.class);
    private final EnumMap<EnumDyeColor,ItemBlock> items = new EnumMap<>(EnumDyeColor.class);

    public void registerBlock(RegistryEvent.Register<Block> e){
        switch(this){
            case PRIMITIVE:
                Arrays.stream(EnumDyeColor.values()).forEach(color -> this.blocks.put(color, new ScarecrowBlock(this, color)));
        }
        this.blocks.values().forEach(e.getRegistry()::register);
    }

    public void registerTileEntity(RegistryEvent.Register<Block> e){
        GameRegistry.registerTileEntity(ScarecrowTile.PrimitiveScarecrowTile.class, new ResourceLocation("scarecrowsterritory", this.name().toLowerCase(Locale.ROOT) + "_tile"));
    }

    public void registerItem(RegistryEvent.Register<Item> e){
        this.blocks.forEach((color, block) -> {
            ItemBlock item = new ItemBlock(block);
            item.setRegistryName(this.getRegistryName(color));
            item.setCreativeTab(ScarecrowsTerritory.GROUP);
            this.items.put(color, item);
        });
        this.items.values().forEach(e.getRegistry()::register);
    }

    public String getRegistryName(EnumDyeColor color){
        return (color == EnumDyeColor.PURPLE ? this.name().toLowerCase(Locale.ROOT) :
            color == EnumDyeColor.SILVER ? "light_gray" : color.getName()) + "_scarecrow";
    }

    public BaseBlock.Properties getBlockProperties(EnumDyeColor color){
        switch(this){
            case PRIMITIVE:
                return BaseBlock.Properties.create(Material.CLOTH, color).sound(SoundType.CLOTH).harvestTool(ToolType.AXE).hardnessAndResistance(0.5f);
        }
        return BaseBlock.Properties.create(Material.AIR);
    }

    public AxisAlignedBB[] getBlockShape(EnumFacing facing, boolean bottom){
        switch(this){
            case PRIMITIVE:
                return bottom ? PRIMITIVE_SHAPES_BOTTOM[facing.getHorizontalIndex()] : PRIMITIVE_SHAPES_TOP[facing.getHorizontalIndex()];
        }
        return new AxisAlignedBB[]{};
    }

    public ScarecrowTile createTileEntity(){
        switch(this){
            case PRIMITIVE:
                break;
        }
        return new ScarecrowTile.PrimitiveScarecrowTile();
    }

    public BlockRenderLayer getRenderLayer(){
        switch(this){
            case PRIMITIVE:
                return BlockRenderLayer.TRANSLUCENT;
        }
        return BlockRenderLayer.SOLID;
    }

    public boolean is2BlocksHigh(){
        return this == PRIMITIVE;
    }

}
