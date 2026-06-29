package net.rim.wica.runtime.messaging.internal;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.persistence.Recryptable;

class PersistableMessage implements Persistable, Recryptable {
   private long _agId;
   private Object _serviceId;
   private Object _messageName;
   private int _destinationType;
   private int _securityMode;
   private Object _transportMsg;

   PersistableMessage(MessageImpl msg) {
      this._agId = msg.getAGID();
      this._serviceId = PersistentContent.encode(msg.getServiceID());
      this._messageName = PersistentContent.encode(msg.getMessageName());
      this._destinationType = msg.getDestinationType();
      this._securityMode = msg.getSecurityMode();
      this._transportMsg = PersistentContent.encode(msg.serialize());
   }

   long getAgId() {
      return this._agId;
   }

   int getDestinationType() {
      return this._destinationType;
   }

   synchronized String getMessageName() {
      return PersistentContent.decodeString(this._messageName);
   }

   int getSecurityMode() {
      return this._securityMode;
   }

   synchronized String getServiceId() {
      return PersistentContent.decodeString(this._serviceId);
   }

   synchronized byte[] getTransportMsg() {
      return PersistentContent.decodeByteArray(this._transportMsg);
   }

   @Override
   public synchronized void recrypt() {
      if (!PersistentContent.checkEncoding(this._serviceId)) {
         this._serviceId = PersistentContent.reEncode(this._serviceId);
      }

      if (!PersistentContent.checkEncoding(this._messageName)) {
         this._messageName = PersistentContent.reEncode(this._messageName);
      }

      if (!PersistentContent.checkEncoding(this._transportMsg)) {
         this._transportMsg = PersistentContent.reEncode(this._transportMsg);
      }
   }
}
