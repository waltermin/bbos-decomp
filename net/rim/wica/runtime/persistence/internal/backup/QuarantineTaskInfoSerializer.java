package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.lifecycle.QuarantineTaskInfo;
import net.rim.wica.runtime.util.SerializerUtil;

final class QuarantineTaskInfoSerializer extends AbstractSerializer {
   private static QuarantineTaskInfoSerializer _instance;
   private static final byte ACTION = 0;
   private static final byte REASON = 1;

   static final QuarantineTaskInfoSerializer getInstance() {
      if (_instance == null) {
         _instance = new QuarantineTaskInfoSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      QuarantineTaskInfo quarantineTask = (QuarantineTaskInfo)obj;
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            quarantineTask.setAction(SerializerUtil.readBoolean(buffer));
            return;
         case 1:
            quarantineTask.setReason(ConverterUtilities.readInt(buffer));
      }
   }

   @Override
   protected final Object createObject() {
      return new QuarantineTaskInfo();
   }

   private QuarantineTaskInfoSerializer() {
   }
}
