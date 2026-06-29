package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.cldc.util.CalendarExtensions;

final class DayVerbManager extends CalendarViewVerbManager {
   private GotoDateVerb _nextDayVerb = new GotoDateVerb(329, 479525, 340);
   private GotoDateVerb _prevDayVerb = new GotoDateVerb(328, 479520, 341);
   private GotoDateVerb _nextWeekVerb = new GotoDateVerb(327, 479541, 340);
   private GotoDateVerb _prevWeekVerb = new GotoDateVerb(326, 479536, 341);
   private Calendar _cal = Calendar.getInstance();
   private CalendarExtensions _calEx = (CalendarExtensions)this._cal;
   private static final int DAYS_IN_WEEK;

   public DayVerbManager(CalendarActions calActions) {
      super(calActions);
      IntHashtable hk = this.getHotKeys();
      hk.put(this._nextDayVerb.getChar(), this._nextDayVerb);
      hk.put(this._prevDayVerb.getChar(), this._prevDayVerb);
   }

   @Override
   protected final boolean isVerbSupported(byte verbId) {
      return verbId != 1;
   }

   @Override
   protected final void addStandardVerbs(VerbToMenu verbs) {
      super.addStandardVerbs(verbs);
      verbs.addVerb(this._nextWeekVerb);
      verbs.addVerb(this._prevWeekVerb);
   }

   @Override
   protected final Verb addUnitNavigationVerbs(VerbToMenu verbs, long selectedDate) {
      Verb verbToMark = null;
      verbs.addVerb(this._prevDayVerb);
      verbs.addVerb(this._nextDayVerb);
      return verbToMark;
   }

   @Override
   protected final void establishDatesForUnitNavigationVerbs(long selectedDate, boolean updateForMenu) {
      CalendarExtensions calEx = this._calEx;
      calEx.setTimeLong(selectedDate);
      calEx.add(5, -1);
      this._prevDayVerb.setTarget(calEx.getTimeLong());
      calEx.add(5, 2);
      this._nextDayVerb.setTarget(calEx.getTimeLong());
      if (updateForMenu) {
         calEx.add(5, -8);
         this._prevWeekVerb.setTarget(calEx.getTimeLong());
         calEx.add(5, 14);
         this._nextWeekVerb.setTarget(calEx.getTimeLong());
      }
   }
}
