package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarFolder;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.system.InternalServices;

class EventViewer$ConflictCheckingThread extends Thread {
   boolean _alive;
   boolean _checkForConflicts;
   boolean _abort;
   boolean _sameCalendar;
   Object _lock;
   private final EventViewer this$0;

   EventViewer$ConflictCheckingThread(EventViewer _1) {
      this.this$0 = _1;
      this._alive = true;
      this._sameCalendar = false;
      this._lock = new Object();
   }

   public void startConflictingChecking() {
      synchronized (this._lock) {
         this._checkForConflicts = true;
         this._abort = true;
         this._lock.notify();
      }
   }

   public void stopConflictThread() {
      this._alive = false;
      synchronized (this._lock) {
         this._abort = true;
         this._lock.notify();
      }
   }

   @Override
   public void run() {
      while (this._alive) {
         boolean doCheck = false;
         synchronized (this._lock) {
            if (!this._checkForConflicts) {
               label42:
               try {
                  this._lock.wait();
               } finally {
                  break label42;
               }
            } else {
               this._checkForConflicts = false;
               this._abort = false;
               doCheck = true;
            }
         }

         if (doCheck) {
            this.checkForConflictingAppointments();
         }
      }
   }

   private void showConflictMessage(String conflictMessage) {
      synchronized (this.this$0._uiApp.getAppEventLock()) {
         this.this$0._conflictsBlock.deleteAll();
         if (conflictMessage != null) {
            this.this$0._conflictsField.setText(conflictMessage);
            this.this$0._conflictsBlock.add(this.this$0._conflictsField);
         }
      }
   }

