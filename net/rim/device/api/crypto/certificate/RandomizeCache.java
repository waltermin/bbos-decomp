package net.rim.device.api.crypto.certificate;

import net.rim.device.api.system.GlobalEventListener;

class RandomizeCache implements GlobalEventListener {
   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7131874474196788121L) {
         CertificateFactory.randomizeCache();
      }
   }
}
