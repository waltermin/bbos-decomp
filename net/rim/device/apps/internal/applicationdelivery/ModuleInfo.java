package net.rim.device.apps.internal.applicationdelivery;

import java.util.Enumeration;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Memory;

class ModuleInfo implements Persistable {
   private int _moduleSize;
   private int _bytesReceived;
   private int[] _sequencesReceived;
   private IntHashtable _cachedSequences;
   private byte[] _moduleHash;
   private static final int LMM_ADDITIONAL_MEMORY_CONSTANT_FACTOR = 131072;

   ModuleInfo(int moduleSize, byte[] moduleHash) {
      this._moduleSize = moduleSize;
      this._sequencesReceived = new int[0];
      this._moduleHash = moduleHash;
   }

   private boolean incrementBytesReceived(int sequenceNumber, int numReceived) {
      for (int i = 0; i < this._sequencesReceived.length; i++) {
         if (this._sequencesReceived[i] == sequenceNumber) {
            return false;
         }
      }

      Arrays.add(this._sequencesReceived, sequenceNumber);
      this._bytesReceived += numReceived;
      return true;
   }

   void writeSequence(int sequenceNumber, int moduleOffset, byte[] sequenceData, int sequenceOffset, int sequenceLength) {
      if (!this.incrementBytesReceived(sequenceNumber, sequenceLength)) {
         ApplicationDeliveryEventLogger.logEvent(1229221968, 0);
      } else if (CodeModuleManager.getModuleHandle(this._moduleHash) != 0) {
         ApplicationDeliveryEventLogger.logEvent(1229800824, 0);
      } else {
         if (this._cachedSequences == null) {
            this._cachedSequences = new IntHashtable();
         }

         this._cachedSequences
            .put(sequenceNumber, new ModuleInfo$CachedModuleSequence(sequenceNumber, moduleOffset, sequenceData, sequenceOffset, sequenceLength));
      }
   }

   private boolean ensureAvailableFlash(int size) {
      boolean retval = true;
      int flashFree = Memory.getFlashFree();
      if (size > flashFree) {
         ModuleInfo$ApplicationDeliveryLowMemoryListener lmmListener = new ModuleInfo$ApplicationDeliveryLowMemoryListener(null);
         LowMemoryManager.addLowMemoryFailedListener(lmmListener);

         do {
            Memory.recoverFlash(size);
            LowMemoryManager.poll();
         } while (!lmmListener._lmmFailed && size > Memory.getFlashFree());

         LowMemoryManager.removeLowMemoryFailedListener(lmmListener);
         retval = !lmmListener._lmmFailed;
      }

      return retval;
   }

   boolean saveModule(int transactionHandle, ApplicationDeliveryTransmissionService service, ApplicationInfo applicationInfo) {
      if (CodeModuleManager.getModuleHandle(this._moduleHash) != 0) {
         ApplicationDeliveryEventLogger.logEvent(1229800824, 0);
         return true;
      }

      if (this.haveEntireModule() && this._cachedSequences != null) {
         int flashNeeded = this._moduleSize + 131072;
         if (ApplicationDeliveryTransmissionService._debugMode) {
            System.out.println("APPD - Saving module with size: " + this._moduleSize);
            System.out.println("APPD - Flash needed: " + flashNeeded);
            int flashFree = Memory.getFlashFree();
            System.out.println("APPD - Available Flash: " + flashFree);
         }

         if (!this.ensureAvailableFlash(flashNeeded)) {
            if (ApplicationDeliveryTransmissionService._debugMode) {
               System.out.println("APPD - Flash requested is NOT free");
            }

            applicationInfo.error(service, 1163087213, 3);
            return false;
         } else {
            if (ApplicationDeliveryTransmissionService._debugMode) {
               System.out.println("APPD - Flash requested is free");
            }

            ModuleInfo$CachedModuleSequence ms = (ModuleInfo$CachedModuleSequence)this._cachedSequences.get(0);
            if (ms == null) {
               applicationInfo.error(service, 1163359309, 12);
               return false;
            }

            byte[] previousSequenceData = ms.getSequenceData();
            int moduleHandle = CodeModuleManager.createNewModule(this._moduleSize, previousSequenceData, previousSequenceData.length);
            if (ApplicationDeliveryTransmissionService._debugMode) {
               int flashFree = Memory.getFlashFree();
               System.out.println("APPD - Flash free after creating module: " + flashFree);
            }

            if (moduleHandle == 0) {
               applicationInfo.error(service, 1162048077, 13);
               return false;
            }

            this._cachedSequences.remove(0);
            Enumeration enumeration = this._cachedSequences.elements();

            while (enumeration.hasMoreElements()) {
               ms = (ModuleInfo$CachedModuleSequence)enumeration.nextElement();
               previousSequenceData = ms.getSequenceData();
               if (!CodeModuleManager.writeNewModule(moduleHandle, ms.getModuleOffset(), previousSequenceData, 0, previousSequenceData.length)) {
                  applicationInfo.error(service, 1163359309, 13);
                  CodeModuleManager.deleteNewModule(moduleHandle);
                  return false;
               }

               this._cachedSequences.remove(ms.getSequenceNumber());
            }

            int saveStatus = CodeModuleManager.saveNewModule(moduleHandle, true, transactionHandle);
            if (saveStatus == 0) {
               ApplicationDeliveryEventLogger.logEvent(1229801838, 0);
            } else {
               if (saveStatus != 1) {
                  if (saveStatus == 2) {
                     applicationInfo.error(service, 1162695267, 9);
                     return false;
                  }

                  ApplicationDeliveryEventLogger.logNumberEvent(saveStatus, 2);
                  applicationInfo.error(service, 1163097677, -1);
                  CodeModuleManager.deleteNewModule(moduleHandle);
                  return false;
               }

               applicationInfo.setResetRequired(true);
               ApplicationDeliveryEventLogger.logEvent(1229801838, 0);
            }

            this._cachedSequences = null;
            return true;
         }
      } else {
         applicationInfo.error(service, 1163097677, 12);
         return false;
      }
   }

   int getBytesReceived() {
      return this._bytesReceived;
   }

   boolean haveEntireModule() {
      return this._bytesReceived == this._moduleSize;
   }
}
