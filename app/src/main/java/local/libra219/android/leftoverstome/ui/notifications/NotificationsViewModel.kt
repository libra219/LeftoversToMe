package local.libra219.android.leftoverstome.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "現在受け渡し待ち"
    }
    val text: LiveData<String> = _text
}