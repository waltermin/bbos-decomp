package net.rim.device.api.io;

import javax.microedition.io.Datagram;

class SendPacketThread$SPTRequest {
   public Object _sendObj;
   public Datagram _datagram;

   public SendPacketThread$SPTRequest(Object sendObj, Datagram datagram) {
      this._sendObj = sendObj;
      this._datagram = datagram;
   }
}
