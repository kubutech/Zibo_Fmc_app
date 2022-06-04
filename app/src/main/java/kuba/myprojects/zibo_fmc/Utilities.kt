package kuba.myprojects.zibo_fmc

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import java.lang.NumberFormatException
import java.nio.ByteBuffer

object Utilities {

    val keypad = arrayOf(
        arrayOf('A', 'B', 'C', 'D', 'E'),
        arrayOf('F', 'G', 'H', 'I', 'J'),
        arrayOf('K', 'L', 'M', 'N', 'O'),
        arrayOf('P', 'Q', 'R', 'S', 'T'),
        arrayOf('U', 'V', 'W', 'X', 'Y'),
        arrayOf('Z', "SP", "del", "slash", "clr"))

    val numpad = arrayOf(
        arrayOf(1,2,3),
        arrayOf(4,5,6),
        arrayOf(7,8,9),
        arrayOf("period", "0", "minus"))

    val funpad = arrayOf(
        arrayOf("init_ref", "rte", "clb", "crz", "des"),
        arrayOf("menu", "legs", "dep_app", "hold", "prog"),
        arrayOf("n1_lim", "fix"),
        arrayOf("prev_page", "next_page")
    )

    val navpadL = arrayOf("1L", "2L", "3L", "4L", "5L", "6L")
    val navpadR = arrayOf("1R", "2R", "3R", "4R", "5R", "6R")

    var isNotificationActive = false

    class Display(val activity: Activity) {

        private val statusDisplay = getTextView(R.id.networkStatus)
        private var status = String()


        private val line6LX = getTextView(R.id.line6LX)

        private val linesMain = listOf<TextView>(
            getTextView(R.id.line0),
            getTextView(R.id.line1),
            getTextView(R.id.line2),
            getTextView(R.id.line3),
            getTextView(R.id.line4),
            getTextView(R.id.line5),
            getTextView(R.id.line6),
            getTextView(R.id.lineInput)
        )

        private val linesMainMagenta = listOf<TextView>(
            getTextView(R.id.line0M),
            getTextView(R.id.line1M),
            getTextView(R.id.line2M),
            getTextView(R.id.line3M),
            getTextView(R.id.line4M),
            getTextView(R.id.line5M),
            getTextView(R.id.line6M)
        )


        private val linesMainGreen = listOf<TextView>(
            getTextView(R.id.line0G),
            getTextView(R.id.line1G),
            getTextView(R.id.line2G),
            getTextView(R.id.line3G),
            getTextView(R.id.line4G),
            getTextView(R.id.line5G),
            getTextView(R.id.line6G)
        )


        private val linesSmallLabel = listOf<TextView>(
            getTextView(R.id.line0S),
            getTextView(R.id.line1X),
            getTextView(R.id.line2X),
            getTextView(R.id.line3X),
            getTextView(R.id.line4X),
            getTextView(R.id.line5X),
            getTextView(R.id.line6X))


        private val linesSmall = listOf<TextView>(
            getTextView(R.id.line1S),
            getTextView(R.id.line2S),
            getTextView(R.id.line3S),
            getTextView(R.id.line4S),
            getTextView(R.id.line5S),
            getTextView(R.id.line6S))



        val background = getImageView(R.id.cduBackground)

        private val msgAnnunciator = getView(R.id.msg_annunciator)
        private val execAnnunciator = getView(R.id.exec_annunciator)

        var isNotificationSent = false



        val buttonL = getButton(R.id.fmcCpt)
        val buttonR = getButton(R.id.fmcFO)

        private val letterSpacing = 0.17F
        private var letterSize = 53F
        private val scaleFactor = 0.779F

        private var data: ByteArray = ByteArray(1024)



        fun setData(data: ByteArray) {
            this.data = data
        }

