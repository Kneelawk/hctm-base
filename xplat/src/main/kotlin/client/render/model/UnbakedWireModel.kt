package net.dblsaiko.hctm.client.render.model

import net.dblsaiko.hctm.client.render.model.CenterVariant.*
import net.dblsaiko.hctm.client.render.model.ExtVariant.*
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.Baker
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Direction.*
import net.minecraft.util.math.Direction.Axis.*
import net.minecraft.util.math.Direction.AxisDirection.NEGATIVE
import net.minecraft.util.math.Direction.AxisDirection.POSITIVE
import net.minecraft.util.math.Vec2f
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import java.util.function.Function
import kotlin.math.PI
import kotlin.math.atan2

class UnbakedWireModel(
    val factory: WireModelFactory,
    val texture: Identifier,
    val cableWidth: Float,
    val cableHeight: Float,
    val textureSize: Float
) : UnbakedModel {

    private val armLength: Float = (1 - cableWidth) / 2
    private val armInnerLength: Float = armLength - cableHeight
    private val armInnerSp: Float = armLength - armInnerLength
    private val scaleFactor: Float = textureSize / 16F

    // texture positions
    private val arm1TopUv = Vector2f(0.0f, 0.0f)
    private val arm2TopUv = Vector2f(0.0f, armLength + cableWidth) / scaleFactor
    private val centerTopUv = Vector2f(0.0f, armLength) / scaleFactor
    private val centerTopCUv = Vector2f(0.0f, 1.0f) / scaleFactor
    private val arm1Side1Uv = Vector2f(cableWidth, 0.0f) / scaleFactor
    private val arm2Side1Uv = Vector2f(cableWidth, armLength + cableWidth) / scaleFactor
    private val centerSide1Uv = Vector2f(cableWidth, armLength) / scaleFactor
    private val arm1Side2Uv = Vector2f(cableWidth + cableHeight, 0.0f) / scaleFactor
    private val arm2Side2Uv = Vector2f(cableWidth + cableHeight, armLength + cableWidth) / scaleFactor
    private val centerSide2Uv = Vector2f(cableWidth + cableHeight, armLength) / scaleFactor
    private val arm1BottomUv = Vector2f(cableWidth + 2 * cableHeight, 0.0f) / scaleFactor
    private val arm2BottomUv = Vector2f(cableWidth + 2 * cableHeight, armLength + cableWidth) / scaleFactor
    private val centerBottomUv = Vector2f(cableWidth + 2 * cableHeight, armLength) / scaleFactor
    private val cableFrontUv = Vector2f(cableWidth, 1.0f) / scaleFactor
    private val cableBackUv = Vector2f(cableWidth + cableHeight, 1.0f) / scaleFactor
    private val cornerTop1Uv = Vector2f(0.0f, 1.0f + cableWidth) / scaleFactor
    private val cornerTop2Uv = Vector2f(cableWidth + 2 * cableHeight, 1.0f + cableWidth) / scaleFactor
    private val cornerSide1Uv = Vector2f(cableWidth, 1.0f + cableWidth) / scaleFactor
    private val cornerSide2Uv = Vector2f(cableWidth + cableHeight, 1.0f + cableWidth) / scaleFactor
    private val icornerSide1Uv = Vector2f(2 * cableWidth + 2 * cableHeight, 0.0f) / scaleFactor
    private val icornerSide2Uv = Vector2f(2 * cableWidth + 2 * cableHeight, cableHeight) / scaleFactor
    private val center8Top1Uv = Vector2f(0.0f, 0.25f) / scaleFactor
    private val center8Top2Uv = arm2TopUv
    private val center8Bottom1Uv = Vector2f(cableWidth + 2 * cableHeight, 0.25f) / scaleFactor
    private val center8Bottom2Uv = arm2BottomUv
    private val center8Arm1Side1Uv = Vector2f(cableWidth, 0.25f) / scaleFactor
    private val center8Arm1Side2Uv = Vector2f(cableWidth + cableHeight, 0.25f) / scaleFactor
    private val center8Arm2Side1Uv = arm2Side1Uv
    private val center8Arm2Side2Uv = arm2Side2Uv
    private val innerTop1Uv = Vector2f(0.0f, armInnerSp) / scaleFactor
    private val innerTop2Uv = arm2TopUv
    private val innerBottom1Uv = Vector2f(cableWidth + 2 * cableHeight, armInnerSp) / scaleFactor
    private val innerBottom2Uv = arm2BottomUv
    private val innerArm1Side1Uv = Vector2f(cableWidth, armInnerSp) / scaleFactor
    private val innerArm1Side2Uv = Vector2f(cableWidth + cableHeight, armInnerSp) / scaleFactor
    private val innerArm2Side1Uv = arm2Side1Uv
    private val innerArm2Side2Uv = arm2Side2Uv

    private val materials = Materials(standardAO = true, cornerAO = false)

    override fun bake(ml: Baker, getTexture: Function<SpriteIdentifier, Sprite>, settings: ModelBakeSettings, p3: Identifier): BakedModel {
        val sid = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, texture)
        return factory.build(getTexture.apply(sid), CacheKey(cableWidth, cableHeight, textureSize)) {
            generateParts(it, RenderData(this.materials))
        }
    }

    private fun generateParts(modelBuildCtx: WireModelBuildCtx, d: RenderData) {
        Direction.entries.forEach { generateSide(modelBuildCtx.buildPart(it), d, it) }
    }

    private fun generateSide(builder: WireModelPartBuilder, d: RenderData, side: Direction) {
        CenterVariant.entries.forEach { generateCenter(builder.buildCenter(it), d, side, it) }

        for (ext in Direction.entries.filter { it.axis != side.axis }) {
            for (v in ExtVariant.entries) {
                generateExt(builder.buildExt(ext, v), d, side, ext, v)
            }
        }
    }

    private fun generateCenter(builder: QuadMeshBuilder, d: RenderData, side: Direction, variant: CenterVariant) {
        val axis = when (variant) {
            STRAIGHT_1, STANDALONE, CROSSING -> when (side.axis) {
                X -> Z
                Y -> X
                Z -> X
                null -> throw AssertionError()
            }
            STRAIGHT_2 -> when (side.axis) {
                X -> Y
                Y -> Z
                Z -> Y
                null -> throw AssertionError()
            }
        }

        val (topUv, bottomUv) = when (variant) {
            CROSSING -> Pair(centerTopCUv, centerTopCUv)
            STRAIGHT_1, STRAIGHT_2, STANDALONE -> Pair(centerTopUv, centerBottomUv)
        }

        box(
            Vector3f(armLength, 0f, armLength),
            Vector3f(1 - armLength, cableHeight, 1 - armLength),
            down = UvCoords(bottomUv, cableWidth / scaleFactor, cableWidth / scaleFactor),
            up = UvCoords(topUv, cableWidth / scaleFactor, cableWidth / scaleFactor)
        ).transform(getExtGenInfo(side, Direction.from(axis, POSITIVE)).first).into(builder, d.materials.standardAO)

        builder.finish()
    }

    private fun generateExt(builder: QuadMeshBuilder, d: RenderData, side: Direction, edge: Direction, variant: ExtVariant) {
        val baseLength = when (variant) {
            EXTERNAL -> armLength
            INTERNAL -> armInnerLength
            CORNER -> armLength
            UNCONNECTED -> 0f
            UNCONNECTED_CROSSING -> 0f
            TERMINAL -> armLength - 0.25f
        }

        val origin = Vec2f(armLength, armLength - baseLength)

        val (mat, dir) = getExtGenInfo(side, edge)

        val swapUnconnectedSides = (side.direction == POSITIVE) xor ((side.axis == Y && edge.axis == X) || (side.axis == X && edge.axis == Y) || (side.axis == Z && edge.axis == Y))

        if (dir == POSITIVE) {
            val uvTop = when (variant) {
                EXTERNAL, UNCONNECTED, UNCONNECTED_CROSSING -> arm1TopUv
                INTERNAL -> innerTop1Uv
                CORNER -> arm1TopUv
                TERMINAL -> center8Top1Uv
            }

            val uvBottom = when (variant) {
                EXTERNAL, UNCONNECTED, UNCONNECTED_CROSSING -> arm1BottomUv
                INTERNAL -> innerBottom1Uv
                CORNER -> arm1BottomUv
                TERMINAL -> center8Bottom1Uv
            }

            val (uvSide1, uvSide2) = when (variant) {
                EXTERNAL, CORNER, UNCONNECTED, UNCONNECTED_CROSSING -> Pair(arm1Side1Uv, arm1Side2Uv)
                INTERNAL -> Pair(innerArm1Side1Uv, innerArm1Side2Uv)
                TERMINAL -> Pair(center8Arm1Side1Uv, center8Arm1Side2Uv)
            }

            val uvFront = cableFrontUv.takeIf { variant in setOf(EXTERNAL, TERMINAL) }

            box(
                Vector3f(armLength, 0f, 1 - armLength),
                Vector3f(1 - armLength, cableHeight, 1 - armLength + baseLength),
                down = UvCoords(uvBottom, cableWidth / scaleFactor, baseLength / scaleFactor),
                up = UvCoords(uvTop, cableWidth / scaleFactor, baseLength / scaleFactor),
                south = uvFront?.let { UvCoords(uvFront, cableHeight / scaleFactor, cableWidth / scaleFactor, BAKE_ROTATE_90 + BAKE_FLIP_U) },
                west = UvCoords(uvSide1, cableHeight / scaleFactor, baseLength / scaleFactor, BAKE_ROTATE_90 + BAKE_FLIP_U),
                east = UvCoords(uvSide2, cableHeight / scaleFactor, baseLength / scaleFactor, BAKE_ROTATE_90 + BAKE_FLIP_U)
            ).transform(mat).into(builder, d.materials.standardAO)

            when (variant) {
                INTERNAL -> {
                    box(
                        Vector3f(armLength, 0f, 1 - cableHeight),
                        Vector3f(1 - armLength, cableHeight, 1f),
                        up = UvCoords(cableFrontUv, cableHeight / scaleFactor, cableWidth / scaleFactor, BAKE_ROTATE_90),
                        west = UvCoords(icornerSide1Uv, cableHeight / scaleFactor, cableHeight / scaleFactor, BAKE_ROTATE_180),
                        east = UvCoords(icornerSide2Uv, cableHeight / scaleFactor, cableHeight / scaleFactor, BAKE_ROTATE_180)
                    ).transform(mat).into(builder, d.materials.standardAO)
                }
                CORNER -> {
                    box(
                        Vector3f(armLength, 0f, 1f),
                        Vector3f(1 - armLength, cableHeight, 1 + cableHeight),
                        up = UvCoords(cornerTop1Uv, cableWidth / scaleFactor, cableHeight / scaleFactor),
                        south = UvCoords(cornerTop2Uv, cableWidth / scaleFactor, cableHeight / scaleFactor),
                        west = UvCoords(cornerSide1Uv, cableHeight / scaleFactor, cableHeight / scaleFactor, BAKE_FLIP_V),
                        east = UvCoords(cornerSide2Uv, cableHeight / scaleFactor, cableHeight / scaleFactor, BAKE_FLIP_V)
                    ).transform(mat).into(builder, d.materials.cornerAO)
                }
                UNCONNECTED, UNCONNECTED_CROSSING -> {
                    val coords = UvCoords(
                        if (!swapUnconnectedSides) centerSide1Uv else centerSide2Uv,
                        cableHeight / scaleFactor, cableWidth / scaleFactor,
                        if (!swapUnconnectedSides) BAKE_ROTATE_90 + BAKE_FLIP_U else BAKE_ROTATE_270
                    )
                    box(
                        Vector3f(armLength, 0f, armLength),
                        Vector3f(1 - armLength, cableHeight, 1 - armLength),
                        south = coords
                    ).transform(mat).into(builder, d.materials.standardAO)
                }
                else -> {
                }
            }
        } else {
            val uvTop = when (variant) {
                EXTERNAL, UNCONNECTED, UNCONNECTED_CROSSING -> arm2TopUv
                INTERNAL -> innerTop2Uv
                CORNER -> arm2TopUv
                TERMINAL -> center8Top2Uv
            }

            val uvBottom = when (variant) {
                EXTERNAL, UNCONNECTED, UNCONNECTED_CROSSING -> arm2BottomUv
                INTERNAL -> innerBottom2Uv
                CORNER -> arm2BottomUv
                TERMINAL -> center8Bottom2Uv
            }

            val (uvSide1, uvSide2) = when (variant) {
                EXTERNAL, CORNER, UNCONNECTED, UNCONNECTED_CROSSING -> Pair(arm2Side1Uv, arm2Side2Uv)
                INTERNAL -> Pair(innerArm2Side1Uv, innerArm2Side2Uv)
                TERMINAL -> Pair(center8Arm2Side1Uv, center8Arm2Side2Uv)
            }

            val uvFront = cableBackUv.takeIf { variant in setOf(EXTERNAL, TERMINAL) }

            box(
                Vector3f(origin.x, 0f, armLength - baseLength),
                Vector3f(1 - armLength, cableHeight, armLength),
                down = UvCoords(uvBottom, cableWidth / scaleFactor, baseLength / scaleFactor),
                up = UvCoords(uvTop, cableWidth / scaleFactor, baseLength / scaleFactor),
                north = uvFront?.let { UvCoords(uvFront, cableHeight / scaleFactor, cableWidth / scaleFactor, BAKE_ROTATE_90 + BAKE_FLIP_U) },
                west = UvCoords(uvSide1, cableHeight / scaleFactor, baseLength / scaleFactor, BAKE_ROTATE_90 + BAKE_FLIP_U),
                east = UvCoords(uvSide2, cableHeight / scaleFactor, baseLength / scaleFactor, BAKE_ROTATE_90 + BAKE_FLIP_U)
            ).transform(mat).into(builder, d.materials.standardAO)

            when (variant) {
                INTERNAL -> {
                    box(
                        Vector3f(armLength, 0f, 0f),
                        Vector3f(1 - armLength, cableHeight, cableHeight),
                        up = UvCoords(cableBackUv, cableHeight / scaleFactor, cableWidth / scaleFactor, BAKE_ROTATE_90 + BAKE_FLIP_U),
                        west = UvCoords(icornerSide1Uv, cableHeight / scaleFactor, cableHeight / scaleFactor, BAKE_FLIP_V),
                        east = UvCoords(icornerSide2Uv, cableHeight / scaleFactor, cableHeight / scaleFactor, BAKE_FLIP_V)
                    ).transform(mat).into(builder, d.materials.standardAO)
                }
                CORNER -> {
                    box(
                        Vector3f(armLength, 0f, -cableHeight),
                        Vector3f(1 - armLength, cableHeight, 0f),
                        up = UvCoords(cornerTop2Uv, cableWidth / scaleFactor, cableHeight / scaleFactor, BAKE_FLIP_V),
                        north = UvCoords(cornerTop1Uv, cableWidth / scaleFactor, cableHeight / scaleFactor),
                        west = UvCoords(cornerSide1Uv, cableHeight / scaleFactor, cableHeight / scaleFactor, BAKE_ROTATE_180),
                        east = UvCoords(cornerSide2Uv, cableHeight / scaleFactor, cableHeight / scaleFactor, BAKE_ROTATE_180)
                    ).transform(mat).into(builder, d.materials.cornerAO)
                }
                UNCONNECTED, UNCONNECTED_CROSSING -> {
                    val coords = UvCoords(
                        if (!swapUnconnectedSides) centerSide2Uv else centerSide1Uv,
                        cableHeight / scaleFactor, cableWidth / scaleFactor,
                        if (!swapUnconnectedSides) BAKE_ROTATE_90 + BAKE_FLIP_U else BAKE_ROTATE_270
                    )
                    box(
                        Vector3f(armLength, 0f, armLength),
                        Vector3f(1 - armLength, cableHeight, 1 - armLength),
                        north = coords
                    ).transform(mat).into(builder, d.materials.standardAO)
                }
                else -> {
                }
            }
        }

        builder.finish()
    }

    override fun getModelDependencies() = emptySet<Identifier>()
    
    override fun setParents(modelLoader: Function<Identifier, UnbakedModel>?) {}
}

