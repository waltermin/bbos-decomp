package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.management.WicletAdminPolicy;
import net.rim.wica.runtime.util.SerializerUtil;

final class WicletAdminPolicySerializer extends AbstractSerializer {
   private static WicletAdminPolicySerializer _instance;
   private static final byte ID;
   private static final byte MESSAGE_SENDING_ALLOWED;
   private static final byte INBOUND_QUEUE_SIZE_LIMIT;
   private static final byte OUTBOUND_QUEUE_SIZE_LIMIT;
   private static final byte PERFORMANCE_MODE;
   private static final byte EXTERNAL_ACCESS_ALLOWED;
   private static final byte DEDICATED_SERVER_ALLOWED;
   private static final byte CLEAR_ERRORS_ALLOWED;

   static final WicletAdminPolicySerializer getInstance() {
      if (_instance == null) {
         _instance = new WicletAdminPolicySerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      WicletAdminPolicy wicletAdminPolicy = (WicletAdminPolicy)obj;
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            wicletAdminPolicy.setId(ConverterUtilities.readLong(buffer));
            return;
         case 1:
            wicletAdminPolicy.setMessageSendingAllowed(SerializerUtil.readBoolean(buffer));
            return;
         case 2:
            wicletAdminPolicy.setInboundQueueSizeLimit(ConverterUtilities.readInt(buffer));
            return;
         case 3:
            wicletAdminPolicy.setOutboundQueueSizeLimit(ConverterUtilities.readInt(buffer));
            return;
         case 4:
            wicletAdminPolicy.setPerformanceMode(SerializerUtil.readBoolean(buffer));
            return;
         case 5:
            wicletAdminPolicy.setExternalAccessAllowed(ConverterUtilities.readInt(buffer));
            return;
         case 6:
            wicletAdminPolicy.setDedicatedServerAllowed(SerializerUtil.readBoolean(buffer));
            return;
         case 7:
            wicletAdminPolicy.setClearErrorsAllowed(SerializerUtil.readBoolean(buffer));
      }
   }

   @Override
   protected final Object createObject() {
      return new WicletAdminPolicy();
   }

   private WicletAdminPolicySerializer() {
   }
}
