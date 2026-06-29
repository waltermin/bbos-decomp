package net.rim.wica.runtime.messaging.internal.outbound;

import net.rim.device.cldc.io.utility.URL;
import net.rim.wica.runtime.messaging.internal.util.SplitQueue;

public final class OutboundQueue extends SplitQueue {
   private long _agId;
   private String _agURL;
   private URL _ioURL;
   private OutboundQueue$State _state;
   private boolean _shouldHeartbeat;
   private boolean[] _scheduled;
   private Object[] _scheduleLocks;
   private Object _stateGuard = new Object();
   private static final OutboundQueue$Enabled _enabled = new OutboundQueue$Enabled();
   private static final OutboundQueue$AgDisabled _agDisabled = new OutboundQueue$AgDisabled();
   private static final OutboundQueue$DeviceDisabled _deviceDisabled = new OutboundQueue$DeviceDisabled();
   private static final OutboundQueue$OnEnabledBridge _onEnabledBridge = new OutboundQueue$OnEnabledBridge();
   private static final OutboundQueue$OnAgDisabledBridge _onAgDisabledBridge = new OutboundQueue$OnAgDisabledBridge();
   private static final OutboundQueue$OnDeviceDisabledBridge _onDeviceDisabledBridge = new OutboundQueue$OnDeviceDisabledBridge();
   private static final OutboundQueue$Unregistered _unregistered = new OutboundQueue$Unregistered();
   public static final int SCHEDULE_FOR_SEND;
   public static final int SCHEDULE_FOR_RESPONSE;
   public static final int SCHEDULE_FOR_STATE;
   public static final int SCHEDULE_FOR_HEARTBEAT;

   OutboundQueue(long id, String url) {
      this._agId = id;
      this._agURL = url;
      this._state = _enabled;
      this._scheduled = new boolean[4];
      this._scheduleLocks = new Object[4];
      this._scheduleLocks[3] = new Object();

      try {
         this._ioURL = (URL)(new Object(this._agURL));
      } finally {
         throw new Object(((StringBuffer)(new Object("Invalid URL "))).append(this._agURL).toString());
      }
   }

   public final long getId() {
      return this._agId;
   }

   final String getURL() {
      return this._agURL;
   }

   final URL getIOURL() {
      return this._ioURL;
   }

   final boolean schedule(int index) {
      if (this._scheduleLocks[index] == null) {
         return this.scheduleInternal(index);
      }

      synchronized (this._scheduleLocks[index]) {
         return this.scheduleInternal(index);
      }
   }

   private final boolean scheduleInternal(int index) {
      if (this._scheduled[index]) {
         return false;
      }

      this._scheduled[index] = true;
      return true;
   }

   final void unschedule(int index) {
      this._scheduled[index] = false;
   }

   final void shouldHeartbeat(boolean shouldHeartbeat) {
      this._shouldHeartbeat = shouldHeartbeat;
   }

   final boolean shouldHeartbeat() {
      return this._shouldHeartbeat;
   }

   final OutboundQueue$State getState() {
      return this._state;
   }

   final boolean atBridgeOnAgEnabled() {
      synchronized (this._stateGuard) {
         return this._state.onAgEnabled(this);
      }
   }

   final boolean atBridgeOnDeviceEnabled() {
      synchronized (this._stateGuard) {
         return this._state.onDeviceEnabled(this);
      }
   }

   final boolean atBridgeOnAgDisabled() {
      synchronized (this._stateGuard) {
         return this._state.onAgDisabled(this);
      }
   }

   final boolean atBridgeOnDeviceDisabled() {
      synchronized (this._stateGuard) {
         return this._state.onDeviceDisabled(this);
      }
   }

   final void changeStateOnRun() {
      synchronized (this._stateGuard) {
         this._state.onRun(this);
      }
   }

   final void changeStateOnUnregister() {
      synchronized (this._stateGuard) {
         this._state.onUnregister(this);
      }
   }
}
