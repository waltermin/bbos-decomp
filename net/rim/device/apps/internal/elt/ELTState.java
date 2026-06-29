package net.rim.device.apps.internal.elt;

import net.rim.device.api.util.Persistable;

final class ELTState implements Persistable {
   private boolean _enabledByITPolicy = false;
   private boolean _enabledByUser = false;
   private int _interval = 60;
   static final int DEFAULT_TRACKING_INTERVAL;

   final boolean isEnabledByITPolicy() {
      return this._enabledByITPolicy;
   }

   final void setEnabledByITPolicy(boolean byITPolicy) {
      this._enabledByITPolicy = byITPolicy;
   }

   final boolean isEnabledByUser() {
      return this._enabledByUser;
   }

   final void setEnabledByUser(boolean byUser) {
      this._enabledByUser = byUser;
   }

   final int getGPSInterval() {
      return this._interval;
   }

   final void setGPSInterval(int interval) {
      this._interval = interval;
   }
}
