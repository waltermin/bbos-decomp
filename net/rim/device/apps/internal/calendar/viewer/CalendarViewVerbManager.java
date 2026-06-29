package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.framework.hotkeys.HotKeys;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.calendar.eventprovider.ViewByLUID;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Array;

class CalendarViewVerbManager {
   private GotoDateVerb _pickDateVerb;
   private GotoDateVerb _gotoTodayVerb;
   private Verb _closeVerb;
   private Verb _optionsVerb = new CalendarOptionsVerb();
   private GotoViewVerb _viewDayVerb;
   private GotoViewVerb _viewWeekVerb;
   private GotoViewVerb _viewMonthVerb;
   private GotoViewVerb _viewAgendaVerb;
   private Verb _chooseCalendarServiceVerb;
   private ViewCalendarDatabaseVerbManager _viewDatabaseVerbManager = new ViewCalendarDatabaseVerbManager();
   private Verb[] _verbArray = new Verb[0];
   private IntHashtable _hk = new IntHashtable();
   private CalendarActions _calActions;
   private Verb _defaultVerb;
   protected static final byte VIEW_DAY_VERB = 1;
   protected static final byte VIEW_WEEK_VERB = 2;
   protected static final byte VIEW_MONTH_VERB = 3;
   protected static final byte VIEW_AGENDA_VERB = 4;

   public CalendarViewVerbManager(CalendarActions calActions) {
      this._calActions = calActions;
      this._closeVerb = new CloseCalendarVerb(calActions);
      this._pickDateVerb = new GotoDateVerb(320, 479509, 348);
      this._pickDateVerb.setTargetType(2);
      this._gotoTodayVerb = new GotoDateVerb(321, 479504, 349);
      this._gotoTodayVerb.setTargetType(1);
      if (this.isVerbSupported((byte)1)) {
         this._viewDayVerb = new GotoViewVerb(400, 1126656, 351, 2, this._calActions, false);
      }

      if (this.isVerbSupported((byte)2)) {
         this._viewWeekVerb = new GotoViewVerb(401, 1126672, 352, 3, this._calActions, false);
      }

      if (this.isVerbSupported((byte)3)) {
         this._viewMonthVerb = new GotoViewVerb(402, 1126688, 350, 1, this._calActions, false);
      }

      if (this.isVerbSupported((byte)4)) {
         this._viewAgendaVerb = new GotoViewVerb(403, 1126704, 353, 4, this._calActions, false);
      }

      this._chooseCalendarServiceVerb = this._viewDatabaseVerbManager.getChooseCalendarVerb();
      this.addHotKeys();
   }

   protected void setDefaultVerb(byte verbId) {
      switch (verbId) {
         case 1:
            this._defaultVerb = this._viewDayVerb;
            return;
         case 4:
            this._defaultVerb = this._viewAgendaVerb;
            return;
         default:
            this._defaultVerb = null;
      }
   }

   protected boolean isVerbSupported(byte _1) {
      throw null;
   }

   protected IntHashtable getHotKeys() {
      return this._hk;
   }

   private void addHotKey(CalendarViewerVerb verb, IntHashtable hk) {
      if (verb != null) {
         hk.put(verb.getChar(), verb);
      }
   }

   private void addHotKeys() {
      IntHashtable hk = this._hk;
      if (!InternalServices.isReducedFormFactor()) {
         this.addHotKey(this._viewAgendaVerb, hk);
         this.addHotKey(this._viewDayVerb, hk);
         this.addHotKey(this._viewWeekVerb, hk);
         this.addHotKey(this._viewMonthVerb, hk);
         this.addHotKey(this._pickDateVerb, hk);
      }

      this.addHotKey(this._gotoTodayVerb, hk);
      char[] repositoryHotKeys = HotKeys.getUsedHotKeys(4);
      if (repositoryHotKeys != null) {
         for (int i = 0; i < repositoryHotKeys.length; i++) {
            hk.put(repositoryHotKeys[i], HotKeys.getVerb(4, repositoryHotKeys[i]));
         }
      }
   }

   private void addVerb(Verb verb, VerbToMenu verbs) {
      if (verb != null) {
         verbs.addVerb(verb);
      }
   }

   public Verb getVerbs(VerbToMenu verbs, long selectedDate, Object objectForExtraVerbs) {
      this.addStandardVerbs(verbs);
      this.addRepositoryVerbs(verbs);
      Verb verbToMark = this.addUnitNavigationVerbs(verbs, selectedDate);
      this.establishDatesForUnitNavigationVerbs(selectedDate, true);
      boolean defaultVerbForObjectSet = this.addObjectVerbs(verbs, objectForExtraVerbs);
      if (this._defaultVerb != null && !defaultVerbForObjectSet) {
         verbs.setDefaultVerb(this._defaultVerb);
      }

      return verbToMark;
   }

   public void setPreviousView(int previousView) {
   }

   protected void addStandardVerbs(VerbToMenu verbs) {
      this.addVerb(this._viewDayVerb, verbs);
      this.addVerb(this._viewWeekVerb, verbs);
      this.addVerb(this._viewMonthVerb, verbs);
      this.addVerb(this._viewAgendaVerb, verbs);
      this.addVerb(this._pickDateVerb, verbs);
      this.addVerb(this._gotoTodayVerb, verbs);
      this.addVerb(this._closeVerb, verbs);
      this.addVerb(this._optionsVerb, verbs);
      Verb viewByLUID = ViewByLUID.getInstance();
      if (viewByLUID != null) {
         this.addVerb(viewByLUID, verbs);
      }

      this.addVerb(this._chooseCalendarServiceVerb, verbs);
   }

   protected void addRepositoryVerbs(VerbToMenu verbs) {
      CalendarProxy calProxy = CalendarProxy.getInstance();
      Object[] commonVerbs = calProxy.getRepositoryCopy(-2786162410658704605L);
      if (commonVerbs != null) {
         for (int i = 0; i < commonVerbs.length; i++) {
            Verb verb = (Verb)commonVerbs[i];
            verbs.addVerb(verb);
            if (i == 0) {
               verbs.setDefaultVerb(verb);
            }
         }
      }
   }

   protected boolean addObjectVerbs(VerbToMenu verbs, Object objectForExtraVerbs) {
      if (objectForExtraVerbs instanceof VerbProvider) {
         VerbProvider calVerbProvider = (VerbProvider)objectForExtraVerbs;
         Verb defaultVerb = calVerbProvider.getVerbs(new ContextObject(86), this._verbArray);
         verbs.addVerbs(this._verbArray);
         Array.resize(this._verbArray, 0);
         if (defaultVerb != null) {
            verbs.setDefaultVerb(defaultVerb);
            return true;
         }
      }

      return false;
   }

   protected Verb addUnitNavigationVerbs(VerbToMenu _1, long _2) {
      throw null;
   }

   protected void establishDatesForUnitNavigationVerbs(long _1, boolean _3) {
      throw null;
   }

   Verb findFromChar(char keyPressed, long selectedDate) {
      if (keyPressed == 0) {
         return null;
      }

      Verb verbToReturn = (Verb)this._hk.get(Character.toLowerCase(keyPressed));
      if (verbToReturn == null) {
         return null;
      }

      this.establishDatesForUnitNavigationVerbs(selectedDate, false);
      return verbToReturn;
   }
}
