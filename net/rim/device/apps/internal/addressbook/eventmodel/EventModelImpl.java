package net.rim.device.apps.internal.addressbook.eventmodel;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.EventModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.RecurUtil;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.cldc.util.CalendarExtensions;

public class EventModelImpl implements EventModel, FieldProvider, ConversionProvider, FieldChangeListener {
   long _eventDate = -1;
   int _eventType = -1;

   @Override
   public long getEventDate() {
      return this._eventDate;
   }

   @Override
   public void setEventDate(long date) {
      this._eventDate = date;
   }

   @Override
   public int getEventType() {
      return this._eventType;
   }

   @Override
   public void setEventType(int eventType) {
      this._eventType = eventType;
   }

   @Override
   public Field getField(Object context) {
      if (!ContextObject.getFlag(context, 0)) {
         if (this._eventDate != -1) {
            DateField date = (DateField)(new Object(this.getDescription(), this._eventDate, DateFormat.getInstance(48), 9007199254740992L));
            date.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
            return date;
         } else {
            return null;
         }
      } else {
         ResourceBundle calendarResources = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
         Object[] choices = new Object[]{CommonResources.getString(9061), calendarResources.getString(106)};
         ObjectChoiceField ocf = (ObjectChoiceField)(new Object(this.getDescription(), choices, this._eventDate != -1 ? 1 : 0));
         ocf.setChangeListener(this);
         VerticalFieldManager dateVFM = (VerticalFieldManager)(new Object(1152921504606846976L));
         dateVFM.add(ocf);
         if (this._eventDate != -1) {
            DateField date = (DateField)(new Object("", this._eventDate, DateFormat.getInstance(48)));
            date.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
            dateVFM.add(date);
         }

         dateVFM.setCookie(this);
         return dateVFM;
      }
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addField(this._eventType, EventModelFactory.longToString(this.getEventDate()));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      if (!(field instanceof Object)) {
         return false;
      }

      VerticalFieldManager dateVFM = (VerticalFieldManager)field;
      int fieldCount = dateVFM.getFieldCount();
      String subject = null;
      long newDate;
      if (fieldCount > 1) {
         DateField dateField = (DateField)dateVFM.getField(1);
         newDate = dateField.getDate();
         Object selectedItem = ContextObject.get(context, 3696141428889703675L);
         if (selectedItem instanceof Object) {
            subject = this.getSubject((RIMModel)selectedItem);
         }
      } else {
         newDate = -1;
      }

      if (newDate != -1 || this._eventDate != -1) {
         this.updateCalendar(ContextObject.get(context, -4055106280780392421L), newDate, subject, -1);
      }

      this.setEventDate(newDate);
      return newDate != -1;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public int getOrder(Object context) {
      boolean editable = ContextObject.getFlag(context, 0);
      if (this._eventType == 82) {
         return editable ? 4350 : 5100;
      } else {
         return editable ? 4375 : 5125;
      }
   }

   @Override
   public void fieldChanged(Field f, int context) {
      if (f instanceof Object) {
         ObjectChoiceField cf = (ObjectChoiceField)f;
         VerticalFieldManager dateVFM = (VerticalFieldManager)cf.getManager();
         int index = cf.getSelectedIndex();
         int fieldCount = dateVFM.getFieldCount();
         if (index == 1) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
            DateTimeUtilities.zeroCalendarTime(cal);
            DateField df = (DateField)(new Object("", ((CalendarExtensions)cal).getTimeLong(), DateFormat.getInstance(48)));
            df.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
            dateVFM.add(df);
            return;
         }

         if (index == 0 && fieldCount > 1) {
            dateVFM.delete(dateVFM.getField(1));
         }
      }
   }

   @Override
   public void updateEventDate(long newDate, Object context) {
      if (context instanceof Object) {
         RIMModel model = (RIMModel)ContextObject.get(context, 254);
         if (model != null) {
            String subject = this.getSubject(model);
            if (newDate != -1 || this._eventDate != -1) {
               this.updateCalendar(ContextObject.get(context, -4055106280780392421L), newDate, subject, -1);
            }

            this.setEventDate(newDate);
         }
      }
   }

   public EventModelImpl(Object initialData) {
      if (initialData instanceof Object) {
         RIMModel model = (RIMModel)ContextObject.get(initialData, 254);
         if (model != null) {
            long[] values = (long[])ContextObject.get(initialData, 27786846527369561L);
            Object type = ContextObject.get(initialData, -4054673099568009991L);
            if (type instanceof Object) {
               this._eventType = type;
            }

            if (values != null && values.length == 2) {
               Calendar cal = Calendar.getInstance();
               cal.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
               ((CalendarExtensions)cal).setTimeLong(values[0]);
               DateTimeUtilities.zeroCalendarTime(cal);
               this._eventDate = ((CalendarExtensions)cal).getTimeLong();
               String name = this.getSubject(model);
               this.updateCalendar(null, this._eventDate, name, values[1]);
            }
         }
      }
   }

