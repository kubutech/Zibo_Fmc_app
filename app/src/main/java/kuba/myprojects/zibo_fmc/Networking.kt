package kuba.myprojects.zibo_fmc

import android.os.Looper
import java.lang.Exception
import java.net.*

object Networking {

    var xplaneAddr = ""
    var fmcVersion:Int = 1

    class UDPSender (val cmd: String,  val display: Utilities.Display) : Runnable {

        override fun run() {
            var sock = DatagramSocket(8000)
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
            sock.close()
        }
    }


    class UDPReceiver(val display: Utilities.Display,val handler: android.os.Handler) {



        val getData: Runnable = object : Runnable {

            override fun run() {
                var sock = DatagramSocket(3000)
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