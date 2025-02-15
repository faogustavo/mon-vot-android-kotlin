package ro.code4.monitorizarevot.ui.forms.questions

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.adapters.QuestionsAdapter.Companion.TYPE_QUESTION
import ro.code4.monitorizarevot.adapters.QuestionsAdapter.Companion.TYPE_SECTION
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class QuestionsViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val preferences: SharedPreferences by inject()
    private val questionsLiveData = MutableLiveData<ArrayList<ListItem>>()
    lateinit var selectedFormCode: String

    fun questions(): LiveData<ArrayList<ListItem>> = questionsLiveData

    private fun getQuestions(formCode: String) {

        selectedFormCode = formCode
        repository.getSectionsWithQuestions(formCode).observeForever {
            processList(it)
        }

    }

    private fun processList(sections: List<SectionWithQuestions>) {
        val list = ArrayList<ListItem>()
        sections.forEachIndexed { index, sectionWithQuestion ->
            list.add(ListItem(TYPE_SECTION, Pair(index + 1, sectionWithQuestion.section)))
            list.addAll(sectionWithQuestion.questions.map { ListItem(TYPE_QUESTION, it) })
        }
        questionsLiveData.postValue(list)
    }

    fun setData(formDetails: FormDetails) {
        getQuestions(formDetails.code)
    }

}