package net.rim.device.apps.internal.ribbon;

import java.util.Hashtable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.vm.Persistable;

final class RibbonOptions$PersistedRibbonOptions implements Persistable {
   ToIntHashtable _applicationPriorities;
   ToIntHashtable _applicationVisibilities;
   boolean _showHiddenApps;
   boolean _debugMode;
   Hashtable _backgroundImages;

   RibbonOptions$PersistedRibbonOptions() {
      this.resetToDefaults();
   }

   final void resetToDefaults() {
      this._applicationPriorities = (ToIntHashtable)(new Object());
      this._applicationVisibilities = (ToIntHashtable)(new Object());
      this._showHiddenApps = false;
      this._debugMode = false;
      this._backgroundImages = (Hashtable)(new Object());
   }
}
