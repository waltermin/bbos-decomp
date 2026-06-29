package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongIntHashtable;

final class LongIntHashtableSerializer extends AbstractSerializer {
   private static LongIntHashtableSerializer _instance;

   static final LongIntHashtableSerializer getInstance() {
      if (_instance == null) {
         _instance = new LongIntHashtableSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void serializeObject(DataBuffer buffer, Object obj) {
      LongIntHashtable hashtable = (LongIntHashtable)obj;
      LongEnumeration longEnum = hashtable.keys();

      while (longEnum.hasMoreElements()) {
         long key = longEnum.nextElement();
         ConverterUtilities.writeLong(buffer, 124, key);
         ConverterUtilities.writeInt(buffer, 123, hashtable.get(key));
      }
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      LongIntHashtable hashtable = (LongIntHashtable)obj;
      switch (type) {
         case 124:
            hashtable.put(ConverterUtilities.readLong(buffer), ConverterUtilities.readInt(buffer));
            return;
         default:
            ConverterUtilities.skipField(buffer);
      }
   }

   @Override
   protected final Object createObject() {
      return new LongIntHashtable();
   }

   private LongIntHashtableSerializer() {
   }
}
