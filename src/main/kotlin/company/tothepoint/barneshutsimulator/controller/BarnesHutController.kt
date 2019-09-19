package company.tothepoint.barneshutsimulator.controller

import company.tothepoint.barneshutsimulator.simulator.Simulator
import company.tothepoint.barneshutsimulator.view.BarnesHutCanvas
import company.tothepoint.barneshutsimulator.view.StatsObserver
import tornadofx.Controller
import javax.swing.SwingUtilities

class BarnesHutController() : Controller() {

    private val observers: MutableList<StatsObserver> = mutableListOf()
    private var view: BarnesHutCanvas? = null
    val simulator = Simulator()
    val timer = javax.swing.Timer(0) { e -> stepThroughSimulation() }

    fun initialize() {
        simulator.init2Galaxies()
        view?.repaint()
    }

    fun stepThroughSimulation() {
        return SwingUtilities.invokeLater {
            val (bodies, quad) = simulator.step(simulator.bodies)
            simulator.bodies = bodies
            simulator.quad = quad
            notifyTimeObservers()
            view?.repaint()
        }
    }

    fun setView(barnesHutCanvas: BarnesHutCanvas) {
        this.view = barnesHutCanvas
    }

    fun start() {
        timer.start()
    }

    fun stop() {
        timer.stop()
    }

    fun switchShowRenderQuad() = simulator.switchShowRenderQuad()
    fun shouldRenderQuad(): Boolean = simulator.shouldRenderQuad


    fun bodies() = simulator.bodies

    fun screen() = simulator.screen

    fun quad() = simulator.quad

    fun updateTotalBodies(total: Int) = simulator.updateTotalBodies(total)

    fun updateNumberOfCores(cores: Int) = simulator.updateCores(cores)

    fun addTimeObserver(observer: StatsObserver) {
        this.observers.add(observer)
    }

    fun notifyTimeObservers() {
        observers.forEach { it.update(simulator.timeStatistics) }
    }

}