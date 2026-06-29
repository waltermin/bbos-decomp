package net.rim.blackberry.api.mail.event;

import net.rim.blackberry.api.mail.Message;

public class MessageEvent extends MailEvent {
   private Message _msg;
   private int _type;
   public static final int UPDATED;
   public static final int OPENED;
   public static final int CLOSED;
   public static final int NEW;
   public static final int FORWARD;
   public static final int REPLY;

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
      return ((StringBuffer)(new Object("MessageEvent: "))).append(this._type).toString();
   }
}
