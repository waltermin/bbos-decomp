package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.timesync.TimeSync;

final class DateTimeOptionsItem$UpdateTimeVerb extends Verb {
   TimeSync _timeSync;
   public boolean _updateTimeRequested = false;

   public DateTimeOptionsItem$UpdateTimeVerb(TimeSync timeSync) {
      super(1115488);
      this._timeSync = timeSync;
   }

   @Override
   public final String toString() {
      return OptionsResources.getString(1447);
   }

   @Override
   public final Object invoke(Object parameter) {
      this._timeSync.synchronize(true);
      this._updateTimeRequested = true;
      return null;
   }
}
