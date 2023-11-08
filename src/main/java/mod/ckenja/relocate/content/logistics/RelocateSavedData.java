package mod.ckenja.relocate.content.logistics;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RelocateSavedData extends SavedData {
    DirectedMultigraph<UUID, DefaultEdge> RelocateGraph = new DirectedMultigraph<>(DefaultEdge.class);
    public Map<UUID, RelocateNode> RelocateNodes = new HashMap<>();
    public RelocateSavedData() {
    }
    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        return null;
    }
    public RelocateNode getNode(UUID uuid){
        return RelocateNodes.get(uuid);
    }
    public RelocateNode addNode(UUID uuid, RelocateNode relocateNode){
        return RelocateNodes.put(uuid, relocateNode);
    }
}
