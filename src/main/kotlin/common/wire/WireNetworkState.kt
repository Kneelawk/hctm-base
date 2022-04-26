package net.dblsaiko.hctm.common.wire

import com.google.common.collect.HashMultimap
import net.dblsaiko.hctm.common.graph.Graph
import net.dblsaiko.hctm.common.graph.Link
import net.dblsaiko.hctm.common.graph.Node
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.PersistentState
import net.minecraft.world.World
import java.util.*

typealias NetNode = Node<NetworkPart<out PartExt>, Nothing?>
typealias NetGraph = Graph<NetworkPart<out PartExt>, Nothing?>
typealias NetLink = Link<NetworkPart<out PartExt>, Nothing?>

typealias TNetNode<T> = Node<NetworkPart<T>, Nothing>

class WireNetworkState(val world: ServerWorld) : PersistentState() {
    var controller = WireNetworkController(::markDirty, world)

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        return nbt.copyFrom(controller.toTag())
    }

    companion object {
        fun load(tag: NbtCompound, world: ServerWorld): WireNetworkState {
            val state = WireNetworkState(world)
            state.controller = WireNetworkController.fromTag(tag, world)
            state.controller.changeListener = state::markDirty
            return state
        }
    }
}

class WireNetworkController(var changeListener: () -> Unit = {}, internal val world: ServerWorld? = null) {
    private val networks = mutableMapOf<UUID, Network>()
    @JvmSynthetic
    internal val networksInPos = HashMultimap.create<BlockPos, Network>()
    @JvmSynthetic
    internal val nodesToNetworks = mutableMapOf<NetNode, UUID>()

    private var changed = setOf<NetNode>()

    fun onChanged(world: ServerWorld, pos: BlockPos) {
        val worldExts = WireRegistries.getPartExtsInBlock(world, pos)

        onNodesChanged(worldExts, pos, world)
    }

    private fun onNodesChanged(worldExts: Set<PartExt>, pos: BlockPos, world: ServerWorld) {
        val new = worldExts.toMutableSet()

        for (net in networksInPos[pos].toSet()) {
            for (node in net.getNodesAt(pos)) {
                if (node.data.ext !in worldExts) {
                    net.destroyNode(node)
                }
                new -= node.data.ext
            }
        }

        for (ext in new) {
            val net = createNetwork()
            val node = net.createNode(pos.toImmutable(), ext)
            updateNodeConnections(world, node)
        }
    }

    fun getNodesAt(pos: BlockPos): Set<NetNode> {
        return networksInPos[pos].flatMap { net -> net.getNodesAt(pos).map { it } }.toSet()
    }

    fun getNetworksAt(pos: BlockPos): Set<Network> {
        return networksInPos[pos]
    }

    fun getNetworks() = networks.values.toSet()

    fun updateNodeConnections(world: ServerWorld, node: NetNode) {
        changeListener()
        val nodeNetId = getNetIdForNode(node)

        val nv = NodeView(world)
        val oldConnections = node.connections.map { it.other(node) }.toSet()
        val ids = node.data.ext.tryConnect(node, world, node.data.pos, nv)
        val returnedConnections = ids.filter { node in it.data.ext.tryConnect(it, world, it.data.pos, nv) }.toSet()
        val newConnections = returnedConnections.filter { getNetIdForNode(it) != nodeNetId || it !in oldConnections }
        val removedConnections = oldConnections.filter { it !in returnedConnections }

        for (other in newConnections) {
            val net = networks.getValue(nodeNetId)
            if (getNetIdForNode(other) != nodeNetId) {
                val otherNet = networks.getValue(getNetIdForNode(other))
                net.merge(otherNet)
            }

            net.link(node, other)
        }

        val net = networks.getValue(nodeNetId)
        for (other in removedConnections) {
            net.unlink(node, other)
        }

        if (removedConnections.isNotEmpty()) {
            // This is expensive, so I want to avoid it if at all possible
            net.split().forEach { rebuildRefs(it.id) }
            if (net.getNodes().isEmpty()) destroyNetwork(net.id)
            rebuildRefs(net.id)
        }
    }

    fun getNetIdForNode(node: NetNode) = nodesToNetworks.getValue(node)

    fun getNetwork(id: UUID): Network? = networks[id]

    fun createNetwork(): Network {
        changeListener()
        val net = Network(this, UUID.randomUUID())
        networks += net.id to net
        return net
    }

    fun destroyNetwork(id: UUID) {
        changeListener()
        networks -= id

        for ((k, v) in networksInPos.entries().toSet()) {
            if (v.id == id) networksInPos.remove(k, v)
        }

        nodesToNetworks -= nodesToNetworks.filter { it.value == id }.keys
    }

