package dev.dl.demoapp.files

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FilesViewModel {

    private val viewmodelScope = CoroutineScope(Dispatchers.IO)

    private val _selectedDate = MutableStateFlow(DEFAULT_DATE)
    val selectedDate = _selectedDate.asStateFlow()

//    private val _

    private var urgentDateOptions: List<String> = emptyList()
    private var normalDateOptions: List<String> = emptyList()
    private var photoDateOptions: List<String> = emptyList()

    private var allUrgentItems: Map<String, List<FileItem>> = emptyMap()
    private var allNormalItems: Map<String, List<FileItem>> = emptyMap()
    private var allPhotoItems: Map<String, List<FileItem>> = emptyMap()

    private var currentType: FileType = FileType.Urgent

    fun refreshFiles(apiCall: suspend ()-> List<FileItem>) {
        viewmodelScope.launch {
            val result = apiCall()

            // TODO update urgentDateOptions
            // TODO update allUrgentItems
            // TODO update normalDateOptions
            // TODO update allNormalItems
            // TODO update photoDateOptions
            // TODO update allPhotoItems

            calculateUiState()
        }
    }

    fun changeToType(newType: FileType) {
        currentType = newType
    }

    private fun calculateUiState() {

    }

    companion object {
        private const val DEFAULT_DATE = "--"
    }
}

enum class FileType {
    Urgent,
    Normal,
    Photo
}

data class FileItem(
    val url: String,
)