   private void checkForConflictingAppointments() {
      if (!this.this$0._viewingAppointmentStatus) {
         long startedChecking = InternalServices.getUptime();
         boolean messageShown = false;
         ServiceIdentifier[] calendarServices = CalendarServiceManager.getInstance().getCalendarServices(true);
         int calendarCount = calendarServices.length - 1;
         int conflictCount = 0;
         String[] conflictArray = new Object[conflictCount];
         int adjacentCount = 0;
         String[] adjacentArray = new Object[adjacentCount];
         boolean allDay = this.this$0._allDayFlag.getChecked();
         if (this.this$0._newEvent || !allDay && this.this$0._wasAllDay || this.this$0._eventData.getFreeBusy() != 0) {
            int percentComplete = 0;
            ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
            String conflictMessage = MessageFormat.format(rb.getString(640), new Object[]{Integer.toString(percentComplete)});
            TimeZone tz = allDay ? EventViewer._gmtTZ : this.this$0._sharedTZ;
            Calendar cal = this.this$0._cal;
            cal.setTimeZone(tz);
            ((CalendarExtensions)cal).setTimeLong(this.this$0._start.getDate());
            if (allDay) {
               DateTimeUtilities.zeroCalendarTime(cal);
            }

            long startRange = ((CalendarExtensions)cal).getTimeLong();
            ((CalendarExtensions)cal).setTimeLong(this.this$0._end.getDate());
            if (allDay) {
               cal.set(5, cal.get(5) + 1);
               DateTimeUtilities.zeroCalendarTime(cal);
            }

            long endRange = ((CalendarExtensions)cal).getTimeLong();
            boolean recurring = this.this$0._recurCopy.getRecurType() != 0;
            boolean appointmentInThePast = endRange < System.currentTimeMillis();
            boolean done = false;
            long originalStart = startRange;
            long instanceDuration = endRange - startRange;
            long conflictCheckingStartTime = System.currentTimeMillis();
            if (recurring) {
               long endOfRecurrence = this.this$0._recurCopy.getEndDate();
               if (this.this$0._recurCopy.getInclusionCount() > 0) {
                  long possibleEnd = this.this$0._recurCopy.getInclusion(this.this$0._recurCopy.getInclusionCount() - 1);
                  possibleEnd += instanceDuration;
                  if (possibleEnd > endOfRecurrence) {
                     endOfRecurrence = possibleEnd;
                  }
               }

               appointmentInThePast = this.this$0._recurCopy.isFinite() ? endOfRecurrence < System.currentTimeMillis() : false;
               long timeToSearchFrom = conflictCheckingStartTime - 86400000;
               boolean validInstance = EventImpl._recurCalc
                  .getAnInstance(timeToSearchFrom, true, this.this$0._recurHandle, originalStart, instanceDuration, this.this$0._recurCopy, tz);
               if (validInstance) {
                  startRange = this.this$0._recurHandle._handle;
                  endRange = startRange + instanceDuration;
               }
            }

            long instanceStartedCheckingAt = startRange;
            int approximateInstances = 60;
            long rangeLimit = 5184000000L;
            switch (this.this$0._recurCopy.getRecurType()) {
               case 1:
                  break;
               case 2:
                  rangeLimit = 30758400000L;
                  approximateInstances = 52;
                  break;
               case 3:
                  rangeLimit = 153792000000L;
                  break;
               case 4:
               default:
                  rangeLimit = 307584000000L;
                  approximateInstances = 10;
            }

            int instance = 1;
            long lastUpdatedScreen = InternalServices.getUptime();
            if (!allDay) {
               while (!done && conflictCount < 2 && !this._abort) {
                  percentComplete = instance * 100 / approximateInstances;
                  if (percentComplete > 100) {
                     percentComplete = 100;
                  }

                  conflictMessage = MessageFormat.format(rb.getString(640), new Object[]{Integer.toString(percentComplete)});
                  long uptime = InternalServices.getUptime();
                  if (uptime - lastUpdatedScreen > 2000 || uptime - startedChecking > 5000) {
                     if (!messageShown && uptime - startedChecking > 5000) {
                        this.showConflictMessage(conflictMessage);
                        messageShown = true;
                     } else {
                        lastUpdatedScreen = InternalServices.getUptime();
                        synchronized (this.this$0._uiApp.getAppEventLock()) {
                           this.this$0._conflictsField.setText(conflictMessage);
                        }
                     }
                  }

                  for (int h = 0; h < calendarServices.length; h++) {
                     CalendarService service = (CalendarService)calendarServices[h];
                     Object[] events = service.getCalendarDatabase().getAllEventsInRange(startRange, endRange);
                     if (events != null) {
                        for (int i = 0; i < events.length; i++) {
                           Event e = (Event)events[i];
                           if (e.getFreeBusy() != 0
                              && e.getLUID() != this.this$0._eventData.getLUID()
                              && e.getLUID() != this.this$0._eventData.getRelatedLUID()) {
                              long start = e.getStartDate(tz);
                              long end = start + e.getInstanceDuration();
                              int checkCount = 1;
                              boolean firstCheck = true;

                              while (checkCount > 0) {
                                 if (e.isRecurring()) {
                                    Recur recurCopy = e.getReadOnlyRecurrence();
                                    if (!firstCheck) {
                                       if (EventImpl._recurCalc
                                          .getAnInstance(
                                             end + 1,
                                             true,
                                             this.this$0._recurHandle,
                                             e.getStartDate(null),
                                             e.getInstanceDuration(),
                                             recurCopy,
                                             TimeZone.getTimeZone(e.getTimeZoneID())
                                          )) {
                                          start = this.this$0._recurHandle._handle;
                                          end = start + e.getInstanceDuration();
                                       }
                                    } else {
                                       firstCheck = false;
                                       if (EventImpl._recurCalc
                                          .getAnInstance(
                                             startRange - 1,
                                             false,
                                             this.this$0._recurHandle,
                                             e.getStartDate(null),
                                             e.getInstanceDuration(),
                                             recurCopy,
                                             TimeZone.getTimeZone(e.getTimeZoneID())
                                          )) {
                                          start = this.this$0._recurHandle._handle;
                                          end = start + e.getInstanceDuration();
                                          checkCount = 3;
                                       } else if (EventImpl._recurCalc
                                          .getAnInstance(
                                             startRange - 1,
                                             true,
                                             this.this$0._recurHandle,
                                             e.getStartDate(null),
                                             e.getInstanceDuration(),
                                             recurCopy,
                                             TimeZone.getTimeZone(e.getTimeZoneID())
                                          )) {
                                          start = this.this$0._recurHandle._handle;
                                          end = start + e.getInstanceDuration();
                                          checkCount = 2;
                                       }
                                    }
                                 }

                                 checkCount--;
                                 if (start == endRange || end == startRange) {
                                    adjacentCount++;
                                    this.updateConflictAdjacentArray(adjacentArray, service, e.getCalendarKey().getCalendarFolderID());
                                 } else if (end > startRange && start < endRange) {
                                    conflictCount++;
                                    this.updateConflictAdjacentArray(conflictArray, service, e.getCalendarKey().getCalendarFolderID());
                                    if (recurring && e.isRecurring()) {
                                       conflictCount++;
                                       this.updateConflictAdjacentArray(conflictArray, service, e.getCalendarKey().getCalendarFolderID());
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  long temp = startRange - instanceStartedCheckingAt;
                  if (recurring
                     && EventImpl._recurCalc
                        .getFromKnownInstance(startRange, true, this.this$0._recurHandle, originalStart, instanceDuration, this.this$0._recurCopy, tz)
                     && temp < rangeLimit
                     && instance <= approximateInstances) {
                     startRange = this.this$0._recurHandle._handle;
                     endRange = startRange + instanceDuration;
                     instance++;
                  } else {
                     done = true;
                  }
               }
            }

            if (conflictArray.length > 0) {
            }

            if (adjacentArray.length > 0) {
            }

            synchronized (this.this$0._uiApp.getAppEventLock()) {
               this.this$0._conflictsBlock.deleteAll();
               if (conflictCount > 1 && recurring) {
                  if (calendarCount <= 1) {
                     conflictMessage = rb.getString(119);
                  } else if (this._sameCalendar) {
                     conflictMessage = rb.getString(125);
                  } else {
                     conflictMessage = MessageFormat.format(rb.getString(614), new Object[]{this.buildCalendarNameList(conflictArray)});
                  }

                  this.this$0._conflictsField.setText(conflictMessage);
               } else if (conflictCount > 0) {
                  if (calendarCount <= 1) {
                     conflictMessage = rb.getString(117);
                  } else if (this._sameCalendar) {
                     conflictMessage = rb.getString(121);
                  } else {
                     conflictMessage = MessageFormat.format(rb.getString(616), new Object[]{this.buildCalendarNameList(conflictArray)});
                  }

                  this.this$0._conflictsField.setText(conflictMessage);
               } else if (adjacentCount > 1 && recurring) {
                  if (calendarCount <= 1) {
                     conflictMessage = rb.getString(118);
                  } else if (this._sameCalendar) {
                     conflictMessage = rb.getString(124);
                  } else {
                     conflictMessage = MessageFormat.format(rb.getString(615), new Object[]{this.buildCalendarNameList(adjacentArray)});
                  }

                  this.this$0._conflictsField.setText(conflictMessage);
               } else if (adjacentCount > 0) {
                  if (calendarCount <= 1) {
                     conflictMessage = rb.getString(116);
                  } else if (this._sameCalendar) {
                     conflictMessage = rb.getString(120);
                  } else {
                     conflictMessage = MessageFormat.format(rb.getString(617), new Object[]{this.buildCalendarNameList(adjacentArray)});
                  }

                  this.this$0._conflictsField.setText(conflictMessage);
               } else {
                  if (!appointmentInThePast) {
                     return;
                  }

                  this.this$0._conflictsField.setText(rb.getString(618));
               }

               this.this$0._conflictsBlock.add(this.this$0._conflictsField);
            }
         }
      }
   }

   private void updateConflictAdjacentArray(String[] array, CalendarService service, long folderID) {
      CalendarFolder folder = service.getCalendarFolder(folderID);
      String calendarName = ((StringBuffer)(new Object())).append(service.getServiceName()).append(folder.getFolderNameSuffix()).toString();
      if (!Arrays.contains(array, calendarName)) {
         Arrays.add(array, calendarName);
      }

      if (this.this$0._eventData.getCalendarKey().getCalendarServiceID() == service.getUniqueServiceID()
         && this.this$0._eventData.getCalendarKey().getCalendarFolderID() == folderID) {
         this._sameCalendar = true;
      }
   }

   private String buildCalendarNameList(String[] array) {
      StringBuffer sb = (StringBuffer)(new Object());

      for (int i = 0; i < array.length; i++) {
         sb.append(array[i]);
         sb.append(", ");
      }

      if (sb.length() > 1) {
         sb.deleteCharAt(sb.length() - 1);
         sb.deleteCharAt(sb.length() - 1);
      }

      return sb.toString();
   }
}
