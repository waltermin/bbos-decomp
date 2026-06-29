package net.rim.device.apps.internal.bluetooth;

import java.util.Vector;
import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.AudioRouterListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.bluetooth.BluetoothA2DP;
import net.rim.device.internal.bluetooth.BluetoothA2DPListener;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.media.MediaRemoteControl;
import net.rim.device.internal.system.InternalServices;

final class BluetoothA2DPManager extends BluetoothProfileManager implements BluetoothA2DPListener, AudioRouterListener, VerbFactory, Runnable {
   private int _streamHandle = -1;
   private AudioRouter _audioRouter;
   private int _playState;
   private int _nextPlayState;
   private long _startedWaitingTime;
   private byte[] _sbcStreamElements = new byte[]{34, 21, 19, 48};
   private static final int PLAY_STATE_NONE = 0;
   private static final int PLAY_STATE_SUSPENDED = 1;
   private static final int PLAY_STATE_SUSPEND_PENDING = 2;
   private static final int PLAY_STATE_STARTED = 3;
   private static final int PLAY_STATE_START_PENDING = 4;
   private static final int PLAY_STATE_WAITING_TO_SUSPEND = 5;
   private static final byte MIN_BITPOOL = 19;
   private static final byte MAX_BITPOOL = 48;
   private static final byte[] SBC_CONFIG_ELEMENTS = new byte[]{46, -1, 19, 48};
   public static final long LOG_GUID = 884619351721863275L;
   private static final int LOG_OPEN_STREAM = 1330839552;
   private static final int LOG_CLOSE_STREAM = 1129512960;
   private static final int LOG_SUSPEND_STREAM = 1397948416;
   private static final int LOG_START_STREAM = 1398013952;
   private static final int LOG_OPEN_STREAM_RESPONSE = 1330774016;
   private static final int LOG_START_STREAM_RESPONSE = 1397882880;
   private static final int LOG_STREAM_STARTED = 1396768768;
   private static final int LOG_STREAM_IDLE = 1229193216;
   private static final int LOG_STREAM_SUSPENDED = 1398079488;
   private static final int LOG_STREAM_ABORTED = 1094844416;
   private static final int LOG_BAD_CODEC_DATA = 1111573571;
   private static final int LOG_CONFIG_STREAM = 1128660992;
   private static final int LOG_RECONFIG_REQUIRED = 1380270927;
   private static final int LOG_START_ALREADY_PENDING = 1397769550;
   private static final int LOG_WAITING_TO_SUSPEND_STREAM = 1463896404;

   BluetoothA2DPManager(BluetoothDeviceManagerImpl btManager) {
      super(btManager, 884619351721863275L, "net.rim.bluetooth.a2dp");
   }

