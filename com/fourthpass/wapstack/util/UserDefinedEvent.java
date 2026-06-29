package com.fourthpass.wapstack.util;

public final class UserDefinedEvent {
   private short _eventType;
   private Object _eventData;
   public static final short EVENT_ERROR = 1;
   public static final short EVENT_WARNING = 2;
   public static final short EVENT_PUSH = 3;
   public static final short EVENT_RENEGOTIATE = 4;

   public UserDefinedEvent(short type, Object data) {
      this._eventType = type;
      this._eventData = data;
   }

   public final short getEventType() {
      return this._eventType;
   }

   public final Object getEventData() {
      return this._eventData;
   }
}
