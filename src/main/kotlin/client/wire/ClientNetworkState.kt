package net.dblsaiko.hctm.client.wire

object ClientNetworkState {

//    private val caches = mutableMapOf<RegistryKey<World>, Entry>()

//    fun request(world: World): WireNetworkController? {
//        if (world !is ServerWorld) error("Yeah let's not do that.")
//
//        val worldKey = world.registryKey
//
//        if (caches[worldKey]?.isExpired() != false) {
//            HctmBaseClient.getInstance().clientNetHandler.debugNetRequest.send(DebugNetRequest(worldKey.value))
//        }
//
//        return caches[worldKey]?.controller
//    }

//    fun update(dt: RegistryKey<World>, tag: NbtCompound) {
//        caches[dt] = Entry(WireNetworkController.fromTag(tag))
//    }

}

//private data class Entry(val controller: WireNetworkController, val created: Long = utime()) {
//    fun isExpired() = utime() - created > 1
//}

private fun utime() = System.currentTimeMillis() / 1000