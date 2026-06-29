package net.rim.device.internal.crypto.pgp;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public class PGPPacket implements Persistable {
   private int _tag;
   private byte[] _encoding;

   public PGPPacket(int tag, byte[] encoding) {
      this._tag = tag;
      this._encoding = Arrays.copy(encoding);
   }

   public int getTag() {
      return this._tag;
   }

   public byte[] getEncoding() {
      return Arrays.copy(this._encoding);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof PGPPacket)) {
         return false;
      }

      PGPPacket pgpPacket = (PGPPacket)o;
      return this._tag == pgpPacket._tag && Arrays.equals(this._encoding, pgpPacket._encoding);
   }
}
