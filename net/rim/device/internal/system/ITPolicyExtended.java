package net.rim.device.internal.system;

import java.io.EOFException;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEUtilities;

public final class ITPolicyExtended {
   private ITPolicyExtended() {
   }

   public static final String getString(int group, int id, int dataId, boolean readLegacy) {
      return readLegacy ? ITPolicy.getString(group, id) : readString(249, group, dataId, id);
   }

   public static final byte[] getByteArray(int group, int id, int dataId, boolean readLegacy) {
      return readLegacy ? ITPolicy.getByteArray(group, id) : readByteArray(249, group, dataId, id);
   }

   public static final byte[] getByteArray(int group, int dataId) {
      return readByteArray(249, group, dataId);
   }

   public static final int getGroupNumber(int group) {
      return readCount(249, group);
   }

   public static final boolean getBoolean(int group, int id, boolean defaultValue, int dataId, boolean readLegacy) {
      return readLegacy ? ITPolicy.getBoolean(group, id, defaultValue) : readByte(249, group, dataId, id, (byte)(defaultValue ? 1 : 0)) != 0;
   }

   public static final int getInteger(int group, int id, int defaultValue, int dataId, boolean readLegacy) {
      return readLegacy ? ITPolicy.getInteger(group, id, defaultValue) : readInt(249, group, dataId, id, defaultValue);
   }

   public static final byte getByte(int group, int id, byte defaultValue, int dataId, boolean readLegacy) {
      return readLegacy ? ITPolicy.getByte(group, id, defaultValue) : readByte(249, group, dataId, id, defaultValue);
   }

   private static final byte readByte(int group, int subGroup, int dataId, int type, byte defaultValue) {
      byte[] subGroupData = getPolicyGroupData(group, subGroup, dataId);
      Byte result = ITPolicyInternal.readByte(subGroupData, type);
      return result != null ? result : defaultValue;
   }

   private static final int readInt(int group, int subGroup, int dataId, int type, int defaultValue) {
      byte[] subGroupData = getPolicyGroupData(group, subGroup, dataId);
      Integer result = ITPolicyInternal.readInteger(subGroupData, type);
      return result != null ? result : defaultValue;
   }

   private static final String readString(int group, int subGroup, int dataId, int type) {
      byte[] subGroupData = getPolicyGroupData(group, subGroup, dataId);
      return ITPolicyInternal.readString(subGroupData, type);
   }

   private static final byte[] readByteArray(int group, int subGroup, int dataId, int type) {
      byte[] subGroupData = getPolicyGroupData(group, subGroup, dataId);
      return ITPolicyInternal.readByteArray(subGroupData, type);
   }

   private static final byte[] readByteArray(int group, int subGroup, int dataId) {
      return getPolicyGroupData(group, subGroup, dataId);
   }

   private static final int readCount(int group, int subGroup) {
      int count = 0;
      byte[] aggregatedGroupData = getAggregatedPolicyGroupData(group);
      if (aggregatedGroupData != null) {
         DataBuffer aggregatedBuffer = new DataBuffer(aggregatedGroupData, 0, aggregatedGroupData.length, true);

         try {
            while (TLEUtilities.findType(aggregatedBuffer, subGroup)) {
               TLEUtilities.skipField(aggregatedBuffer);
               count++;
            }
         } catch (EOFException var6) {
         }
      }

      return count;
   }

   private static final byte[] getPolicyGroupData(int aggregateGroup, int subGroup, int dataId) {
      byte[] targetSubGroup = null;
      byte[] aggregatedGroupData = getAggregatedPolicyGroupData(aggregateGroup);
      if (aggregatedGroupData != null) {
         DataBuffer aggregatedBuffer = new DataBuffer(aggregatedGroupData, 0, aggregatedGroupData.length, true);
         boolean error = false;

         for (int i = 0; i < dataId - 1; i++) {
            try {
               if (!TLEUtilities.findType(aggregatedBuffer, subGroup)) {
                  error = true;
                  break;
               }

               TLEUtilities.skipField(aggregatedBuffer);
            } catch (EOFException e) {
               error = true;
               break;
            }
         }

         if (!error) {
            try {
               if (TLEUtilities.findType(aggregatedBuffer, subGroup)) {
                  return TLEUtilities.readDataField(aggregatedBuffer, subGroup);
               }
            } catch (EOFException var9) {
            }
         }
      }

      return targetSubGroup;
   }

   private static final byte[] getAggregatedPolicyGroupData(int aggregateGroup) {
      byte[] aggregatedGroupData = null;
      byte[] wipeablePolicy = readWipeablePolicyData();
      if (wipeablePolicy != null) {
         DataBuffer policyBuffer = new DataBuffer(wipeablePolicy, 0, wipeablePolicy.length, true);

         try {
            if (TLEUtilities.findType(policyBuffer, aggregateGroup)) {
               aggregatedGroupData = TLEUtilities.readDataField(policyBuffer, aggregateGroup);
            }
         } catch (EOFException var7) {
         }
      }

      if (aggregatedGroupData == null) {
         byte[] persistablePolicy = readPersistablePolicyData();
         if (persistablePolicy != null) {
            DataBuffer policyBuffer = new DataBuffer(persistablePolicy, 0, persistablePolicy.length, true);
            policyBuffer.skipBytes(1);

            try {
               if (TLEUtilities.findType(policyBuffer, aggregateGroup)) {
                  return TLEUtilities.readDataField(policyBuffer, aggregateGroup);
               }
            } catch (EOFException var6) {
            }
         }
      }

      return aggregatedGroupData;
   }

   private static final byte[] readWipeablePolicyData() {
      return ITPolicyInternal.readWipeablePolicyData();
   }

   private static final byte[] readPersistablePolicyData() {
      return NvStore.readData(4);
   }
}
