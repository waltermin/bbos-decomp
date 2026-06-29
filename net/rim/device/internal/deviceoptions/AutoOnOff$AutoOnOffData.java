package net.rim.device.internal.deviceoptions;

import net.rim.vm.Persistable;

final class AutoOnOff$AutoOnOffData implements Persistable {
   public int _weekdayOn;
   public int _weekdayOff;
   public boolean _weekdayAutoOnOffEnabled;
   public int _weekendOn;
   public int _weekendOff;
   public boolean _weekendAutoOnOffEnabled;

   public AutoOnOff$AutoOnOffData() {
      this.reset();
   }

   public final void reset() {
      this._weekdayOn = 25200000;
      this._weekdayOff = 82800000;
      this._weekdayAutoOnOffEnabled = false;
      this._weekendOn = 25200000;
      this._weekendOff = 82800000;
      this._weekendAutoOnOffEnabled = false;
   }
}
