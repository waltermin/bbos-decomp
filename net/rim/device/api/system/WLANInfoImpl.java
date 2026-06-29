package net.rim.device.api.system;

import net.rim.device.internal.proxy.Proxy;

final class WLANInfoImpl implements WLANExtendedListener, RadioStatusListener {
   private String _profileName;
   private String _ssid;
   private String _bssid;
   private int _radioBand;
   private int _dataRate;
   private int _signalLevel;
   private static final long ID;
   private static WLANInfoImpl _instance;

   private WLANInfoImpl() {
      Proxy proxy = Proxy.getInstance();
      synchronized (this) {
         proxy.addRadioListener(4, this);
         this.updateWLANInfo();
      }
   }

   static final WLANInfoImpl getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (WLANInfoImpl)ar.getOrWaitFor(7226364515336065262L);
         if (_instance == null) {
            _instance = new WLANInfoImpl();
            ar.put(7226364515336065262L, _instance);
         }
      }

      return _instance;
   }

   final void addListener(WLANListener listener) {
      if (listener instanceof WLANConnectionListener) {
         WLANConnectionListener wcListener = (WLANConnectionListener)listener;
         MyWLANConnectionListener myListener = new MyWLANConnectionListener(wcListener);
         Application.getApplication().addRadioListener(myListener);
      }
   }

   final void removeListener(WLANListener listener) {
      if (listener instanceof WLANConnectionListener) {
         WLANConnectionListener wcListener = (WLANConnectionListener)listener;
         MyWLANConnectionListener myListener = new MyWLANConnectionListener(wcListener);
         Application.getApplication().removeRadioListener(myListener);
      }
   }

   final synchronized boolean isConnected() {
      return this._ssid != null;
   }

   final synchronized WLANInfo$WLANAPInfo getAPInfo() {
      return this.isConnected()
         ? new WLANInfo$WLANAPInfo(this._profileName, this._ssid, this._bssid, this._radioBand, this._dataRate, this._signalLevel)
         : null;
   }

   private final synchronized void updateWLANInfo() {
      WLANExtendedNetInfo netInfo = WLAN.getExtendedWLANNetworkInfo();
      if (netInfo != null) {
         this._profileName = WLAN.getWLANSystem().getActiveProfileName();
         this._ssid = WLAN.getWLANSystem().getActiveProfileSSID();
         this._bssid = WLAN.bssidToString(WLAN.getBSSID());
         this._radioBand = netInfo._band;
         this._dataRate = netInfo._dataRateMbps;
         this._signalLevel = netInfo._signalRssi;
      } else {
         this._profileName = null;
         this._ssid = null;
         this._bssid = null;
         this._radioBand = -1;
         this._dataRate = -1;
         this._signalLevel = -1;
      }
   }

   private final synchronized void updateSignalLevel(int signalLevel) {
      this._signalLevel = signalLevel;
   }

   @Override
   public final void wlanExtendedInfoChange() {
      this.updateWLANInfo();
   }

   @Override
   public final void radioStatus(boolean started) {
   }

   @Override
   public final void networkSuccess() {
   }

   @Override
   public final void networkFail(int status, int error, int extendedInfo) {
   }

   @Override
   public final void networkFound(int handle) {
   }

   @Override
   public final void networkApChange() {
   }

   @Override
   public final void wlanChallengeOccurred(int challengeType) {
   }

   @Override
   public final void wlanRecordChangeOccurred(int recordType) {
   }

   @Override
   public final void signalLevel(int level) {
      this.updateSignalLevel(level);
   }

   @Override
   public final void networkStarted(int networkId, int service) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
   }
}
