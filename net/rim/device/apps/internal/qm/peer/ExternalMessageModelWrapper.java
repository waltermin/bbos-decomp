package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;

final class ExternalMessageModelWrapper implements MessengerMessage {
   private String _sender;
   private String _text;
   private Field _field;
   private long _time;

   ExternalMessageModelWrapper(String text, Field field, String sender) {
      this._text = text;
      this._sender = sender;
      this._field = field;
      this._time = System.currentTimeMillis();
   }

   @Override
   public final Field getField(Object context) {
      return this._field;
   }

   @Override
   public final String getSender() {
      return this._sender;
   }

   @Override
   public final String getText() {
      return this._text;
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
      return false;
   }
}
