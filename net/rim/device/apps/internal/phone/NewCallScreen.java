package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class NewCallScreen extends PhoneAppScreen {
   private VoiceApp _voiceApp;
   static final int CANCELLED;

   NewCallScreen(VoiceApp voiceApp) {
      super(voiceApp, true);
      this._voiceApp = voiceApp;
      this.setCloseVerb(new NewCallScreen$CancelNewCallScreenVerb(this, this));
   }

   @Override
   public final void loadCloseVerb(SystemEnabledMenu menu, int instance) {
      if (instance == 0) {
         if (menu != null && super._closeVerb != null) {
            menu.add(super._closeVerb);
         }
      }
   }

   @Override
   protected final Field getTitleField() {
      return (Field)(new Object(PhoneResources.getString(420)));
   }

   @Override
   public final boolean onClose() {
      return true;
   }

   private final void close(int reason) {
      this.close();
      if (reason != 0) {
         this._voiceApp.onNewCallScreenClosed(reason);
      }
   }

   @Override
   protected final void cancel() {
      this.close(1);
   }

   @Override
   protected final void onEvent(int eventId, int param1, Object param2) {
      switch (eventId) {
         case 1100:
         case 3006:
            if (!this.isDisplayed()) {
               break;
            }

            this.close();
         default:
            super.onEvent(eventId, param1, param2);
            return;
         case 1110:
         case 100300:
      }

      this.cancel();
   }

   @Override
   protected final ContextObject getMenuContextObject() {
      ContextObject context = (ContextObject)(new Object(20));
      PhoneUtilities.setPrivateFlag(context, 51);
      PhoneUtilities.setPrivateFlag(context, 76);
      PhoneUtilities.setPrivateFlag(context, 55);
      return context;
   }

   static final void access$000(NewCallScreen x0, Verb x1) {
      x0.setCloseVerb(x1);
   }
}
