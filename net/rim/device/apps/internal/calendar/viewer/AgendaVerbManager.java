package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.VerbToMenu;

final class AgendaVerbManager extends CalendarViewVerbManager {
   private CalendarViewerVerb _prevDayVerb;
   private CalendarViewerVerb _nextDayVerb;

   public AgendaVerbManager(CalendarActions calActions, CalendarViewerVerb nextDayVerb, CalendarViewerVerb prevDayVerb) {
      super(calActions);
      this._nextDayVerb = nextDayVerb;
      this._prevDayVerb = prevDayVerb;
      IntHashtable hk = this.getHotKeys();
      hk.put(this._nextDayVerb.getChar(), this._nextDayVerb);
      hk.put(this._prevDayVerb.getChar(), this._prevDayVerb);
   }

   @Override
   protected final boolean isVerbSupported(byte verbId) {
      return verbId != 4;
   }

   @Override
   protected final Verb addUnitNavigationVerbs(VerbToMenu verbs, long selectedDate) {
      verbs.addVerb(this._prevDayVerb);
      verbs.addVerb(this._nextDayVerb);
      return null;
   }

   @Override
   protected final void establishDatesForUnitNavigationVerbs(long selectedDate, boolean updateForMenu) {
   }

   @Override
   public final Verb getVerbs(VerbToMenu verbs, long selectedDate, Object objectForExtraVerbs) {
      Verb result = super.getVerbs(verbs, selectedDate, objectForExtraVerbs);
      if (objectForExtraVerbs == null) {
         verbs.addVerb(new DeletePriorVerb(selectedDate));
      }

      return result;
   }
}
