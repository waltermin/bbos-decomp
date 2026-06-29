package net.rim.device.apps.api.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class TimeChoiceField extends ObjectChoiceField {
   private TimeChoiceField$TimeChoice[] _timeChoices;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(2545338480386147321L, "net.rim.device.apps.internal.resource.Ui");
   public static final long TIME_NONE = -1L;
   public static final long TIME_ZERO_MINUTES = 0L;
   public static final long TIME_ONE_SECOND = 1000L;
   public static final long TIME_ONE_MINUTE = 60000L;
   public static final long TIME_ONE_HOUR = 3600000L;
   public static final long TIME_ONE_DAY = 86400000L;
   public static final long TIME_ONE_WEEK = 604800000L;
   public static final long TIME_ONE_MONTH = 2592000000L;
   public static final long TIME_ONE_YEAR = 31536000000L;
   public static final long TIME_NEVER = Long.MAX_VALUE;
   private static final int NONE_INDEX = 0;
   private static final int ZERO_MINUTES_INDEX = 1;
   private static final int ONE_MINUTE_INDEX = 2;
   private static final int TWO_MINUTES_INDEX = 3;
   private static final int FIVE_MINUTES_INDEX = 4;
   private static final int TEN_MINUTES_INDEX = 5;
   private static final int FIFTEEN_MINUTES_INDEX = 6;
   private static final int TWENTY_MINUTES_INDEX = 7;
   private static final int THIRTY_MINUTES_INDEX = 8;
   private static final int FOURTY_FIVE_MINUTES_INDEX = 9;
   private static final int ONE_HOUR_INDEX = 10;
   private static final int TWO_HOURS_INDEX = 11;
   private static final int THREE_HOURS_INDEX = 12;
   private static final int FOUR_HOURS_INDEX = 13;
   private static final int FIVE_HOURS_INDEX = 14;
   private static final int SIX_HOURS_INDEX = 15;
   private static final int SEVEN_HOURS_INDEX = 16;
   private static final int EIGHT_HOURS_INDEX = 17;
   private static final int NINE_HOURS_INDEX = 18;
   private static final int TEN_HOURS_INDEX = 19;
   private static final int ELEVEN_HOURS_INDEX = 20;
   private static final int TWELVE_HOURS_INDEX = 21;
   private static final int ONE_DAY_INDEX = 22;
   private static final int TWO_DAYS_INDEX = 23;
   private static final int THREE_DAYS_INDEX = 24;
   private static final int FOUR_DAYS_INDEX = 25;
   private static final int FIVE_DAYS_INDEX = 26;
   private static final int SIX_DAYS_INDEX = 27;
   private static final int ONE_WEEK_INDEX = 28;
   private static final int TWO_WEEKS_INDEX = 29;
   private static final int THREE_WEEKS_INDEX = 30;
   private static final int FOUR_WEEKS_INDEX = 31;
   private static final int ONE_MONTH_INDEX = 32;
   private static final int TWO_MONTHS_INDEX = 33;
   private static final int THREE_MONTHS_INDEX = 34;
   private static final int FOUR_MONTHS_INDEX = 35;
   private static final int FIVE_MONTHS_INDEX = 36;
   private static final int SIX_MONTHS_INDEX = 37;
   private static final int SEVEN_MONTHS_INDEX = 38;
   private static final int EIGHT_MONTHS_INDEX = 39;
   private static final int NINE_MONTHS_INDEX = 40;
   private static final int TEN_MONTHS_INDEX = 41;
   private static final int ELEVEN_MONTHS_INDEX = 42;
   private static final int ONE_YEAR_INDEX = 43;
   private static final int NEVER_INDEX = 44;
   private static final int NONE = -1;
   private static final int ZERO_MINUTES = 0;
   private static final int ONE_MINUTE = 60000;
   private static final int TWO_MINUTES = 120000;
   private static final int FIVE_MINUTES = 300000;
   private static final int TEN_MINUTES = 600000;
   private static final int FIFTEEN_MINUTES = 900000;
   private static final int TWENTY_MINUTES = 1200000;
   private static final int THIRTY_MINUTES = 1800000;
   private static final int FOURTY_FIVE_MINUTES = 2700000;
   private static final int ONE_HOUR = 3600000;
   private static final int TWO_HOURS = 7200000;
   private static final int THREE_HOURS = 10800000;
   private static final int FOUR_HOURS = 14400000;
   private static final int FIVE_HOURS = 18000000;
   private static final int SIX_HOURS = 21600000;
   private static final int SEVEN_HOURS = 25200000;
   private static final int EIGHT_HOURS = 28800000;
   private static final int NINE_HOURS = 32400000;
   private static final int TEN_HOURS = 36000000;
   private static final int ELEVEN_HOURS = 39600000;
   private static final int TWELVE_HOURS = 43200000;
   private static final int ONE_DAY = 86400000;
   private static final int TWO_DAYS = 172800000;
   private static final int THREE_DAYS = 259200000;
   private static final int FOUR_DAYS = 345600000;
   private static final int FIVE_DAYS = 432000000;
   private static final int SIX_DAYS = 518400000;
   private static final int ONE_WEEK = 604800000;
   private static final int TWO_WEEKS = 1209600000;
   private static final int THREE_WEEKS = 1814400000;
   private static final long FOUR_WEEKS = 2419200000L;
   private static final long ONE_MONTH = 2592000000L;
   private static final long TWO_MONTHS = 5184000000L;
   private static final long THREE_MONTHS = 7776000000L;
   private static final long FOUR_MONTHS = 10368000000L;
   private static final long FIVE_MONTHS = 12960000000L;
   private static final long SIX_MONTHS = 15552000000L;
   private static final long SEVEN_MONTHS = 18144000000L;
   private static final long EIGHT_MONTHS = 20736000000L;
   private static final long NINE_MONTHS = 23328000000L;
   private static final long TEN_MONTHS = 25920000000L;
   private static final long ELEVEN_MONTHS = 28512000000L;
   private static final long ONE_YEAR = 31536000000L;
   private static WeakReference _sbWR = new WeakReference(null);

   public TimeChoiceField(String label, long style) {
      super(label, null, 0, style | 134217728);
   }

   public TimeChoiceField(String label, long[] timeChoices) {
      this(label, 0);
      if (timeChoices != null) {
         this.setTimeChoices(timeChoices);
      }
   }

   public TimeChoiceField(String label, long[] timeChoices, long selectedTime) {
      this(label, timeChoices);
      this.setSelectedTimeInMillis(selectedTime);
   }

   public TimeChoiceField(String label, long[] timeChoices, long selectedTime, boolean roundUp) {
      this(label, timeChoices);
      this.setSelectedTimeInMillis(selectedTime, roundUp);
   }

   public TimeChoiceField(String label, long[] timeChoices, long minTimeChoice, long maxTimeChoice, long selectedTime, boolean roundUp) {
      this(label, null);
      this.setTimeChoices(timeChoices, minTimeChoice, maxTimeChoice);
      this.setSelectedTimeInMillis(selectedTime, roundUp);
   }

   public void setTimeChoices(long[] timeChoices) {
      this.setTimeChoices(timeChoices, Long.MIN_VALUE, Long.MAX_VALUE);
   }

   public void setTimeChoices(long[] timeChoices, long minTimeChoice, long maxTimeChoice) {
      if (timeChoices == null) {
         throw new IllegalArgumentException();
      }

      int numTimeChoices = timeChoices.length;
      this._timeChoices = new TimeChoiceField$TimeChoice[numTimeChoices];
      int numAvailableTimeChoices = 0;
      int zeroMinIndex = -1;

      for (int i = 0; i < numTimeChoices; i++) {
         long timeChoice = timeChoices[i];
         if (timeChoice >= minTimeChoice && timeChoice <= maxTimeChoice) {
            if (timeChoice == 0) {
               zeroMinIndex = numAvailableTimeChoices;
            }

            this._timeChoices[numAvailableTimeChoices++] = new TimeChoiceField$TimeChoice(this.getTimeChoiceString(timeChoice), timeChoice);
         }
      }

      if (zeroMinIndex >= 0 && zeroMinIndex < numAvailableTimeChoices - 1) {
         String unit = this.getUnit(this._timeChoices[zeroMinIndex + 1].toString());
         if (unit != null) {
            this._timeChoices[zeroMinIndex] = new TimeChoiceField$TimeChoice("0" + unit, 0);
         }
      }

      if (numAvailableTimeChoices == 0) {
         throw new IllegalArgumentException();
      }

      Array.resize(this._timeChoices, numAvailableTimeChoices);
      this.setChoices(this._timeChoices);
   }

   public void setTimeChoicesIncludeMaxTimeChoice(long[] timeChoices, long minTimeChoice, long maxTimeChoice) {
      if (timeChoices == null) {
         throw new IllegalArgumentException();
      }

      int numTimeChoices = timeChoices.length;

      for (int i = numTimeChoices - 1; i >= 0; i--) {
         if (timeChoices[i] == maxTimeChoice) {
            this.setTimeChoices(timeChoices, minTimeChoice, maxTimeChoice);
            return;
         }
      }

      Arrays.add(timeChoices, maxTimeChoice);
      this.setTimeChoices(timeChoices, minTimeChoice, maxTimeChoice);
   }

   private String getUnit(String string) {
      int index = string.indexOf(32);
      return index >= 0 && index <= string.length() ? string.substring(index) : null;
   }

   private String getTimeChoiceString(long timeChoice) {
      int choiceStringIndex = -1;
      int intTimeChoice = (int)timeChoice;
      if (intTimeChoice == timeChoice) {
         choiceStringIndex = this.findKnownTimeChoiceStringFromInt(intTimeChoice);
      } else {
         choiceStringIndex = this.findKnownTimeChoiceStringFromLong(timeChoice);
      }

      return choiceStringIndex != -1 ? _rb.getStringArray(201)[choiceStringIndex] : this.buildTimeChoiceString(timeChoice);
   }

   private int findKnownTimeChoiceStringFromInt(int timeChoice) {
      int choiceStringIndex = -1;
      switch (timeChoice) {
         case -1:
            return 0;
         case 0:
            return 1;
         case 60000:
            return 2;
         case 120000:
            return 3;
         case 300000:
            return 4;
         case 600000:
            return 5;
         case 900000:
            return 6;
         case 1200000:
            return 7;
         case 1800000:
            return 8;
         case 2700000:
            return 9;
         case 3600000:
            return 10;
         case 7200000:
            return 11;
         case 10800000:
            return 12;
         case 14400000:
            return 13;
         case 18000000:
            return 14;
         case 21600000:
            return 15;
         case 25200000:
            return 16;
         case 28800000:
            return 17;
         case 32400000:
            return 18;
         case 36000000:
            return 19;
         case 39600000:
            return 20;
         case 43200000:
            return 21;
         case 86400000:
            return 22;
         case 172800000:
            return 23;
         case 259200000:
            return 24;
         case 345600000:
            return 25;
         case 432000000:
            return 26;
         case 518400000:
            return 27;
         case 604800000:
            return 28;
         case 1209600000:
            return 29;
         case 1814400000:
            return 30;
         default:
            return -1;
      }
   }

   private int findKnownTimeChoiceStringFromLong(long timeChoice) {
      int choiceStringIndex = -1;
      if (timeChoice == 2419200000L) {
         return 31;
      }

      if (timeChoice == 2592000000L) {
         return 32;
      }

      if (timeChoice == 5184000000L) {
         return 33;
      }

      if (timeChoice == 7776000000L) {
         return 34;
      }

      if (timeChoice == 10368000000L) {
         return 35;
      }

      if (timeChoice == 12960000000L) {
         return 36;
      }

      if (timeChoice == 15552000000L) {
         return 37;
      }

      if (timeChoice == 18144000000L) {
         return 38;
      }

      if (timeChoice == 20736000000L) {
         return 39;
      }

      if (timeChoice == 23328000000L) {
         return 40;
      }

      if (timeChoice == 25920000000L) {
         return 41;
      }

      if (timeChoice == 28512000000L) {
         return 42;
      }

      if (timeChoice == 31536000000L) {
         return 43;
      }

      if (timeChoice == Long.MAX_VALUE) {
         choiceStringIndex = 44;
      }

      return choiceStringIndex;
   }

   private String buildTimeChoiceString(long timeChoice) {
      long numUnits = timeChoice / 604800000;
      if (numUnits * 604800000 == timeChoice) {
         return this.buildTimeChoiceString(numUnits, _rb.getString(202));
      }

      numUnits = timeChoice / 86400000;
      if (numUnits * 86400000 == timeChoice) {
         return this.buildTimeChoiceString(numUnits, _rb.getString(203));
      }

      numUnits = timeChoice / 3600000;
      if (numUnits * 3600000 == timeChoice) {
         return this.buildTimeChoiceString(numUnits, _rb.getString(204));
      }

      numUnits = timeChoice / 60000;
      if (numUnits * 60000 == timeChoice) {
         return this.buildTimeChoiceString(numUnits, _rb.getString(205));
      }

      numUnits = timeChoice / 1000;
      return numUnits * 1000 == timeChoice
         ? this.buildTimeChoiceString(numUnits, _rb.getString(206))
         : this.buildTimeChoiceString(timeChoice, _rb.getString(207));
   }

   private synchronized String buildTimeChoiceString(long numUnits, String unitString) {
      StringBuffer sb = WeakReferenceUtilities.getStringBuffer(_sbWR);
      sb.setLength(0);
      sb.append(numUnits);
      sb.append(' ');
      sb.append(unitString);
      return sb.toString();
   }

   public long getSelectedTimeInMillis() {
      if (this._timeChoices != null) {
         int selectedIndex = this.getSelectedIndex();
         return selectedIndex >= 0 && selectedIndex < this._timeChoices.length ? this._timeChoices[selectedIndex].getTime() : -1;
      } else {
         return -1;
      }
   }

   public void setSelectedTimeInMillis(long selectedTime) {
      if (this._timeChoices != null) {
         TimeChoiceField$TimeChoice[] timeChoices = this._timeChoices;
         int numChoices = this._timeChoices.length;

         for (int i = 0; i < numChoices; i++) {
            if (timeChoices[i].getTime() == selectedTime) {
               this.setSelectedIndex(i);
               return;
            }
         }
      }

      throw new IllegalArgumentException();
   }

   public void setSelectedTimeInMillis(long selectedTime, boolean roundUp) {
      if (this._timeChoices == null) {
         throw new IllegalArgumentException();
      }

      TimeChoiceField$TimeChoice[] timeChoices = this._timeChoices;
      int numChoices = this._timeChoices.length;

      for (int i = 0; i < numChoices; i++) {
         long result = timeChoices[i].getTime() - selectedTime;
         if (result >= 0) {
            if (result != 0 && !roundUp && i != 0) {
               this.setSelectedIndex(i - 1);
               return;
            }

            this.setSelectedIndex(i);
            return;
         }
      }

      this.setSelectedIndex(numChoices - 1);
   }
}
