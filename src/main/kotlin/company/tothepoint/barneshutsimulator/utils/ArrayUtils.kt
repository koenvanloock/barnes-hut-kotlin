package company.tothepoint.barneshutsimulator.utils

object ArrayUtils {
    inline fun <reified T> emptyArrayOfSize(size: Int): Array<T> =
            @Suppress("UNCHECKED_CAST")
            (arrayOfNulls<T>(size) as Array<T>)

    inline fun <reified T> plusplus(first: Array<T>, second: Array<T>): Array<T> {
            val firstSize = first.size;
            val secondSize = second.size

            @SuppressWarnings("unchecked")
            val c = arrayOfNulls<T>(firstSize + secondSize) as Array<T>;
            System.arraycopy(first, 0, c, 0, firstSize);
            System.arraycopy(second, 0, c, firstSize, secondSize);
            return c
        }
}