package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.cldc.util.CalendarExtensions;

final class MonthVerbManager extends CalendarViewVerbManager {
   private GotoDateVerb _nextMonthVerb = new GotoDateVerb(325, 479557, 340);
   private GotoDateVerb _prevMonthVerb = new GotoDateVerb(324, 479552, 341);
   private GotoDateVerb _nextYearVerb = new GotoDateVerb(323, 479573, 340);
   private GotoDateVerb _prevYearVerb = new GotoDateVerb(322, 479568, 341);
   private Calendar _cal = Calendar.getInstance();
   private CalendarExtensions _calEx = (CalendarExtensions)this._cal;
   private static final int MONTHS_IN_YEAR = 12;

   public MonthVerbManager(CalendarActions calActions) {
      super(calActions);
      IntHashtable hk = this.getHotKeys();
      hk.put(this._nextMonthVerb.getChar(), this._nextMonthVerb);
      hk.put(this._prevMonthVerb.getChar(), this._prevMonthVerb);
      this.setDefaultVerb((byte)1);
   }

   @Override
   public final void setPreviousView(int previousView) {
      switch (previousView) {
         case 2:
            this.setDefaultVerb((byte)1);
            return;
         case 4:
            this.setDefaultVerb((byte)4);
            return;
         default:
            switch (CalendarOptions.getOptions().getInitialView()) {
               case 3:
                  this.setDefaultVerb((byte)4);
                  return;
               default:
                  this.setDefaultVerb((byte)1);
            }
      }
   }

   @Override
   protected final boolean isVerbSupported(byte verbId) {
      return verbId != 3;
   }

   @Override
   protected final void addStandardVerbs(VerbToMenu verbs) {
      super.addStandardVerbs(verbs);
      verbs.addVerb(this._nextYearVerb);
      verbs.addVerb(this._prevYearVerb);
   }

   @Override
   protected final Verb addUnitNavigationVerbs(VerbToMenu verbs, long selectedDate) {
      Verb verbToMark = null;
      verbs.addVerb(this._prevMonthVerb);
      verbs.addVerb(this._nextMonthVerb);
      return verbToMark;
   }

   @Override
   protected final void establishDatesForUnitNavigationVerbs(long selectedDate, boolean updateForMenu) {
      CalendarExtensions calEx = this._calEx;
      calEx.setTimeLong(selectedDate);
      calEx.add(2, -1);
      this._prevMonthVerb.setTarget(calEx.getTimeLong());
      calEx.setTimeLong(selectedDate);
      calEx.add(2, 1);
      this._nextMonthVerb.setTarget(calEx.getTimeLong());
      if (updateForMenu) {
         calEx.setTimeLong(selectedDate);
         calEx.add(1, -1);
         this._prevYearVerb.setTarget(calEx.getTimeLong());
         calEx.add(1, 2);
         this._nextYearVerb.setTarget(calEx.getTimeLong());
      }
   }
}
