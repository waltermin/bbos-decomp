package net.rim.device.apps.internal.applicationdelivery;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Memory;

class ApplicationInfo implements Persistable {
   Hashtable _modules;
   byte[] _groupData;
   int _moduleCount;
   long _transactionId;
   long _lastReceivedTimeStamp;
   boolean _haveSentAck;
   boolean _errorOccurred;
   boolean _resetRequired;

   ApplicationInfo(int moduleCount, long transactionId) {
      this._moduleCount = moduleCount;
      this._transactionId = transactionId;
      this._modules = new Hashtable();
   }

   void setApplicationGroupData(byte[] groupData) {
      this._groupData = groupData;
   }

   byte[] getApplicationGroupData() {
      return this._groupData;
   }

   Hashtable getModules() {
      return this._modules;
   }

   long getTransactionId() {
      return this._transactionId;
   }

   boolean hasErrorOccurred() {
      return this._errorOccurred;
   }

   boolean isResetRequired() {
      return this._resetRequired;
   }

   void setResetRequired(boolean required) {
      this._resetRequired = required;
   }

   boolean haveAllModulesBeenReceived() {
      if (this._modules.size() != this._moduleCount) {
         return false;
      }

      Enumeration enumeration = this._modules.elements();

      while (enumeration.hasMoreElements()) {
         ModuleInfo moduleInfo = (ModuleInfo)enumeration.nextElement();
         if (!moduleInfo.haveEntireModule()) {
            return false;
         }
      }

      return true;
   }

   boolean saveAllModules(ApplicationDeliveryTransmissionService service) {
      boolean errorOccured = false;
      if (ApplicationDeliveryTransmissionService._debugMode) {
         int flashFree = Memory.getFlashFree();
         System.out.println("APPD - Available Flash before transaction: " + flashFree);
      }

      int transactionHandle = CodeModuleManager.beginTransaction();
      if (transactionHandle == 0) {
         this.error(service, 1161979732, -1);
         return false;
      }

      int groupHandle = 0;
      if (this._groupData != null) {
         boolean duplicateGroup = false;
         CodeModuleGroup thisGroup = CodeModuleGroup.loadUnpersisted(this._groupData);
         if (thisGroup != null) {
            CodeModuleGroup[] existingGroups = CodeModuleGroupManager.loadAll();
            if (existingGroups != null) {
               String thisName = thisGroup.getName();
               String thisVersion = thisGroup.getVersion();

               for (int i = existingGroups.length - 1; i >= 0; i--) {
                  String existingName = existingGroups[i].getName();
                  if (thisName.equals(existingName)) {
                     String existingVersion = existingGroups[i].getVersion();
                     if (thisVersion.equals(existingVersion)) {
                        duplicateGroup = true;
                        break;
                     }
                  }
               }
            }
         }

         if (!duplicateGroup) {
            groupHandle = CodeModuleGroupManager.createGroup(this._groupData);
            if (groupHandle <= 0) {
               ApplicationDeliveryEventLogger.logEvent(1163085641, 2);
            }
         }
      }

      Enumeration enumeration = this._modules.elements();

      while (enumeration.hasMoreElements()) {
         ModuleInfo moduleInfo = (ModuleInfo)enumeration.nextElement();
         if (!moduleInfo.saveModule(transactionHandle, service, this)) {
            errorOccured = true;
         }
      }

      if (errorOccured) {
         CodeModuleManager.cancelTransaction(transactionHandle);
         this.setResetRequired(false);
         this.deleteCodeModuleGroup(groupHandle);
         return false;
      }

      int endTransactionResult = CodeModuleManager.endTransaction(transactionHandle);
      if (ApplicationDeliveryTransmissionService._debugMode) {
         int flashFree = Memory.getFlashFree();
         System.out.println("APPD - Available Flash after transaction: " + flashFree);
      }

      if (endTransactionResult == 13) {
         this.error(service, 1162178132, -1);
         this.setResetRequired(false);
         this.deleteCodeModuleGroup(groupHandle);
         return false;
      }

      if (endTransactionResult == 15) {
         this.setResetRequired(true);
      }

      service.sendAcknowledgement(this, 1);
      service._applicationDeliveryPersistedData._pendingApplications.remove(this._transactionId);
      ApplicationDeliveryEventLogger.logEvent(1229015406, 0);
      if (this.isResetRequired()) {
         service.scheduleDeviceReset();
      }

      return true;
   }

   private void deleteCodeModuleGroup(int groupHandle) {
      if (groupHandle > 0) {
         CodeModuleGroup group = CodeModuleGroupManager.load(groupHandle);
         if (group != null) {
            group.delete();
         }
      }
   }

   void setLastReceivedTimeStamp() {
      this._lastReceivedTimeStamp = System.currentTimeMillis();
   }

   long getLastReceivedTimeStamp() {
      return this._lastReceivedTimeStamp;
   }

   void error(ApplicationDeliveryTransmissionService service, int logCode, int ackCode) {
      this.error(service, logCode, ackCode, false);
   }

   void error(ApplicationDeliveryTransmissionService service, int logCode, int ackCode, boolean disableRemove) {
      this._errorOccurred = true;
      ApplicationDeliveryEventLogger.logEvent(logCode, 2);
      if (!this._haveSentAck) {
         this._haveSentAck = service.sendAcknowledgement(this, ackCode);
      }

      if (this.haveAllModulesBeenReceived() && !disableRemove) {
         service._applicationDeliveryPersistedData._pendingApplications.remove(this._transactionId);
         service.isCleanerNecessary();
      }
   }
}
