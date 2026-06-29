package net.rim.wica.runtime.management;

import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.Persistable;

public final class RuntimeInfo implements Persistable {
   private long _deviceId;
   private ClientAdminPolicy _policy;
   private WicletAdminPolicy _trustedWicletAdminPolicy;
   private WicletAdminPolicy _untrustedWicletAdminPolicy;
   private boolean _upgraded;
   private long _keyRefreshAGID = -1;
   private boolean _doingRegistration;
   private boolean _registered;
   private boolean _doingKeyRefresh;
   private boolean _reactivate;
   private AGInfo _defaultAGInfo;
   private AGInfo _newAGInfo;
   private long _timeLastREStatusSent;
   private LongHashtable _keyRefreshParams = (LongHashtable)(new Object(1));

   @Override
   public final String toString() {
      StringBuffer b = (StringBuffer)(new Object("M Details\n"));
      b.append("Registered: ");
      b.append(this._registered);
      b.append("Registering: ");
      b.append(this._doingRegistration);
      b.append("Reactivate: ");
      b.append(this._reactivate);
      return b.toString();
   }

   public final boolean getDoingKeyRefresh() {
      return this._doingKeyRefresh;
   }

   public final void setDoingKeyRefresh(boolean keyRefresh) {
      this._doingKeyRefresh = keyRefresh;
   }

   public final long getKeyRefreshAGID() {
      return this._keyRefreshAGID;
   }

   public final void setKeyRefreshAGID(long refreshAGID) {
      this._keyRefreshAGID = refreshAGID;
   }

   public final boolean isRegistered() {
      return this._registered;
   }

   public final void setRegistered(boolean registered) {
      this._registered = registered;
   }

   public final boolean getUpgraded() {
      return this._upgraded;
   }

   public final void setUpgraded(boolean upgraded) {
      this._upgraded = upgraded;
   }

   public final synchronized boolean getDoingRegistration() {
      return this._doingRegistration;
   }

   public final synchronized void setDoingRegistration(boolean doingRegistration) {
      this._doingRegistration = doingRegistration;
   }

   public final long getDeviceId() {
      return this._deviceId;
   }

   public final void setDeviceId(long deviceId) {
      this._deviceId = deviceId;
   }

   public final ClientAdminPolicy getClientAdminPolicy() {
      return this._policy;
   }

   public final void setClientAdminPolicy(ClientAdminPolicy policy) {
      this._policy = policy;
   }

   public final WicletAdminPolicy getTrustedWicletAdminPolicy() {
      return this._trustedWicletAdminPolicy;
   }

   public final void setTrustedWicletAdminPolicy(WicletAdminPolicy policy) {
      this._trustedWicletAdminPolicy = policy;
   }

   public final WicletAdminPolicy getUntrustedWicletAdminPolicy() {
      return this._untrustedWicletAdminPolicy;
   }

   public final void setUntrustedWicletAdminPolicy(WicletAdminPolicy policy) {
      this._untrustedWicletAdminPolicy = policy;
   }

   public final synchronized AGInfo getDefaultAGInfo() {
      return this._defaultAGInfo;
   }

   public final synchronized void setDefaultAGInfo(AGInfo info) {
      this._defaultAGInfo = info;
   }

   public final AGInfo getNewAGInfo() {
      return this._newAGInfo;
   }

   public final void setNewAGInfo(AGInfo info) {
      this._newAGInfo = info;
   }

   public final long getTimeLastREStatusSent() {
      return this._timeLastREStatusSent;
   }

   public final void setTimeLastREStatusSent(long lastREStatusSent) {
      this._timeLastREStatusSent = lastREStatusSent;
   }

   public final synchronized boolean isReactivate() {
      return this._reactivate;
   }

   public final synchronized void setReactivate(boolean reactivate) {
      this._reactivate = reactivate;
   }

   public final LongHashtable getKeyRefreshParams() {
      return this._keyRefreshParams;
   }
}
