package net.rim.device.apps.internal.ribbon.indicators;

import net.rim.device.apps.api.ribbon.indicators.Indicator;

class IndicatorDisplay {
   Indicator _indicator;
   int _area;

   public IndicatorDisplay(Indicator indicator) {
      this._indicator = indicator;
      this._area = 0;
   }
}
