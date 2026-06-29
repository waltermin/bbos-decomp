package net.rim.device.apps.internal.ribbon.skin.svg.eventprovider;

import net.rim.device.api.util.ToIntHashtable;

class MessagesEventProvider extends CountEventProvider {
   boolean _registeredForNewEmail;
   static final String NEW_EMAIL = "new_email";
   static final String NO_NEW_EMAIL = "no_new_email";
   static final String NEW_EMAIL_VISIBLE = "new_email_visible";
   static final String NO_NEW_EMAIL_VISIBLE = "no_new_email_visible";
   private static final int EVENT_TYPE_NEW_EMAIL = 0;
   private static UnreadCountListenerHelper _ucHelper;
   private static ToIntHashtable _events;

   static void initialize() {
      _ucHelper = new UnreadCountListenerHelper();
      _events = new ToIntHashtable(10);
      _events.put("new_email", 0);
      _events.put("no_new_email", 0);
      _events.put("new_email_visible", 0);
   }

   MessagesEventProvider(SkinEventProvider skinProvider) {
      super(skinProvider);
   }

   public boolean isEventSupported(String eventId) {
      return _events.get(eventId) != -1;
   }

   public void provideEvent(String eventId) {
      int eventType = _events.get(eventId);
      switch (eventType) {
         case 0:
            if (!this._registeredForNewEmail) {
               _ucHelper.addComponentForUpdate(this);
               super._skinProvider.registerEventPair("new_email", "no_new_email");
               super._skinProvider.registerEventPair("new_email_visible", "no_new_email_visible");
            }
      }
   }

   @Override
   public void dispatchNew() {
      super._skinProvider.dispatchEvent("new_email");
   }

   @Override
   public void dispatchNoNew() {
      super._skinProvider.dispatchEvent("no_new_email");
   }

   @Override
   public void dispatchNewVisible() {
      super._skinProvider.dispatchEvent("new_email_visible");
   }

   @Override
   public void dispatchNoNewVisible() {
      super._skinProvider.dispatchEvent("no_new_email_visible");
   }
}
