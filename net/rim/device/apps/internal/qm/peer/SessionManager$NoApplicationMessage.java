package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;

final class SessionManager$NoApplicationMessage implements MessengerMessage {
   private String _text;
   private long _time;

   public SessionManager$NoApplicationMessage(String text) {
      this._text = text;
      this._time = System.currentTimeMillis();
   }

   @Override
   public final String getText() {
      return this._text;
   }

   @Override
   public final Field getField(Object context) {
      return (Field)(new Object(this._text));
   }

   @Override
   public final String getSender() {
      return "SessionManager";
   }

   @Override
   public final long getTime() {
      return this._time;
   }

   @Override
   public final boolean isIncoming() {
      return false;
   }

   @Override
   public final boolean isSystem() {
      return true;
   }
}
