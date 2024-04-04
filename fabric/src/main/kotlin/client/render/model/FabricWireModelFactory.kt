package net.dblsaiko.hctm.fabric.client.render.model

import net.dblsaiko.hctm.client.render.model.*
import net.fabricmc.fabric.api.renderer.v1.Renderer
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder
import net.fabricmc.fabric.api.util.TriState
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.util.math.Direction
import java.util.concurrent.ConcurrentHashMap

class FabricWireModelFactory(
    private val renderer: Renderer, private val cache: ConcurrentHashMap<CacheKey, FabricWireModelParts>
) : WireModelFactory {
    override fun build(sprite: Sprite, cacheKey: CacheKey, builder: WireModelBuilder): BakedModel {
        val parts = cache.computeIfAbsent(cacheKey) {
            val ctx = FabricWireModelBuildCtx(renderer)
            builder.build(ctx)
            ctx.finish()
        }

        return FabricWireModel(sprite, parts)
    }
}

class FabricWireModelBuildCtx(private val renderer: Renderer) : WireModelBuildCtx {
    private val sides = mutableMapOf<Direction, FabricWireModelPart>()

    fun finish() = FabricWireModelParts(sides)

    override fun buildPart(side: Direction): WireModelPartBuilder {
        val centerMap = mutableMapOf<CenterVariant, Mesh>()
        val extMap = mutableMapOf<Pair<Direction, ExtVariant>, Mesh>()
        sides[side] = FabricWireModelPart(centerMap, extMap)
        return FabricWireModelPartBuilder(renderer, centerMap, extMap)
    }
}

class FabricWireModelPartBuilder(
    private val renderer: Renderer, private val centerMap: MutableMap<CenterVariant, Mesh>,
    private val extMap: MutableMap<Pair<Direction, ExtVariant>, Mesh>
) : WireModelPartBuilder {
    override fun buildCenter(cv: CenterVariant): QuadMeshBuilder {
        return FabricQuadMeshBuilder(renderer.meshBuilder(), renderer.materialFinder()) { centerMap[cv] = it }
    }

    override fun buildExt(ext: Direction, v: ExtVariant): QuadMeshBuilder {
        return FabricQuadMeshBuilder(renderer.meshBuilder(), renderer.materialFinder()) { extMap[Pair(ext, v)] = it }
    }
}

class FabricQuadMeshBuilder(
    private val builder: MeshBuilder, private val materialFinder: MaterialFinder, private val finished: (Mesh) -> Unit
) : QuadMeshBuilder {
    private val emitter = builder.emitter

    private var vertexIndex = 0
    private var aoEnabled = false

    override fun pos(x: Float, y: Float, z: Float) {
        emitter.pos(vertexIndex, x, y, z)
    }

    override fun normal(x: Float, y: Float, z: Float) {
        emitter.normal(vertexIndex, x, y, z)
    }

    override fun color(color: Int) {
        emitter.color(vertexIndex, color)
    }

    override fun uv(u: Float, v: Float) {
        emitter.uv(vertexIndex, u, v)
    }

    override fun lightmap(lightmap: Int) {
        emitter.lightmap(vertexIndex, lightmap)
    }

    override fun aoEnabled(aoEnabled: Boolean) {
        this.aoEnabled = aoEnabled
    }

    override fun emitVertex() {
        if (++vertexIndex >= 4) {
            vertexIndex = 0
            emitter.material(materialFinder.clear().ambientOcclusion(TriState.of(aoEnabled)).find())
            emitter.emit()
        }
    }

    override fun finish() {
        finished(builder.build())
    }
}
