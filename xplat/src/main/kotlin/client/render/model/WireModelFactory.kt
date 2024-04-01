package net.dblsaiko.hctm.client.render.model

import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.util.math.Direction

/**
 * Platform dependent source of wire models.
 */
interface WireModelFactory {
    fun build(sprite: Sprite, cacheKey: CacheKey, builder: WireModelBuilder): BakedModel
}

/**
 * Conditionally invoked.
 */
fun interface WireModelBuilder {
    fun build(ctx: WireModelBuildCtx)
}

/**
 * The actual builder.
 */
interface WireModelBuildCtx {
    fun buildPart(side: Direction): WireModelPartBuilder
}

interface WireModelPartBuilder {
    fun buildCenter(cv: CenterVariant): QuadMeshBuilder
    
    fun buildExt(ext: Direction, v: ExtVariant): QuadMeshBuilder
}

/**
 * Outputs either `Mesh`s or `List<BakedQuad>`s, depending on the platform.
 */
interface QuadMeshBuilder {
    fun pos(x: Float, y: Float, z: Float)
    
    fun normal(x: Float, y: Float, z: Float)
    
    fun color(color: Int)
    
    fun uv(u: Float, v: Float)
    
    fun lightmap(lightmap: Int)
    
    fun emitVertex()

    fun aoEnabled(aoEnabled: Boolean)

    fun finish()
}