        //function that positions lines of display depending on device screen resolution
        //All values are scaled relative to 1080p screen
        val setDisplay: Runnable = object : Runnable {

            override fun run() {
                val scale = (background.width.toFloat() / 1080)

                val typeface = ResourcesCompat.getFont(activity, R.font.b738_font)

                for (i in linesMain.indices) {
                    val layout = linesMain[i].layoutParams as ConstraintLayout.LayoutParams
                    layout.width = (700 * scale).toInt()
                    if (i == linesMain.lastIndex) {
                        layout.topMargin = (760 * scale).toInt()
                    } else {
                        layout.topMargin = ((145 + i * 93) * scale).toInt()
                    }
                    linesMain[i].layoutParams = layout
                    linesMain[i].typeface = typeface
                    linesMain[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, letterSize * scale)
                }


                for (i in linesSmallLabel.indices) {
                    val layout = linesSmallLabel[i].layoutParams as ConstraintLayout.LayoutParams
                    layout.width = (700 * scale).toInt()
                    if (i == 0) {
                        layout.topMargin = (150 * scale).toInt()
                    } else {
                        layout.topMargin = ((106 + 93 * i) * scale).toInt()
                    }
                    linesSmallLabel[i].layoutParams = layout
                    linesSmallLabel[i].typeface = typeface
                    linesSmallLabel[i].setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        letterSize * scale * scaleFactor
                    )
                    linesSmallLabel[i].letterSpacing = letterSpacing
                }

                for (i in linesSmall.indices) {
                    val layout = linesSmall[i].layoutParams as ConstraintLayout.LayoutParams
                    layout.width = (700 * scale).toInt()
                    layout.topMargin = ((245 + 93 * i) * scale).toInt()
                    linesSmall[i].layoutParams = layout
                    linesSmall[i].typeface = typeface
                    linesSmall[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, letterSize * scale * scaleFactor)
                    linesSmall[i].letterSpacing = letterSpacing
                }


                for (i in linesMainMagenta.indices) {
                    val layout = linesMainMagenta[i].layoutParams as ConstraintLayout.LayoutParams
                    layout.width = (700 * scale).toInt()
                    layout.topMargin = ((145 + i * 93) * scale).toInt()
                    linesMainMagenta[i].layoutParams = layout
                    linesMainMagenta[i].typeface = typeface
                    linesMainMagenta[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, letterSize * scale)
                }

                for (i in linesMainGreen.indices) {
                    val layout = linesMainGreen[i].layoutParams as ConstraintLayout.LayoutParams
                    layout.width = (700 * scale).toInt()
                    layout.topMargin = ((145 + i * 93) * scale).toInt()
                    linesMainGreen[i].layoutParams = layout
                    linesMainGreen[i].typeface = typeface
                    linesMainGreen[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, letterSize * scale)
                }

                val lay = line6LX.layoutParams as ConstraintLayout.LayoutParams
                lay.width = (700 * scale).toInt()
                lay.topMargin = (660 * scale).toInt()
                line6LX.layoutParams = lay
                line6LX.typeface = typeface
                line6LX.setTextSize(TypedValue.COMPLEX_UNIT_PX, letterSize * scale)

                statusDisplay.setTextSize(TypedValue.COMPLEX_UNIT_PX, letterSize * scale * 0.9F)
                val par = statusDisplay.layoutParams as ConstraintLayout.LayoutParams
                par.width = (680 * scale).toInt()
                statusDisplay.layoutParams = par

                var buttonParams = buttonL.layoutParams as ConstraintLayout.LayoutParams
                buttonParams.width = (180 * scale).toInt()
                buttonParams.height = (140 * scale).toInt()
                buttonL.layoutParams = buttonParams
                buttonL.setTextSize(TypedValue.COMPLEX_UNIT_PX, letterSize * scale)
                buttonL.setPadding((5 * scale).toInt())

                buttonParams = buttonR.layoutParams as ConstraintLayout.LayoutParams
                buttonParams.width = (180 * scale).toInt()
                buttonParams.height = (140 * scale).toInt()
                buttonR.setPadding((5 * scale).toInt())
                buttonR.layoutParams = buttonParams
                buttonR.setTextSize(TypedValue.COMPLEX_UNIT_PX, letterSize * scale)

                var announciatorParams = msgAnnunciator.layoutParams as ConstraintLayout.LayoutParams
                announciatorParams.height = (120 * scale).toInt()
                announciatorParams.width = (1000 * scale).toInt()
                announciatorParams.topMargin = (1362 * scale).toInt()
                msgAnnunciator.layoutParams = announciatorParams

                announciatorParams = execAnnunciator.layoutParams as ConstraintLayout.LayoutParams
                announciatorParams.height = (60 * scale).toInt()
                announciatorParams.width = (1000 * scale).toInt()
                announciatorParams.topMargin = (1040 * scale).toInt()
                execAnnunciator.layoutParams = announciatorParams
            }
        }


