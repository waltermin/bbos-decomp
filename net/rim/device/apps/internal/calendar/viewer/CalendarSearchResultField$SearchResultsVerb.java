package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.cldc.util.CalendarExtensions;

final class CalendarSearchResultField$SearchResultsVerb extends CalendarViewerVerb {
   private int _type;
   private final CalendarSearchResultField this$0;
   public static final int GOTO_PREV = 0;
   public static final int GOTO_TODAY = 1;
   public static final int GOTO_NEXT = 2;
   public static final int GOTO_PICK = 3;

   CalendarSearchResultField$SearchResultsVerb(CalendarSearchResultField _1, int displayStringId, int order, int hotkeyResourceId, int type) {
      super(displayStringId, order, hotkeyResourceId);
      this.this$0 = _1;
      this._type = type;
   }

   private final void gotoTime(long target) {
      Calendar cal = this.this$0._cal;
      CalendarExtensions calEx = this.this$0._calEx;
      calEx.setTimeLong(target);
      DateTimeUtilities.zeroCalendarTime(cal);
      long targetDayStart = calEx.getTimeLong();
      calEx.add(5, 1);
      long nextDayStart = calEx.getTimeLong();
      TimeZone tz = TimeZone.getDefault();
      Event curr = null;
      Event prev = null;
      synchronized (this.this$0._results) {
         int max = this.this$0._results.size();

         for (int i = 0; i < max; i++) {
            prev = curr;
            curr = (Event)this.this$0._results.getAt(i);
            long currTime = curr.getStartDate(tz);
            if (currTime >= target) {
               if (currTime >= nextDayStart && prev != null && prev.getStartDate(tz) >= targetDayStart) {
                  curr = prev;
               }
               break;
            }
         }
      }

      this.this$0.setSelectedObject(curr);
   }

   private final void gotoNextDay() {
      for (int i = this.this$0.getSelectedOffset() + 1; i < this.this$0._list.getLength(); i++) {
         if (this.this$0._list.getAt(i)._transitionType == 1) {
            this.this$0.setSelectedOffset(i);
            return;
         }
      }
   }

   private final void gotoPrevDay() {
      for (int i = this.this$0.getSelectedOffset() - 1; i >= 0; i--) {
         if (this.this$0._list.getAt(i)._transitionType == 1) {
            this.this$0.setSelectedOffset(i);
            return;
         }
      }
   }

   private final void gotoPickDay() {
      SelectDate utilSelect = new SelectDate();
      utilSelect.setDate(System.currentTimeMillis());
      if (utilSelect.doSelection()) {
         this.gotoTime(utilSelect.getDate());
      }
   }

   @Override
   public final Object invoke(Object parameter) {
      switch (this._type) {
         case 0:
         default:
            this.gotoPrevDay();
            return null;
         case 1:
            this.gotoTime(System.currentTimeMillis());
            return null;
         case 2:
            this.gotoNextDay();
            return null;
         case 3:
            this.gotoPickDay();
         case -1:
            return null;
      }
   }
}
