package net.rim.device.apps.api.calendar.calconstants;

public class CalConstants {
   public static final int NEXT_VIEW_ID = 0;
   public static final int MONTH_VIEW_ID = 1;
   public static final int DAY_VIEW_ID = 2;
   public static final int WEEK_VIEW_ID = 3;
   public static final int AGENDA_VIEW_ID = 4;
   public static final long VIEW_CALENDAR_VERB_ID = 8025740836317336000L;
   public static final long COMMON_VERBS_ID = -2786162410658704605L;
   public static final long EVENT_VERBS_ID = 5182228461004335870L;
   public static final long IN_PLACE_INPUTS = -2932280743217917193L;
   public static final long SORT_KEY_START = -7347526267900023482L;
   public static final long SORT_KEY_END = -104331952420113366L;
   public static final long SORT_KEY_DUR = -1863931878973078146L;
   public static final long CAL_VIEWER_CLOSED_DUE_TO_CONTENT_PROTECTION = -4802534882197295853L;
   public static final long TIME_ADJUSTMENT = 10000L;
   public static final long DEFAULT_DURATION = 3600000L;
   public static final long REFRESH_VIEW = 5483692278053761660L;

   private CalConstants() {
   }

   public static long getAdjustedCurrentTimeMillis() {
      return System.currentTimeMillis() + 10000;
   }
}
