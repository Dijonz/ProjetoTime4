package userDataRepo

import android.content.Context
import androidx.core.content.edit


private const val USER_PREFERENCES_NAME = "prefs_tokens"

private const val UID_KEY = "uid"
private const val FCMTOKEN_KEY = "fcmToken"



class UserPreferenceRepository private constructor(context: Context){

    private val sharedPreferences =
        context.applicationContext.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)

    /**
     * Obter o UID.
     */
    var uid: String = ""
        get() {
            return sharedPreferences.getString(UID_KEY, "")!!
        }

    fun updateUid(newUid: String) {
        sharedPreferences.edit {
            putString(UID_KEY, newUid)
        }
    }

    var fcmToken: String = ""
        get() {
            return sharedPreferences.getString(FCMTOKEN_KEY, "")!!
        }

    fun updateFcmToken(newFcmToken: String) {
        sharedPreferences.edit {
            putString(FCMTOKEN_KEY, newFcmToken)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferenceRepository? = null

        fun getInstance(context: Context): UserPreferenceRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }

                val instance = UserPreferenceRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }



}