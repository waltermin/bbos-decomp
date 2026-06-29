package net.rim.device.apps.internal.ribbon.skin.svg.eventprovider;

import net.rim.device.api.util.ToIntHashtable;

class PhoneEventProvider extends CountEventProvider {
   boolean _registeredForNewMissedCall;
   static final String NEW_MISSED_CALL = "new_missed_call";
   static final String NO_NEW_MISSED_CALL = "no_new_missed_call";
   static final String NEW_MISSED_CALL_VISIBLE = "new_missed_call_visible";
   static final String NO_NEW_MISSED_CALL_VISIBLE = "no_new_missed_call_visible";
   private static final int EVENT_TYPE_NEW_MISSED_CALL = 0;
   private static PhoneEventProvider$Helper _helper;
   private static ToIntHashtable _events;

   static void initialize() {
      _helper = new PhoneEventProvider$Helper();
      _events = new ToIntHashtable(10);
      _events.put("new_missed_call", 0);
      _events.put("no_new_missed_call", 0);
      _events.put("new_missed_call_visible", 0);
   }

   PhoneEventProvider(SkinEventProvider skinProvider) {
      super(skinProvider);
   }

   public boolean isEventSupported(String eventId) {
      return _events.get(eventId) != -1;
   }

   public void provideEvent(String eventId) {
      int eventType = _events.get(eventId);
      switch (eventType) {
         case 0:
            if (!this._registeredForNewMissedCall) {
               _helper.addComponentForUpdate(this);
               super._skinProvider.registerEventPair("new_missed_call", "no_new_missed_call");
               super._skinProvider.registerEventPair("new_missed_call_visible", "no_new_missed_call_visible");
            }
      }
   }

   @Override
   public void dispatchNew() {
      super._skinProvider.dispatchEvent("new_missed_call");
   }

   @Override
   public void dispatchNoNew() {
      super._skinProvider.dispatchEvent("no_new_missed_call");
   }

   @Override
   public void dispatchNewVisible() {
      super._skinProvider.dispatchEvent("new_missed_call_visible");
   }

   @Override
   public void dispatchNoNewVisible() {
      super._skinProvider.dispatchEvent("no_new_missed_call_visible");
   }
}
