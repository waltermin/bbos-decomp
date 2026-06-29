package net.rim.device.apps.internal.ribbon;

import net.rim.vm.Persistable;

final class BackgroundImage implements Persistable {
   String _name;
   String[] _properties;

   BackgroundImage() {
      this.resetToDefaults();
   }

   final void resetToDefaults() {
      this._name = null;
      this._properties = null;
   }
}
