package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.utility.props.ObjectProps;
import net.rim.device.apps.api.utility.props.StringProps;
import net.rim.device.apps.internal.ribbon.ApplicationLauncher;

public final class ModuleEntryPointDescriptor extends AbstractEntryPointDescriptor implements EntryPointDescriptor, StringProps, ObjectProps, Runnable {
   private String _uniqueName;
   private String _application;

   ModuleEntryPointDescriptor(String uniqueName, String application, String bundleName, int resourceId) {
      super(bundleName, resourceId);
      this._uniqueName = ApplicationEntry.removeControlCharacters(uniqueName);
      this._application = application;
   }

   @Override
   protected final String getDefaultDescription() {
      return this._uniqueName;
   }

   @Override
   public final String get(long propID, String defaultReturned) {
      if (propID == 1) {
         return this._uniqueName;
      } else {
         return propID == 3 ? this.getDescription() : defaultReturned;
      }
   }

   @Override
   public final void set(long propID, String valueToSet) {
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      return propID == 5 ? ThemeManager.getActiveTheme().getApplicationIcon(this._uniqueName, 0, 0, null, 0) : defaultReturned;
   }

   @Override
   public final void set(long propID, Object valueToSet) {
   }

   @Override
   public final void run() {
      ApplicationLauncher.launch(this._application);
   }
}
