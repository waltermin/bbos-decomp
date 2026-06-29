package net.rim.device.internal.provisioning;

import java.io.IOException;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.CBPacketHeader;
import net.rim.device.api.system.CBPacketListener;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.system.SMSPacketListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.system.UDPPacketHeader;
import net.rim.device.api.system.UDPPacketListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.internal.system.ICMPPacketHeader;
import net.rim.device.internal.system.ICMPPacketListener;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.system.TCPPacketHeader;
import net.rim.device.internal.system.TCPPacketListener;

final class BBXpHandler
   implements ProvisioningHandler,
   Runnable,
   RadioStatusListener,
   UDPPacketListener,
   SMSPacketListener,
   CBPacketListener,
   ICMPPacketListener,
   TCPPacketListener,
   SystemListener2 {
   private int _state;
   private boolean _leaveRadioOff;
   private int _attemptCount;
   private int _newBBXp;
   private Application _app;
   private int _invokeLaterId;
   private boolean _radioOnAtStart;
   private static final int DEFAULT_VALUE = 0;
   private static final int STATE_DELAY = 0;
   private static final int STATE_START = 1;
   private static final int STATE_CHECK_BB_XP = 2;
   private static final int STATE_TURN_RADIO_OFF = 3;
   private static final int STATE_WAIT_FOR_RADIO_OFF = 4;
   private static final int STATE_TOGGLE_BB_XP = 5;
   private static final int STATE_VERIFY_BB_XP = 6;
   private static final int STATE_TURN_RADIO_ON = 7;
   private static final int STATE_RESTART = 8;
   private static final int STATE_IDLE = 9;
   private static final int MAX_TRIES = 5;
   private static final int DELAY_TIMEOUT = 60000;
   private static final int RADIO_OFF_TIMEOUT = 10000;
   private static final int MODE_BITMASK = 17;
   private static final String NOT_SUPPORTED_ERRROR_STRING = "BBXp bailing due to non-support on platform ";

   public final void mobilityManagementEvent(int eventCode, int cause) {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized void run() {
      try {
         switch (this._state) {
            case -1:
            case 2:
               break;
            case 0:
            default:
               ProvisioningService.log("BBXp_state: delay", 0);
               this._invokeLaterId = this._app.invokeLater(this, 60000, false);
               this._state = 1;
               break;
            case 1:
               ProvisioningService.log("BBXp_state: start", 0);
               this._radioOnAtStart = RadioInfo.getState() != 0;
               this._state = this.doStart(true);
               this._app.invokeLater(this);
               this._invokeLaterId = -1;
               break;
            case 3:
               ProvisioningService.log("BBXp_state: turn Radio Off", 0);
               if (RadioInfo.getState() != 0) {
                  this._state = 4;
                  Radio.requestPowerOff();
                  this.scheduleInvokeLater(10000);
               } else {
                  this._state = 5;
                  this._app.invokeLater(this);
               }
               break;
            case 4:
               ProvisioningService.log("BBXp_state: timeout waiting for Radio Off", 3);
               this._invokeLaterId = -1;
               this.failedAttempt(3);
               this._app.invokeLater(this);
               break;
            case 5:
               ProvisioningService.log("BBXp_state: toggle BBXp", 0);
               boolean var8 = false /* VF: Semaphore variable */;

               label118:
               try {
                  var8 = true;
                  RadioInternal.setBlackBerryExperienceMode(this._newBBXp);
                  this._state = 6;
                  var8 = false;
               } finally {
                  if (var8) {
                     ProvisioningService.log("BBXp bailing due to non-support on platform (2)", 0);
                     this._state = 7;
                     break label118;
                  }
               }

               this._app.invokeLater(this);
               break;
            case 6:
               ProvisioningService.log("BBXp_state: verify BBXp", 0);
               boolean var12 = false /* VF: Semaphore variable */;

               label114:
               try {
                  var12 = true;
                  int t = RadioInternal.getBlackBerryExperienceMode();
                  ProvisioningService.log("mode=" + Integer.toHexString(t), 5);
                  if ((t & 17) == this._newBBXp) {
                     this._state = 7;
                     var12 = false;
                  } else {
                     ProvisioningService.log("BBXp_state: Wanted=0x" + Integer.toHexString(this._newBBXp) + " got=0x" + Integer.toHexString(t), 3);
                     this.failedAttempt(8);
                     var12 = false;
                  }
               } finally {
                  if (var12) {
                     ProvisioningService.log("BBXp bailing due to non-support on platform (3)", 0);
                     this._state = 7;
                     break label114;
                  }
               }

               this._app.invokeLater(this);
               break;
            case 7:
               ProvisioningService.log("BBXp_state: turn radio on", 0);
               if (!this._leaveRadioOff) {
                  Radio.requestPowerOn();
               }

               this._state = 9;
               this._app.invokeLater(this);
               break;
            case 8:
               ProvisioningService.log("BBXp_state: restart", 0);
               this._state = this.doStart(false);
               this._app.invokeLater(this);
               break;
            case 9:
               ProvisioningService.log("BBXp_state: done", 0);
         }

         ProvisioningService.log("newState: " + this._state, 5);
      } finally {
         this.handleReallyBadError();
         return;
      }
   }

   @Override
   public final synchronized void radioTurnedOff() {
      try {
         if (this._state == 4) {
            ProvisioningService.log("BBXp_state: radio turned off", 0);
            if (RadioInfo.getState() == 0) {
               this.unscheduleInvokeLater();
               this._state = 5;
               this._app.invokeLater(this);
               return;
            }
         }
      } finally {
         ;
      }
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
   }

   @Override
   public final void baseStationChange() {
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

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void updateProvisioningData(IntIntHashtable chunks, DataBuffer db) {
      if (DeviceInfo.isSimulator()) {
         ProvisioningService.log("BBXp bailing due to simulator", 0);
      } else {
         synchronized (this) {
            boolean var15 = false /* VF: Semaphore variable */;

            int curBBXp;
            try {
               var15 = true;
               curBBXp = RadioInternal.getBlackBerryExperienceMode();
               var15 = false;
            } finally {
               if (var15) {
                  ProvisioningService.log("BBXp bailing due to non-support on platform (1)", 0);
                  return;
               }
            }

            if (!chunks.containsKey(2)) {
               ProvisioningService.log("BBXp mode not specified.", 5);
               ProvisioningService.log("BBXp: cur mode=" + Integer.toHexString(curBBXp), 5);
            } else {
               db.setPosition(chunks.get(2));
               if (db.readCompressedInt() != 4) {
                  throw new IOException();
               }

               int newValue = db.readInt();
               if (this._state == 0) {
                  this.unscheduleInvokeLater();
                  this._state = 9;
               }

               this._newBBXp = newValue;
               if ((curBBXp & 17) != this._newBBXp) {
                  if (this._state == 9) {
                     this._state = 0;
                  } else {
                     this._state = 8;
                  }

                  this._attemptCount = 0;
                  if (this.isDynamicModeChangeSupported()) {
                     boolean var11 = false /* VF: Semaphore variable */;

                     try {
                        var11 = true;
                        RadioInternal.setBlackBerryExperienceMode(this._newBBXp);
                        ProvisioningService.log("BBXp: new mode=" + Integer.toHexString(RadioInternal.getBlackBerryExperienceMode()), 0);
                        var11 = false;
                     } finally {
                        if (var11) {
                           ProvisioningService.log("BBXp bailing due to non-support on platform (4)", 0);
                           return;
                        }
                     }
                  } else {
                     this._app.invokeLater(this);
                  }
               } else {
                  ProvisioningService.log("BBXp: cur mode=" + Integer.toHexString(curBBXp), 5);
               }
            }
         }
      }
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
   }

   @Override
   public final void packetSent(int packetId, int networkId) {
      this.radioActivity();
   }

   @Override
   public final void packetNotSent(int packetId, int error) {
   }

   @Override
   public final void packetStatus(int packetId, int status) {
   }

   @Override
   public final void packetDelivered(int networkId, int status, int messageID) {
   }

   @Override
   public final void packetReceived(ICMPPacketHeader header, byte[] data) {
   }

   @Override
   public final void packetReceived(TCPPacketHeader header, byte[] data) {
   }

   @Override
   public final void packetReceived(CBPacketHeader header, byte[] data) {
   }

   @Override
   public final void packetReceived(SMSPacketHeader header, byte[] data) {
   }

   @Override
   public final void packetReceived(UDPPacketHeader header, byte[] data) {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void fastReset() {
      try {
         ProvisioningService provisioningService = ProvisioningService.getInstance();
         ServiceRecord provSR = provisioningService.getServiceRecord();
         if (provSR != null && provSR != null && provSR.getType() == 0) {
            provisioningService.updateHandler(this);
            return;
         }
      } catch (Throwable var4) {
         ProvisioningService.log("BBXp fast reset exception: " + e, 2);
         return;
      }
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerOffRequested(int reason) {
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void backlightStateChange(boolean on) {
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }

   private final void scheduleInvokeLater(long timeout) {
      this.unscheduleInvokeLater();
      this._invokeLaterId = this._app.invokeLater(this, timeout, false);
   }

   private final void failedAttempt(int retryState) {
      if (++this._attemptCount < 5) {
         this._state = retryState;
      } else {
         ProvisioningService.log("BBXp_state: ERROR Max Attempts", 2);
         this._state = 7;
      }
   }

   public BBXpHandler(Application app) {
      this._app = app;
      this._invokeLaterId = -1;
      this._state = 9;
      ProtocolDaemon pd = ProtocolDaemon.getInstance();
      if (!this.isDynamicModeChangeSupported()) {
         pd.addRadioListener(this);
      } else {
         pd.addSystemListener(this);
      }
   }

   private final boolean isDynamicModeChangeSupported() {
      return (InternalServices.getOSAPIVersion() & -65536) >= 1048576;
   }

   private final synchronized void radioActivity() {
      try {
         if (this._state == 1) {
            this.scheduleInvokeLater(60000);
            return;
         }
      } finally {
         ;
      }
   }

   private final void handleReallyBadError() {
      if (this._radioOnAtStart) {
         Radio.requestPowerOn();
      }

      this._state = 9;
      this.unscheduleInvokeLater();
   }

   private final int doStart(boolean setLeaveRadioOff) {
      int newState;
      switch (RadioInfo.getState()) {
         case 0:
            newState = 5;
            if (setLeaveRadioOff) {
               this._leaveRadioOff = true;
               return newState;
            }
            break;
         case 6:
            newState = 4;
            if (setLeaveRadioOff) {
               this._leaveRadioOff = true;
               return newState;
            }
            break;
         default:
            newState = 3;
      }

      return newState;
   }

   private final void unscheduleInvokeLater() {
      if (this._invokeLaterId != -1) {
         this._app.cancelInvokeLater(this._invokeLaterId);
         this._invokeLaterId = -1;
      }
   }
}
