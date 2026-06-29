package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.search.SearchResultField;
import net.rim.device.cldc.util.CalendarExtensions;

final class CalendarSearchResultField extends DayList implements SearchResultField, CollectionListener, PersistentContentListener {
   private Vector _eventVector = new Vector();
   private CalendarSearchResultCollection _results;
   private Application _application = Application.getApplication();
   private CalendarSearchResultField$UpdateListRunnable _updateListRunnable = new CalendarSearchResultField$UpdateListRunnable(this);

   CalendarSearchResultField(CalendarSearchResultCollection results) {
      super(false);
      this._results = results;
      super._splitDaysThatSpanMidnight = false;
      this.setSummaryLines(2);
      this.setDisplayTimeBars(false);
      this.setDisplayEndTimes(false);
      this.setDisplayLines(false);
      this.setClampTimes(false);
      this.setDisplayInformationIcons(true);
      this._results.addCollectionListener(this);
      PersistentContent.addWeakListener(this);
      this.updateList();
   }

   @Override
   protected final CalendarViewListField createCalendarViewListField(CalendarViewListField$CalendarViewListFieldCallback callback, boolean multiSelectAllowed) {
      return new CalendarSearchResultField$CalendarViewListFieldVerbProvider(this, callback, multiSelectAllowed);
   }

   @Override
   protected final boolean supportAdvancedThemeing() {
      return false;
   }

   private final void updateList() {
      if (this._application == Application.getApplication() && Application.isEventDispatchThread()) {
         this.doUpdateList();
      } else {
         this._application.invokeLater(this._updateListRunnable);
      }
   }

   private final void doUpdateList() {
      Vector events = this._eventVector;
      synchronized (this._results) {
         int numEvents = this._results.size();

         for (int i = 0; i < numEvents; i++) {
            events.addElement(this._results.getAt(i));
         }
      }

      if (events.size() > 0) {
         Duration firstElement = (Duration)events.firstElement();
         if (firstElement != null) {
            this.setStartOfList(firstElement.getStart(super._tz));
         }

         Duration lastElement = (Duration)events.lastElement();
         if (lastElement != null) {
            long duration = lastElement.getDuration(super._tz) < 0 ? 0 : lastElement.getDuration(super._tz);
            this.setEndOfList(lastElement.getStart(super._tz) + duration);
         }
      }

      this.updateTransitions(events, -1);
      events.removeAllElements();
      this.getField().setSize(super._numTransitions, -1);
   }

   @Override
   protected final void addDateTimeTransitions() {
      Calendar cal = super._cal;
      CalendarExtensions calEx = super._calEx;
      int maxEvents = super._numTransitions;
      int insertionOffset = maxEvents;
      int prevYear = 0;
      int prevDay = -1;

      for (int i = 0; i < maxEvents; i++) {
         calEx.setTimeLong(super._list.getAt(i)._timeInMillis);
         int currYear = cal.get(1);
         int currDay = cal.get(6);
         if (currYear != prevYear || currDay != prevDay) {
            DateTimeUtilities.zeroCalendarTime(cal);
            super._list.setTransition(insertionOffset, (byte)1, calEx.getTimeLong(), -1);
            insertionOffset++;
            prevYear = currYear;
            prevDay = currDay;
         }
      }

      super._numTransitions = insertionOffset;
      super._list.clearTransitions(insertionOffset);
      this.sortTransitions();
   }

   @Override
   public final String getEmptyString(Object field) {
      ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      return rb.getString(621);
   }

   @Override
   public final Object getSelectedObject() {
      return super.getSelectedObject();
   }

   final Object getSelectedObjectInternal() {
      return this.getSelectedObject();
   }

   @Override
   public final void reset(Collection collection) {
      this.updateList();
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.updateList();
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.updateList();
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.updateList();
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      if (state == 2) {
         this.reCrypt();
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
   }
}
