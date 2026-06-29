package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

final class PersistableAGInfoSerializer extends AbstractArraySerializer {
   private static PersistableAGInfoSerializer _instance;

   static final PersistableAGInfoSerializer getInstance() {
      if (_instance == null) {
         _instance = new PersistableAGInfoSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      switch (type) {
         default:
            ConverterUtilities.skipField(buffer);
      }
   }

   @Override
   protected final Object[] createArray(int size) {
      return new Object[size];
   }

   private PersistableAGInfoSerializer() {
   }
}
