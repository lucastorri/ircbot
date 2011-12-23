package co.torri.ircbot

import java.io.File.{separator => |}
import java.io.File
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date

import jerklib.events.IRCEvent.Type.CHANNEL_MESSAGE
import jerklib.events.IRCEvent.Type.CONNECT_COMPLETE
import jerklib.events.IRCEvent
import jerklib.events.MessageEvent
import jerklib.listeners.IRCEventListener
import jerklib.ConnectionManager
import jerklib.Profile


object IRCBot {

    def main(args: Array[String]) {
        val ircNick = args(0)
        val ircServer = args(1)
        val ircChannel = args(2)
        val logDir = new File(args(3))
            
        val manager = new ConnectionManager(new Profile(ircNick))
        val session = manager.requestConnection(ircServer)
        val ircLogger = MessageLogger(logDir)
        
        session.addIRCEventListener(IRCProtocolHandler(ircChannel, ircLogger).protocolHandler)
    }
}


case class IRCProtocolHandler(channel: String, logger: MessageLogger) {

    def onMessage(channel: String, sender: String, msg: String, pvt: Boolean = false, date: Date = new Date) : Unit =
        if (channel == channel && !pvt) logger(IRCMessage(channel, sender, msg, date))

    private def onMessage(msgEvent: MessageEvent) : Unit =
        onMessage(msgEvent.getChannel.getName, msgEvent.getNick, msgEvent.getMessage, msgEvent.isPrivate)

    object protocolHandler extends IRCEventListener {

        override def receiveEvent(e: IRCEvent): Unit =
            e.getType match {
                case CONNECT_COMPLETE => {
                    val session = e.getSession
                    session.join(channel)
                    session.setRejoinOnKick(true)
                }
                case CHANNEL_MESSAGE => onMessage(e.asInstanceOf[MessageEvent])
                case _ => {}
            }

    }
}

case class MessageLogger(dir: File) {
    require(dir.isDirectory)

    private val logFileFormat = "%s_%s.log"
    private val logFileDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    private val logException: PartialFunction[Throwable, Unit] = { case e => println("Error logging chat messages"); e.printStackTrace }
    private var currentFile: (String, Option[PrintWriter]) = ("", None)

    private def logFileFor(msg: IRCMessage) =
        (dir.getCanonicalPath + | +
            logFileFormat.
            format(msg.channel, logFileDateFormat.format(msg.time))).replaceAll("#", "")

    private def loggerFor(msg: IRCMessage) = {
        val filename = logFileFor(msg)
        if (currentFile._1 != filename) {
            currentFile._2.map { printer =>
                try printer.close
                catch logException
            }
            try currentFile = (filename, Some(new PrintWriter(filename)))
            catch logException
        }
        currentFile._2.get
    }

    def apply(msg: IRCMessage) =
        try {
            val logger = loggerFor(msg)
            logger.println(msg)
            logger.flush
        } catch logException

}

object IRCMessage {

    private val _timeFormat = new SimpleDateFormat("[yyyy-MM-dd/HH:mm:ss]")

    def formatTime(msg: IRCMessage) =
        _timeFormat.format(msg.time)

}

case class IRCMessage(channel: String, sender: String, msg: String, time: Date) {

    override def toString =
        IRCMessage.formatTime(this) + " " + sender + ": " + msg

}