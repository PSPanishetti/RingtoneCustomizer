package com.app.ringtonecustomizer

import android.R.attr.path
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.documentfile.provider.DocumentFile
import com.app.ringtonecustomizer.ui.theme.RingtoneCustomizerTheme
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File


class MainActivity : ComponentActivity() {


    //region selectedDirectory declaration
    private val mSelectedDirectory: MutableStateFlow<String> =
        MutableStateFlow("")

    val selectedDirectory: StateFlow<String> = mSelectedDirectory

    fun setSelectedDirectory(newState: String) {
        mSelectedDirectory.value = newState
    }
    //endregion


    val selectCSVFileLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
            Log.d("ExportBODA", "Uri Got For file : $uri and uriPath is ${uri?.path}")
            if (uri != null) {
                setSelectedDirectory(uri.toString())
                listFiles(uri)
                Prefs.putString(PrefsConstants.URI_PATH, uri.toString())
                // setRingtone(uri)
            } else {
                Toast.makeText(
                    this,
                    "No Folder Selected",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun listFiles(uri: Uri) {
        val filenamesToDocumentFile = mutableMapOf<String, DocumentFile>()
        val documentsTree = DocumentFile.fromTreeUri(this, uri) ?: return
        val childDocuments = documentsTree.listFiles()
        val set = arrayListOf<String>()
        for (childDocument in childDocuments) {
            set.add(childDocument.uri.toString())
            childDocuments[0].name?.let {
                filenamesToDocumentFile[it] = childDocument
            }
        }
        Log.d(
            PrefsConstants.APP_TAG,
            "Saved list as ${set.toMutableSet().toString()}"
        )
        Prefs.putOrderedStringSet(PrefsConstants.LIST_OF_RINGTONES, set.toMutableSet())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectCSVFileLauncher.launch(null)

        setContent {
            val directory by selectedDirectory.collectAsState()
            RingtoneCustomizerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(directory.toString())
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RingtoneCustomizerTheme {
        Greeting("Android")
    }
}