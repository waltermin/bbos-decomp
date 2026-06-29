package net.rim.device.internal.crypto.pgp;

import net.rim.device.api.crypto.pgp.PGPEncodingException;
import net.rim.device.api.util.Persistable;

public class PGPTrustPacket extends PGPPacket implements Persistable {
   private byte _trust;

   public PGPTrustPacket(int tag, byte[] encoding) {
      super(tag, encoding);
      if (encoding.length < 1) {
         throw new PGPEncodingException("TrLM");
      }

      this._trust = encoding[0];
   }

   public int getTrustState() {
      byte lowestThreeBits = (byte)(this._trust & 7);
      switch (lowestThreeBits) {
         case 0:
            if ((this._trust & 16) != 0) {
               return 2;
            }
         case -1:
         case 1:
         case 3:
         case 4:
            return 3;
         case 2:
            return 2;
         case 5:
            return 1;
         case 6:
         case 7:
         default:
            return 0;
      }
   }
}
