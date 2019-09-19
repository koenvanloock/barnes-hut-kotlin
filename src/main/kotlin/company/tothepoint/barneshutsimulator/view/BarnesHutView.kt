package company.tothepoint.barneshutsimulator.view

import company.tothepoint.barneshutsimulator.controller.BarnesHutController
import tornadofx.View
import tornadofx.borderpane

class BarnesHutView : View() {
    val controller: BarnesHutController by inject()
    val barnesHutCanvas = BarnesHutCanvas(controller)
    val buttonPanel = ButtonPanel(controller)

    init {
        controller.setView(barnesHutCanvas)
        controller.addTimeObserver(buttonPanel)
        controller.initialize()
    }


    override val root = borderpane() {
        right = buttonPanel
        center = barnesHutCanvas
    }
}