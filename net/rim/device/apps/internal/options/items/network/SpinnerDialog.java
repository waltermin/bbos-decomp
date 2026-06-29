package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ExtendedRadioStatusListener;
import net.rim.device.api.system.ModemListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.RadioInternal;

final class SpinnerDialog extends Dialog implements Runnable, RadioStatusListener, ModemListener, ExtendedRadioStatusListener {
   private SpinnerDialog$SpinnerField _spinner;
   private int _ilId;
   private int _timeoutCount;
   private int _retryTimeout;
   private int _retryCount;
   private int _newNetworkIndex;
   private int _newNetworkId;
   private int _mode;
   private boolean _paused;
   private int _myReturnValue = 0;
   private int TICK_IN_MILLIS = 500;
   private int RETRY_TIMEOUT = 4;
   public static final int MODE_SCAN = 1;
   public static final int MODE_MANUAL_SELECT = 2;
   public static final int OK = 1;
   public static final int NONE = 0;
   public static final int TIMEOUT = -1000;
   public static final int FAILED = -1001;
   private static final int SELECT_RETRY_TIMEOUT = 4;
   private static final int INIT_MANUAL_SELECT_RETRY_COUNT = 5;
   private static final int INIT_SCAN_RETRY_COUNT = 0;

   public SpinnerDialog(int mode, int netIndex, int netId) {
      super(getString(mode, false), null, null, -1, null);
      this._mode = mode;
      this._newNetworkIndex = netIndex;
      this._newNetworkId = netId;
      this._retryCount = getRetryCount(mode);
   }

   private static final String getString(int mode, boolean paused) {
      int index;
      switch (getCurrentMode(mode)) {
         case 0:
            return null;
         case 1:
         default:
            if (paused) {
               index = 1843;
            } else {
               index = 916;
            }
            break;
         case 2:
            index = 1474;
      }

      return OptionsResources.getString(index);
   }

   private static final int getRetryCount(int mode) {
      switch (getCurrentMode(mode)) {
         case 0:
            return 0;
         case 1:
         default:
            return 0;
         case 2:
            return 5;
      }
   }

   private static final int getCurrentMode(int mode) {
      int mask;
      for (mask = 1; mask != 0; mask <<= 1) {
         if ((mode & mask) != 0) {
            return mask;
         }
      }

      return mask;
   }

   private final int getTimeoutInMilliseconds() {
      int mode = getCurrentMode(this._mode);
      if (this._paused) {
         return Integer.MAX_VALUE;
      }

      int base = 180;
      if (mode == 2) {
         base += this._retryCount * this.RETRY_TIMEOUT;
      }

      return base * this.TICK_IN_MILLIS;
   }

   private final synchronized void doneMode(int mode) {
      this._mode &= ~mode;
      if (this._mode == 0) {
         this.select(1);
      } else {
         this.getLabel().setText(getString(this._mode, this._paused));
      }
   }

   private final void pause() {
      this._paused = true;
      this.getLabel().setText(getString(this._mode, this._paused));
   }

