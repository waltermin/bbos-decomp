package net.rim.plazmic.internal.contentpreview.device.apps;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.plazmic.internal.contentpreview.playback.PlaybackCommandHandler;
import net.rim.plazmic.internal.contentpreview.service.LoopingThreadService;
import net.rim.plazmic.internal.contentpreview.service.Service;
import net.rim.plazmic.internal.contentpreview.service.ServiceException;

public final class PlaybackCommandServer implements Service {
   private final LoopingThreadService _threadService = new LoopingThreadService(new PlaybackCommandServer$PlaybackCommandServerTask(this, null), false);
   private final DatagramConnection _connection;
   private final int _port;
   private final Datagram _packet;
   private PlaybackCommandHandler _handler = null;
   private boolean _closed = false;
   public static final String rcsid = "$Id: //depot/projects/JavaDevice/4.3.0/JavaApplications/sdk/CDK/net/rim/plazmic/internal/contentpreview/device/apps/PlaybackCommandServer.java#1 $";
   private static final boolean DEBUG = false;

   PlaybackCommandServer(int port) {
      this._port = port;
      this._connection = (DatagramConnection)Connector.open("udp://:" + String.valueOf(this._port));
      this._packet = this._connection.newDatagram(this._connection.getMaximumLength());
   }

   final DatagramConnection getConnection() {
      return this._connection;
   }

   final int getPort() {
      return this._port;
   }

   final synchronized void setHandler(PlaybackCommandHandler handler) {
      this._handler = handler;
   }

   public final synchronized void startService() {
      if (this._closed) {
         throw new IllegalStateException("net.rim.device.apps.internal.browser.pme.PlaybackCommandServer may not be restarted.");
      }

      this._threadService.startService();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized void stopService() throws ServiceException {
      this._closed = true;
      this._threadService.stopService(false);

      try {
         try {
            this._connection.close();
         } catch (Throwable var13) {
            throw new ServiceException(e);
         }
      } finally {
         ;
      }

      try {
         this._threadService.waitForStop();
      } catch (Throwable var12) {
         throw new ServiceException(e);
      }
   }
}
