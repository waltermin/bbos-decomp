package net.rim.device.apps.internal.sms;

import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.internal.sms.resources.SMSResources;

final class SMSOptionsScreen extends SaveableMainScreenOptionsListItem {
   SMSOptionsMainScreen _SMSscreen = new SMSOptionsMainScreen();

   static final void init() {
      OptionsProviderRegistration$OptionsProvider op = new SMSOptionsScreen$1();
      OptionsProviderRegistration.registerOptionsProvider(op);
   }

   private SMSOptionsScreen() {
      super(SMSResources.getString(610));
   }

   @Override
   protected final MainScreen createMainScreen() {
      this._SMSscreen.rebuildFieldCollection();
      return this._SMSscreen;
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
   }

   @Override
   public final boolean isValid(Object context) {
      return !this._SMSscreen.hasFieldsToDisplay() ? false : super.isValid(context);
   }

   static final void register() {
      OptionsProviderRegistration$OptionsProvider op = new SMSOptionsScreen$2();
      OptionsProviderRegistration.registerOptionsProvider(op);
   }

   SMSOptionsScreen(SMSOptionsScreen$1 x0) {
      this();
   }
}
