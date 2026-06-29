package net.rim.device.cldc.io.waphttp;

import com.fourthpass.wapstack.IPacketTransiver;
import com.fourthpass.wapstack.bearer.IBearer;
import com.fourthpass.wapstack.bearer.RIM_SMS_Bearer;
import com.fourthpass.wapstack.bearer.RIM_UDP_Bearer;
import com.fourthpass.wapstack.ota.Dispatcher;
import com.fourthpass.wapstack.wdp.WDPLayer;
import com.fourthpass.wapstack.wdp.WDPPacket;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.browser.push.PushEventLogger;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.system.EventLogger;

public final class WAPServerConnection implements ServerSocketConnection, PushEventLogger {
   private int _localPort;
   private boolean _isActive;
   private IBearer _pushBearer;
   private IPacketTransiver _transiver;
   private int _bearer;

   @Override
   public final void close() {
      synchronized (this) {
         if (!this._isActive) {
            return;
         }

         this._isActive = false;
      }

      this._pushBearer.closeConnection();
   }

   @Override
   public final StreamConnection acceptAndOpen() {
      WDPPacket receiveDataPacket = new WDPPacket();

      while (true) {
         synchronized (this) {
            if (!this._isActive) {
               return null;
            }
         }

         receiveDataPacket.setSource(null);
         int size = this._transiver.receive(receiveDataPacket);
         EventLogger.logEvent(-1133226195824034738L, 1333030258, 5);
         if (size > 0) {
            byte[] rawData = new byte[receiveDataPacket.getDataLength()];
            System.arraycopy(receiveDataPacket.getPacketData(), 0, rawData, 0, receiveDataPacket.getDataLength());
            EventLogger.logEvent(-1133226195824034738L, 1333027428, 5);
            Object[] data = Dispatcher.processRawData(true, rawData, this._bearer, receiveDataPacket.getSource(), null, null);
            EventLogger.logEvent(-1133226195824034738L, 1333027172, 5);
            if (data != null) {
               return new ServerProtocol((HttpHeaders)data[0], (PushInputStream)data[1]);
            }
         }
      }
   }

   @Override
   public final String getLocalAddress() {
      return "127.0.0.1";
   }

   @Override
   public final int getLocalPort() {
      return this._localPort;
   }

   public WAPServerConnection(int localPort, int bearer) {
      this._localPort = localPort;
      this._bearer = bearer;
      if (this._bearer == 0) {
         this._pushBearer = new RIM_UDP_Bearer(this._localPort);
      } else {
         this._pushBearer = new RIM_SMS_Bearer(-1, this._localPort);
      }

      this._transiver = new WDPLayer(this._pushBearer);
      this._isActive = true;
   }
}
