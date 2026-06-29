package net.rim.plazmic.internal.contentpreview.device.apps;

import java.io.ByteArrayOutputStream;
import java.util.Vector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.plazmic.internal.contentpreview.playback.PlaybackInfoHandler;
import net.rim.plazmic.internal.contentpreview.playback.io.PlaybackInfoMessageWriter;
import net.rim.plazmic.internal.contentpreview.service.LoopingThreadService;
import net.rim.plazmic.internal.contentpreview.service.Service;

public final class ControlPanelClient implements PlaybackInfoHandler, Service {
   private final LoopingThreadService _threadService = new LoopingThreadService(new ControlPanelClient$ControlPanelClientTask(this, null), true);
   private final DatagramConnection _connection;
   private final Datagram _packet;
   private byte[] _lastTime;
   private final Vector _otherMessages;
   private final ByteArrayOutputStream _buffer;
   private final PlaybackInfoMessageWriter _writer;
   public static final String rcsid;
   private static final boolean DEBUG;

   ControlPanelClient(DatagramConnection connection, String host, int serverPort, int clientPort) {
      if (connection == null) {
         throw new Object("connection must not be null");
      }

      if (host == null) {
         throw new Object("host must not be null");
      }

      this._lastTime = null;
      this._otherMessages = (Vector)(new Object());
      this._buffer = (ByteArrayOutputStream)(new Object());
      this._writer = new PlaybackInfoMessageWriter(this._buffer);
      this._connection = connection;
      this._packet = this._connection
         .newDatagram(
            0,
            ((StringBuffer)(new Object("//")))
               .append(host)
               .append(":")
               .append(serverPort)
               .append(";")
               .append(clientPort)
               .append("/rim.net.gprs|UDP")
               .toString()
         );
   }

   public final void startService() {
      this._threadService.startService();
   }

   public final void sceneInfo(int version, String mime, String[] custom, String[] font, String[] image, String[] media, String[] sound) {
      synchronized (this) {
         this._buffer.reset();
         this._writer.sceneInfo(version, mime, custom, font, image, media, sound);
         this._otherMessages.addElement(this._buffer.toByteArray());
      }

      this._threadService.next();
   }

   public final void sceneTimeChanged(long msTime) {
      synchronized (this) {
         this._buffer.reset();
         this._writer.sceneTimeChanged(msTime);
         this._lastTime = this._buffer.toByteArray();
      }

      this._threadService.next();
   }

   public final void started() {
      synchronized (this) {
         this._buffer.reset();
         this._writer.started();
         this._otherMessages.addElement(this._buffer.toByteArray());
      }

      this._threadService.next();
   }

   public final void stopped() {
      synchronized (this) {
         this._buffer.reset();
         this._writer.stopped();
         this._otherMessages.addElement(this._buffer.toByteArray());
      }

      this._threadService.next();
   }

   public final void rateChanged(float newRate) {
      synchronized (this) {
         this._buffer.reset();
         this._writer.rateChanged(newRate);
         this._otherMessages.addElement(this._buffer.toByteArray());
      }

      this._threadService.next();
   }

   public final void clientInfo(int version, int reserved) {
      synchronized (this) {
         this._buffer.reset();
         this._writer.clientInfo(version, reserved);
         this._otherMessages.addElement(this._buffer.toByteArray());
      }

      this._threadService.next();
   }
}
