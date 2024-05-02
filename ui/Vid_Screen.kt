package com.example.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ui.databinding.ActivityMainBinding
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import okhttp3.internal.http.HTTP_GONE

class Vid_Screen : AppCompatActivity() {
    private lateinit var biding: ActivityMainBinding
    private val appId = ""
    private val channelName = "call_vid"
    private val token =
        "007eJxTYGC6vG/na+2ZqnfUf529tFPuu+EHx+1lcuL/BE8lz3+hJKCjwGBikpJmaGZiamBuZm5iYJloYZCUaGxukpKYZJiakpxq7BjLkNYQyMiQ2VPAxMgAgSA+B0NyYk5OfFlmCgMDAP2+IMI="
    private val uid = 0
    private var isJoined = false
    private var agoraEngine: RtcEngine? = null
    private var localSurfaceView: SurfaceView? = null
    private var remoteSurfaceView: SurfaceView? = null
    private val PERMISSSION_ID = 12
    private val REQUESTED_PERMISSION = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )

    private fun checkSelfPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            this, REQUESTED_PERMISSION[0]
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this, REQUESTED_PERMISSION[1]
        ) != PackageManager.PERMISSION_GRANTED)
    }

    private fun setUpVideoSdkEngine() {
        val config = RtcEngineConfig()
        config.mContext = baseContext
        config.mAppId = appId
        config.mEventHandler = mRtcEventHandler
        agoraEngine = RtcEngine.create(config)
        agoraEngine!!.enableVideo()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vid_screen)
        biding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(biding.root)
        if (!checkSelfPermission()) {
            ActivityCompat
                .requestPermissions(this, REQUESTED_PERMISSION, PERMISSSION_ID)
        }
        setUpVideoSdkEngine()
        joinCall()
    }

    private fun setupRemoteVideo(uid:Int){

    }
    private fun setuplocalVideo(){

    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()
        Thread{
            RtcEngine.destroy()
            agoraEngine=null
        }.start()
    }

    fun PTer(view: View) {
        finish()
    }

    fun vid_s(view: View) {
        val intent = Intent(this, Camera_photo::class.java)
        startActivity(intent)
        finish()
    }

    private fun leaveCall() {

    }

    private fun joinCall() {
        if(checkSelfPermission()){
            val option=ChannelMediaOptions()
            option.channelProfile=Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType=Constants.CLIENT_ROLE_BROADCASTER
            setuplocalVideo()
            localSurfaceView!!.visibility=VISIBLE
            agoraEngine!!.startPreview()
            agoraEngine!!.joinChannel(token,channelName,uid,option)
        }
    }

    private val mRtcEventHandler: IRtcEngineEventHandler =
        object : IRtcEngineEventHandler() {
            override fun onUserJoined(uid: Int, elapsed: Int) {
                runOnUiThread{
                    setupRemoteVideo(uid)
                }
            }

            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                isJoined = true
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                super.onUserOffline(uid, reason)
                runOnUiThread{
                    remoteSurfaceView!!.visibility= GONE

                }
            }
        }
}