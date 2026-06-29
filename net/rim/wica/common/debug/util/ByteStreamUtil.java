package net.rim.wica.common.debug.util;

import net.rim.wica.common.debug.io.ByteArrayOutputByteStream;
import net.rim.wica.common.debug.protocol.messages.ISerializableMessage;

public final class ByteStreamUtil {
   public static final byte[] serialize(ISerializableMessage message) {
      try {
         ByteArrayOutputByteStream byteStream = new ByteArrayOutputByteStream();
         message.serialize(byteStream);
         return byteStream.getByteArray();
      } finally {
         ;
      }
   }

   private ByteStreamUtil() {
   }
}
