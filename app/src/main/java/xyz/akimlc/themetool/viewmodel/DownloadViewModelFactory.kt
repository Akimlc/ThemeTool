package xyz.akimlc.themetool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import xyz.akimlc.themetool.data.db.DownloadDao

class DownloadViewModelFactory(private val dao: DownloadDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DownloadViewModel(dao) as T
    }
}
