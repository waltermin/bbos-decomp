package net.rim.wica.transport.message;

import net.rim.wica.transport.UnsupportedVersionException;

public class TransportMessageHandler {
   public void handleMessage(TransportMessageV1 tm) throws UnsupportedVersionException {
      throw new UnsupportedVersionException();
   }

   public void handleMessage(TransportMessageV2 tm) throws UnsupportedVersionException {
      throw new UnsupportedVersionException();
   }
}