   @Override
   final int getID() {
      return 1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   final boolean init() {
      if (!BluetoothA2DP.isSupported()) {
         return false;
      }

      BluetoothA2DP.addListener(super._btManager, this);

      label24:
      try {
         this._streamHandle = BluetoothA2DP.registerStream(0, 0, SBC_CONFIG_ELEMENTS);
      } catch (Throwable var3) {
         System.out.println(ex);
         break label24;
      }

      this._audioRouter = AudioRouter.getInstance();
      AudioRouter.addListener(super._btManager, this);
      VerbFactoryRepository.addFactory(-5280468186386428176L, this);
      return true;
   }

   @Override
   final boolean canConnect(BluetoothDevice device) {
      return this._streamHandle != -1 && super._state == 0 && device.hasA2DPSink();
   }

   @Override
   final boolean isConnected(BluetoothDevice device) {
      return super._device == device;
   }

   @Override
   final synchronized int connect(BluetoothDevice device) {
      if (!BluetoothME.isPowerOn()) {
         return 4;
      }

      if (super._state != 0) {
         return 3;
      }

      if (device.hasA2DPSink()) {
         if (!BluetoothA2DP.isEnabled()) {
            return 2;
         }

         int rc = BluetoothA2DP.openStream(this._streamHandle, device.getAddress());
         this.logResult(1330839552, rc);
         if (rc != 0 && rc != 2) {
            return 1;
         }

         this.updateState(1, device);
         return 0;
      } else {
         return 1;
      }
   }

   @Override
   final void disconnect(BluetoothDevice device) {
      if (super._device == device) {
         int rc = BluetoothA2DP.closeStream(this._streamHandle);
         this.logResult(1129512960, rc);
         if (rc == 0 || rc == 2) {
            this.updateState(3);
         }
      }
   }

   @Override
   final void cleanup() {
      super.cleanup();
      if (super._device != null) {
         this.a2dpDisconnected(0, 0);
      }
   }

   @Override
   final boolean sniffModeDesired() {
      switch (this._playState) {
         case 2:
            return true;
         case 3:
         case 4:
         case 5:
         default:
            return false;
      }
   }

   @Override
   final String getName() {
      return BluetoothMainScreen.getString(87);
   }

   private final void suspendStream() {
      switch (this._playState) {
         case -1:
         case 4:
            this._nextPlayState = 1;
         case 1:
         case 2:
         case 5:
            break;
         case 3:
         default:
            if (this._startedWaitingTime != 0) {
               this._startedWaitingTime = InternalServices.getUptime();
               this._playState = 5;
               return;
            }

            if (super._btManager.invokeLater(this, 5000, false) != -1) {
               this._startedWaitingTime = InternalServices.getUptime();
               this.log(1463896404);
               this._playState = 5;
               return;
            }
         case 0:
            int rc = BluetoothA2DP.suspendStream(this._streamHandle);
            this.logResult(1397948416, rc);
            if (rc == 0 || rc == 2) {
               this._playState = 2;
               super._device.updateSniffMode();
               return;
            }
      }
   }

   @Override
   public final void run() {
      long elapsed = InternalServices.getUptime() - this._startedWaitingTime;
      this._startedWaitingTime = 0;
      if (this._playState == 5) {
         if (elapsed > 4000) {
            int rc = BluetoothA2DP.suspendStream(this._streamHandle);
            this.logResult(1397948416, rc);
            if (rc != 0 && rc != 2) {
               this._playState = 3;
               return;
            }

            this._playState = 2;
            super._device.updateSniffMode();
            return;
         }

         this._playState = 3;
         this.suspendStream();
      }
   }

   private final void startStream() {
      switch (this._playState) {
         case -1:
         case 2:
            this._nextPlayState = 3;
            break;
         case 0:
         case 1:
            int rc = BluetoothA2DP.startStream(this._streamHandle);
            this.logResult(1398013952, rc);
            if (rc == 0 || rc == 2) {
               this._playState = 4;
               super._device.updateSniffMode();
               return;
            }
         case 3:
         case 4:
            break;
         case 5:
         default:
            this._playState = 3;
            this.logResult(1396768768, 0);
            return;
      }
   }

   private final void updateSinkProperties(boolean enabled) {
      String name = null;
      if (enabled) {
         name = super._device.getName();
      }

      this._audioRouter.setBluetoothA2DPSinkProperties(enabled, name);
   }

   @Override
   public final void a2dpIncomingConnection(int handle, int error, byte[] address, int codecType, byte[] codecElements) {
      if (error == 0) {
         if (!BluetoothA2DP.isEnabled()) {
            BluetoothA2DP.openStreamResponse(handle, 129, 0);
         } else {
            BluetoothDevice device = super._btManager.getPairedDevice(address);
            if (device != null) {
               device.addSupportedProfile(64);
               int rc = BluetoothA2DP.openStreamResponse(handle, 0, 0);
               this.logResult(1330774016, rc);
               if (rc == 0 || rc == 2) {
                  this.updateState(1, device);
               }
            }
         }
      }
   }

   @Override
   public final void a2dpConnected(int handle, int error, byte[] address) {
      if (error != 0) {
         this.updateState(0);
      } else {
         this.updateState(2);
         this.updateSinkProperties(true);
      }
   }

   @Override
   public final void a2dpDisconnected(int handle, int error) {
      if (error != 0) {
         this.updateState(2);
      } else {
         this.updateState(0);
         if (this._playState == 3) {
            MediaRemoteControl.postPanelEvent(70, 2);
            Thread.yield();
         }

         this._playState = 0;
         this.updateSinkProperties(false);
      }
   }

   @Override
   public final void a2dpStartRequested(int handle, int error) {
      if (error == 0) {
         if (this._playState == 4) {
            this.log(1397769550);
            BluetoothA2DP.startStreamResponse(handle, 129);
         } else {
            this._playState = 4;
            int rc = BluetoothA2DP.startStreamResponse(handle, 0);
            this.logResult(1397882880, rc);
            if (rc == 0 || rc == 2) {
               super._device.updateSniffMode();
            }
         }
      }
   }

   @Override
   public final void a2dpStarted(int handle, int error) {
      this.logResult(1396768768, error);
      if (error != 0) {
         this._playState = 0;
      } else {
         this._playState = 3;
         if (this._audioRouter.getSink() == 5 && this._nextPlayState != 1) {
            super._device.updateSniffMode();
         } else {
            this._nextPlayState = 0;
            this.suspendStream();
         }
      }
   }

   @Override
   public final void a2dpIdle(int handle, int error) {
      this.logResult(1229193216, error);
   }

   @Override
   public final void a2dpSuspended(int handle, int error) {
      this.logResult(1398079488, error);
      if (error != 0) {
         this._playState = 0;
      } else {
         this._playState = 1;
         if (this._nextPlayState == 3) {
            this._nextPlayState = 0;
            this.startStream();
         } else {
            super._device.updateSniffMode();
         }
      }
   }

   @Override
   public final void a2dpAborted(int handle, int error) {
      this.logResult(1094844416, error);
   }

   @Override
   public final void a2dpCodecInfo(int handle, int error, int codecType, byte[] codecElements) {
      if (codecElements.length != 4) {
         this.log(1111573571);
      } else {
         this._sbcStreamElements[2] = (byte)Math.max(19, codecElements[2]);
         this._sbcStreamElements[3] = (byte)Math.min(48, codecElements[3]);
      }
   }

   @Override
   public final void a2dpConfigRequired(int handle, int error) {
      int rc = BluetoothA2DP.configStream(handle, 0, this._sbcStreamElements);
      this.logResult(1128660992, rc);
   }

   @Override
   public final void a2dpReconfigRequired(int handle, int error, int codecType, byte[] codecElements) {
      this.log(1380270927);
   }

   @Override
   public final synchronized void audioVolumeChanged(boolean remote) {
   }

   @Override
   public final synchronized void audioSinkChanged() {
      if (this._audioRouter.getSink() == 5) {
         if (super._state != 2) {
            this._audioRouter.resetSink();
         } else {
            this.startStream();
         }
      } else {
         if (super._state == 2) {
            this.suspendStream();
         }
      }
   }

   @Override
   public final void audioSourceChanged() {
   }

   @Override
   public final synchronized Verb[] getVerbs(Object context) {
      Verb[] verbs = new Object[0];
      if (BluetoothME.isPowerOn() && context instanceof Object) {
         AudioPathControl control = (AudioPathControl)context;
         if (control.canSwitchToPath(5)) {
            if (super._state == 2) {
               switch (this._playState) {
                  case 0:
                  case 1:
                  case 5:
                     Arrays.add(verbs, new BluetoothA2DPManager$ConnectVerb(this, super._device, control));
                     return verbs;
               }
            } else {
               Vector v = super._btManager.getPairedDevices();
               int length = v.size();

               for (int i = 0; i < length; i++) {
                  BluetoothDevice device = (BluetoothDevice)v.elementAt(i);
                  if (this.canConnect(device)) {
                     Arrays.add(verbs, new BluetoothA2DPManager$ConnectVerb(this, device, control));
                  }
               }
            }
         }
      }

      return verbs;
   }
}
