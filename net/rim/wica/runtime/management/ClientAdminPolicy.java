package net.rim.wica.runtime.management;

import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.messaging.MessageException;
import net.rim.wica.runtime.messaging.ReadableDataStream;

public final class ClientAdminPolicy implements Persistable {
   private long _id;
   private boolean _pullReUpgradeAllowed;
   private boolean _discoveryAllowed;
   private int _pullProvisioningAllowed;
   private boolean _silentPushProvisioningAllowed;
   private boolean _deleteAllowed;
   private int _inboundQueueSizeLimit;
   private int _outboundQueueSizeLimit;
   private boolean _secureSystemMessages;
   private int _externalAccessAllowed;
   private boolean _multiDomainAllowed;

   public final long getId() {
      return this._id;
   }

   public final void setId(long id) {
      this._id = id;
   }

   public final boolean isPullReUpgradeAllowed() {
      return this._pullReUpgradeAllowed;
   }

   public final void setPullReUpgradeAllowed(boolean pullReUpgradeAllowed) {
      this._pullReUpgradeAllowed = pullReUpgradeAllowed;
   }

   public final boolean isDiscoveryAllowed() {
      return this._discoveryAllowed;
   }

   public final void setDiscoveryAllowed(boolean discoveryAllowed) {
      this._discoveryAllowed = discoveryAllowed;
   }

   public final int getPullProvisioningAllowed() {
      return this._pullProvisioningAllowed;
   }

   public final void setPullProvisioningAllowed(int pullProvisioningAllowed) {
      this._pullProvisioningAllowed = pullProvisioningAllowed;
   }

   public final boolean isSilentPushProvisioningAllowed() {
      return this._silentPushProvisioningAllowed;
   }

   public final void setSilentPushProvisioningAllowed(boolean silentPushProvisioningAllowed) {
      this._silentPushProvisioningAllowed = silentPushProvisioningAllowed;
   }

   public final boolean isDeleteAllowed() {
      return this._deleteAllowed;
   }

   public final void setDeleteAllowed(boolean deleteAllowed) {
      this._deleteAllowed = deleteAllowed;
   }

   public final int getInboundQueueSizeLimit() {
      return this._inboundQueueSizeLimit;
   }

   public final void setInboundQueueSizeLimit(int inboundQueueSizeLimit) {
      this._inboundQueueSizeLimit = inboundQueueSizeLimit;
   }

   public final int getOutboundQueueSizeLimit() {
      return this._outboundQueueSizeLimit;
   }

   public final void setOutboundQueueSizeLimit(int outboundQueueSizeLimit) {
      this._outboundQueueSizeLimit = outboundQueueSizeLimit;
   }

   public final boolean getSecureSystemMessages() {
      return this._secureSystemMessages;
   }

   public final void setSecureSystemMessages(boolean secureSystemMessages) {
      this._secureSystemMessages = secureSystemMessages;
   }

   public final int getExternalAccessAllowed() {
      return this._externalAccessAllowed;
   }

   public final void setExternalAccessAllowed(int externalAccessAllowed) {
      this._externalAccessAllowed = externalAccessAllowed;
   }

   public final boolean isMultiDomainAllowed() {
      return this._multiDomainAllowed;
   }

   public final void setMultiDomainAllowed(boolean multiDomainAllowed) {
      this._multiDomainAllowed = multiDomainAllowed;
   }

   public static final ClientAdminPolicy createDefaultPolicy() {
      ClientAdminPolicy policy = new ClientAdminPolicy();
      policy._id = -1;
      policy._pullReUpgradeAllowed = false;
      policy._discoveryAllowed = true;
      policy._pullProvisioningAllowed = 2;
      policy._silentPushProvisioningAllowed = true;
      policy._deleteAllowed = true;
      policy._inboundQueueSizeLimit = 8;
      policy._outboundQueueSizeLimit = 16;
      policy._secureSystemMessages = true;
      policy._externalAccessAllowed = 0;
      policy._multiDomainAllowed = false;
      return policy;
   }

   public static final ClientAdminPolicy readFromStream(ReadableDataStream readStream) {
      try {
         if (!readStream.startComponentRead()) {
            return null;
         }

         ClientAdminPolicy policy = new ClientAdminPolicy();
         policy._id = readStream.readLong();
         policy._pullReUpgradeAllowed = readStream.readBoolean();
         policy._discoveryAllowed = readStream.readBoolean();
         policy._pullProvisioningAllowed = readStream.readInt();
         policy._silentPushProvisioningAllowed = readStream.readBoolean();
         policy._deleteAllowed = readStream.readBoolean();
         policy._inboundQueueSizeLimit = readStream.readInt();
         policy._outboundQueueSizeLimit = readStream.readInt();
         policy._secureSystemMessages = readStream.readBoolean();
         policy._externalAccessAllowed = readStream.readInt();
         policy._multiDomainAllowed = readStream.readBoolean();
         return policy;
      } catch (MessageException e) {
         return null;
      }
   }
}
