package company.tothepoint.barneshutsimulator.view

import company.tothepoint.barneshutsimulator.controller.BarnesHutController
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.layout.VBox
import tornadofx.*
import java.lang.Integer.parseInt


class ButtonPanel(controller: BarnesHutController) : VBox() {

    val items: Array<String> = (1..Runtime.getRuntime().availableProcessors()).map { it.toString() }.toTypedArray()
    val numberOfCores = FXCollections.observableArrayList(*items)
    val selectedNumber = SimpleStringProperty()

    init {
        selectedNumber.onChange { controller.updateNumberOfCores(parseInt(it)) }
        selectedNumber.set(items[items.size - 1])
        this += hbox {
            label("parallellism")
            combobox<String>(selectedNumber, numberOfCores)
        }

        this += hbox {
            label("total bodies")
            spinner(min = 32, max = 1_000_000, initialValue = 25000, amountToStepBy = 1000).valueProperty()
                    .addListener { ref, old, new ->
                        controller.updateTotalBodies(new)
                    }
        }

        this += hbox {
            button {
                text = "step"
                action {
                    controller.stepThroughSimulation()
                }
            }
            button {
                text = "start/stop"
                action {
                    controller.start()
                }
            }
        }

        this += hbox {
            checkbox {
                text = "show quad"
                action {
                    controller.switchShowRenderQuad()
                }
            }
            button {
                text = "reset"
                action {
                    controller.initialize()
                }
            }
        }
    }

}