   private final void unPause() {
      this._paused = false;
      this.getLabel().setText(getString(this._mode, this._paused));
      this._timeoutCount = 0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final int go() {
      boolean showDialog = false;
      int curMode = getCurrentMode(this._mode);
      Application.getApplication().addRadioListener(this);
      boolean var10 = false /* VF: Semaphore variable */;

      short var5;
      label205: {
         label204: {
            int i;
            label203: {
               try {
                  label201: {
                     var10 = true;
                     if (curMode == 1) {
                        RadioInternal.scanForNetworks();
                        showDialog = true;
                     } else if (curMode == 2) {
                        boolean var18 = false /* VF: Semaphore variable */;

                        try {
                           var18 = true;
                           showDialog = RadioInfo.getNetworkId(RadioInfo.getCurrentNetworkIndex()) != this._newNetworkId;
                           if (RadioInfo.getNetworkId(this._newNetworkIndex) != this._newNetworkId) {
                              int numNets = RadioInfo.getNumberOfNetworks();

                              for (i = 0; i < numNets; i++) {
                                 if (RadioInfo.getNetworkId(i) == this._newNetworkId) {
                                    this._newNetworkIndex = i;
                                    break;
                                 }
                              }

                              if (i >= numNets) {
                                 Dialog.alert(OptionsResources.getString(1426));
                                 var5 = -1001;
                                 var10 = false;
                                 var18 = false;
                                 break label205;
                              }

                              var18 = false;
                           } else {
                              var18 = false;
                           }
                        } finally {
                           if (var18) {
                              Dialog.alert(OptionsResources.getString(1426));
                              i = -1001;
                              var10 = false;
                              break label203;
                           }
                        }

                        if (!showDialog) {
                           this._myReturnValue = 1;
                        }

                        boolean var14 = false /* VF: Semaphore variable */;

                        try {
                           var14 = true;
                           RadioInternal.changeNetworks(this._newNetworkIndex);
                           var14 = false;
                        } finally {
                           if (var14) {
                              Dialog.alert(OptionsResources.getString(1426));
                              i = -1001;
                              var10 = false;
                              break label201;
                           }
                        }
                     }

                     if (showDialog) {
                        this._spinner = new SpinnerDialog$SpinnerField(8);
                        this.add(this._spinner);
                        this._timeoutCount = 0;
                        this._ilId = Application.getApplication().invokeLater(this, this.TICK_IN_MILLIS, true);
                        if (this.doModal() == -1) {
                           this._myReturnValue = -1;
                        }

                        this.stop();
                        var10 = false;
                     } else {
                        var10 = false;
                     }
                     break label204;
                  }
               } finally {
                  if (var10) {
                     Application.getApplication().removeRadioListener(this);
                  }
               }

               Application.getApplication().removeRadioListener(this);
               return i;
            }

            Application.getApplication().removeRadioListener(this);
            return i;
         }

         Application.getApplication().removeRadioListener(this);
         return this._myReturnValue;
      }

      Application.getApplication().removeRadioListener(this);
      return var5;
   }

   private final synchronized void stop() {
      if (this._ilId >= 0) {
         Application.getApplication().cancelInvokeLater(this._ilId);
      }

      this._ilId = -1;
      if (this._myReturnValue == -1 && getCurrentMode(this._mode) == 1 && RadioInfo.areWAFsSupported(1)) {
         RadioInternal.abortScanForNetworks();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized void run() {
      if (getCurrentMode(this._mode) == 2 && --this._retryTimeout == 0) {
         this._retryTimeout = Integer.MAX_VALUE;
         boolean var3 = false /* VF: Semaphore variable */;

         label33:
         try {
            var3 = true;
            RadioInternal.changeNetworks(this._newNetworkIndex);
            var3 = false;
         } finally {
            if (var3) {
               Dialog.alert(OptionsResources.getString(1426));
               this._myReturnValue = -1001;
               this.select(1);
               break label33;
            }
         }
      }

      if (this._ilId != -1) {
         this._spinner.advance(1);
         this._timeoutCount = this._timeoutCount + this.TICK_IN_MILLIS;
         if (this._timeoutCount >= this.getTimeoutInMilliseconds()) {
            this._myReturnValue = -1000;
            this.select(1);
         }
      }
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      int curMode = getCurrentMode(this._mode);
      switch (curMode) {
         case 2:
            this._myReturnValue = networkId == this._newNetworkId ? 1 : -1001;
            this.doneMode(curMode);
      }
   }

   @Override
   public final void networkScanComplete(boolean success) {
      if (getCurrentMode(this._mode) == 1) {
         this._myReturnValue = success ? 1 : -1001;
         this.doneMode(1);
      }
   }

   @Override
   public final void signalLevel(int level) {
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
   public final void networkStateChange(int status) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
   }

   @Override
   public final void networkSelectionModeChanged(int mode) {
      if (mode != 3) {
         this.select(1);
      }
   }

   @Override
   public final void queryNetworkDisplayName(int networkId) {
   }

   @Override
   public final void networkChangeResult(int parameter, int mode) {
   }

   @Override
   public final void networkSelectionFailed(int networkId, int cause) {
      int curMode = getCurrentMode(this._mode);
      switch (curMode) {
         case 2:
            if (networkId == this._newNetworkId) {
               if (cause == 2) {
                  if (--this._retryCount >= 0) {
                     this._retryTimeout = this.RETRY_TIMEOUT;
                     return;
                  }

                  this._myReturnValue = -1000;
                  this.doneMode(curMode);
                  return;
               } else if (!RadioInfo.areWAFsSupported(1)) {
                  Dialog.alert(OptionsResources.getString(1477));
                  this._myReturnValue = -1001;
                  this.doneMode(curMode);
               }
            }
      }
   }

   @Override
   public final void flowControlStatusChange(int status) {
   }

   @Override
   public final void networkScanStatus(int status) {
      if (getCurrentMode(this._mode) == 1) {
         if (status == 0) {
            this.pause();
            return;
         }

         if (status == 1) {
            this.unPause();
         }
      }
   }

   @Override
   public final void networkNameChangeViaNITZ(int longNameLength, int shortNameLength) {
   }
}
