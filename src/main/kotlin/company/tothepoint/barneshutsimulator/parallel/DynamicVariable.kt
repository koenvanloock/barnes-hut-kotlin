package company.tothepoint.barneshutsimulator.parallel

class DynamicVariable<T>(init: T) {

    private val t1 = object : InheritableThreadLocal<T>() {
         override fun initialValue() = init
    }

    fun value(): T = t1.get()

    fun <S> withValue(newVal: T, thunk: () -> S) {
        val oldval = value()
        t1.set(newVal)
        try {
            thunk()
        } finally {
            t1.set(oldval)
        }
    }

    fun value_ (newVal:T): Unit = t1.set(newVal)
    override fun toString() = "DynamicVariable(${value()})"

}