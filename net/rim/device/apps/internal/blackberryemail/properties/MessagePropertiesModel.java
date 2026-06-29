package net.rim.device.apps.internal.blackberryemail.properties;

import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.internal.blackberryemail.email.RemoveWhenSendingModel;

public class MessagePropertiesModel implements RemoveWhenSendingModel, CloneProvider {
   private int _serviceRecordUIDHash;
   private int _serviceRecordUserID;
   private int _messageClassificationHash;
   private long _messageEncodingUID;
   private int _messageEncodingAction;

   public int getServiceRecordUIDHash() {
      return this._serviceRecordUIDHash;
   }

   public int getServiceRecordUserID() {
      return this._serviceRecordUserID;
   }

   public int getMessageClassificationHash() {
      return this._messageClassificationHash;
   }

   public long getMessageEncodingUID() {
      return this._messageEncodingUID;
   }

   public int getMessageEncodingAction() {
      return this._messageEncodingAction;
   }

   public void setServiceRecordData(int serviceRecordUIDHash, int serviceRecordUserID) {
      this._serviceRecordUIDHash = serviceRecordUIDHash;
      this._serviceRecordUserID = serviceRecordUserID;
   }

   @Override
   public Object clone(Object context) {
      MessagePropertiesModel messagePropertiesModel = new MessagePropertiesModel();
      messagePropertiesModel.setMessageClassificationData(this._messageClassificationHash);
      messagePropertiesModel.setMessageEncodingData(this._messageEncodingUID, this._messageEncodingAction);
      messagePropertiesModel.setServiceRecordData(this._serviceRecordUIDHash, this._serviceRecordUserID);
      return messagePropertiesModel;
   }

   public void setMessageEncodingData(long messageEncodingUID, int messageEncodingAction) {
      this._messageEncodingUID = messageEncodingUID;
      this._messageEncodingAction = messageEncodingAction;
   }

   public void setMessageClassificationData(int messageClassificationHash) {
      this._messageClassificationHash = messageClassificationHash;
   }

   @Override
   public boolean removeAfterSending() {
      return true;
   }

   @Override
   public boolean removeBeforeSending() {
      return false;
   }

   MessagePropertiesModel() {
   }
}