    fun rebuildRefs(vararg networks: UUID) {
        changeListener()
        val toRebuild = networks.takeIf { it.isNotEmpty() }?.map { Pair(it, this.networks[it]) }
            ?: this.networks.entries.map { Pair(it.key, it.value) }

        for ((id, net) in toRebuild) {
            for ((pos, net) in networksInPos.entries().toSet()) {
                if (net.id == id) networksInPos.remove(pos, net)
            }

            nodesToNetworks -= nodesToNetworks.filterValues { it == id }.keys

            if (net != null) {
                net.rebuildRefs()
                net.getNodes()
                    .onEach { nodesToNetworks[it] = net.id }
                    .map { it.data.pos }.toSet()
                    .forEach { networksInPos.put(it, net) }
            }
        }
    }

    fun cleanup() {
        for (net in networks.values.toSet()) {
            if (net.getNodes().isEmpty()) {
                destroyNetwork(net.id)
            }
        }
    }

    fun toTag(): NbtCompound {
        val tag = NbtCompound()
        val list = NbtList()
        networks.values.map { it.toTag(NbtCompound()) }.forEach { list.add(it) }
        tag.put("networks", list)
        return tag
    }

    fun scheduleUpdate(node: Node<NetworkPart<out PartExt>, Nothing?>) {
        changed += node
    }

    fun flushUpdates() {
        while (changed.isNotEmpty()) {
            val n = changed.first()
            world?.also { n.data.ext.onChanged(n, world, n.data.pos) }
            changed -= n
        }
    }

    companion object {
        fun fromTag(tag: NbtCompound, world: ServerWorld? = null): WireNetworkController {
            val controller = WireNetworkController(world = world)

            val sNetworks = tag.getList("networks", NbtType.COMPOUND)
            for (sNetwork in sNetworks.map { it as NbtCompound }) {
                val net = Network.fromTag(controller, sNetwork) ?: continue
                controller.networks += net.id to net
            }
            controller.rebuildRefs()
            controller.cleanup()
            return controller
        }
    }

}

class Network(val controller: WireNetworkController, val id: UUID) {
    private val graph = NetGraph()

    private val nodesInPos = HashMultimap.create<BlockPos, NetNode>()

    fun getNodesAt(pos: BlockPos) = nodesInPos[pos].toSet()

    fun createNode(pos: BlockPos, ext: PartExt): NetNode {
        controller.changeListener()
        val node = graph.add(NetworkPart(pos, ext))
        nodesInPos.put(pos, node)
        controller.networksInPos.put(pos, this)
        controller.nodesToNetworks[node] = this.id
        controller.scheduleUpdate(node)
        return node
    }

    fun destroyNode(node: NetNode) {
        controller.changeListener()
        val connected = node.connections.map { it.other(node) }
        graph.remove(node)
        controller.scheduleUpdate(node)
        for (other in connected) controller.scheduleUpdate(other)

        split().forEach { controller.rebuildRefs(it.id) }

        if (graph.nodes.isEmpty()) controller.destroyNetwork(id)
        controller.rebuildRefs(id)
    }

    fun link(node1: NetNode, node2: NetNode) {
        graph.link(node1, node2, null)
        controller.scheduleUpdate(node1)
        controller.scheduleUpdate(node2)
    }

    fun unlink(node1: NetNode, node2: NetNode) {
        graph.unlink(node1, node2, null)
        controller.scheduleUpdate(node1)
        controller.scheduleUpdate(node2)
    }

    fun merge(other: Network) {
        controller.changeListener()
        if (other.id != id) {
            graph.join(other.graph)
            nodesInPos.putAll(other.nodesInPos)
            for (key in controller.networksInPos.keySet()) {
                controller.networksInPos.replaceValues(key, controller.networksInPos.get(key).map { if (it == other) this else it }.toSet())
            }
            controller.nodesToNetworks += graph.nodes.associate { it to this.id }
            controller.destroyNetwork(other.id)
        }
    }

    fun getNodes() = graph.nodes

    fun split(): Set<Network> {
        val newGraphs = graph.split()

        if (newGraphs.isNotEmpty()) {
            controller.changeListener()

            val networks = newGraphs.map {
                val net = controller.createNetwork()
                net.graph.join(it)
                net
            }

            networks.forEach { controller.rebuildRefs(it.id) }
            controller.rebuildRefs(id)

            return networks.toSet()
        }

        return emptySet()
    }

    fun rebuildRefs() {
        controller.changeListener()
        nodesInPos.clear()
        for (node in graph.nodes) {
            nodesInPos.put(node.data.pos, node)
        }
    }

