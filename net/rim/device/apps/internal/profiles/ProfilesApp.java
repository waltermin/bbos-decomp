package net.rim.device.apps.internal.profiles;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.internal.deviceoptions.DeviceOptions;
import net.rim.device.internal.proxy.Proxy;

final class ProfilesApp extends UiApplication {
   static final Tag TAG = Tag.create("profiles-menu");

   public static final void main(String[] args) {
      if (args != null && args.length > 0) {
         if (args[0].equals("init")) {
            Proxy proxy = Proxy.getInstance();
            ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
            registry.put(Profiles.PROFILES_APP_DESCRIPTOR_ID, ApplicationDescriptor.currentApplicationDescriptor());
            ProfilesOptions.getOptions().enableSynchronization();
            Profiles profiles = Profiles.getInstance();
            SyncManager syncManager = SyncManager.getInstance();
            if (syncManager != null) {
               syncManager.enableSynchronization(profiles);
            }

            DeviceOptions.addLegacyDeviceOptionsListener(profiles);
            AddressBook ab = AddressBookServices.getAddressBook();
            if (ab != null) {
               ab.addCollectionListener(Overrides.getInstance());
            }

            ProfilesApp$HomeScreenUpdater updater = new ProfilesApp$HomeScreenUpdater(profiles);
            proxy.addGlobalEventListener(updater);
            NotificationsEngineImpl notificationsEngine = new NotificationsEngineImpl();
            registry.put(6720217471165517311L, notificationsEngine);
            proxy.startThread(notificationsEngine);
            proxy.addHolsterListener(notificationsEngine);
            AlertEngine alertEngine = AlertEngine.getInstance();
            proxy.addAlertListener(alertEngine);
            proxy.startThread(alertEngine);
            AlertConsequence consequence = new AlertConsequence(alertEngine);
            registry.put(-2870941457036655797L, consequence);
            NotificationsManager.registerConsequence(-2870941457036655797L, consequence);
            EventLogger.register(6982943375119825480L, "profiles", 2);
            ProfilesPopupScreen.register(ThemeManager.getActiveTheme().getAttributeSet(TAG) != null);
            updater.updateHomeScreen(profiles.getEnabled());
            AudioRouterVerbFactory.registerOnceOnSystemStart();
            return;
         }
      } else {
         new ProfilesApp().enterEventDispatcher();
      }
   }

   private ProfilesApp() {
      if (ThemeManager.getActiveTheme().getAttributeSet(TAG) != null) {
         Application.getApplication().requestBackground();
         ProfilesPopupScreen.show(true, false);
      } else {
         UiApplication.getUiApplication().pushScreen(new ProfilesScreen(true));
      }
   }
}
