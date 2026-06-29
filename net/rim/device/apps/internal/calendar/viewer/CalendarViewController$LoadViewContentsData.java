package net.rim.device.apps.internal.calendar.viewer;

final class CalendarViewController$LoadViewContentsData {
   long _time;
   byte _timeToUseFlag;
   Object _object;
   byte _objectToUseFlag;
   boolean _updateSelectedDate;
   boolean _reposition;
   boolean _preserveSelectedTime;
   byte _loadType;

   CalendarViewController$LoadViewContentsData(
      long time,
      byte timeToUseFlag,
      Object object,
      byte objectToUseFlag,
      boolean updateSelectedDate,
      boolean reposition,
      boolean preserveSelectedTime,
      byte loadType
   ) {
      this._time = time;
      this._timeToUseFlag = timeToUseFlag;
      this._object = object;
      this._objectToUseFlag = objectToUseFlag;
      this._updateSelectedDate = updateSelectedDate;
      this._reposition = reposition;
      this._preserveSelectedTime = preserveSelectedTime;
      this._loadType = loadType;
   }
}
