package net.rim.device.internal.system;

import net.rim.device.api.system.ExtendedRadioStatusListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;

public class RadioStatusListenerFilter {
   private int _wafFilter;
   private RadioStatusListener _listener;

   public RadioStatusListenerFilter(int wafFilter, RadioStatusListener listener) {
      this._wafFilter = wafFilter;
      this._listener = listener;
   }

   public void dispatchEvent(int event, int subMessage, int data0, int data1) {
      switch (event) {
         case 1540:
            if ((data1 & this._wafFilter) != 0) {
               this._listener.signalLevel(subMessage);
            }

            return;
         case 1541:
            if ((data1 & this._wafFilter) != 0) {
               this._listener.networkStarted(RadioInfo.convertNetworkId(subMessage), data0);
            }

            return;
         case 1543:
            if ((data1 & this._wafFilter) != 0) {
               this._listener.radioTurnedOff();
            }

            return;
         case 1588:
            if ((data1 & this._wafFilter) != 0) {
               this._listener.networkServiceChange(RadioInfo.convertNetworkId(subMessage), data0);
            }

            return;
         default:
            if (this._wafFilter != 4) {
               switch (event) {
                  case 1542:
                     this._listener.baseStationChange();
                     return;
                  case 1545:
                     this._listener.pdpStateChange(subMessage, data0 == 0 ? 1 : 0, 0);
                     return;
                  case 1547:
                     this._listener.pdpStateChange(subMessage, 2, data0);
                     return;
                  case 1552:
                     this._listener.pdpStateChange(subMessage, 3, 0);
                     return;
                  case 1584:
                     this._listener.networkScanComplete(true);
                     return;
                  case 1586:
                     this._listener.networkStateChange(subMessage);
                     return;
                  case 1590:
                     this._listener.networkScanComplete(false);
                     return;
                  default:
                     if (this._listener instanceof ExtendedRadioStatusListener) {
                        ExtendedRadioStatusListener elistener = (ExtendedRadioStatusListener)this._listener;
                        switch (event) {
                           case 1591:
                              elistener.networkSelectionFailed(RadioInfo.convertNetworkId(subMessage), data0);
                              return;
                           case 1592:
                              elistener.networkScanStatus(data0);
                              return;
                           case 1648:
                              elistener.flowControlStatusChange(data0);
                              return;
                           case 1682:
                              elistener.networkNameChangeViaNITZ(data0, data1);
                        }
                     }
               }
            }
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof RadioStatusListenerFilter)) {
         return false;
      }

      RadioStatusListenerFilter f = (RadioStatusListenerFilter)obj;
      return f._listener == this._listener;
   }

   @Override
   public int hashCode() {
      return this._listener.hashCode();
   }
}
