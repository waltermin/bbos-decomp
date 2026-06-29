package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefStruct;
import net.rim.wica.runtime.util.SerializerUtil;

final class ComponentDefStructSerializer extends AbstractSerializer {
   private static ComponentDefStructSerializer _instance;
   private static final byte DEFS;
   private static final byte VAR_DATA;
   private static final byte OBJECT_DATA;

   static final ComponentDefStructSerializer getInstance() {
      if (_instance == null) {
         _instance = new ComponentDefStructSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void serializeObject(DataBuffer buffer, Object obj) {
      ComponentDefStruct componentDefStruct = (ComponentDefStruct)obj;
      SerializerUtil.writeIntArray(buffer, (byte)0, componentDefStruct._defs);
      SerializerUtil.writeIntArray(buffer, (byte)1, componentDefStruct._varData);
      ObjectSerializer.getInstance().serializeArray(buffer, (byte)2, componentDefStruct._objectData);
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      ComponentDefStruct componentDefStruct = (ComponentDefStruct)obj;
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            componentDefStruct._defs = ConverterUtilities.readIntArray(buffer);
            return;
         case 1:
            componentDefStruct._varData = ConverterUtilities.readIntArray(buffer);
            return;
         case 2:
            componentDefStruct._objectData = ObjectSerializer.getInstance().deserializeArray(buffer);
      }
   }

   @Override
   protected final Object createObject() {
      return new ComponentDefStruct();
   }

   private ComponentDefStructSerializer() {
   }
}
