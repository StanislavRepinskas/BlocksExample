package ru.detmir.blocksexample.framework.block.viewmodel

import ru.detmir.blocksexample.framework.block.block.Block
import ru.detmir.blocksexample.framework.block.block.BlockContext

class Registration(
    val block: Block<*, *>,
    val attach: (BlockContext) -> Unit
)

class BlockRegistry() {
    private val registrations = mutableListOf<Registration>()

    fun <State> register(block: Block<State, Unit>) {
        register(block, Unit)
    }

    fun <State, Callbacks> register(
        block: Block<State, Callbacks>,
        callbacks: Callbacks
    ) {
        registrations += Registration(
            block = block,
            attach = { context -> block.attach(context, callbacks) }
        )
    }

    fun getRegistrations(): List<Registration> = registrations
}
