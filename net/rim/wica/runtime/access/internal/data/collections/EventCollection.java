package net.rim.wica.runtime.access.internal.data.collections;

import java.util.Enumeration;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.utility.props.LongProp;
import net.rim.wica.common.builtindata.componentdefn.EventCompDef;
import net.rim.wica.runtime.access.internal.data.enumeration.FreeBusyEnumConverter;
import net.rim.wica.runtime.access.internal.data.handlers.BooleanFieldHandler;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;
import net.rim.wica.runtime.access.internal.data.handlers.LongFieldHandler;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.component.InnerDataVector;

public class EventCollection extends StdCmpCollectionImpl implements InnerDataArrayFieldProvider {
   private CalDB _calendar = CalendarProxy.getInstance().getCalendarDatabase();
   private static final long REMINDER_NONE = -1L;
   private static Factory _eventFactory;
   private static Factory _reminderModelFactory;
   private static final long NUM_OF_MILLISECONDS_IN_DAY = 86400000L;

   public EventCollection(WicletEx wiclet) {
      super(wiclet, EventCompDef.getInstance());
   }

   @Override
   public void initFieldHandlers() {
      super._objectFieldHandlers = (IntHashtable)(new Object(6));
      super._objectFieldHandlers.put(3, new EventCollection$NoteHandler(null));
      super._objectFieldHandlers.put(1, new EventCollection$SummaryHandler(null));
      super._objectFieldHandlers.put(2, new EventCollection$LocationHandler(null));
      super._intFieldHandlers = (IntHashtable)(new Object(3));
      super._intFieldHandlers.put(0, new EventCollection$UIDHandler(null));
      super._intFieldHandlers.put(9, new EventCollection$FreeBusyHandler(null));
      super._longFieldHandlers = (IntHashtable)(new Object(4));
      super._longFieldHandlers.put(4, new EventCollection$StartHandler(null));
      super._longFieldHandlers.put(5, new EventCollection$EndHandler(null));
      super._longFieldHandlers.put(6, new EventCollection$AlarmHandler(null));
      super._booleanFieldHandlers = (IntHashtable)(new Object(1));
      super._booleanFieldHandlers.put(10, new EventCollection$AllDayHandler(null));
   }

   @Override
   public void loadItem(long dataHandle, Object item) {
      if (item instanceof Object) {
         this.setIntFieldValue(dataHandle, 0, ((IntFieldHandler)super._intFieldHandlers.get(0)).getValue(item));
         this.setIntFieldValue(dataHandle, 9, ((IntFieldHandler)super._intFieldHandlers.get(9)).getValue(item));
         this.setObjectFieldValue(dataHandle, 3, ((ObjectFieldHandler)super._objectFieldHandlers.get(3)).getValue(item));
         this.setObjectFieldValue(dataHandle, 1, ((ObjectFieldHandler)super._objectFieldHandlers.get(1)).getValue(item));
         this.setObjectFieldValue(dataHandle, 2, ((ObjectFieldHandler)super._objectFieldHandlers.get(2)).getValue(item));
         this.setLongFieldValue(dataHandle, 4, ((LongFieldHandler)super._longFieldHandlers.get(4)).getValue(item));
         this.setLongFieldValue(dataHandle, 5, ((LongFieldHandler)super._longFieldHandlers.get(5)).getValue(item));
         this.setLongFieldValue(dataHandle, 6, ((LongFieldHandler)super._longFieldHandlers.get(6)).getValue(item));
         this.setBooleanFieldValue(dataHandle, 10, ((BooleanFieldHandler)super._booleanFieldHandlers.get(10)).getValue(item));
      }
   }

   @Override
   public IntVector uidsInExternalDB() {
      int numItems = this._calendar.size();
      if (numItems < 1) {
         return null;
      }

      IntVector uidsInDB = (IntVector)(new Object(numItems));
      Enumeration e = this._calendar.getElements();

      while (e.hasMoreElements()) {
         Event item = (Event)e.nextElement();
         if (item != null) {
            uidsInDB.addElement(item.getUID());
         }
      }

      return uidsInDB;
   }

   @Override
   public void saveDeletedItems() {
      if (super._handles.size() == 0) {
         this._calendar.removeAll();
      } else {
         for (int i = super._deletedItems.size() - 1; i >= 0; i--) {
            Object eventToRemove = this.getDBItemFromHandle((long)super._defs.getId() << 32 | 4294967295L & super._deletedItems.elementAt(i));
            if (eventToRemove != null) {
               this._calendar.remove(eventToRemove);
            }
         }
      }
   }

   @Override
   public void saveModifiedItems() {
      this.saveItemsToDB(super._modifiedItems);
   }

   @Override
   public void saveCreatedItems() {
      this.saveItemsToDB(super._createdItems);
   }

