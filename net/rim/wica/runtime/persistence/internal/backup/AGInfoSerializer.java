package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.util.SerializerUtil;

final class AGInfoSerializer extends AbstractSerializer {
   private static AGInfoSerializer _instance;
   private static final byte AG_REG_URL = 0;
   private static final byte AG_COMPACT_MSG_URL = 1;
   private static final byte AG_ID = 2;
   private static final byte SERVICE_RECORD_ID = 4;
   private static final byte CID = 5;
   private static final byte IPPP_UID = 6;
   private static final byte UID = 7;
   private static final byte AG_FRIENDLY_NAME = 8;
   private static final byte BES_GROUPS = 9;
   private static final byte GENERATION_COUNT = 10;

   static final AGInfoSerializer getInstance() {
      if (_instance == null) {
         _instance = new AGInfoSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void serializeObject(DataBuffer dataBuffer, Object obj) {
      AGInfo agInfo = (AGInfo)obj;
      SerializerUtil.writeString(dataBuffer, (byte)0, agInfo.getAgRegURL());
      SerializerUtil.writeString(dataBuffer, (byte)1, agInfo.getAgCompactMsgURL());
      ConverterUtilities.writeLong(dataBuffer, 2, agInfo.getAgID());
      ConverterUtilities.writeInt(dataBuffer, 4, agInfo.getServiceRecordID());
      SerializerUtil.writeString(dataBuffer, (byte)6, agInfo.getIPPP_UID());
      SerializerUtil.writeString(dataBuffer, (byte)7, agInfo.getMDSUID());
      SerializerUtil.writeString(dataBuffer, (byte)8, agInfo.getAGFriendlyName());
      SerializerUtil.writeString(dataBuffer, (byte)9, agInfo.getBESGroups());
      ConverterUtilities.writeInt(dataBuffer, 10, agInfo.getGenerationCount());
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      AGInfo agInfo = (AGInfo)obj;
      switch (type) {
         case -1:
         case 3:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            agInfo.setAGRegURL(ConverterUtilities.readString(buffer));
            return;
         case 1:
            agInfo.setAGCompactMsgURL(ConverterUtilities.readString(buffer));
            return;
         case 2:
            agInfo.setAgID(ConverterUtilities.readLong(buffer));
            return;
         case 4:
            agInfo.setServiceRecordID(ConverterUtilities.readInt(buffer));
            return;
         case 5:
            ConverterUtilities.skipField(buffer);
            return;
         case 6:
            agInfo.setIPPP_UID(ConverterUtilities.readString(buffer));
            return;
         case 7:
            agInfo.setMDSUID(ConverterUtilities.readString(buffer));
            return;
         case 8:
            agInfo.setAGFriendlyName(ConverterUtilities.readString(buffer));
            return;
         case 9:
            agInfo.setBESGroups(ConverterUtilities.readString(buffer));
            return;
         case 10:
            agInfo.setGenerationCount(ConverterUtilities.readInt(buffer));
      }
   }

   @Override
   protected final Object createObject() {
      return new AGInfo();
   }

   private AGInfoSerializer() {
   }
}
