package net.rim.device.internal.crypto.pgp;

import net.rim.device.api.util.Persistable;

public final class PGPSignatureSubPacket extends PGPPacket implements Persistable {
   public static final int X509_SIGNATURE_SUBPACKET_TAG;

   public PGPSignatureSubPacket(int tag, byte[] encoding) {
      super(tag, encoding);
   }

   public final boolean isCritical() {
      return (this.getTag() & 128) != 0;
   }
}