   private void updateCalendar(Object originalAddress, long date, String subject, long reminder) {
      long dateToSearch = -1;
      String subjectToSearch = null;
      boolean subjectChanged = false;
      boolean dateChanged = false;
      boolean deleteEvent = false;
      if (originalAddress instanceof Object) {
         AddressCardModel originalACM = (AddressCardModel)originalAddress;
         EventModel originalEvent = originalACM.getEvent(this._eventType);
         if (originalEvent != null) {
            dateToSearch = originalEvent.getEventDate();
            subjectToSearch = this.getSubject(originalACM);
            if (date == -1) {
               deleteEvent = true;
            } else {
               if (dateToSearch != date) {
                  dateChanged = true;
               }

               if (!subjectToSearch.equals(subject)) {
                  subjectChanged = true;
               }

               if (!subjectChanged && !dateChanged) {
                  return;
               }
            }
         }
      }

      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("sync");
      ServiceRecord calendarKey = records != null && records.length != 0 ? EventUtilities.getCICALServiceFromOtherService(records[0]) : null;
      CalendarService calendarService = calendarKey != null
         ? CalendarServiceManager.getInstance().findCalendarServiceByKey(calendarKey)
         : CalendarServiceManager.getInstance().getDefaultCalendarService();
      CalDB calDB = calendarService.getCalendarDatabase();
      TimeZone currentTZ = TimeZone.getDefault();
      date = EventUtilities.adjustAllDayDate(date, currentTZ);
      Event event = null;
      if (dateChanged || subjectChanged || deleteEvent) {
         dateToSearch = EventUtilities.adjustAllDayDate(dateToSearch, currentTZ);
         event = findCalendarEvent(calDB, dateToSearch, subjectToSearch);
         if (event != null) {
            if (deleteEvent) {
               EventUtilities.removeEvent(event, false);
               return;
            }

            if (dateChanged) {
               if (ObjectGroup.isInGroup(event)) {
                  event = (Event)ObjectGroup.expandGroup(event);
               }

               event.setStartDate(date, currentTZ);
            }

            if (subjectChanged) {
               if (ObjectGroup.isInGroup(event)) {
                  event = (Event)ObjectGroup.expandGroup(event);
               }

               event.setSubject(subject);
            }
         }
      } else if (date != -1 && subject != null) {
         event = (Event)FactoryUtil.createInstance(-1986287563994289176L, null);
         event.setAllDayFlag(true);
         event.setTimeZoneID(currentTZ.getID());
         event.setStartDate(date, currentTZ);
         event.setInstanceDuration(86400000);
         event.setSubject(subject);
         event.setFreeBusy((byte)0);
         event.setSensitivity((byte)0);
         CalendarKey key = (CalendarKey)(new Object(calendarService.getUniqueServiceID(), calendarService.getUniqueServiceID()));
         event.setCalendarKey(key);
         Recur recur = (Recur)(new Object());
         recur.setRecurType((byte)4);
         if (RecurUtil.getRecurrenceCapabilities().finiteRecurrencesOnly) {
            Calendar cal = Calendar.getInstance();
            ((CalendarExtensions)cal).setTimeLong(System.currentTimeMillis());
            int year = cal.get(1) + 10;
            ((CalendarExtensions)cal).setTimeLong(date);
            cal.set(1, year);
            recur.setEndDate(((CalendarExtensions)cal).getTimeLong());
         }

         event.setRecurrence(recur);
         if (reminder != -1) {
            ReminderModel rm = event.getReminderData();
            if (rm != null) {
               rm.setTime(reminder);
            }
         }
      }

      if (event != null) {
         synchronized (calDB.getLockObject()) {
            calDB.addWithAction(event, 1);
         }
      }
   }

   private static Event findCalendarEvent(CalDB calDB, long date, String subject) {
      Event event = null;
      SimpleSortingVector events = (SimpleSortingVector)(new Object());
      TimeZone currentTZ = TimeZone.getDefault();
      calDB.getElementsVisibleDuring(date, 86400000, currentTZ, events);

      for (int i = 0; i < events.size(); i++) {
         Object object = events.elementAt(i);
         if (object instanceof Object) {
            event = (Event)calDB.get(((UniqueIDProvider)object).getLUID(null));
            if (subject.equalsIgnoreCase(event.getSubject())) {
               return event;
            }
         }
      }

      return null;
   }

   private String getSubject(RIMModel model) {
      StringBuffer buffer = (StringBuffer)(new Object());
      if (model instanceof Object) {
         AddressCardModel addressCard = (AddressCardModel)model;
         model = addressCard.getName();
         if (model == null) {
            model = addressCard.getCompanyInfo();
         }
      }

      if (!(model instanceof Object)) {
         if (model instanceof Object) {
            buffer.append(model.toString());
         }
      } else {
         PersonNameModel pnm = (PersonNameModel)model;
         if (pnm.getFirstName() != null) {
            buffer.append(pnm.getFirstName());
            if (pnm.getLastName() != null) {
               buffer.append(' ').append(pnm.getLastName());
            }
         }
      }

      Object[] parms = new Object[]{buffer.toString()};
      int resId = this._eventType == 82 ? 1803 : 1802;
      return MessageFormat.format(AddressBookResources.getString(resId), parms);
   }

   private String getDescription() {
      return this._eventType == 82 ? AddressBookResources.getString(1800) : AddressBookResources.getString(1801);
   }
}
