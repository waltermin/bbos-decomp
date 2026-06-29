package net.rim.device.cldc.io.datarecovery;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.internal.proxy.Proxy;

public class DataRecovery implements GlobalEventListener {
   protected final int _linkType;
   protected final long _guid;
   protected int _errorLevel;
   protected long _lastRecoveryTime;
   protected int _currentRecoveryBackoff;
   protected boolean _primed;
   protected Object[] _listeners;
   public static final long GUID;
   public static final long GUID_WIFI;
   public static final int LINK_TYPE_CELLULAR;
   public static final int LINK_TYPE_WIFI;
   public static final int CONNECTION_TYPE_RELAY;
   public static final int CONNECTION_TYPE_ROUTER;
   public static final int CONNECTION_TYPE_UNC;
   public static final int CONNECTION_TYPE_VPN;
   public static final int REPORT_ACTIVITY;
   public static final int REPORT_NO_RESPONSE;
   public static final int REPORT_UNREACHABLE;
   public static final int REPORT_TX_FLOW_CTRL;
   protected static final int THRESHOLD_RECOVERY;
   protected static final int THRESHOLD_RELAY_UNREACHABLE;
   protected static final int START_RECOVERY_BACKOFF;
   protected static final int MAX_RECOVERY_BACKOFF;

   public synchronized void addListener(DataRecoveryListener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   public synchronized void removeListener(DataRecoveryListener listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   public void fileReport(int report) {
      this.fileReport(report, 1, 1);
   }

   public void fileReport(int report, int linkType, int connectionType) {
      if (linkType == this._linkType) {
         int event;
         Object[] listeners;
         synchronized (this) {
            switch (report) {
               case -1:
                  return;
               case 0:
               default:
                  if (this._currentRecoveryBackoff > 3600000) {
                     this._currentRecoveryBackoff >>= 1;
                  }

                  if (this._errorLevel >= 4) {
                     this._errorLevel = 0;
                     event = 1;
                  } else {
                     this._errorLevel = 0;
                     if (this._primed) {
                        return;
                     }

                     this._primed = true;
                     event = 1;
                  }
                  break;
               case 1:
                  this._errorLevel++;
                  if (this._errorLevel >= 326) {
                     event = 3;
                     EventLogger.logEvent(this._guid, 1382831470, 3);
                  } else {
                     if (this._errorLevel < 4) {
                        return;
                     }

                     if (Phone.isSupported() && Phone.getInstance().isActive()) {
                        EventLogger.logEvent(this._guid, 1383298419, 3);
                        return;
                     }

                     long now = System.currentTimeMillis();
                     if (this._currentRecoveryBackoff < 3600000) {
                        this._lastRecoveryTime = now;
                        this._currentRecoveryBackoff = 3600000;
                        event = 2;
                     } else {
                        if (now - this._lastRecoveryTime < this._currentRecoveryBackoff) {
                           return;
                        }

                        this._lastRecoveryTime = now;
                        this._currentRecoveryBackoff <<= 1;
                        if (this._currentRecoveryBackoff > 115200000) {
                           this._currentRecoveryBackoff = 115200000;
                        }

                        event = 2;
                     }

                     EventLogger.logEvent(this._guid, 1382248310, 3);
                  }
            }

            listeners = this._listeners;
         }

         this.informListeners(listeners, event, linkType);
      }
   }

   public synchronized boolean isConnectionAvailable() {
      return this._errorLevel < 4;
   }

   protected void informListeners(Object[] listeners, int event, int linkType) {
      if (listeners != null) {
         Proxy.getInstance().invokeRunnable(new DataRecoveryRunnable(listeners, event, linkType));
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8877632280522743328L) {
         synchronized (this) {
            if (this._lastRecoveryTime != 0) {
               this._lastRecoveryTime = System.currentTimeMillis() - this._currentRecoveryBackoff / 2;
            }
         }
      }
   }

   public static DataRecovery getInstance(int linkType) {
      long guid = 0;
      switch (linkType) {
         case 2:
            guid = 1916912427746348095L;
            break;
         default:
            guid = -8817198204729214607L;
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      DataRecovery dataRecovery = (DataRecovery)ar.getOrWaitFor(guid);
      if (dataRecovery == null) {
         try {
            switch (linkType) {
               case 2:
                  dataRecovery = new DataRecoveryWLAN(2, guid);
                  break;
               default:
                  dataRecovery = new DataRecovery(1, guid);
            }
         } catch (Exception var6) {
         }

         if (dataRecovery != null) {
            ar.put(guid, dataRecovery);
         }
      }

      return dataRecovery;
   }

   public static DataRecovery getInstance() {
      return getInstance(1);
   }

   protected DataRecovery(int linkType, long guid) {
      this._linkType = linkType;
      this._guid = guid;
      switch (this._linkType) {
         case 2:
            EventLogger.register(this._guid, "net.rim.recovery.wlan", 2);
            break;
         default:
            EventLogger.register(this._guid, "net.rim.recovery", 2);
      }

      Proxy.getInstance().addGlobalEventListener(this);
   }
}