private data class RenderData(val materials: Materials)

private data class Materials(val standardAO: Boolean, val cornerAO: Boolean)

private data class UvCoords(val uv: Vector2f, val twidth: Float, val theight: Float, val flags: Int = 0)

private data class Vertex(val x: Float, val y: Float, val z: Float, val u: Float, val v: Float) {

    constructor(xyz: Vector3f, u: Float, v: Float) : this(xyz.x, xyz.y, xyz.z, u, v)

    fun transform(mat: Matrix4f): Vertex {
        val vec = mat.transform(Vector4f(x, y, z, 1f))
        return Vertex(vec.x, vec.y, vec.z, u, v)
    }

}

private data class Quad(val v1: Vertex, val v2: Vertex, val v3: Vertex, val v4: Vertex) {

    fun sort(face: Direction): Quad {
        val all = listOf(v1, v2, v3, v4)

        val center = Vector3f((v1.x + v2.x + v3.x + v4.x) / 4, (v1.y + v2.y + v3.y + v4.y) / 4, (v1.z + v2.z + v3.z + v4.z) / 4)

        val (v1, v2, v3, v4) = all.sortedBy {
            when (face.axis) {
                X -> atan2(-(it.z - center.z).toDouble() * face.direction.offset(), -(it.y - center.y).toDouble())
                Y -> atan2(-(it.x - center.x).toDouble() * face.direction.offset(), -(it.z - center.z).toDouble())
                Z -> atan2((it.x - center.x).toDouble() * face.direction.offset(), -(it.y - center.y).toDouble())
                null -> throw AssertionError()
            }
        }

        return Quad(v1, v2, v3, v4)
    }