        //Function for displaying received data on on the screen
        val refreshDisplay: Runnable = object : Runnable {

            override fun run() {
                val lines: List<String> = String(data).split("&")
                if (lines.size == linesMain.size + linesSmallLabel.size + linesSmall.size + linesMainMagenta.size + linesMainGreen.size + 4) {
                    for (i in linesMain.indices) {
                        linesMain[i].text = lines[i]
                    }
                    for (i in linesSmallLabel.indices) {
                        linesSmallLabel[i].text = lines[i + linesMain.size]
                    }
                    for (i in linesSmall.indices) {
                        linesSmall[i].text = lines[i + linesMain.size + linesSmallLabel.size]
                    }
                    for (i in linesMainMagenta.indices) {
                        linesMainMagenta[i].text = lines[i + linesMain.size + linesSmallLabel.size + linesSmall.size]
                    }
                    for (i in linesMainGreen.indices) {
                        linesMainGreen[i].text = lines[i + linesMain.size + linesSmallLabel.size + linesSmall.size + linesMainMagenta.size]
                    }

                    line6LX.text = lines[lines.size - 4]

                    execAnnunciator.setBackgroundColor(if (lines[lines.size - 2] == "1"){activity.getColor(R.color.annunciator_yellow)} else {activity.getColor(R.color.black_background)})

                    msgAnnunciator.setBackgroundColor(if ( lines[lines.size - 1][0] == '1') {activity.getColor(R.color.annunciator_orange)} else {activity.getColor(R.color.black_background)})

                    val distance = lines[lines.size - 3].toInt()
                    if (distance == 40 && !isNotificationSent && isNotificationActive) {

                        val intent = Intent(activity, activity::class.java)
                        intent.action = Intent.ACTION_MAIN
                        intent.addCategory(Intent.CATEGORY_LAUNCHER)

                        val pendingIntent: PendingIntent =
                            PendingIntent.getActivity(activity, 0, intent, 0)

                        var builder = NotificationCompat.Builder(activity, "ID")
                            .setSmallIcon(R.drawable.icon)
                            .setContentTitle("Approaching Top Of Descent!")
                            .setContentText("Descent will start in ${distance}NM")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setOnlyAlertOnce(true)

                        with(NotificationManagerCompat.from(activity)) {
                            notify(3, builder.build())
                        }
                        isNotificationSent = true
                    }
                }
            }
        }


        //Function for displaying connection status
        val displayStatus: Runnable = object : Runnable {

            override fun run() {
                statusDisplay.text = status
            }
        }

        fun setStatus(stat:String) {
            this.status = stat
        }


        private fun getTextView(id: Int): TextView {
            return activity.findViewById(id)
        }

        private fun getImageView(id:Int): ImageView {
            return activity.findViewById(id)
        }

        private fun getButton(id:Int): Button {
            return activity.findViewById(id)
        }

        private fun getView(id:Int): View {
            return activity.findViewById(id)
        }


    }






//Function for finding button based on coordinates of touch action
    fun findKeyboardButton(x:Float, y:Float): String {
        if (x > 438 && x < 958 && y > 1192 && y < 1882) {
            val xKeypad = ((x - 438) / 104).toInt()
            val yKeypad = ((y - 1192) / 115).toInt()
            return keypad[yKeypad][xKeypad].toString()
        } else if (x > 106 && x < 418 && y > 1422 && y < 1882) {
            val xNumpad = ((x - 106) / 104).toInt()
            val yNumpad = ((y - 1422) / 115).toInt()
            return numpad[yNumpad][xNumpad].toString()
        } else if ((x > 118 && x < 391 && y > 942 && y < 1409) || (x >= 391 && x < 796 && y > 942 && y < 1172)) {
            val xFunpad = ((x - 118) / 135.6).toInt()
            val yFunpad = ((y - 942) / 116.75).toInt()
            return funpad[yFunpad][xFunpad]
        } else if (x < 105 && y < 792 && y > 232) {
            val yNavpad = ((y - 232) / 93.33).toInt()
            return navpadL[yNavpad]
        } else if (x > 977 && y < 792 && y > 232) {
            val yNavpad = ((y - 232) / 93.33).toInt()
            return navpadR[yNavpad]
        } else if (x > 816 && x < 948 && y > 1083 && y < 1185) {
            return "exec"
        } else {
            return "x"
        }
    }
}