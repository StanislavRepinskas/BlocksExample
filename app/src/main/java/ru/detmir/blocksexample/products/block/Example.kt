package ru.detmir.blocksexample.products.block

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.launch
import ru.detmir.blocksexample.framework.UIStatus
import ru.detmir.blocksexample.framework.block.block.Block
import ru.detmir.blocksexample.framework.block.block.BlockRegistry
import ru.detmir.blocksexample.framework.block.example.DmBlockContext
import ru.detmir.blocksexample.framework.block.example.DmBlockViewModel
import javax.inject.Inject

private class ExampleIndependentBlock : Block<DmBlockContext, ExampleIndependentBlock.State, Unit>() {

    override fun onStart() {
        super.onStart()
        load()
    }

    private fun load() {
        updateState { prev -> prev.copy(uiStatus = UIStatus.LOADING) }
        context.scope.launch {
            runCatching {
                // TODO
            }.onSuccess {
                updateState { prev -> prev.copy(uiStatus = UIStatus.SUCCESS) }
            }.onFailure {
                updateState { prev -> prev.copy(uiStatus = UIStatus.ERROR) }
            }
        }
    }

    override fun getInitialState(): State {
        return State(list = emptyList(), uiStatus = UIStatus.LOADING)
    }

    data class State(val list: List<String>, val uiStatus: UIStatus)
}

private class ExampleIndependentParamBlock : Block<DmBlockContext, ExampleIndependentParamBlock.State, Unit>() {

    fun load(param: String) {
        updateState { prev -> prev.copy(uiStatus = UIStatus.LOADING) }
        context.scope.launch {
            runCatching {
                // TODO repository(param)
            }.onSuccess {
                updateState { prev -> prev.copy(uiStatus = UIStatus.SUCCESS) }
            }.onFailure {
                updateState { prev -> prev.copy(uiStatus = UIStatus.ERROR) }
            }
        }
    }

    override fun getInitialState(): State {
        return State(list = emptyList(), uiStatus = UIStatus.LOADING)
    }

    data class State(val list: List<String>, val uiStatus: UIStatus)
}

private class ExampleIndependentParamViewModel @Inject constructor(
    private val block: ExampleIndependentParamBlock,
    private val savedStateHandle: SavedStateHandle
) : DmBlockViewModel() {

    override fun onRegisterBlocks(registry: BlockRegistry<DmBlockContext>) {
        registry.register(block)
    }

    override fun start() {
        super.start()
        block.load(savedStateHandle.get<String>("key") ?: "")
    }

    override fun onUpdateBlocks() {
        TODO("Not yet implemented")
    }
}

// ------------------------------------------------------------------------------------------------

private class ExampleDependentBlock :
    Block<DmBlockContext, ExampleDependentBlock.State, ExampleDependentBlock.Callbacks>() {

    private fun setData(list: List<String>) {
        updateState { prev -> prev.copy(list = list, uiStatus = UIStatus.SUCCESS) }
    }

    private fun clear() {
        updateState { prev -> prev.copy(list = emptyList(), uiStatus = UIStatus.SUCCESS) }
        callbacks.onClear()
    }

    override fun getInitialState(): State {
        return State(list = emptyList(), uiStatus = UIStatus.LOADING)
    }

    data class State(val list: List<String>, val uiStatus: UIStatus)

    interface Callbacks {
        fun onClear()
    }
}
