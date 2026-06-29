package net.rim.device.apps.internal.secureemail.encodings.pgp.cache;

import net.rim.device.apps.internal.secureemail.cache.CachedMessage;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPFactory;

public final class CachedPGPMessage extends CachedMessage {
   public CachedPGPMessage() {
      super(PGPFactory.getInstance());
   }
}
