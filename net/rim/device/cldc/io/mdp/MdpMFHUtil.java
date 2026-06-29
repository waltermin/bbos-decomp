package net.rim.device.cldc.io.mdp;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.vm.Array;

final class MdpMFHUtil {
   static final long GUID;
   private static Transport _transport = Transport.getInstance();

   private MdpMFHUtil() {
   }

   static final Transport getTransport() {
      return _transport;
   }

   static final int getDefaultRCWindowSize() {
      switch (RadioInfo.getNetworkType()) {
         case 4:
            return 6;
         default:
            return 3;
      }
   }

   static final int getDefaultMFHOutstandingPacketsWindowSize() {
      return getDefaultRCWindowSize() + (getDefaultRCWindowSize() << 1) / 3;
   }

   static final int getDefaultMFHLostPacketTimer() {
      switch (RadioInfo.getNetworkType()) {
         case 4:
            return 15000;
         default:
            return 10000;
      }
   }

   static final int getDefaultMFHRelayPacketMaxRTT() {
      return getDefaultMFHLostPacketTimer() >> 1;
   }

   static final boolean completeStatusArray(int maxSequence, byte[] statusArray, int start, int length) {
      if (statusArray != null && start + length <= statusArray.length) {
         int end = start + length;

         for (int sequence = 0; sequence <= maxSequence; sequence++) {
            int index = start + (sequence >> 3);
            if (index >= end) {
               EventLogger.logEvent(GUID, 1380152434, 3);
               return false;
            }

            if ((statusArray[index] & (byte)(1 << (sequence & 7))) == 0) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   static final boolean receivedAdditionalPackets(byte[] existingArray, byte[] incomingArray, int start, int length) {
      if (existingArray == null) {
         return true;
      }

      if (incomingArray == null) {
         return false;
      }

      if (existingArray.length == incomingArray.length && start + length <= existingArray.length) {
         int size = start + length;

         for (int i = start; i < size; i++) {
            if (existingArray[i] != incomingArray[i] && (byte)(existingArray[i] & incomingArray[i]) == existingArray[i]) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   static final boolean isPacketReceived(int sequence, byte[] statusArray, int start, int length) {
      if (statusArray != null && start + length <= statusArray.length) {
         int index = start + (sequence >> 3);
         if (index < start + length && (statusArray[index] & (byte)(1 << (sequence & 7))) != 0) {
            return true;
         }
      }

      return false;
   }

   static final boolean packetResent(int sequence, byte[] resendArray, int start, int length) {
      if (resendArray != null && start + length <= resendArray.length) {
         int index = start + (sequence >> 3);
         if (index >= start + length) {
            return false;
         } else if ((resendArray[index] & (byte)(1 << (sequence & 7))) == 0) {
            resendArray[index] |= (byte)(1 << (sequence & 7));
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   static final byte[] updateResendPackets(byte[] resendArray, byte[] statusArray, int offset, int length) {
      if (statusArray != null && offset + length <= statusArray.length && length > 0) {
         if (resendArray == null) {
            resendArray = new byte[length];
            System.arraycopy(statusArray, offset, resendArray, 0, length);
            return resendArray;
         }

         if (length != resendArray.length) {
            EventLogger.logEvent(GUID, 1380019314, 3);
            Array.resize(resendArray, length);
         }

         int missingPackets = 0;
         byte value = 0;

         for (int i = 0; i < length; i++) {
            if ((value = statusArray[i + offset]) == 0) {
               missingPackets++;
            }

            resendArray[i] |= value;
         }

         if (missingPackets == length) {
            resendArray = null;
         }
      }

      return resendArray;
   }

   static final int checkForMissingPackets(int maxSequence, byte[] statusArray, int start, int length, boolean includeTail, byte[] resendArray) {
      if (statusArray != null && start + length <= statusArray.length) {
         int missingPacketsCount = 0;
         int end = start + length;

         for (int sequence = maxSequence; sequence >= 0; sequence--) {
            int index = start + (sequence >> 3);
            if (index < end) {
               if ((statusArray[index] & (byte)(1 << (sequence & 7))) == 0) {
                  if (!includeTail && missingPacketsCount != -1) {
                     missingPacketsCount++;
                  } else if (resendArray == null || index - start >= resendArray.length || (resendArray[index - start] & (byte)(1 << (sequence & 7))) == 0) {
                     return sequence + 1;
                  }
               } else {
                  missingPacketsCount = -1;
               }
            } else {
               EventLogger.logEvent(GUID, 1380152434, 3);
            }
         }
      }

      return 0;
   }

   static {
      GUID = Transport.getInstance().GUID;
   }
}
