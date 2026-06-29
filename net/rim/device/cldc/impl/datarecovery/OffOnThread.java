package net.rim.device.cldc.impl.datarecovery;

import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.datarecovery.DataRecovery;
import net.rim.device.cldc.io.datarecovery.DataRecoveryListener;
import net.rim.device.internal.system.RadioInternal;

public final class OffOnThread extends Thread implements DataRecoveryListener, OffOnEvent {
   private int _mode;
   private int _activeRadios;
   private Object _lock = new Object();
   private int _waitTmo;
   private int _waitState;
   private int _nextAction;
   public static final int CYCLE_RADIO = 0;
   public static final int CYCLE_ATTACH = 1;
   private static final int OFF_TMO = 60000;
   private static final int ON_TMO = 30000;
   private static final int WAIT_NONE = 0;
   private static final int WAIT_OFF = 1;
   private static final int WAIT_ON = 2;
   private static final int ACTION_NONE = 0;
   private static final int ACTION_OFF = 1;
   private static final int ACTION_ON = 2;

   OffOnThread(int mode) {
      this._mode = mode;
      this._activeRadios = RadioInternal.getSupportedRadios() & 11;
      EventLogger.register(-2640923519649933485L, "net.rim.offon", 2);
      EventLogger.logEvent(-2640923519649933485L, 1231972724, 0);
      ProtocolDaemon.getInstance().startThread(this);
   }

   @Override
   public final void dataRecoveryEventOccurred(int event, int linkType) {
      if (event == 2) {
         EventLogger.logEvent(-2640923519649933485L, 1380151808 | 48 + event, 3);
         synchronized (this._lock) {
            if (this._waitState == 0) {
               this._nextAction = 1;
               this._lock.notify();
            }
         }
      }
   }

   @Override
   public final void run() {
      DataRecovery.getInstance().addListener(this);
      synchronized (this._lock) {
         while (true) {
            try {
               switch (this._nextAction) {
                  case 0:
                     this.actionSleep();
                     break;
                  case 1:
                  default:
                     this.actionOff();
                     break;
                  case 2:
                     this.actionOn();
               }

               this._lock.wait(this._waitTmo);
            } finally {
               continue;
            }
         }
      }
   }

   private final void actionOff() {
      switch (this._mode) {
         case -1:
            break;
         case 0:
         default:
            EventLogger.logEvent(-2640923519649933485L, 1380020070, 3);
            RIMGlobalMessagePoster.postGlobalEvent(-2816799803471967993L, 0, 0, "Radio Off", "Source: datarecovery");
            int activeRadios = RadioInternal.getActiveRadios();
            if ((activeRadios & 4) != 0) {
               ServiceRouting sr = ServiceRouting.getInstance();
               if (sr != null && sr.isRouteActive(3) || (RadioInfo.getNetworkService() & 16384) == 0) {
                  activeRadios &= -5;
               }
            }

            this._activeRadios = activeRadios;
            RadioInternal.deactivateRadios(this._activeRadios);
            break;
         case 1:
            EventLogger.logEvent(-2640923519649933485L, 1145136229, 3);
            RadioInternal.gprsAttach(false);
      }

      this._waitTmo = 60000;
      this._waitState = 1;
      this._nextAction = 2;
   }

   private final void actionOn() {
      switch (this._mode) {
         case -1:
            break;
         case 0:
         default:
            EventLogger.logEvent(-2640923519649933485L, 1380020078, 3);
            RadioInternal.activateRadios(this._activeRadios);
            break;
         case 1:
            EventLogger.logEvent(-2640923519649933485L, 1145135476, 3);
            RadioInternal.gprsAttach(true);
      }

      this._waitTmo = 30000;
      this._waitState = 2;
      this._nextAction = 0;
   }

   private final void actionSleep() {
      this._waitTmo = 0;
      this._waitState = 0;
      this._nextAction = 0;
   }
}
