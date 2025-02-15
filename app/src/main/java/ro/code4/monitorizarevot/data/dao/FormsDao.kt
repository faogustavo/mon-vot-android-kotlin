package ro.code4.monitorizarevot.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Completable
import io.reactivex.Maybe
import ro.code4.monitorizarevot.data.model.Answer
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.model.Section
import ro.code4.monitorizarevot.data.model.answers.AnsweredQuestion
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import java.util.*

@Dao
interface FormsDao {
    @Query("SELECT * FROM form_details")
    fun getAllForms(): Maybe<List<FormDetails>>

    @Insert(onConflict = REPLACE)
    fun saveForm(vararg forms: FormDetails): Completable

    @Delete
    fun deleteForms(vararg forms: FormDetails): Completable

    @Query("SELECT * FROM section WHERE code=:formCode")
    fun getSectionsByCode(formCode: String): Maybe<List<Section>>

    @Query("DELETE FROM section WHERE code=:formCode")
    fun deleteSectionsByCode(formCode: String): Completable

    @Transaction
    fun save(vararg sections: Section) {
        saveSections(*sections)
        val questions = sections.fold(ArrayList<Question>(), { list, section ->
            list.addAll(section.questions)
            list
        })
        saveQuestions(*questions.map { it }.toTypedArray())
        val answers = questions.fold(ArrayList<Answer>(), { list, question ->
            list.addAll(question.answers)
            list
        })
        saveAnswers(*answers.map { it }.toTypedArray())
    }

    @Insert(onConflict = REPLACE)
    fun saveSections(vararg sections: Section)

    @Insert(onConflict = REPLACE)
    fun saveQuestions(vararg questions: Question)

    @Insert(onConflict = REPLACE)
    fun saveAnswers(vararg answers: Answer)


    @Insert(onConflict = REPLACE)
    fun saveAnsweredQuestions(vararg answeredQuestions: AnsweredQuestion): Completable

    @Delete
    fun deleteAnsweredQuestions(vararg answeredQuestions: AnsweredQuestion): Completable

    @Query("SELECT * FROM answered_question WHERE countyCode=:countyCode AND sectionNumber=:branchNumber ")
    fun getAnswersFor(countyCode: String, branchNumber: Int): LiveData<List<AnsweredQuestionPOJO>>


    @Query("SELECT * FROM form_details")
    fun getFormsWithSections(): LiveData<List<FormWithSections>>

    @Query("SELECT * FROM section where formCode=:formCode")
    fun getSectionsWithQuestions(formCode: String): LiveData<List<SectionWithQuestions>>


}