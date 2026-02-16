package ru.detmir.blocksexample.products.block

import javax.inject.Inject
import ru.detmir.blocksexample.framework.block.Block

class HeaderBlock @Inject constructor() : Block<HeaderBlock.State, Unit>() {

    override fun getInitialState(): State {
        return State(
            text = "Products",
            filterState = null
        )
    }

    data class State(
        val text: String,
        val filterState: FilterState?
    )

    data class FilterState(
        val text: String
    )
}
