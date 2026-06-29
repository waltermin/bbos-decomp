package net.rim.device.internal.crypto.pgp;

import net.rim.device.api.util.Persistable;

public class PGPUnsupportedPacket extends PGPPacket implements Persistable {
   public PGPUnsupportedPacket(int tag, byte[] encoding) {
      super(tag, encoding);
   }
}
