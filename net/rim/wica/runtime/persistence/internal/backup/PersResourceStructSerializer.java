package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

final class PersResourceStructSerializer extends AbstractArraySerializer {
   private static PersResourceStructSerializer _instance;
   private static final byte URL = 0;
   private static final byte BYTES = 1;
   private static final byte MIME_TYPE = 2;

   static final PersResourceStructSerializer getInstance() {
      if (_instance == null) {
         _instance = new PersResourceStructSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      ConverterUtilities.skipField(buffer);
   }

   @Override
   protected final Object createObject() {
      return new Object();
   }

   @Override
   protected final Object[] createArray(int size) {
      return new Object[size];
   }

   private PersResourceStructSerializer() {
   }
}
