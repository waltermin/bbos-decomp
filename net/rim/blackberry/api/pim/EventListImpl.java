package net.rim.blackberry.api.pim;

import java.util.Enumeration;
import java.util.TimeZone;
import java.util.Vector;
import net.rim.blackberry.api.pim.resource.PIMResResource;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ObjectEnumerator;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.framework.model.Recur$Handle;

public final class EventListImpl extends PIMListImpl implements EventList, PIMResResource {
   private static ResourceBundle _resources = ResourceBundle.getBundle(6683049446475877841L, "net.rim.blackberry.api.pim.resource.PIMRes");
   static String LIST_NAME = "Event List";
   private static String LIST_CLOSED_MESSAGE = "EventList is closed.";
   private static String READONLY_MESSAGE = "EventList is read-only.";
   private static String WRITEONLY_MESSAGE = "EventList is write-only.";
   private static EventListImpl$_eventComparator __evComparator = new EventListImpl$_eventComparator();

   EventListImpl(int mode) {
      super._closed = false;
      super._mode = mode;
   }

   @Override
   protected final boolean verifyField(int field) {
      switch (field) {
         case 100:
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
         case 101:
         case 105:
            return false;
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getFieldLabel(int field) {
      switch (field) {
         case 100:
            return _resources.getString(21);
         case 101:
         case 105:
         case 20000927:
         case 20000928:
         case 20000929:
            throw new UnsupportedFieldException(field);
         case 102:
            return _resources.getString(22);
         case 103:
            return _resources.getString(16);
         case 104:
            return _resources.getString(17);
         case 106:
            return _resources.getString(23);
         case 107:
            return _resources.getString(18);
         case 108:
            return _resources.getString(20);
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final int getFieldDataType(int field) {
      switch (field) {
         case 100:
         case 20000929:
            return 3;
         case 101:
         case 105:
            throw new UnsupportedFieldException(field);
         case 102:
         case 106:
            return 2;
         case 103:
         case 104:
         case 107:
         case 108:
         case 20000927:
            return 4;
         case 20000928:
            return 1;
         default:
            throw new IllegalArgumentException();
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
   public final Enumeration items(int searchType, long startDate, long endDate, boolean initialEventOnly) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new SecurityException(WRITEONLY_MESSAGE);
      }

      if (searchType != 0 && searchType != 1 && searchType != 2) {
         throw new IllegalArgumentException();
      }

      TimeZone tz = TimeZone.getDefault();
      Object[] tmpCalEvents = new Object[EventStatics._cal.size()];
      EventStatics._cal.getElements(tmpCalEvents);
      int asize = tmpCalEvents.length;
      Vector v = new Vector();

      for (int i = 0; i < asize; i++) {
         net.rim.device.apps.api.calendar.modelcontrollerinterface.Event e = (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)tmpCalEvents[i];
         long eStartDate = e.getStartDate(null);
         long eEndDate = eStartDate + e.getInstanceDuration();
         if (this.isEventInPeriod(searchType, startDate, endDate, eStartDate, eEndDate)) {
            EventImpl e1 = new EventImpl(super._mode, e, this);
            v.addElement(e1);
         } else if (!initialEventOnly && e.isRecurring()) {
            boolean handleFound = false;
            Recur$Handle handle = new Recur$Handle();
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
      return new ObjectEnumerator(_calEvents);
   }

   @Override
   public final Event importEvent(Event element) {
      if (element == null) {
         throw new NullPointerException();
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
                  if (fields[i] == 100) {
                     newEvent.setInt(fields[i], 0, 0, element.getInt(fields[i], 0));
                  } else {
                     newEvent.addInt(fields[i], 0, element.getInt(fields[i], 0));
                  }
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
   public final void removeEvent(Event element) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 1) {
         throw new SecurityException(READONLY_MESSAGE);
      }

      if (element == null) {
         throw new IllegalArgumentException();
      }

      if (!(element instanceof EventImpl)) {
         throw new PIMException(PIMListImpl.NOT_FOUND_MESSAGE, 6);
      }

      EventImpl e = (EventImpl)element;
      if (e.getPIMList().equals(this)) {
         e.prepareToDie();
         RecurUtilities.scanAndDeleteRelatedEvents(EventStatics._cal, e.getRimEvent());
         EventStatics._cal.remove(e.getRimEvent());
      } else {
         throw new PIMException(PIMListImpl.NOT_FOUND_MESSAGE, 6);
      }
   }

   @Override
   public final Enumeration items() throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new SecurityException(WRITEONLY_MESSAGE);
      }

      Object[] _calEvents = new Object[EventStatics._cal.size()];
      EventStatics._cal.getElements(_calEvents);
      int asize = _calEvents.length;

      for (int i = 0; i < asize; i++) {
         EventImpl e1 = new EventImpl(super._mode, (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)_calEvents[i], this);
         _calEvents[i] = e1;
      }

      return new ObjectEnumerator(_calEvents);
   }

   @Override
   public final Enumeration items(PIMItem matching) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new SecurityException(WRITEONLY_MESSAGE);
      }

      Vector v = new Vector();
      Object[] tmpCalEvents = new Object[EventStatics._cal.size()];
      EventStatics._cal.getElements(tmpCalEvents);
      int asize = tmpCalEvents.length;

      for (int i = 0; i < asize; i++) {
         net.rim.device.apps.api.calendar.modelcontrollerinterface.Event e = (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)tmpCalEvents[i];
         EventImpl e1 = new EventImpl(super._mode, e, this);
         if (__evComparator.matches((Event)matching, e1)) {
            v.addElement(e1);
         }
      }

      Object[] _calEvents = new Object[v.size()];
      v.copyInto(_calEvents);
      return new ObjectEnumerator(_calEvents);
   }

   @Override
   public final Enumeration items(String matching) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new SecurityException(WRITEONLY_MESSAGE);
      }

      if (matching == null) {
         throw new NullPointerException();
      }

      Vector v = new Vector();
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
      return new ObjectEnumerator(_calEvents);
   }

   @Override
   public final void close() throws PIMException {
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
         -804650995,
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
         -805044223,
         -1258225469,
         990052549,
         1987001170,
         990052387,
         990052467,
         -2109495181,
         7936359,
         113916675,
         990052452,
         588342511,
         526976000,
         1812332780,
         -2109512446,
         7936359,
         638058504
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
   public final int[] getSupportedRepeatRuleFields(int frequency) {
      switch (frequency) {
         case 15:
            throw new IllegalArgumentException();
         case 16:
            return new int[0];
         case 17:
            return new int[]{2, -804651005, 3, 15};
         case 18:
            return new int[]{1, 10, -804651007, 2, -804651005, 3, 15, 7};
         case 19:
         default:
            return new int[]{3, 15, 7, -804651003, 8, 16, 64, 512, 4, -804651004, 19, 18};
      }
   }

   @Override
   public final int[] getSupportedRepeatRuleFrequencies() {
      return new int[]{19, 18, 17, 16, -804651006, 51, 4342354, -804650998, 100, 102, 103, 104, 106, 107, 108, 20000927};
   }
}
