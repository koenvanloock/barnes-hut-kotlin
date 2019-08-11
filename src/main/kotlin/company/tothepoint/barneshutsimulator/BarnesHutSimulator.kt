package company.tothepoint.barneshutsimulator

import company.tothepoint.barneshutsimulator.view.BarnesHutView
import tornadofx.*

class BarnesHutSimulator: App(BarnesHutView::class){
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch<BarnesHutSimulator>(args)
        }
    }
}