package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.container.VerticalFieldManager;

final class AgendaController extends CalendarViewController {
   private VerticalFieldManager _listVFM = new AgendaController$AgendaVerticalFieldManager(this);
   private AgendaField _agenda = new AgendaField();

   public AgendaController(CalendarApp calendarUIApplication, CalendarActions calActions) {
      super(calendarUIApplication, calActions, null, true, false);
      AgendaVerbManager verbManager = new AgendaVerbManager(
         calActions, new AgendaController$AdjacentDayVerb(this, true), new AgendaController$AdjacentDayVerb(this, false)
      );
      this.setVerbManager(verbManager);
      this.setDelayedHeaderRendering(true);
   }

   @Override
   protected final void initializeAdditionalFields() {
      this._agenda.init(this);
   }

   @Override
   protected final void uninitializeAdditionalFields() {
      this._agenda.uninit();
   }

   @Override
   protected final void addAdditionalFields(Screen screen) {
      this._listVFM.add(this._agenda.getField());
      screen.add(this._listVFM);
      this._agenda.getField().setFocus();
   }

   @Override
   protected final long getSelectedStartTime() {
      return this._agenda.getSelectedStartTime();
   }

   @Override
   protected final long getSelectedEndTime() {
      return this._agenda.getSelectedEndTime();
   }

   @Override
   protected final Object getSelectedObject() {
      return this._agenda.getSelectedObject();
   }

   @Override
   public final void selectedEventChanged(Object event) {
   }

   @Override
   public final int moveFocus(Field field, int amount) {
      return 0;
   }

   @Override
   protected final Runnable loadViewContentsNow(
      long time, Object object, boolean updateSelectedDate, boolean reposition, boolean preserveSelectedTime, byte loadType
   ) {
      return this._agenda.loadAgenda(time, object, loadType);
   }

   @Override
   protected final boolean onRight(int eventTime, long lastTimeViewed, Screen screen) {
      return super.onPageDown(eventTime, lastTimeViewed, screen);
   }

   @Override
   protected final boolean onLeft(int eventTime, long lastTimeViewed, Screen screen) {
      return super.onPageUp(eventTime, lastTimeViewed, screen);
   }

   @Override
   protected final boolean onTop(int eventTime, long lastTimeViewed, Screen screen) {
      return screen.scroll(1);
   }

   @Override
   protected final boolean onBottom(int eventTime, long lastTimeViewed, Screen screen) {
      return screen.scroll(2);
   }

   @Override
   protected final char mapKey(char key, int status) {
      int hkResource = 0;
      if (key == ' ') {
         if ((status & 2) != 0) {
            hkResource = 341;
         } else {
            hkResource = 340;
         }
      } else if (key == 132) {
         hkResource = 340;
      } else if (key == 131) {
         hkResource = 341;
      }

      if (hkResource != 0) {
         key = CalendarApp._rb.getString(hkResource).charAt(0);
      }

      return key;
   }

   @Override
   public final void dateChanged() {
   }

   @Override
   public final void optionsChanged(int changedOptions) {
   }

   @Override
   public final boolean trackwheelRoll(int amount, int status, int time) {
      if ((status & 1) == 0) {
         return false;
      }

      if (amount != 0) {
         this._agenda.gotoAdjacentDay(amount > 0);
      }

      return true;
   }

   @Override
   public final void selectedDateChanged(long selectedDate) {
      this.updateSelectedDate(selectedDate);
   }
}
