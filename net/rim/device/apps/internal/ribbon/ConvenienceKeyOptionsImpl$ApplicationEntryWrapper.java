package net.rim.device.apps.internal.ribbon;

import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;

final class ConvenienceKeyOptionsImpl$ApplicationEntryWrapper {
   ApplicationEntry _application;
   String _description;
   String _uniqueId;

   ConvenienceKeyOptionsImpl$ApplicationEntryWrapper(ApplicationEntry app) {
      this._application = app;
   }

   ConvenienceKeyOptionsImpl$ApplicationEntryWrapper(String description, String uniqueId) {
      this._description = description;
      this._uniqueId = uniqueId;
   }

   @Override
   public final String toString() {
      return this._description == null ? this._application.getDescriptionNoHotkey() : this._description;
   }

   final String getUniqueId() {
      return this._uniqueId == null ? this._application.getPropertiesName() : this._uniqueId;
   }

   final ApplicationEntry getApplicationEntry() {
      return this._application;
   }
}
