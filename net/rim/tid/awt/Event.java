package net.rim.tid.awt;

import net.rim.tid.itie.IComponent;

public class Event {
   protected IComponent _source;
   protected int _ID;
   protected int _mask;
   protected boolean _consumed;
   public static int COMPONENT_EVENT_MASK = 1;
   public static int NAVIGATION_EVENT_MASK = 2;
   public static int FOCUS_EVENT_MASK = 4;
   public static int KEY_EVENT_MASK = 8;
   public static int ACTION_EVENT_MASK = 128;
   public static int INPUT_EVENT_MASK = 2048;
   public static int INPUT_METHOD_EVENT_MASK = 2048;

   public Event(IComponent aSource, int aId, int aMask) {
      this._source = aSource;
      this._ID = aId;
      this._mask = aMask;
   }

   public void init(IComponent aSource, int aId) {
      this._source = aSource;
      this._ID = aId;
      this._consumed = false;
   }

   public void setSource(IComponent aSource) {
      this._source = aSource;
   }

   public int getEventMask() {
      return this._mask;
   }

   public int getID() {
      return this._ID;
   }

   public IComponent getSource() {
      return this._source;
   }

   public boolean isConsumed() {
      return this._consumed;
   }

   public void consume() {
      this._consumed = true;
   }

   public boolean isComponentDispatchEnabled() {
      return false;
   }
}
