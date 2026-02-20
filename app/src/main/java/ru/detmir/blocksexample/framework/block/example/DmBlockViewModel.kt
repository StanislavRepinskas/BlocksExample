package ru.detmir.blocksexample.framework.block.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.detmir.blocksexample.framework.block.viewmodel.BlockViewModel
import ru.detmir.blocksexample.framework.block.viewmodel.lazyBlockController
import java.util.UUID

abstract class DmBlockViewModel(
) : ViewModel(), BlockViewModel<DmBlockContext> {

    val uuid: String by lazy {
        UUID.randomUUID().toString()
    }

    override val blockController by lazyBlockController()

    override fun createBlockContext(): DmBlockContext {
        return DmBlockContext(
            scope = viewModelScope,
            uuid = uuid,
        )
    }

    override fun onCleared() {
        onBlockViewModelCleared()
        super.onCleared()
    }
}
