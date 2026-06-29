package net.rim.plazmic.internal.mediaengine.event;

public class Event {
   public long _time;
   public long _uid;
   public int _event;
   public int _eventParam;
   public long _eventParamLong;
   public Object _listener;
   public Object _sender;
   public Object _data;

   public void copy(Event ev) {
      this._time = ev._time;
      this._uid = ev._uid;
      this._listener = ev._listener;
      this._eventParam = ev._eventParam;
      this._eventParamLong = ev._eventParamLong;
      this._data = ev._data;
      this._event = ev._event;
      this._sender = ev._sender;
   }

   public void clear() {
      this._event = 0;
      this._eventParam = 0;
      this._eventParamLong = 0;
      this._time = 0;
      this._uid = 0;
      this._listener = null;
      this._data = null;
      this._sender = null;
   }

   @Override
   public String toString() {
      return "Time: " + this._time + " Event: " + this._event + " Param: " + this._eventParam + " Id: " + this._uid;
   }
}
