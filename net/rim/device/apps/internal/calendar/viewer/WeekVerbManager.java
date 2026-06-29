package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.cldc.util.CalendarExtensions;

final class WeekVerbManager extends CalendarViewVerbManager {
   private GotoDateVerb _nextWeekVerb = new GotoDateVerb(327, 479541, 340);
   private GotoDateVerb _prevWeekVerb = new GotoDateVerb(326, 479536, 341);
   private Calendar _cal = Calendar.getInstance();
   private CalendarExtensions _calEx = (CalendarExtensions)this._cal;
   private static final int DAYS_IN_WEEK = 7;

   public WeekVerbManager(CalendarActions calActions) {
      super(calActions);
      IntHashtable hk = this.getHotKeys();
      hk.put(this._nextWeekVerb.getChar(), this._nextWeekVerb);
      hk.put(this._prevWeekVerb.getChar(), this._prevWeekVerb);
   }

   @Override
   protected final boolean isVerbSupported(byte verbId) {
      return verbId != 2;
   }

   @Override
   protected final Verb addUnitNavigationVerbs(VerbToMenu verbs, long selectedDate) {
      verbs.addVerb(this._prevWeekVerb);
      verbs.addVerb(this._nextWeekVerb);
      return null;
   }

   @Override
   protected final void establishDatesForUnitNavigationVerbs(long selectedDate, boolean updateForMenu) {
      CalendarExtensions calEx = this._calEx;
      calEx.setTimeLong(selectedDate);
      calEx.add(5, -7);
      this._prevWeekVerb.setTarget(calEx.getTimeLong());
      calEx.add(5, 14);
      this._nextWeekVerb.setTarget(calEx.getTimeLong());
   }
}
