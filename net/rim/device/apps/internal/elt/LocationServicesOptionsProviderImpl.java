package net.rim.device.apps.internal.elt;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.location.LocationServicesOptionsProvider;
import net.rim.device.apps.api.ui.BooleanChoiceField;

final class LocationServicesOptionsProviderImpl extends LocationServicesOptionsProvider {
   private BooleanChoiceField _locationTrackingState;
   private static ResourceBundleFamily _lbsBundle = ResourceBundle.getBundle(-1953930147342635535L, "net.rim.device.apps.internal.elt.ELT");
   static Class class$net$rim$device$apps$internal$elt$ETApplication;

   @Override
   public final void populateMainScreen(MainScreen mainScreen) {
      ETManager manager = ETApplication.getETManager();
      if (manager.isEnabledByITPolicy()) {
         mainScreen.add((Field)(new Object()));
         mainScreen.add(this._locationTrackingState = (BooleanChoiceField)(new Object(_lbsBundle.getString(0), 0, false)));
         this._locationTrackingState.setAffirmative(manager.isEnabledByUser());
      }
   }

   @Override
   public final void activationStatus() {
      Backlight.enable(true);
      ETManager manager = ETApplication.getETManager();
      BooleanChoiceField locationTrackingState = this._locationTrackingState;
      if (ITPolicyListener.getInstance().hasITPolicyChanged()) {
         Application.getApplication().invokeLater(new LocationServicesOptionsProviderImpl$1(this, manager, locationTrackingState));
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void save() {
      if (this._locationTrackingState != null && this._locationTrackingState.isDirty()) {
         ETManager manager = ETApplication.getETManager();
         if (manager.isEnabledByITPolicy()) {
            if (this._locationTrackingState.isAffirmative()) {
               ETCollection.getInstance().setEnabledbyUser(true);
               int module = CodeModuleManager.getModuleHandleForClass(
                  class$net$rim$device$apps$internal$elt$ETApplication == null
                     ? (class$net$rim$device$apps$internal$elt$ETApplication = class$("net.rim.device.apps.internal.elt.ETApplication"))
                     : class$net$rim$device$apps$internal$elt$ETApplication
               );
               ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(module);

               try {
                  ApplicationManager.getApplicationManager().runApplication(descriptors[0]);
                  return;
               } catch (Throwable var6) {
                  Logger.logError(this, ((StringBuffer)(new Object("ApplicationManagerException: "))).append(e).toString());
                  return;
               }
            }

            manager.disableTracking();
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
