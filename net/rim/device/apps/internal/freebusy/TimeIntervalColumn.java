package net.rim.device.apps.internal.freebusy;

class TimeIntervalColumn {
   private int _hour;
   private long _date;
   private boolean _hasFocus;
   private TimeIntervalData[] _data;

   TimeIntervalColumn(long date, int hour, int numAttendees) {
      this._date = date;
      this._hour = hour;
      this._data = new TimeIntervalData[numAttendees + 1];
      this.populateTestData();
   }

   private void populateTestData() {
      int[] subIntervalData = new int[]{
         1, 2, 3, 4, -805044223, 2, -804651007, 51, 527827200, 16810638, 134228598, 455608320, 134219553, -1183554751, 996693093, 1091043429
      };
      if (this._data.length > 1) {
         this._data[1] = new TimeIntervalData(subIntervalData);
      }

      int maxValue = 4;

      for (int i = 1; i < this._data.length - 1; i++) {
         this._data[i + 1] = new TimeIntervalData(i);
         if (i > maxValue) {
            maxValue = i;
         }
      }

      this._data[0] = new TimeIntervalData(maxValue);
   }

   public long getDate() {
      return this._date;
   }

   public int getHour() {
      return this._hour;
   }

   public boolean isFocus() {
      return this._hasFocus;
   }

   public void setFocus(boolean hasFocus) {
      this._hasFocus = hasFocus;
   }

   public TimeIntervalData getDataAtAttendee(int attendee) {
      return this._data[attendee];
   }

   public void setDataAtAttendee(int attendee, TimeIntervalData data) {
      this._data[attendee] = data;
   }

   public TimeIntervalData[] getData() {
      return this._data;
   }

   public void setData(TimeIntervalData[] data) {
      this._data = data;
   }

   public String getDisplayHour() {
      boolean am = false;
      if (this._hour < 12) {
         am = true;
      } else {
         am = false;
      }

      int hour = this._hour;
      if (this._hour > 11) {
         hour -= 12;
      }

      String hourString = Integer.toString(hour);
      if (hourString.equals("0")) {
         hourString = "12";
      }

      return am ? hourString + " AM" : hourString + " PM";
   }
}
