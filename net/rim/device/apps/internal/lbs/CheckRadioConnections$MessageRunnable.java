package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.internal.system.DataServices;

final class CheckRadioConnections$MessageRunnable implements Runnable {
   private String _msg;
   private Object _lock;
   private int _msgType;
   private final CheckRadioConnections this$0;
   static final int MESSAGE_ALERT = 0;
   static final int MESSAGE_RADIO = 1;
   static final int MESSAGE_DATASERVICES = 2;

   public CheckRadioConnections$MessageRunnable(CheckRadioConnections this$0, String msg, int msgType) {
      this.this$0 = this$0;
      this._lock = new Object();
      this._msg = msg;
      this._msgType = msgType;
   }

   @Override
   public final void run() {
      synchronized (Application.getEventLock()) {
         switch (this._msgType) {
            case -1:
               break;
            case 0:
            default:
               Dialog.alert(this._msg);
               break;
            case 1:
               if (Dialog.ask(3, this._msg, 4) == 4) {
                  if (WLAN.isWLANAllowed()) {
                     new HelpDialog().doModal();
                  } else {
                     Radio.requestPowerOn();

                     while (RadioInfo.getState() == 5) {
                        synchronized (this._lock) {
                           try {
                              this._lock.wait(1000);
                           } finally {
                              continue;
                           }
                        }
                     }
                  }

                  if (RadioInfo.getActiveWAFs() != 0) {
                     this.this$0._radioTurnedOn = true;
                     this.this$0._dataServicesEnabled = true;
                     CheckRadioConnections._mapReqRadioChecked = false;
                  }
               }
               break;
            case 2:
               if (Dialog.ask(3, this._msg, 4) == 4) {
                  DataServices.getInstance().setMode(1);
               }

               if (DataServices.getInstance().isDataServicesEnabled()) {
                  this.this$0._dataServicesEnabled = true;
                  this.this$0._radioTurnedOn = true;
                  CheckRadioConnections._mapReqRadioChecked = false;
               }
         }

         this.this$0._callback.radioChecked(this.this$0._radioTurnedOn, this.this$0._dataServicesEnabled, this.this$0._outOfCoverage);
      }
   }
}
