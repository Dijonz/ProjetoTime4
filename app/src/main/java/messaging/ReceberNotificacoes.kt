package messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dijonz.projeto_grupo4.CadastroConcluido
import com.dijonz.projeto_grupo4.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import userDataRepo.UserPreferenceRepository


const val channelId = "notification_channel"
class ReceberNotificacoes : FirebaseMessagingService() {

    private lateinit var UserPreferenceRepository: UserPreferenceRepository


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val msgData =remoteMessage.data
        val msg = msgData.['text']

    }





    fun BuildNotify(title:String, message:String){

        val intent = Intent(this,CadastroConcluido)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.notification)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContent(pendingIntent)

        builder = builder.setContent(getRemoteView(title,message))


    }





}
