package kuba.myprojects.zibo_fmc

import android.os.Looper
import java.lang.Exception
import java.net.*
import android.system.Os.socket
import java.io.IOException
import android.system.Os.socket





object Networking {

    var xplaneAddr = ""
    var fmcVersion:Int = 1


    //Class for sending button press commands to simulator
    class UDPSender () {

        private var cmd: String = ""
        private var sock: DatagramSocket = DatagramSocket()

        private val sendCommand: Runnable = object: Runnable {
            override fun run() {

                val buf: ByteArray = cmd.toByteArray()
                try {
                    val sendPacket = DatagramPacket(
                        buf,
                        cmd.length,
                        InetAddress.getByName(xplaneAddr),
                        49000
                    )
                    sock.send(sendPacket)
                } catch (e: Exception) {

                }
            }
        }

        fun sendButtonPress(cmd:String) {
            this.cmd = cmd
            val t = Thread(this.sendCommand)
            t.start()
            t.join()
        }
    }


    //Class for receiving display data from plugin running inside simulator
    class UDPReceiver(val display: Utilities.Display,val handler: android.os.Handler) {

        val getData: Runnable = object : Runnable {

            override fun run() {
                var sock = DatagramSocket()
                sock.soTimeout = 100;
                while (true) {
                    val msg = fmcVersion.toString()
                    val sendBuf: ByteArray = msg.toByteArray()
                    val rcvBuf = ByteArray(1024)
                    try {
                        val sendPacket = DatagramPacket(
                            sendBuf,
                            msg.length,
                            InetAddress.getByName(xplaneAddr),
                            10000
                        )

                        sock.send(sendPacket)
                    } catch (e: Exception) {

                    }
                    val rcvPacket = DatagramPacket(rcvBuf, rcvBuf.size)
                    try {
                        sock.receive(rcvPacket)
                        display.setStatus("Connected!\nDisplaying FMC ${if (fmcVersion == 1){"CPT"} else {"FO"}}")
                        handler.post(display.displayStatus)

                    } catch (e: SocketTimeoutException) {
                        display.setStatus("Cannot connect to X-plane. Click here to enter X-Plane IP")
                        handler.post(display.displayStatus)
                        continue;
                    }
                    display.setData(rcvPacket.data)
                    handler.post(display.refreshDisplay)
                    Thread.sleep(50)

                }
            }
        }

    }

}