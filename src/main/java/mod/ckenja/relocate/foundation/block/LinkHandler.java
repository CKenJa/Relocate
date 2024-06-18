package mod.ckenja.relocate.foundation.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.utility.Lang;

import mod.ckenja.relocate.content.funnel.RelocateFunnelBlockEntity;
import mod.ckenja.relocate.content.funnel.RelocateFunnelItem;
import mod.ckenja.relocate.registry.RelocateBlocks;
import mod.ckenja.relocate.infrastructure.networking.RelocatePackets;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import static com.jozufozu.flywheel.backend.Backend.isGameActive;
import static com.simibubi.create.AllItems.WRENCH;

@EventBusSubscriber(value = Dist.CLIENT)
public class LinkHandler {

    static List<LinkPoint> currentSelection = new ArrayList<>();
    static ItemStack currentItem;

    static long lastBlockPos = -1;

    @SubscribeEvent
    public static void rightClickingBlocksSelectsThem(PlayerInteractEvent.RightClickBlock event) {
        if (currentItem == null)
            return;
        BlockPos pos = event.getPos();
        Level world = event.getLevel();
        if (!world.isClientSide)
            return;
        Player player = event.getEntity();
        if (player != null && player.isSpectator())
            return;

        LinkPoint selected = getSelected(pos);
        BlockState state = world.getBlockState(pos);

        if (selected == null) {
            if (!RelocateFunnelItem.isRelocateFunnelBLockEntity(state))
                return;
            selected = new LinkPoint(world,pos);
            put(selected);
        }

        if (player != null) {
            Lang.builder().translate(selected.pos.toString()).sendStatus(player);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void leftClickingBlocksDeselectsThem(PlayerInteractEvent.LeftClickBlock event) {
        if (currentItem == null)
            return;
        if (!event.getLevel().isClientSide)
            return;
        BlockPos pos = event.getPos();
        if (remove(pos) != null) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    public static void flushSettings(BlockPos pos) {
        if (currentSelection == null)
            return;
        LocalPlayer player = Minecraft.getInstance().player;
        Lang.builder().translate("mechanical_arm.summary")
                    .style(ChatFormatting.WHITE)
                    .sendStatus(player);
        RelocatePackets.channel.sendToServer(new RelocateFunnelPlacementPacket(currentSelection, pos));
        currentSelection.clear();
        currentItem = null;
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (!isGameActive())
            return;

        if (event.phase == TickEvent.Phase.START)
            return;
        Player player = Minecraft.getInstance().player;

        if (player == null)
            return;

        ItemStack heldItemMainhand = player.getMainHandItem();
        if (!RelocateBlocks.RELOCATE_FUNNEL.isIn(heldItemMainhand)) {
            currentItem = null;
        } else {
            if (heldItemMainhand != currentItem) {
                currentSelection.clear();
                currentItem = heldItemMainhand;
            }

            drawOutlines(currentSelection);
        }

        checkForWrench(heldItemMainhand);
    }

    private static void checkForWrench(ItemStack heldItem) {
        if (!WRENCH.isIn(heldItem)) {
            return;
        }

        HitResult objectMouseOver = Minecraft.getInstance().hitResult;
        if (!(objectMouseOver instanceof BlockHitResult result)) {
            return;
        }

        BlockPos pos = result.getBlockPos();

        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
        if (!(be instanceof RelocateFunnelBlockEntity funnel)) {
            lastBlockPos = -1;
            currentSelection.clear();
            return;
        }

        if (lastBlockPos == -1 || lastBlockPos != pos.asLong()) {
            currentSelection.clear();
            funnel.linkedBlocks.forEach(LinkHandler::put);
            lastBlockPos = pos.asLong();
        }

        if (lastBlockPos != -1) {
            drawOutlines(currentSelection);
        }
    }

    private static void drawOutlines(Collection<LinkPoint> selection) {
        for (Iterator<LinkPoint> iterator = selection.iterator(); iterator.hasNext();) {
            LinkPoint point = iterator.next();

            if (!point.isValid()) {
                iterator.remove();
                continue;
            }

            Level level = point.level;
            BlockPos pos = point.pos;
            BlockState state = level.getBlockState(pos);
            VoxelShape shape = state.getShape(level, pos);
            if (shape.isEmpty())
                continue;

            CreateClient.OUTLINER.showAABB(point, shape.bounds()
                            .move(pos))
                    .lineWidth(1 / 16f);
        }
    }

    private static void put(LinkPoint point) {
        currentSelection.add(point);
    }

    private static LinkPoint remove(BlockPos pos) {
        LinkPoint result = getSelected(pos);
        if (result != null)
            currentSelection.remove(result);
        return result;
    }

    private static LinkPoint getSelected(BlockPos pos) {
        for (LinkPoint point : currentSelection)
            if (point.pos.equals(pos))
                return point;
        return null;
    }

}
