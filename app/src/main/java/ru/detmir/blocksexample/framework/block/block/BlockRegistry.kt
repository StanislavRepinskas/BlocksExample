package ru.detmir.blocksexample.framework.block.block

class Registration<Context : BlockContext>(
    val block: Block<Context, *, *>,
    val attach: (Context) -> Unit
)

class BlockRegistry<Context : BlockContext>() {
    private val registrations = mutableListOf<Registration<Context>>()

    fun <State> register(block: Block<Context, State, Unit>) {
        register(block, Unit)
    }

    fun <State, Callbacks> register(
        block: Block<Context, State, Callbacks>,
        callbacks: Callbacks
    ) {
        registrations += Registration(
            block = block,
            attach = { context -> block.attach(context, callbacks) }
        )
    }

    fun getRegistrations(): List<Registration<Context>> = registrations
}
