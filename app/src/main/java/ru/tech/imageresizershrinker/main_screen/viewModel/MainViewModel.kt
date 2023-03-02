package ru.tech.imageresizershrinker.main_screen.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.popUpTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.main_screen.components.Screen
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class MainViewModel : ViewModel() {

    val navController = navController<Screen>(Screen.Main)

    private val _uri = mutableStateOf<Uri?>(null)
    val uri by _uri

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _showSelectDialog = mutableStateOf(false)
    val showSelectDialog by _showSelectDialog

    private val _showUpdateDialog = mutableStateOf(false)
    val showUpdateDialog by _showUpdateDialog

    private val _cancelledUpdate = mutableStateOf(false)

    private val _tag = mutableStateOf("")
    val tag by _tag

    init {
        tryGetUpdate()
    }

    fun cancelledUpdate() {
        _cancelledUpdate.value = true
        _showUpdateDialog.value = false
    }

    fun tryGetUpdate() {
        if (!_cancelledUpdate.value) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    kotlin.runCatching {
                        val nodes = DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                            .parse(
                                URL("https://github.com/T8RIN/ImageResizer/releases.atom")
                                    .openConnection()
                                    .getInputStream()
                            )
                            ?.getElementsByTagName("feed")

                        if (nodes != null) {
                            for (i in 0 until nodes.length) {
                                val element = nodes.item(i) as Element
                                val title = element.getElementsByTagName("entry")
                                val line = (title.item(0) as Element)
                                _tag.value = (line.getElementsByTagName("title")
                                    .item(0) as Element).textContent
                            }
                        }

                        if (tag != BuildConfig.VERSION_NAME) {
                            _showUpdateDialog.value = true
                        }
                    }
                }
            }
        }
    }

    fun updateUri(uri: Uri?) {
        _uri.value = null
        _uri.value = uri
        if (uri != null && navController.backstack.entries.lastOrNull()?.destination == Screen.Main) _showSelectDialog.value =
            true
    }

    fun hideSelectDialog() {
        _showSelectDialog.value = false
    }

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris
        val dest = navController.backstack.entries.lastOrNull()?.destination

        if (uris != null && dest != Screen.BatchResize) {
            navController.popUpTo { it == Screen.Main }
            navController.navigate(Screen.BatchResize)
        }
    }

}