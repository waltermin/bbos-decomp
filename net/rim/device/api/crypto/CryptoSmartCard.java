package net.rim.device.api.crypto;

import net.rim.device.api.smartcard.SmartCard;
import net.rim.vm.Persistable;

public class CryptoSmartCard extends SmartCard implements Persistable {
   protected CryptoSmartCard() {
   }

   public String[] getAlgorithms() {
      throw null;
   }

   public CryptoToken getCryptoToken(String _1) {
      throw null;
   }
}
