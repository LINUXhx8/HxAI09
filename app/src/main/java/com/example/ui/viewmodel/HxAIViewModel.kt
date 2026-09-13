package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HxAIViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val APP_ENTRY_PASSWORD = "U6992654341"
        const val MEDIA_ACTION_PASSWORD = "4201451963H"
    }

    private val repository: MediaRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = MediaRepository(db.mediaDao(), application)
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // Lock screen authentication state
    private val _isAppUnlocked = MutableStateFlow(false)
    val isAppUnlocked: StateFlow<Boolean> = _isAppUnlocked

    private val _lockScreenError = MutableStateFlow<String?>(null)
    val lockScreenError: StateFlow<String?> = _lockScreenError

    // Search and filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedMediaType = MutableStateFlow<MediaType?>(null)
    val selectedMediaType: StateFlow<MediaType?> = _selectedMediaType

    // Filtered media list
    val mediaList: StateFlow<List<MediaItemEntity>> = combine(
        repository.allMedia,
        _searchQuery,
        _selectedMediaType
    ) { allItems, query, typeFilter ->
        allItems.filter { item ->
            val matchesType = typeFilter == null || item.type == typeFilter
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.note.contains(query, ignoreCase = true)
            matchesType && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Dialog & authorization state
    private val _showUploadAuthDialog = MutableStateFlow(false)
    val showUploadAuthDialog: StateFlow<Boolean> = _showUploadAuthDialog

    private val _showUploadMediaDialog = MutableStateFlow(false)
    val showUploadMediaDialog: StateFlow<Boolean> = _showUploadMediaDialog

    private val _pendingDeleteItem = MutableStateFlow<MediaItemEntity?>(null)
    val pendingDeleteItem: StateFlow<MediaItemEntity?> = _pendingDeleteItem

    private val _showDeleteAuthDialog = MutableStateFlow(false)
    val showDeleteAuthDialog: StateFlow<Boolean> = _showDeleteAuthDialog

    private val _showDescriptionDialog = MutableStateFlow(false)
    val showDescriptionDialog: StateFlow<Boolean> = _showDescriptionDialog

    private val _viewingItem = MutableStateFlow<MediaItemEntity?>(null)
    val viewingItem: StateFlow<MediaItemEntity?> = _viewingItem

    private val _actionErrorMessage = MutableStateFlow<String?>(null)
    val actionErrorMessage: StateFlow<String?> = _actionErrorMessage

    fun attemptAppUnlock(password: String): Boolean {
        if (password.trim() == APP_ENTRY_PASSWORD) {
            _isAppUnlocked.value = true
            _lockScreenError.value = null
            return true
        } else {
            _lockScreenError.value = "Incorrect password. Access denied to HxAI."
            return false
        }
    }

    fun lockApp() {
        _isAppUnlocked.value = false
        _lockScreenError.value = null
    }

    fun clearLockScreenError() {
        _lockScreenError.value = null
    }

    // Media Plus (+) click flow
    fun onPlusClicked() {
        _actionErrorMessage.value = null
        _showUploadAuthDialog.value = true
    }

    fun dismissUploadAuthDialog() {
        _showUploadAuthDialog.value = false
        _actionErrorMessage.value = null
    }

    fun verifyUploadPassword(password: String): Boolean {
        if (password.trim() == MEDIA_ACTION_PASSWORD) {
            _showUploadAuthDialog.value = false
            _showUploadMediaDialog.value = true
            _actionErrorMessage.value = null
            return true
        } else {
            _actionErrorMessage.value = "Authorization failed. Password incorrect."
            return false
        }
    }

    fun dismissUploadMediaDialog() {
        _showUploadMediaDialog.value = false
    }

    fun uploadPicture(title: String, uri: Uri, note: String) {
        viewModelScope.launch {
            try {
                val savedPath = repository.saveUriToFile(uri, "jpg", "media_pictures")
                repository.insertMedia(
                    title = title.ifBlank { "Picture ${System.currentTimeMillis() % 10000}" },
                    type = MediaType.PICTURE,
                    contentUriOrUrl = savedPath,
                    note = note
                )
                _showUploadMediaDialog.value = false
            } catch (e: Exception) {
                _actionErrorMessage.value = "Failed to upload image: ${e.message}"
            }
        }
    }

    fun uploadPdf(title: String, uri: Uri, note: String) {
        viewModelScope.launch {
            try {
                val savedPath = repository.saveUriToFile(uri, "pdf", "media_pdfs")
                repository.insertMedia(
                    title = title.ifBlank { "Document ${System.currentTimeMillis() % 10000}.pdf" },
                    type = MediaType.PDF,
                    contentUriOrUrl = savedPath,
                    note = note
                )
                _showUploadMediaDialog.value = false
            } catch (e: Exception) {
                _actionErrorMessage.value = "Failed to upload PDF: ${e.message}"
            }
        }
    }

    fun uploadLink(title: String, url: String, note: String) {
        viewModelScope.launch {
            val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else {
                url
            }
            repository.insertMedia(
                title = title.ifBlank { url },
                type = MediaType.LINK,
                contentUriOrUrl = formattedUrl,
                note = note
            )
            _showUploadMediaDialog.value = false
        }
    }

    // Delete flow
    fun onRequestDelete(item: MediaItemEntity) {
        _pendingDeleteItem.value = item
        _actionErrorMessage.value = null
        _showDeleteAuthDialog.value = true
    }

    fun dismissDeleteAuthDialog() {
        _showDeleteAuthDialog.value = false
        _pendingDeleteItem.value = null
        _actionErrorMessage.value = null
    }

    fun verifyDeletePassword(password: String): Boolean {
        if (password.trim() == MEDIA_ACTION_PASSWORD) {
            val itemToDelete = _pendingDeleteItem.value
            if (itemToDelete != null) {
                viewModelScope.launch {
                    repository.deleteMedia(itemToDelete)
                    _pendingDeleteItem.value = null
                    _showDeleteAuthDialog.value = false
                    _actionErrorMessage.value = null
                }
            }
            return true
        } else {
            _actionErrorMessage.value = "Delete authorization failed. Invalid security code."
            return false
        }
    }

    // Viewer
    fun openItemViewer(item: MediaItemEntity) {
        _viewingItem.value = item
    }

    fun closeItemViewer() {
        _viewingItem.value = null
    }

    // Description dialog
    fun openDescriptionDialog() {
        _showDescriptionDialog.value = true
    }

    fun closeDescriptionDialog() {
        _showDescriptionDialog.value = false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedMediaType(type: MediaType?) {
        _selectedMediaType.value = type
    }
}
