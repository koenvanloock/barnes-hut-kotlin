package company.tothepoint.barneshutsimulator.utils

object ArrayUtils {
    inline fun <reified T> emptyArrayOfSize(size: Int): Array<T> =
            @Suppress("UNCHECKED_CAST")
            (arrayOfNulls<T>(size) as Array<T>)
}