    fun toTag(tag: NbtCompound): NbtCompound {
        val serializedNodes = mutableListOf<NbtCompound>()
        val serializedLinks = mutableListOf<NbtCompound>()
        val nodes = graph.nodes.toList()
        val n1 = nodes.withIndex().associate { it.value to it.index }
        for (node in nodes) {
            serializedNodes += node.data.toTag(NbtCompound())
        }
        for (link in nodes.flatMap { it.connections }.distinct()) {
            val sLink = NbtCompound()
            sLink.putInt("first", n1.getValue(link.first))
            sLink.putInt("second", n1.getValue(link.second))
            // sLink.put("data", link.data.toTag())
            serializedLinks += sLink
        }
        tag.put("nodes", NbtList().also { t -> serializedNodes.forEach { t.add(it) } })
        tag.put("links", NbtList().also { t -> serializedLinks.forEach { t.add(it) } })
        tag.putUuid("id", id)
        return tag
    }

    companion object {
        fun fromTag(controller: WireNetworkController, tag: NbtCompound): Network? {
            val id = tag.getUuid("id")
            val network = Network(controller, id)
            val sNodes = tag.getList("nodes", NbtType.COMPOUND)
            val sLinks = tag.getList("links", NbtType.COMPOUND)

            val nodes = mutableListOf<NetNode?>()

            for (node in sNodes.map { it as NbtCompound }) {
                val part = NetworkPart.fromTag(node)
                if (part == null) {
                    nodes += null as NetNode?
                    continue
                }
                nodes += network.createNode(part.pos, part.ext)
            }

            for (link in sLinks.map { it as NbtCompound }) {
                val first = nodes[link.getInt("first")]
                val second = nodes[link.getInt("second")]
                // val data = /* something */
                if (first != null && second != null) {
                    network.graph.link(first, second, null)
                }
            }

            network.rebuildRefs()

            return network
        }
    }

}

data class NetworkPart<T : PartExt>(var pos: BlockPos, val ext: T) {
    fun toTag(tag: NbtCompound): NbtCompound {
        tag.putInt("x", pos.x)
        tag.putInt("y", pos.y)
        tag.putInt("z", pos.z)

        ext.toTag()?.let { tag.put("ext", it) }

        val typeId = WireRegistries.EXT_PART_TYPE.getId(ext.type)
        // This would be a programmer error, so better make a loud noise!
            ?: throw IllegalStateException("Attempted to store unknown PartExtType: ${ext.type}")

        tag.putString("type", typeId.toString())

        return tag
    }

    companion object {
        fun fromTag(tag: NbtCompound): NetworkPart<PartExt>? {
            val type = when {
                tag.contains("type", NbtType.STRING) -> {
                    val typeId = Identifier(tag.getString("type"))
                    val type = WireRegistries.EXT_PART_TYPE[typeId]
                    if (type == null) {
                        System.err.println("Tried to load unknown PartExtType: $typeId")
                        return null
                    }
                    type
                }
                tag.contains("block", NbtType.STRING) -> {
                    val blockId = Identifier(tag.getString("block"))
                    val block = Registry.BLOCK[blockId]
                    if (block == Blocks.AIR) {
                        System.err.println("Tried to load PartExt with unknown Block Id: $blockId")
                        return null
                    }
                    if (block !is PartExtProvider) {
                        System.err.println(
                            "Tried to load PartExt from Block that cannot provide PartExts. Block Id: $blockId"
                        )
                        return null
                    }
                    block.partExtType
                }
                else -> return null
            }

            val pos = BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"))

            val extTag = tag["ext"]
            val ext = type.createExtFromTag(extTag)

            if (ext == null) {
                System.err.println("Unable to load PartExt with part type: $type, pos: $pos")
                return null
            }

            return NetworkPart(pos, ext)
        }
    }
}

interface PartExtType {
    fun createExtsForContainer(world: World, pos: BlockPos, provider: PartExtProvider): Sequence<PartExt>

    fun createExtFromTag(tag: NbtElement?): PartExt?
}

/**
 * This must be immutable and have equals/hashCode implemented correctly.
 * You **can** store data here, but again, it must be immutable, and hashed correctly.
 * Kotlin's data class with only `val`s used should do all this automatically, so use that.
 */
interface PartExt {
    val type: PartExtType

    /**
     * Return the nodes that this node wants to connect to.
     * Will only actually connect if other node also wants to connect to this
     */
    fun tryConnect(self: NetNode, world: ServerWorld, pos: BlockPos, nv: NodeView): Set<NetNode>

    fun toTag(): NbtElement?

    /**
     * Node created, removed, connected, disconnected
     */
    fun onChanged(self: NetNode, world: ServerWorld, pos: BlockPos) {
    }

    override fun hashCode(): Int

    override fun equals(other: Any?): Boolean
}

interface PartExtProvider {
    val partExtType: PartExtType
}

class NodeView(world: ServerWorld) {
    private val wns = world.getWireNetworkState()

    fun getNodes(pos: BlockPos): Set<NetNode> = wns.controller.getNodesAt(pos)
}

fun ServerWorld.getWireNetworkState(): WireNetworkState {
    return persistentStateManager.getOrCreate(
        { WireNetworkState.load(it, this) },
        { WireNetworkState(this) },
        "wirenet"
    )
}