package net.rim.device.apps.api.calendar.calconstants;

import net.rim.device.api.system.ApplicationRegistry;

public final class CalOptionCache {
   private static CalOptionCache$OptionCacheData _options;
   private static final long ID;

   private CalOptionCache() {
   }

   public static final void setObjectWithFocus(Object object) {
      init();
      _options._objectWithFocus = object;
   }

   public static final Object getObjectWithFocus() {
      init();
      return _options._objectWithFocus;
   }

   public static final void setTimeWithFocus(long time) {
      init();
      _options._timeWithFocus = time;
   }

   public static final long getTimeWithFocus() {
      init();
      return _options._timeWithFocus;
   }

   public static final void setSuggestedUserDuration(long dur) {
      init();
      _options._duration = dur;
   }

   public static final long getSuggestedUserDuration() {
      init();
      return _options._duration;
   }

   public static final void setSuggestedUserText(String text) {
      init();
      _options._text = text;
   }

   public static final String getSuggestedUserText() {
      init();
      return _options._text;
   }

   public static final Object getCurrentOpenViewer() {
      init();
      return _options._viewer;
   }

   public static final void setCurrentOpenViewer(Object viewer) {
      init();
      _options._viewer = viewer;
   }

   private static final void init() {
      if (_options == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         synchronized (reg) {
            _options = (CalOptionCache$OptionCacheData)reg.get(1991608958394134113L);
            if (_options == null) {
               _options = new CalOptionCache$OptionCacheData();
               reg.put(1991608958394134113L, _options);
            }
         }
      }
   }
}
