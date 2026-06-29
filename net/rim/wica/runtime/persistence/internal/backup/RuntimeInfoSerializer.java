package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.management.RuntimeInfo;
import net.rim.wica.runtime.util.SerializerUtil;

final class RuntimeInfoSerializer extends AbstractSerializer {
   private static RuntimeInfoSerializer _instance;
   private static final byte DEVICE_ID = 1;
   private static final byte DEVICE_PIN = 2;
   private static final byte TRUSTED_WICLET_ADMIN_POLICY = 4;
   private static final byte UNTRUSTED_WICLET_ADMIN_POLICY = 5;
   private static final byte UPGRADED = 7;
   private static final byte KEY_REFRESH_AG_ID = 8;
   private static final byte DOING_HANDSHAKE = 9;
   private static final byte DOING_REGISTRATION = 10;
   private static final byte REGISTERED = 11;
   private static final byte DOING_KEY_REFRESH = 12;
   private static final byte RESTORED = 13;
   private static final byte DEFAULT_AG_INFO = 14;
   private static final byte NEW_AG_INFO = 15;

   static final RuntimeInfoSerializer getInstance() {
      if (_instance == null) {
         _instance = new RuntimeInfoSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void serializeObject(DataBuffer buffer, Object obj) {
      RuntimeInfo runtimeInfo = (RuntimeInfo)obj;
      AGInfoSerializer.getInstance().serialize(buffer, (byte)14, runtimeInfo.getDefaultAGInfo());
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      RuntimeInfo runtimeInfo = (RuntimeInfo)obj;
      switch (type) {
         case 0:
         case 3:
         case 6:
            ConverterUtilities.skipField(buffer);
            return;
         case 1:
            ConverterUtilities.readLong(buffer);
            return;
         case 2:
            ConverterUtilities.readInt(buffer);
            return;
         case 4:
            WicletAdminPolicySerializer.getInstance().deserialize(buffer);
            return;
         case 5:
            WicletAdminPolicySerializer.getInstance().deserialize(buffer);
            return;
         case 7:
            SerializerUtil.readBoolean(buffer);
            return;
         case 8:
            ConverterUtilities.readLong(buffer);
            return;
         case 9:
            SerializerUtil.readBoolean(buffer);
            return;
         case 10:
            SerializerUtil.readBoolean(buffer);
            return;
         case 11:
            SerializerUtil.readBoolean(buffer);
            return;
         case 12:
            SerializerUtil.readBoolean(buffer);
            return;
         case 13:
            SerializerUtil.readBoolean(buffer);
            return;
         case 14:
         default:
            runtimeInfo.setDefaultAGInfo((AGInfo)AGInfoSerializer.getInstance().deserialize(buffer));
            return;
         case 15:
            AGInfoSerializer.getInstance().deserialize(buffer);
      }
   }

   @Override
   protected final Object createObject() {
      return new RuntimeInfo();
   }

   private RuntimeInfoSerializer() {
   }
}
