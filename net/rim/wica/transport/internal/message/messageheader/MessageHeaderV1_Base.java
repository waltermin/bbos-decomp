package net.rim.wica.transport.internal.message.messageheader;

public class MessageHeaderV1_Base {
   protected int _messageLength;
   protected int _messageCode;
   protected boolean _notification;
   protected boolean _bgProcessingEnabled;
   protected boolean _keepLast;

   public MessageHeaderV1_Base() {
   }

   public MessageHeaderV1_Base(MessageHeaderV1_Base clone) {
      this._messageLength = clone.getMessageLength();
      this._messageCode = clone.getMessageCode();
      this._notification = clone.isNotification();
      this._bgProcessingEnabled = clone.backgroundProcessingEnabled();
      this._keepLast = clone.keepLast();
   }

   public int getMessageCode() {
      return this._messageCode;
   }

   public void setMessageCode(int messageCode) {
      this._messageCode = messageCode;
   }

   public boolean isNotification() {
      return this._notification;
   }

   public boolean backgroundProcessingEnabled() {
      return this._bgProcessingEnabled;
   }

   public boolean keepLast() {
      return this._keepLast;
   }

   public void setNotification(boolean backgroundProcessingEnabled, boolean keepLast) {
      this._notification = true;
      this._bgProcessingEnabled = backgroundProcessingEnabled;
      this._keepLast = keepLast;
   }

   public int getMessageLength() {
      return this._messageLength;
   }
}
