package net.rim.device.api.browser.field;

public class RedirectEvent extends Event {
   private String _location;
   private Event _originalEvent;
   private int _type;
   private int _timerValue;
   public static final int TYPE_300_REDIRECT = 0;
   public static final int TYPE_SINGLE_FRAME_REDIRECT = 1;
   public static final int TYPE_JAVASCRIPT = 2;
   public static final int TYPE_META = 3;
   public static final int TYPE_SDP = 4;

   public RedirectEvent(Object src, String location, Event originalEvent, int type) {
      this(src, location, originalEvent, type, Integer.MAX_VALUE);
   }

   public RedirectEvent(Object src, String location, Event originalEvent, int type, int timerValue) {
      super(10006, src);
      this._type = type;
      this._location = this.resolveUrl(location);
      this._originalEvent = originalEvent;
      this._timerValue = timerValue;
   }

   public String getLocation() {
      return this._location;
   }

   public int getType() {
      return this._type;
   }

   public Event getOriginalEvent() {
      return this._originalEvent;
   }

   public int getTimerValue() {
      return this._timerValue;
   }
}
