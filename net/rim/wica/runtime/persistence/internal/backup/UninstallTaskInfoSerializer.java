package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.lifecycle.UninstallTaskInfo;
import net.rim.wica.runtime.util.SerializerUtil;

final class UninstallTaskInfoSerializer extends AbstractSerializer {
   private static UninstallTaskInfoSerializer _instance;
   private static final byte GRACEFUL;
   private static final byte EXPIRY_DATE;
   private static final byte SILENT;

   static final UninstallTaskInfoSerializer getInstance() {
      if (_instance == null) {
         _instance = new UninstallTaskInfoSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      UninstallTaskInfo uninstallTask = (UninstallTaskInfo)obj;
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            uninstallTask.setGraceful(SerializerUtil.readBoolean(buffer));
            return;
         case 1:
            uninstallTask.setExpiryDate(ConverterUtilities.readLong(buffer));
            return;
         case 2:
            uninstallTask.setSilent(SerializerUtil.readBoolean(buffer));
      }
   }

   @Override
   protected final Object createObject() {
      return new UninstallTaskInfoSerializer();
   }

   private UninstallTaskInfoSerializer() {
   }
}
