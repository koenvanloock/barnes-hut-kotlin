package company.tothepoint.barneshutsimulator.model

class TimeStatistics {
    private val timeMap = mutableMapOf<String, Pair<Double, Int>>()

    fun clear() = timeMap.clear()


    fun <T> timed(title: String, body: () -> T): T {
        var res: T? = null
        fun measure(): Long {
            val startTime = System.currentTimeMillis()
            res = body()
            return (System.currentTimeMillis() - startTime)
        }
        val totalTime = measure()

        timeMap.get(title)
                ?.let { timeMap.put(title, Pair(it.first + totalTime, it.second + 1)) }
                ?: timeMap.put(title, Pair(0.0, 0))

        return res!!
    }

    override fun toString() =
            timeMap.entries.map { entry ->
                entry.key + ": " + (entry.value.first / entry.value.second * 100).toInt() / 100.0 + " ms"
            }.joinToString("\n")
}