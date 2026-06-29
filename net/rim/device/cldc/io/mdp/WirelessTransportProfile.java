package net.rim.device.cldc.io.mdp;

import net.rim.device.api.io.ConnEvent;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.system.EventLogger;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;

final class WirelessTransportProfile implements MdpConstants, ConnEvent {
   int _maxWindowSize;
   int _windowCount;
   int _maxAckDelayTimeOut;
   boolean _legacyMode = true;
   MDPMaxDelayTimeoutThread _delayTimerThread;
   MDPRequestThread _requestTimerRunnable;
   int[] _windowDatagramsKeys;
   byte[] _windowDatagramsPackets;
   boolean _allowRetries = true;
   DatagramAddressBase _key;
   DatagramAddressBase _reverseKey;
   int _wtAddress;
   boolean _mfhLegacyMode = true;
   int _datagramAckTimeout = -1;
   int _maxOutstandingWindowSize;
   int _lostPacketTimeout;
   int _relayMaxPacketRTT;
   int _flags = -1;
   int _timer;
   int _requestPeriod;
   private MFHRetransmissionConfiguration _config;
   long _lastMTHPacketTime;
   long _lastMFHPacketTime;
   static final int EARLY_RECEIPT_CONFIRM_FLAG;
   private static MFHRetransmissionConfiguration _defaultConfig;

   WirelessTransportProfile() {
      this._maxWindowSize = MdpMFHUtil.getDefaultRCWindowSize();
      this._maxOutstandingWindowSize = MdpMFHUtil.getDefaultMFHOutstandingPacketsWindowSize();
      this._lostPacketTimeout = MdpMFHUtil.getDefaultMFHLostPacketTimer();
      this._relayMaxPacketRTT = MdpMFHUtil.getDefaultMFHRelayPacketMaxRTT();
   }

   final synchronized void allowRetries(boolean allow) {
      this._allowRetries = allow || this._requestPeriod > 0;
   }

   final synchronized boolean retriesAllowed() {
      return this._allowRetries || this._requestPeriod > 0;
   }

   final boolean shouldRequestParam() {
      if (this._requestPeriod > 0 && this._requestTimerRunnable != null && !this._requestTimerRunnable.isRunning() && ++this._timer >= this._requestPeriod) {
         this._timer = 0;
         return true;
      } else {
         return false;
      }
   }

   final void requestTimerRunnable(int retriesBackoffMax, int numRetries, DatagramAddressBase subAddress) {
      if (this._requestTimerRunnable == null) {
         if (this.retriesAllowed()) {
            this._requestTimerRunnable = new MDPRequestThread(retriesBackoffMax, this);
            if (this._requestTimerRunnable.createParamRequest(subAddress, false)) {
               this._requestTimerRunnable.setRetries(numRetries);

               try {
                  ProtocolDaemon.getInstance().submitRunnable(this._requestTimerRunnable);
                  return;
               } finally {
                  EventLogger.logEvent(MdpMTHUtil.GUID, 1414022514, 3);
                  return;
               }
            }
         }
      } else if (!this._requestTimerRunnable.isRunning() && (this._legacyMode || this._requestPeriod == 0)) {
         this._requestTimerRunnable = null;
         this.allowRetries(false);
      }
   }

   final MFHRetransmissionConfiguration getMFHRetransmissionConfig() {
      if (this._config == null) {
         this._config = getDefaultMFHRetransmissionConfig();
      }

      return this._config;
   }

   static final MFHRetransmissionConfiguration getDefaultMFHRetransmissionConfig() {
      if (_defaultConfig == null) {
         _defaultConfig = new MFHRetransmissionConfiguration();
         _defaultConfig._lostPacketTimeout = MdpMFHUtil.getDefaultMFHLostPacketTimer();
         _defaultConfig._maxRelayPacketRTT = MdpMFHUtil.getDefaultMFHRelayPacketMaxRTT();
         _defaultConfig._maxPacketTimeout = 60000;
         _defaultConfig._maxAggressivePacketRetry = 1;
         _defaultConfig._maxImmediatePacketRetry = 1;
         _defaultConfig._maxBackoffPacketRetry = 5;
      }

      return _defaultConfig;
   }

   final void cleanUp(boolean force) {
      if (force && this._delayTimerThread != null) {
         this._delayTimerThread.stopTimer(true);
         this._delayTimerThread = null;
      }

      if (this._requestTimerRunnable != null) {
         synchronized (this._requestTimerRunnable) {
            this._requestTimerRunnable.setRetries(0);
            this._requestTimerRunnable.notify();
            if (force || this._requestPeriod == 0) {
               this._requestTimerRunnable = null;
               this.allowRetries(false);
            }
         }
      }
   }

   final void setFlag(int flag, boolean set) {
      if (set) {
         this._flags &= ~flag;
      } else {
         this._flags |= flag;
      }
   }

   final boolean isSet(int flag) {
      return (this._flags & flag) == 0;
   }
}
