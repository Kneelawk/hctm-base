package net.dblsaiko.hctm.neoforge.client.render.model

import net.dblsaiko.hctm.client.render.model.CenterVariant
import net.dblsaiko.hctm.client.render.model.ExtVariant
import net.dblsaiko.hctm.common.block.BaseWireBlockEntity
import net.dblsaiko.hctm.common.block.Connection
import net.dblsaiko.hctm.common.block.ConnectionType
import net.dblsaiko.hctm.common.block.WireRepr
import net.minecraft.block.BlockState
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.render.model.json.ModelOverrideList
import net.minecraft.client.texture.Sprite
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockRenderView
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty

class NeoForgeWireModel(val particle: Sprite, val parts: NeoForgeWireModelParts) : BakedModel {
    override fun getQuads(state: BlockState?, face: Direction?, random: Random) = emitQuads(getItemWireState())

    override fun getQuads(
        state: BlockState?, side: Direction?, rand: Random, data: ModelData, renderType: RenderLayer?
    ) = emitQuads(data.get(WireReprsProperty.property)?.reprs ?: getItemWireState())

    override fun getModelData(
        level: BlockRenderView, pos: BlockPos, state: BlockState, modelData: ModelData
    ): ModelData {
        return modelData.derive().with(WireReprsProperty.property, WireReprsProperty(getWireState(level, pos, state)))
            .build()
    }

    fun emitQuads(state: Set<WireRepr>): List<BakedQuad> {
        val quads = mutableListOf<BakedQuad>()

        for ((side, conns) in state) {
            val s = parts.sides.getValue(side)

            val cv = when (conns.size) {
                0 -> CenterVariant.STANDALONE
                1 -> getCenterVariant(side, conns.first().edge)
                2 -> {
                    val (first, second) = conns.toList()
                    getCenterVariant(side, first.edge, second.edge)
                }
                else -> CenterVariant.CROSSING
            }

            fun getExtVariant(edge: Direction) =
                when (conns.firstOrNull { it.edge == edge }?.type) {
                    ConnectionType.INTERNAL -> ExtVariant.INTERNAL
                    ConnectionType.EXTERNAL -> ExtVariant.EXTERNAL
                    ConnectionType.CORNER -> ExtVariant.CORNER
                    null -> when (cv) {
                        CenterVariant.CROSSING -> ExtVariant.UNCONNECTED_CROSSING
                        CenterVariant.STRAIGHT_1, CenterVariant.STRAIGHT_2 -> {
                            if (conns.size == 2) ExtVariant.UNCONNECTED
                            else {
                                if (conns.first().edge.axis == edge.axis) ExtVariant.TERMINAL
                                else ExtVariant.UNCONNECTED
                            }
                        }
                        CenterVariant.STANDALONE -> {
                            when (Pair(side.axis, edge.axis)) {
                                Pair(Direction.Axis.X, Direction.Axis.Z), Pair(
                                    Direction.Axis.Z, Direction.Axis.X
                                ), Pair(
                                    Direction.Axis.Y, Direction.Axis.X
                                ) -> ExtVariant.TERMINAL
                                else -> ExtVariant.UNCONNECTED
                            }
                        }
                    }
                }

            quads += s.center.getValue(cv)
            for (edge in Direction.values().filter { it.axis != side.axis }) {
                quads += s.exts.getValue(Pair(edge, getExtVariant(edge)))
            }
        }

        return quads
    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    private fun getCenterVariant(side: Direction, edge: Direction): CenterVariant = when (side.axis) {
        Direction.Axis.X -> when (edge.axis) {
            Direction.Axis.X -> error("unreachable")
            Direction.Axis.Y -> CenterVariant.STRAIGHT_2
            Direction.Axis.Z -> CenterVariant.STRAIGHT_1
        }
        Direction.Axis.Y -> when (edge.axis) {
            Direction.Axis.X -> CenterVariant.STRAIGHT_1
            Direction.Axis.Y -> error("unreachable")
            Direction.Axis.Z -> CenterVariant.STRAIGHT_2
        }
        Direction.Axis.Z -> when (edge.axis) {
            Direction.Axis.X -> CenterVariant.STRAIGHT_1
            Direction.Axis.Y -> CenterVariant.STRAIGHT_2
            Direction.Axis.Z -> error("unreachable")
        }
    }

    private fun getCenterVariant(side: Direction, edge1: Direction, edge2: Direction): CenterVariant =
        if (edge1.axis == edge2.axis) getCenterVariant(side, edge1) else CenterVariant.CROSSING

    fun getWireState(world: BlockRenderView, pos: BlockPos, state: BlockState): Set<WireRepr> {
        return (world.getBlockEntity(pos) as? BaseWireBlockEntity)?.connections.orEmpty()
    }

    fun getItemWireState(): Set<WireRepr> {
        return setOf(
            WireRepr(
                side = Direction.DOWN,
                connections = setOf(
                    Connection(edge = Direction.NORTH, type = ConnectionType.EXTERNAL),
                    Connection(edge = Direction.SOUTH, type = ConnectionType.EXTERNAL),
                    Connection(edge = Direction.WEST, type = ConnectionType.EXTERNAL),
                    Connection(edge = Direction.EAST, type = ConnectionType.EXTERNAL)
                )
            )
        )
    }

    override fun useAmbientOcclusion() = true

    override fun hasDepth() = true

    override fun isSideLit() = true

    override fun isBuiltin() = false

    override fun getParticleSprite() = particle

    override fun getOverrides() = ModelOverrideList.EMPTY
}

data class NeoForgeWireModelParts(val sides: Map<Direction, NeoForgeWireModelPart>)

data class NeoForgeWireModelPart(
    val center: Map<CenterVariant, List<BakedQuad>>, val exts: Map<Pair<Direction, ExtVariant>, List<BakedQuad>>
)

data class WireReprsProperty(val reprs: Set<WireRepr>) {
    companion object {
        val property = ModelProperty<WireReprsProperty>()
    }
}
