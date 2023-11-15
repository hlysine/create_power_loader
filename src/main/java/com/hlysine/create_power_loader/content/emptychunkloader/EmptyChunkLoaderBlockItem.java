package com.hlysine.create_power_loader.content.emptychunkloader;

import com.hlysine.create_power_loader.CPLBlocks;
import com.hlysine.create_power_loader.CPLTags.AllEntityTags;
import com.simibubi.create.foundation.utility.VecHelper;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;


@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EmptyChunkLoaderBlockItem extends BlockItem {

    public final BlockEntry<? extends Block> reward;

    private EmptyChunkLoaderBlockItem(Block block, Properties properties, BlockEntry<? extends Block> reward) {
        super(block, properties);
        this.reward = reward;
    }

    public static EmptyChunkLoaderBlockItem createAndesite(Block block, Properties properties) {
        return new EmptyChunkLoaderBlockItem(block, properties, CPLBlocks.ANDESITE_CHUNK_LOADER);
    }

    public static EmptyChunkLoaderBlockItem createBrass(Block block, Properties properties) {
        return new EmptyChunkLoaderBlockItem(block, properties, CPLBlocks.BRASS_CHUNK_LOADER);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack heldItem, Player player, LivingEntity entity,
                                                  InteractionHand hand) {
        if (!AllEntityTags.CHUNK_LOADER_CAPTURABLE.matches(entity))
            return InteractionResult.PASS;

        Level world = player.level();
        spawnCaptureEffects(world, entity.position());
        if (world.isClientSide)
            return InteractionResult.FAIL;

        giveChunkLoaderTo(player, heldItem, hand);
        entity.discard();
        return InteractionResult.FAIL;
    }

    protected void giveChunkLoaderTo(Player player, ItemStack heldItem, InteractionHand hand) {
        ItemStack filled = reward.asStack();
        if (!player.isCreative())
            heldItem.shrink(1);
        if (heldItem.isEmpty()) {
            player.setItemInHand(hand, filled);
            return;
        }
        player.getInventory()
                .placeItemBackInInventory(filled);
    }

    private void spawnCaptureEffects(Level world, Vec3 vec) {
        if (world.isClientSide) {
            for (int i = 0; i < 80; i++) {
                Vec3 position = VecHelper.offsetRandomly(vec, world.random, 1.5f);
                Vec3 motion = position.subtract(vec).scale(1.5f);
                world.addParticle(ParticleTypes.PORTAL, position.x, position.y, position.z, motion.x, motion.y, motion.z);
            }
            return;
        }

        BlockPos soundPos = BlockPos.containing(vec);
        world.playSound(null, soundPos, SoundEvents.GHAST_HURT, SoundSource.HOSTILE, .25f, .75f);
        world.playSound(null, soundPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, .5f, .75f);
    }
}
