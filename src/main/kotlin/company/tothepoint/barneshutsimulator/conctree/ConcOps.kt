package company.tothepoint.barneshutsimulator.conctree

object ConcOps {

    infix fun <T> Conc<T>.merge(that: Conc<T>) = Conc.concatTop(this.normalized(), that.normalized())

    //fun <T> merge(first: Conc<T>, second: Conc<T>) = Conc.concatTop(first.normalized(), second.normalized())
}