package net.rim.device.apps.api.calendar.calconstants;

public class CalConstants {
   public static final int NEXT_VIEW_ID;
   public static final int MONTH_VIEW_ID;
   public static final int DAY_VIEW_ID;
   public static final int WEEK_VIEW_ID;
   public static final int AGENDA_VIEW_ID;
   public static final long VIEW_CALENDAR_VERB_ID;
   public static final long COMMON_VERBS_ID;
   public static final long EVENT_VERBS_ID;
   public static final long IN_PLACE_INPUTS;
   public static final long SORT_KEY_START;
   public static final long SORT_KEY_END;
   public static final long SORT_KEY_DUR;
   public static final long CAL_VIEWER_CLOSED_DUE_TO_CONTENT_PROTECTION;
   public static final long TIME_ADJUSTMENT;
   public static final long DEFAULT_DURATION;
   public static final long REFRESH_VIEW;

   private CalConstants() {
   }

   public static long getAdjustedCurrentTimeMillis() {
      return System.currentTimeMillis() + 10000;
   }
}
