package net.rim.wica.common.debug.io;

public final class ByteStreamBadOnDeserialize extends ByteStreamException {
   public ByteStreamBadOnDeserialize() {
   }

   public ByteStreamBadOnDeserialize(String message) {
      super(message);
   }
}
