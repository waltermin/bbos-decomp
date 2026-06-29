package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.util.SerializerUtil;

public class StringSerializer extends AbstractArraySerializer {
   private static StringSerializer _instance;

   static StringSerializer getInstance() {
      if (_instance == null) {
         _instance = new StringSerializer();
      }

      return _instance;
   }

   static void nullInstance() {
      _instance = null;
   }

   @Override
   public void serialize(DataBuffer buffer, byte type, Object obj) {
      SerializerUtil.writeString(buffer, type, (String)obj);
   }

   @Override
   public Object deserialize(DataBuffer buffer) {
      return ConverterUtilities.readString(buffer);
   }

   @Override
   protected Object[] createArray(int size) {
      return new Object[size];
   }

   private StringSerializer() {
   }
}
