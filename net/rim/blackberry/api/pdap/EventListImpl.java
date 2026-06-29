package net.rim.blackberry.api.pdap;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;
import javax.microedition.pim.Event;
import javax.microedition.pim.EventList;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.UnsupportedFieldException;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.framework.model.Recur$Handle;
import net.rim.device.apps.api.ui.CommonResources;

public final class EventListImpl extends PIMListImpl implements EventList {
   private static ResourceBundle _resources = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
   private static Hashtable _actualListeners;
   private static final long APP_REGISTRY_KEY;
   static String LIST_NAME;
   private static String LIST_CLOSED_MESSAGE;
   private static String READONLY_MESSAGE;
   private static String WRITEONLY_MESSAGE;
   private static String NO_CATEGORIES_MESSAGE;
   private static EventListImpl$_eventComparator __evComparator;

   EventListImpl(int mode) {
      super._closed = false;
      super._mode = mode;
   }

   @Override
   protected final void removePIMItem(PIMItem pi) {
      try {
         if (pi instanceof Event) {
            Event event = (Event)pi;
            this.removeEvent(event);
            return;
         }
      } catch (PIMException var3) {
      }
   }

   @Override
   protected final boolean verifyField(int field) {
      switch (field) {
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 106:
         case 107:
         case 108:
         case 20000927:
         case 20000928:
         case 20000929:
            return true;
         case 105:
            return false;
         default:
            throw new Object();
      }
   }

   @Override
   public final String getFieldLabel(int field) {
      switch (field) {
         case 100:
            return _resources.getString(290);
         case 101:
            return _resources.getString(613);
         case 102:
            return _resources.getString(104);
         case 103:
            return _resources.getString(101);
         case 104:
            return CommonResources.getString(2004);
         case 105:
            throw new UnsupportedFieldException("", field);
         case 106:
            return _resources.getString(103);
         case 107:
            return _resources.getString(100);
         case 108:
            return "UID:";
         case 20000927:
            return ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(604);
         case 20000928:
            return _resources.getString(102);
         case 20000929:
            return _resources.getString(6);
         default:
            throw new Object();
      }
   }

   @Override
   public final int getFieldDataType(int field) {
      switch (field) {
         case 100:
         case 101:
         case 20000929:
            return 3;
         case 102:
         case 106:
            return 2;
         case 103:
         case 104:
         case 107:
         case 108:
         case 20000927:
            return 4;
         case 105:
            throw new UnsupportedFieldException("", field);
         case 20000928:
            return 1;
         default:
            throw new Object();
      }
   }

   @Override
   public final String getName() {
      return LIST_NAME;
   }

   @Override
   public final Event createEvent() {
      return new EventImpl(super._mode, this);
   }

   private final boolean isEventInPeriod(int searchType, long startDate, long endDate, long eventStartDate, long eventEndDate) {
      switch (searchType) {
         case -1:
            break;
         case 0:
         default:
            if (eventStartDate >= startDate && eventStartDate <= endDate) {
               return true;
            }
            break;
         case 1:
            if (eventEndDate >= startDate && eventEndDate <= endDate) {
               return true;
            }
            break;
         case 2:
            if (eventStartDate <= endDate && eventEndDate >= startDate) {
               return true;
            }
      }

      return false;
   }

   @Override
   public final Enumeration items(int searchType, long startDate, long endDate, boolean initialEventOnly) {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      }

      if (searchType != 0 && searchType != 1 && searchType != 2) {
         throw new Object();
      }

      if (startDate > endDate) {
         throw new Object();
      }

      TimeZone tz = TimeZone.getDefault();
      Object[] tmpCalEvents = new Object[EventStatics._cal.size()];
      EventStatics._cal.getElements(tmpCalEvents);
      int asize = tmpCalEvents.length;
      Vector v = (Vector)(new Object());

      for (int i = 0; i < asize; i++) {
         net.rim.device.apps.api.calendar.modelcontrollerinterface.Event e = (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)tmpCalEvents[i];
         long eStartDate = e.getStartDate(null);
         long eEndDate = eStartDate + e.getInstanceDuration();
         if (this.isEventInPeriod(searchType, startDate, endDate, eStartDate, eEndDate)) {
            EventImpl e1 = new EventImpl(super._mode, e, this);
            v.addElement(e1);
         } else if (!initialEventOnly && e.isRecurring()) {
            boolean handleFound = false;
            Recur$Handle handle = (Recur$Handle)(new Object());
            switch (searchType) {
               case -1:
                  break;
               case 0:
               default:
                  handleFound = e.getHandleAfterTime(handle, startDate - 1, tz);
                  break;
               case 1:
               case 2:
                  handleFound = e.getHandleAfterTime(handle, startDate - e.getInstanceDuration(), tz);
            }

            if (handleFound) {
               eStartDate = e.getStartFromHandle(handle._handle, tz);
               eEndDate = eStartDate + e.getDurationFromHandle(handle._handle, tz);
               if (this.isEventInPeriod(searchType, startDate, endDate, eStartDate, eEndDate)) {
                  EventImpl e1 = new EventImpl(super._mode, e, this);
                  v.addElement(e1);
               }
            }
         }
      }