    fun into(qe: QuadMeshBuilder, matAO: Boolean) {
        for (q in listOf(v1, v2, v3, v4)) {
            qe.color(-1)
            qe.pos(q.x, q.y, q.z)
            qe.uv(q.u, q.v)
            qe.emitVertex()
        }
        
        qe.aoEnabled(matAO)
    }

    fun transform(mat: Matrix4f) = Quad(
        v1.transform(mat),
        v2.transform(mat),
        v3.transform(mat),
        v4.transform(mat)
    )

}

private fun quad(face: Direction, xy1: Vector2f, xy2: Vector2f, depth: Float, uv: Vector2f, twidth: Float, theight: Float, flags: Int): List<Quad> {
    val depth = if (face.direction == NEGATIVE) depth else 1 - depth

    fun toVec3(x: Float, y: Float): Vector3f = when (face.axis) {
        X -> Vector3f(depth, y, x)
        Y -> Vector3f(x, depth, y)
        Z -> Vector3f(x, y, depth)
        null -> throw AssertionError()
    }

    val (uv1, uv2, uv3, uv4) = listOf(Vector2f(uv.x, uv.y + theight), Vector2f(uv.x + twidth, uv.y + theight), Vector2f(uv.x + twidth, uv.y), Vector2f(uv.x, uv.y))
        .let { (v1, v2, v3, v4) -> if (flags and BAKE_FLIP_U != 0) listOf(v2, v1, v4, v3) else listOf(v1, v2, v3, v4) }
        .let { l -> if (flags and BAKE_FLIP_V != 0) l.reversed() else l }
        .let { (it + it).subList(flags and 3, (flags and 3) + 4) }

    return listOf(
        Quad(
            Vertex(toVec3(xy1.x, xy1.y), uv1.x, uv1.y),
            Vertex(toVec3(xy2.x, xy1.y), uv2.x, uv2.y),
            Vertex(toVec3(xy2.x, xy2.y), uv3.x, uv3.y),
            Vertex(toVec3(xy1.x, xy2.y), uv4.x, uv4.y)
        ).sort(face)
    )
}

