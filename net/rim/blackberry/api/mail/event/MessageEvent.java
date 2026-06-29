package net.rim.blackberry.api.mail.event;

import net.rim.blackberry.api.mail.Message;

public class MessageEvent extends MailEvent {
   private Message _msg;
   private int _type;
   public static final int UPDATED = 1;
   public static final int OPENED = 2;
   public static final int CLOSED = 3;
   public static final int NEW = 4;
   public static final int FORWARD = 5;
   public static final int REPLY = 6;

   public MessageEvent(int type, Message msg) {
      super(msg);
      this._type = type;
      this._msg = msg;
   }

   @Override
   public void dispatch(Object listener) {
      if (listener instanceof MessageListener) {
         MessageListener ml = (MessageListener)listener;
         switch (this._type) {
            case 1:
               ml.changed(this);
         }
      }

      if (listener instanceof ViewListener) {
         ViewListener vl = (ViewListener)listener;
         switch (this._type) {
            case 1:
               break;
            case 2:
            default:
               vl.open(this);
               break;
            case 3:
               vl.close(this);
         }
      }

      if (listener instanceof ViewListenerExtended) {
         ViewListenerExtended vl = (ViewListenerExtended)listener;
         switch (this._type) {
            case 3:
               break;
            case 4:
            default:
               vl.newMessage(this);
               return;
            case 5:
               vl.forward(this);
               break;
            case 6:
               vl.reply(this);
               return;
         }
      }
   }

   public Message getMessage() {
      return this._msg;
   }

   public int getMessageChangeType() {
      return this._type;
   }

   @Override
   public String toString() {
      return "MessageEvent: " + this._type;
   }
}
