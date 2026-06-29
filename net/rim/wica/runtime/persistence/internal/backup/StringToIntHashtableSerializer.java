package net.rim.wica.runtime.persistence.internal.backup;

import java.util.Enumeration;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.wica.runtime.util.SerializerUtil;

final class StringToIntHashtableSerializer extends AbstractSerializer {
   private static StringToIntHashtableSerializer _instance;

   static final StringToIntHashtableSerializer getInstance() {
      if (_instance == null) {
         _instance = new StringToIntHashtableSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void serializeObject(DataBuffer buffer, Object obj) {
      ToIntHashtable hashtable = (ToIntHashtable)obj;
      Enumeration keys = hashtable.keys();

      while (keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         SerializerUtil.writeString(buffer, (byte)124, key);
         ConverterUtilities.writeInt(buffer, 123, hashtable.get(key));
      }
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      ToIntHashtable hashtable = (ToIntHashtable)obj;
      switch (type) {
         case 124:
            hashtable.put(ConverterUtilities.readString(buffer), ConverterUtilities.readInt(buffer));
            return;
         default:
            ConverterUtilities.skipField(buffer);
      }
   }

   @Override
   protected final Object createObject() {
      return new Object();
   }

   private StringToIntHashtableSerializer() {
   }
}
