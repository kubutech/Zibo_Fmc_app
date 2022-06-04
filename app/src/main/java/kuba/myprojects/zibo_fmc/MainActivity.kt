package kuba.myprojects.zibo_fmc

import android.annotation.SuppressLint
import android.app.*
import android.os.Bundle
import android.app.AlertDialog.*
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.*
import android.text.InputType
import android.util.TypedValue.*
import android.widget.*
import java.net.*
import android.view.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class MainActivity : Activity()  {

    @SuppressLint("ClickableViewAccessibility", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        val handler = Handler(Looper.getMainLooper())


        val sharedPref = getSharedPreferences("savedData",0)

        if(sharedPref.contains("IP") && sharedPref.contains("Settings")) {
            //Networking.xplaneAddr = sharedPref.getString("IP","").toString()
            Utilities.isNotificationActive = sharedPref.getString("Settings","").toBoolean()
        }

        createNotificationChannel()

        val display = Utilities.Display(this)

        handler.postDelayed(display.setDisplay,50)

        //Listener listening for keyboard touch inputs
        display.background.setOnTouchListener { v, touch ->
            if(touch.action == MotionEvent.ACTION_UP) {
                val x = touch.x * 1080 / (display.background.width.toFloat())
                val y = touch.y * 1920 / (display.background.height.toFloat())
                if (Utilities.findKeyboardButton(x,y) != "x") {
                    val cmd = "CMND\u0000laminar/B738/button/fmc${Networking.fmcVersion}_${Utilities.findKeyboardButton(x,y)}"
                    val t = Thread(Networking.UDPSender(cmd,display))
                    t.start()
                    t.join()
                }
                if (y < 156 && x > 180 && x < 900) {
                    popupEnterHostname()
                }
            }
            return@setOnTouchListener true
        }

        //Listeners for buttons that change fmc Version (Captain and First Pilot
        display.buttonR.setOnClickListener { Networking.fmcVersion = 2}
        display.buttonL.setOnClickListener { Networking.fmcVersion = 1}


        Thread(Networking.UDPReceiver(display, handler).getData).start()

    }

    override fun onStop() {
        super.onStop()
        val sharedPref = getSharedPreferences("savedData",0)
        val editor = sharedPref.edit()
        editor.putString("IP",Networking.xplaneAddr)
        editor.putString("Settings",Utilities.isNotificationActive.toString())
        editor.commit()
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel"
            val descriptionText = "Does channel stuff"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ID", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //Popup for entering IP address
    private fun popupEnterHostname() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter X-Plane IP")
        val alertLayout = layoutInflater.inflate(R.layout.alert_layout,null)
        val input = alertLayout.findViewById<EditText>(R.id.textEdit)
        input.setText(Networking.xplaneAddr)
        input.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        val checkbox = alertLayout.findViewById<CheckBox>(R.id.checkbox)
        checkbox.isChecked = Utilities.isNotificationActive
        checkbox.setOnClickListener { v:View ->
            Utilities.isNotificationActive = checkbox.isChecked
        }
        builder.setPositiveButton("Confirm" ){ dialog, which -> Networking.xplaneAddr = input.text.toString() }
        builder.setNeutralButton("How to find X-plane IP?") {dialog, which ->
            val tutBuilder = AlertDialog.Builder(this)
            val tut = TextView(this)
            tutBuilder.setTitle("How to find X-Plane IP Address")
            tut.text = "To find X-Plane IP Address, go to X-Plane Settings -> Network. Your IP is under \"This Machine's IP Address\", 3rd address"
            tut.textSize = 18F
            tutBuilder.setView(tut)
            tutBuilder.setNeutralButton("Understood") {dialog, which->}
            tutBuilder.show()
        }
        builder.setView(alertLayout)
        builder.show()
    }






}


