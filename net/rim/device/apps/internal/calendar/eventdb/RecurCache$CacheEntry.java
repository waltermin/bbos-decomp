package net.rim.device.apps.internal.calendar.eventdb;

import net.rim.device.apps.api.framework.model.RIMModel;

final class RecurCache$CacheEntry {
   String _tzid;
   boolean _moreAvailableEarlier;
   boolean _moreAvailableLater;
   RIMModel[] _occurrences = new Object[14];
   long[] _startTimes = new long[14];
   long[] _endTimes = new long[14];
   int _numOccurrences;
   static final int MAX_EXPAND;
}