   protected void saveItemsToDB(IntVector items) {
      for (int i = items.size() - 1; i >= 0; i--) {
         Event event = this.getEvent(items.elementAt(i));
         this._calendar.add(event);
      }
   }

   private Event getEvent(int handle) {
      Event event = (Event)_eventFactory.createInstance(null);
      if (event != null) {
         if (handle != -1) {
            event.setUID(handle);
         }

         long dataHandle = (long)super._defs.getId() << 32 | 4294967295L & handle;
         boolean isAllDay = this.getBooleanFieldValue(dataHandle, 10);
         event.setAllDayFlag(isAllDay);
         event.setSubject((String)this.getObjectFieldValue(dataHandle, 1));
         event.setLocation((String)this.getObjectFieldValue(dataHandle, 2));
         event.setNotes((String)this.getObjectFieldValue(dataHandle, 3));
         long startDate = this.getLongFieldValue(dataHandle, 4);
         long endDate = this.getLongFieldValue(dataHandle, 5);
         if (isAllDay) {
            long endDateRemainder = endDate % 86400000;
            if (endDateRemainder > 0) {
               endDate += 86400000 - endDateRemainder;
            }

            startDate -= startDate % 86400000;
         }

         event.setStartDate(startDate, null);
         endDate = endDate >= startDate ? endDate : startDate;
         event.setInstanceDuration(endDate - startDate);
         event.setFreeBusy((byte)FreeBusyEnumConverter.commonToDevice(this.getIntFieldValue(dataHandle, 9)));
         this.setReminder(event, dataHandle);
         this.setRepeatRule(event, dataHandle);
         this.setAttendees(event, dataHandle);
      }

      return event;
   }

   private void setAttendees(Event event, long dataHandle) {
      Object o = this.getObjectFieldValue(dataHandle, 8);
      DataCollection dc = super._wiclet.getDataCollection(super._defs.getFieldReferenceType(8));
      if (o instanceof InnerDataVector && dc instanceof AttendeeCollection) {
         InnerDataVector attendees = (InnerDataVector)o;
         AttendeeCollection attendeeCollection = (AttendeeCollection)dc;

         for (int i = attendees.size() - 1; i >= 0; i--) {
            attendeeCollection.saveAttendeeInEvent(attendees.elementAt(i), event);
         }
      }
   }

   private void setRepeatRule(Event event, long dataHandle) {
      DataCollection dc = super._wiclet.getDataCollection(4);
      if (dc instanceof RepeatRuleCollection) {
         long rrHandle = this.getReferenceField(dataHandle, 7);
         if (rrHandle != -1) {
            ((RepeatRuleCollection)dc).saveRepeatRuleToEvent(rrHandle, event);
         }
      }
   }

   private void setReminder(Event event, long dataHandle) {
      long reminderVal = this.getLongFieldValue(dataHandle, 6);
      if (reminderVal != -1) {
         LongProp reminder = (LongProp)_reminderModelFactory.createInstance(null);
         if (reminder != null) {
            reminder.setLong(this.getLongFieldValue(dataHandle, 4) - reminderVal);
            event.put(813899564474876953L, reminder);
         }
      }
   }

   @Override
   public Object getDBItemFromHandle(long dataHandle) {
      int uid = this.getHandle(dataHandle);
      if (uid != -1) {
         Enumeration e = this._calendar.getElements();

         while (e.hasMoreElements()) {
            Event item = (Event)e.nextElement();
            if (uid == item.getUID()) {
               return item;
            }
         }
      }

      return null;
   }

   @Override
   public void initializeVector(InnerDataVector vector) {
      if (vector.getField() == 8) {
         Object o = this.getDBItemFromHandle(vector.getHandle());
         if (o instanceof Object) {
            Event event = (Event)o;
            if (event.isMeeting()) {
               DataCollection dc = (DataCollection)vector.getTypeHandler();
               if (dc instanceof AttendeeCollection) {
                  AttendeeCollection adc = (AttendeeCollection)dc;
                  boolean ignoreTransactions = false;
                  if (super._transactions != null) {
                     ignoreTransactions = super._transactions.getIgnoreTransactions();
                     super._transactions.setIgnoreTransactions(true);
                  }

                  super._ignoreCAPExternalAccess = true;
                  Enumeration attendees = event.getMeetingInfo().getAttendees();

                  while (attendees.hasMoreElements()) {
                     Attendee attendee = (Attendee)attendees.nextElement();
                     long attendeeHandle = adc.create();
                     adc.loadItemFromAttendee(attendeeHandle, attendee);
                     vector.addElement(attendeeHandle, false);
                  }

                  super._ignoreCAPExternalAccess = false;
                  if (super._transactions != null) {
                     super._transactions.setIgnoreTransactions(ignoreTransactions);
                  }
               }
            }
         }
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _eventFactory = (Factory)ar.waitFor(-1986287563994289176L);
      _reminderModelFactory = (Factory)ar.waitFor(813899564474876953L);
   }
}
