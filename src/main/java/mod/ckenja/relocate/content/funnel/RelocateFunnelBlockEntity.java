package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.content.contraptions.ITransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.utility.BlockFace;
import mod.ckenja.relocate.foundation.block.LinkPoint;
import mod.ckenja.relocate.mixin.FunnelBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import static com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint.transformPos;

public class RelocateFunnelBlockEntity extends FunnelBlockEntity implements ITransformableBlockEntity {

    public List<LinkPoint> linkedBlocks;
    public ListTag linkPointTag;
    boolean updateLinkPoints;

    public RelocateFunnelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        linkedBlocks = new ArrayList<>();
    }

    @Override
    public void tick() {
        super.tick();
        initInteractionPoints();
    }

    public static void handleBlockEntityBehaviourEvent(BlockEntityBehaviourEvent<RelocateFunnelBlockEntity> event){
        //MixinでinvManipulationBehaviourを改変するより、継承したやつをMixinで入れたほうがいいかなって。そこんとこどうなの?
        event.remove(InvManipulationBehaviour.TYPE);
        FunnelBlockEntityAccessor access = (FunnelBlockEntityAccessor) event.getBlockEntity();
        access.setInvManipulation(new RelocateInvManipulationBehaviour(event.getBlockEntity(), (w, p, s) -> new BlockFace(p, AbstractFunnelBlock.getFunnelFacing(s)
                .getOpposite())));
        event.attach(access.invManipulation());

        //RelocateFunnelでFilterBehaviourが動くように
        BlockEntityBehaviour filtering = event.remove(FilteringBehaviour.TYPE);
        ((FilteringBehaviour)filtering).onlyActiveWhen(()->true);
        event.attach(filtering);
    }

    protected void initInteractionPoints() {
        if (!updateLinkPoints || linkPointTag == null)
            return;
        linkedBlocks.clear();

        for (Tag tag : linkPointTag) {
            linkedBlocks.add(LinkPoint.deserialize((CompoundTag) tag, level, worldPosition));
        }

        updateLinkPoints = false;
        sendData();
        setChanged();
    }

    public void writeInteractionPoints(CompoundTag compound) {
        if (updateLinkPoints && linkPointTag != null) {
            compound.put("InteractionPoints", linkPointTag);
        } else {
            ListTag pointsNBT = new ListTag();
            linkedBlocks.stream()
                    .map(point -> point.serialize(worldPosition))
                    .forEach(pointsNBT::add);
            compound.put("InteractionPoints", pointsNBT);
        }
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        writeInteractionPoints(compound);
    }

    @Override
    public void writeSafe(CompoundTag compound) {
        super.writeSafe(compound);
        writeInteractionPoints(compound);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        ListTag interactionPointTagBefore = linkPointTag;

        super.read(compound, clientPacket);
        linkPointTag = compound.getList("InteractionPoints", Tag.TAG_COMPOUND);
        if (!clientPacket)
            return;
        if (interactionPointTagBefore == null || interactionPointTagBefore.size() != linkPointTag.size())
            updateLinkPoints = true;
    }

    @Override
    public void transform(StructureTransform transform) {
        if (linkPointTag == null)
            return;

        for (Tag tag : linkPointTag) {
            transformPos((CompoundTag) tag, transform);
        }

        notifyUpdate();
    }
}
