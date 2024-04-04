package net.dblsaiko.hctm.neoforge.client.render.model

import net.dblsaiko.hctm.client.render.model.*
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.texture.Sprite
import net.minecraft.util.math.Direction
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer

class NeoForgeWireModelFactory : WireModelFactory {
    override fun build(sprite: Sprite, cacheKey: CacheKey, builder: WireModelBuilder): BakedModel {
        val ctx = NeoForgeWireModelBuildCtx(sprite)
        builder.build(ctx)
        return NeoForgeWireModel(sprite, ctx.finish())
    }
}

class NeoForgeWireModelBuildCtx(private val sprite: Sprite) : WireModelBuildCtx {
    private val sides = mutableMapOf<Direction, NeoForgeWireModelPart>()

    fun finish() = NeoForgeWireModelParts(sides)

    override fun buildPart(side: Direction): WireModelPartBuilder {
        val centerMap = mutableMapOf<CenterVariant, List<BakedQuad>>()
        val extMap = mutableMapOf<Pair<Direction, ExtVariant>, List<BakedQuad>>()
        sides[side] = NeoForgeWireModelPart(centerMap, extMap)
        return NeoForgeWireModelPartBuilder(sprite, centerMap, extMap)
    }
}

class NeoForgeWireModelPartBuilder(
    private val sprite: Sprite,
    private val centerMap: MutableMap<CenterVariant, List<BakedQuad>>,
    private val extMap: MutableMap<Pair<Direction, ExtVariant>, List<BakedQuad>>
) : WireModelPartBuilder {
    override fun buildCenter(cv: CenterVariant): QuadMeshBuilder {
        return NeoForgeQuadMeshBuilder(sprite) { centerMap[cv] = it }
    }

    override fun buildExt(ext: Direction, v: ExtVariant): QuadMeshBuilder {
        return NeoForgeQuadMeshBuilder(sprite) { extMap[Pair(ext, v)] = it }
    }
}

class NeoForgeQuadMeshBuilder(private val sprite: Sprite, private val finished: (List<BakedQuad>) -> Unit) :
    QuadMeshBuilder {
    private val quads = mutableListOf<BakedQuad>()
    private val builder = QuadBakingVertexConsumer(quads::add)

    override fun pos(x: Float, y: Float, z: Float) {
        builder.vertex(x.toDouble(), y.toDouble(), z.toDouble())
    }

    override fun normal(x: Float, y: Float, z: Float) {
        builder.normal(x, y, z)
    }

    override fun color(color: Int) {
        builder.color(color)
    }

    override fun uv(u: Float, v: Float) {
        builder.texture(sprite.getFrameU(u), sprite.getFrameV(v))
    }

    override fun lightmap(lightmap: Int) {
        builder.light(lightmap)
    }

    override fun emitVertex() {
        builder.next()
    }

    override fun aoEnabled(aoEnabled: Boolean) {
        builder.setHasAmbientOcclusion(aoEnabled)
    }

    override fun finish() {
        finished(quads)
    }
}
