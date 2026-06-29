package net.rim.wica.runtime.lifecycle;

public class WicletUpgradeEvent {
   boolean _isDataCompatible;
   boolean _isMsgCompatible;
   long _installedWicletId;
   Wiclet _wiclet;

   public WicletUpgradeEvent(boolean isDataCompatible, boolean isMsgCompatible, Wiclet wiclet, long installedWicletId) {
      this._isDataCompatible = isDataCompatible;
      this._isMsgCompatible = isMsgCompatible;
      this._installedWicletId = installedWicletId;
      this._wiclet = wiclet;
   }

   public boolean isDataCompatible() {
      return this._isDataCompatible;
   }

   public boolean isMsgCompatible() {
      return this._isMsgCompatible;
   }

   public long getInstalledWicletId() {
      return this._installedWicletId;
   }

   public Wiclet getWiclet() {
      return this._wiclet;
   }
}