private fun box(min: Vector3f, max: Vector3f, down: UvCoords? = null, up: UvCoords? = null, north: UvCoords? = null, south: UvCoords? = null, west: UvCoords? = null, east: UvCoords? = null): List<Quad> {
    val quads = mutableListOf<Quad>()

    if (down != null) quads += quad(DOWN, min.xz, max.xz, min.y, down.uv, down.twidth, down.theight, down.flags)
    if (up != null) quads += quad(UP, min.xz, max.xz, 1 - max.y, up.uv, up.twidth, up.theight, up.flags)
    if (north != null) quads += quad(NORTH, min.xy, max.xy, min.z, north.uv, north.twidth, north.theight, north.flags)
    if (south != null) quads += quad(SOUTH, min.xy, max.xy, 1 - max.z, south.uv, south.twidth, south.theight, south.flags)
    if (west != null) quads += quad(WEST, min.zy, max.zy, min.x, west.uv, west.twidth, west.theight, west.flags)
    if (east != null) quads += quad(EAST, min.zy, max.zy, 1 - max.x, east.uv, east.twidth, east.theight, east.flags)

    return quads
}

private fun getExtGenInfo(side: Direction, edge: Direction): Pair<Matrix4f, AxisDirection> {
    val rotAxis = Axis.entries.single { it != side.axis && it != edge.axis }
    var rot = 0

    var start = when (rotAxis) {
        X, Z -> DOWN
        Y -> WEST
    }

    while (start != side) {
        start = start.rotateClockwise(rotAxis)
        rot += 1
    }

    val mat = Matrix4f()

    mat.translate(0.5f, 0.5f, 0.5f)
    when (rotAxis) {
        X -> mat.rotate(-rot * PI.toFloat() / 2.0f, 1.0f, 0.0f, 0.0f)
        Y -> mat.rotate(-PI.toFloat() / 2.0f, 0.0f, 0.0f, 1.0f).rotate(rot * PI.toFloat() / 2.0f, 1.0f, 0.0f, 0.0f)
        Z -> mat.rotate(PI.toFloat() / 2.0f, 0.0f, 1.0f, 0.0f).rotate(rot * PI.toFloat() / 2.0f, 1.0f, 0.0f, 0.0f)
    }
    mat.translate(-0.5f, -0.5f, -0.5f)

    val dir = when (Pair(side, edge.axis)) {
        Pair(WEST, Y), Pair(EAST, Z), Pair(NORTH, X), Pair(NORTH, Y), Pair(UP, X), Pair(UP, Z) -> edge.opposite.direction
        else -> edge.direction
    }

    return Pair(mat, dir)
}

private fun Collection<Quad>.into(qe: QuadMeshBuilder, matAO: Boolean) = forEach { it.into(qe, matAO) }

private fun Collection<Quad>.transform(mat: Matrix4f) = map { it.transform(mat) }

enum class CenterVariant {
    CROSSING,
    STRAIGHT_1, // X axis
    STRAIGHT_2, // Z axis
    STANDALONE,
}

enum class ExtVariant {
    EXTERNAL,
    INTERNAL,
    CORNER,
    UNCONNECTED,
    UNCONNECTED_CROSSING,
    TERMINAL,
}

const val BAKE_ROTATE_90 = 1
const val BAKE_ROTATE_180 = 2
const val BAKE_ROTATE_270 = 3
const val BAKE_LOCK_UV = 4
const val BAKE_FLIP_U = 8
const val BAKE_FLIP_V = 16