      Object[] _calEvents = new Object[v.size()];
      v.copyInto(_calEvents);
      Arrays.sort(_calEvents, __evComparator);
      return (Enumeration)(new Object(_calEvents));
   }

   @Override
   public final Event importEvent(Event element) {
      if (element == null) {
         throw new Object();
      }

      Event newEvent = this.createEvent();
      int[] fields = element.getFields();

      for (int i = fields.length - 1; i >= 0; i--) {
         int type = this.getFieldDataType(fields[i]);

         try {
            switch (type) {
               case 0:
                  break;
               case 1:
                  newEvent.addBoolean(fields[i], 0, element.getBoolean(fields[i], 0));
                  break;
               case 2:
               default:
                  newEvent.addDate(fields[i], 0, element.getDate(fields[i], 0));
                  break;
               case 3:
                  newEvent.addInt(fields[i], 0, element.getInt(fields[i], 0));
                  break;
               case 4:
                  newEvent.addString(fields[i], 0, element.getString(fields[i], 0));
            }
         } finally {
            continue;
         }
      }

      newEvent.setRepeat(element.getRepeat());
      return newEvent;
   }

   @Override
   public final void removeEvent(Event element) {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 1) {
         throw new Object(READONLY_MESSAGE);
      }

      if (element == null) {
         throw new Object();
      }

      if (!(element instanceof EventImpl)) {
         throw new PIMException(PIMListImpl.NOT_FOUND_MESSAGE, 6);
      }

      EventImpl e = (EventImpl)element;
      net.rim.device.apps.api.calendar.modelcontrollerinterface.Event rimEvent = e.getRimEvent();
      long id = rimEvent.getLUID();
      if (EventStatics._cal.contains(id)) {
         RecurUtilities.scanAndDeleteRelatedEvents(EventStatics._cal, rimEvent);
         EventStatics._cal.remove(rimEvent);
         e.removeFromList();
      } else {
         throw new PIMException(PIMListImpl.NOT_FOUND_MESSAGE, 6);
      }
   }

   @Override
   public final Enumeration items() {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      }

      Object[] _calEvents = new Object[EventStatics._cal.size()];
      EventStatics._cal.getElements(_calEvents);
      int asize = _calEvents.length;

      for (int i = 0; i < asize; i++) {
         EventImpl e1 = new EventImpl(super._mode, (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)_calEvents[i], this);
         _calEvents[i] = e1;
      }

      return (Enumeration)(new Object(_calEvents));
   }

   @Override
   public final Enumeration items(PIMItem matching) {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      }

      if (matching == null) {
         throw new Object();
      }

      if (!(matching instanceof Event)) {
         throw new Object();
      }

      Event matchingEvent = (Event)matching;
      Object v = new Object();
      Object[] tmpCalEvents = new Object[EventStatics._cal.size()];
      EventStatics._cal.getElements(tmpCalEvents);
      int asize = tmpCalEvents.length;

      for (int _calEvents = 0; _calEvents < asize; _calEvents++) {
         net.rim.device.apps.api.calendar.modelcontrollerinterface.Event e = (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)tmpCalEvents[_calEvents];
         EventImpl e1 = new EventImpl(super._mode, e, this);
         if (__evComparator.matches(matchingEvent, e1)) {
            ((Vector)v).addElement(e1);
         }
      }

      Object[] _calEvents = new Object[((Vector)v).size()];
      ((Vector)v).copyInto(_calEvents);
      return (Enumeration)(new Object(_calEvents));
   }

   @Override
   public final Enumeration items(String matching) {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      }

      if (matching == null) {
         throw new Object();
      }

      Vector v = (Vector)(new Object());
      Object[] tmpCalEvents = new Object[EventStatics._cal.size()];
      EventStatics._cal.getElements(tmpCalEvents);
      int asize = tmpCalEvents.length;

      for (int i = 0; i < asize; i++) {
         net.rim.device.apps.api.calendar.modelcontrollerinterface.Event e = (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)tmpCalEvents[i];
         EventImpl e1 = new EventImpl(super._mode, e, this);
         if (e1.countValues(103) > 0 && __evComparator.matchString(e1.getString(103, 0), matching)) {
            v.addElement(e1);
         } else if (e1.countValues(104) > 0 && __evComparator.matchString(e1.getString(104, 0), matching)) {
            v.addElement(e1);
         } else if (e1.countValues(107) > 0 && __evComparator.matchString(e1.getString(107, 0), matching)) {
            v.addElement(e1);
         } else if (e1.countValues(108) > 0 && __evComparator.matchString(e1.getString(108, 0), matching)) {
            v.addElement(e1);
         }
      }

      Object[] _calEvents = new Object[v.size()];
      v.copyInto(_calEvents);
      return (Enumeration)(new Object(_calEvents));
   }

   @Override
   public final void close() {
      if (!super._closed) {
         super._closed = true;
      } else {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }
   }

   @Override
   public final boolean isSupportedField(int fieldID) {
      switch (fieldID) {
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 106:
         case 107:
         case 108:
         case 20000927:
         case 20000928:
         case 20000929:
            return true;
         default:
            return false;
      }
   }

   @Override
   public final int[] getSupportedFields() {
      return new int[]{
         100,
         102,
         103,
         104,
         106,
         107,
         108,
         20000927,
         20000928,
         20000929,
         101,
         -804650991,
         100,
         109,
         103,
         106,
         108,
         115,
         116,
         117,
         20000927,
         20000928,
         20000929,
         20000930,
         20000931,
         118,
         110,
         101,
         20000933,
         -805044223,
         1979777219,
         6646639,
         1802466817,
         1684947301,
         221527,
         12956929,
         995244803,
         2324079,
         7551747,
         -1468843261,
         426213955,
         990052473,
         743352435,
         990052453
      };
   }

   @Override
   protected final PIMItem getPIMItemFor(Object element) {
      return new EventImpl(3, (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)element, this);
   }

   @Override
   protected final CollectionEventSource getCollectionEventSource() {
      return EventStatics._cal;
   }

   @Override
   protected final Hashtable getActualListeners() {
      return _actualListeners;
   }

   @Override
   public final int[] getSupportedRepeatRuleFields(int frequency) {
      switch (frequency) {
         case 15:
            throw new Object();
         case 16:
            return new int[0];
         case 17:
            return new int[]{2, -804651005, 4, 26};
         case 18:
            return new int[]{1, 18, -804651007, 2, -804651005, 4, 26, 9};
         case 19:
         default:
            return new int[]{4, 26, 9, -804651006, 8, 512, -804651000, 8, 16, 64, 512, 4};
      }
   }

   @Override
   public final int maxValues(int field) {
      if (field == 20000927) {
         return -1;
      } else {
         return this.verifyField(field) ? 1 : 0;
      }
   }

   @Override
   public final String[] getCategories() {
      return new Object[0];
   }

   @Override
   public final boolean isCategory(String category) {
      if (category == null) {
         throw new Object();
      } else {
         return false;
      }
   }

   @Override
   public final void addCategory(String category) {
      if (category == null) {
         throw new Object();
      } else {
         throw new PIMException(NO_CATEGORIES_MESSAGE, 0);
      }
   }

   @Override
   public final void deleteCategory(String category, boolean deleteUnassignedItems) {
      if (category == null) {
         throw new Object();
      } else {
         throw new PIMException(NO_CATEGORIES_MESSAGE, 0);
      }
   }

   @Override
   public final int maxCategories() {
      return 0;
   }

   @Override
   public final void renameCategory(String currentCategory, String newCategory) {
      if (currentCategory != null && newCategory != null) {
         throw new PIMException(NO_CATEGORIES_MESSAGE, 0);
      } else {
         throw new Object();
      }
   }

   @Override
   protected final Enumeration getItemsInCategory(String category) {
      return (Enumeration)(category != null && category != PIMList.UNCATEGORIZED ? new Object() : this.items());
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _actualListeners = (Hashtable)ar.getOrWaitFor(7918772433172892313L);
      if (_actualListeners == null) {
         _actualListeners = (Hashtable)(new Object());
         ar.put(7918772433172892313L, _actualListeners);
      }

      LIST_NAME = "Event List";
      LIST_CLOSED_MESSAGE = "EventList is closed.";
      READONLY_MESSAGE = "EventList is read-only.";
      WRITEONLY_MESSAGE = "EventList is write-only.";
      NO_CATEGORIES_MESSAGE = "Categories not supported on this list.";
      __evComparator = new EventListImpl$_eventComparator();
   }
}
