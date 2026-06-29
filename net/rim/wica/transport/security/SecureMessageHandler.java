package net.rim.wica.transport.security;

import net.rim.wica.transport.UnsupportedVersionException;

public class SecureMessageHandler {
   public void handleMessage(SecureMessageV1 message) {
      throw new UnsupportedVersionException();
   }
}
