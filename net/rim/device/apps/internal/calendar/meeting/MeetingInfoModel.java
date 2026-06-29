package net.rim.device.apps.internal.calendar.meeting;

import java.util.Enumeration;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressMatch;
import net.rim.device.apps.api.addressbook.AddressReference;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DescriptionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.ResolvedStatusProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.sync.Checksumable;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.calendar.eventprovider.DeleteEventVerb;
import net.rim.device.apps.internal.calendar.eventprovider.DeleteEventVerb$DeleteEventVerbListener;
import net.rim.device.apps.internal.calendar.eventprovider.EventViewer;
import net.rim.device.apps.internal.calendar.eventprovider.SaveEventVerb;
import net.rim.device.apps.internal.calendar.eventprovider.SaveEventVerb$SaveEventVerbListener;
import net.rim.device.internal.ui.IconCollection;
import net.rim.vm.Array;

class MeetingInfoModel
   implements PersistableRIMModel,
   MeetingInfo,
   Checksumable,
   Copyable,
   FieldProvider,
   VerbProvider,
   DescriptionProvider,
   SaveEventVerb$SaveEventVerbListener,
   EncryptableProvider,
   MatchProvider {
   Attendee[] _attendees;
   private byte _capabilities = 1;
   private static final IconCollection STATUS_ICONS = IconCollection.get("net_rim_Calendar_MeetingStatus", 1);

   @Override
   public boolean proceedWithSave(Event event) {
      int n = this.getAttendeeCount();
      int resolvedCount = 0;
      int unresolvedCount = 0;
      Attendee[] unresolved = new Object[n];

      for (int i = 0; i < n; i++) {
         Attendee attendee = this._attendees[i];
         Object address = this._attendees[i].getAddress();
         resolvedCount++;
         if (address instanceof Object) {
            ResolvedStatusProvider rsp = (ResolvedStatusProvider)address;
            if (!rsp.isResolved()) {
               resolvedCount--;
               unresolved[unresolvedCount++] = attendee;
            }
         }
      }

      if (unresolvedCount == 0) {
         return true;
      }

      if (resolvedCount != 0) {
         String continueText = this.getCalendarResource(700);
         if (Dialog.ask(3, continueText, -1) == 4) {
            for (int i = 0; i < unresolvedCount; i++) {
               this.removeAttendee(unresolved[i]);
            }

            return true;
         }
      } else {
         String cannotContinueText = this.getCalendarResource(701);
         Dialog.ask(0, cannotContinueText);
      }

      return false;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      Event event = null;
      EventViewer eventViewer = (EventViewer)ContextObject.get(context, 4143325197084129318L);
      if (eventViewer != null && eventViewer.isAllDayChecked() && !eventViewer.getCICALConfiguration().canInviteToAllDayMeetings()) {
         return null;
      }

      ServiceRecord cmimeServiceRecord = null;
      if (eventViewer != null) {
         cmimeServiceRecord = ServiceBook.getSB().getRecordByUidAndCid(eventViewer.getCICALConfiguration().getUID(), "CMIME");
      }

      if (ContextObject.getFlag(context, 87)) {
         event = (Event)ContextObject.get(context, 424670468422402792L);
         CICALConfiguration cicalConfiguration = CalendarServiceManager.getInstance().getCICALConfiguration(event);
         if (event.isMeeting() && this.meetingCanBeModified() && cicalConfiguration.isMeetingSyncEnabled()) {
            Verb[] defaultVerbs = (Object[])ContextObject.get(context, 248);
            Verb deleteVerb = defaultVerbs[1];
            if (deleteVerb instanceof DeleteEventVerb) {
               DeleteEventVerb verb = (DeleteEventVerb)deleteVerb;
               ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
               DeleteEventVerb$DeleteEventVerbListener listener = (DeleteEventVerb$DeleteEventVerbListener)ar.waitFor(8503322669810003080L);
               verb.setDeleteEventVerbListener(listener);
            }
         }

         return null;
      } else {
         event = (Event)ContextObject.get(context, 424670468422402792L);
         Verb defaultVerb = null;
         Array.resize(verbs, 0);
         if (event instanceof Object) {
            MeetingInfo meetingInfo = event.getMeetingInfo();
            Field field = (Field)ContextObject.get(context, 9045827404276417370L);
            if (field != null) {
               VerticalFieldManager vfm = locateMeetingInfoFields(field.getScreen());
               if (vfm != null) {
                  String subject = eventViewer != null ? eventViewer.getViewerSubject() : "";
                  Field attendeeField = locateAttendeeField(field);
                  int attendeeCount = vfm.getFieldCount();
                  if (attendeeCount > 0 && (attendeeField == null || attendeeCount != 1) && cmimeServiceRecord != null) {
                     Array.resize(verbs, verbs.length + 1);
                     verbs[verbs.length - 1] = new EmailAttendeeVerb(cmimeServiceRecord, subject, vfm, null);
                  }
               }
            }

            if (!meetingInfo.meetingCanBeModified()) {
               return null;
            }

            CICALConfiguration cicalConfiguration = CalendarServiceManager.getInstance().getCICALConfiguration(event);
            if (!cicalConfiguration.isMeetingSyncEnabled()) {
               return null;
            }

            SaveEventVerb saveVerb = (SaveEventVerb)ContextObject.get(context, SaveEventVerb.SAVE_EVENT_KEY);
            if (saveVerb != null) {
               saveVerb.addSaveEventVerbListener(this);
            }

            if (meetingInfo.getAttendeeCount() > 0 && saveVerb != null) {
               CalDB calDB = CalendarServiceManager.getInstance().findCalendarDatabase(event);
               Event realEvent = null;
               synchronized (calDB.getLockObject()) {
                  realEvent = (Event)calDB.get(event.getLUID());
                  if (realEvent == null && event.getRelatedLUID() != 0) {
                     realEvent = (Event)calDB.get(event.getRelatedLUID());
                  }
               }

               if (realEvent != null) {
                  MeetingInfo realMeetingInfo = realEvent.getMeetingInfo();
                  if (realMeetingInfo != null && realMeetingInfo.getAttendeeCount() > 0) {
                     ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
                     SaveEventVerb$SaveEventVerbListener listener = (SaveEventVerb$SaveEventVerbListener)ar.waitFor(8503322669810003080L);
                     saveVerb.addSaveEventVerbListener(listener);
                  }
               }
            }

            field = (Field)ContextObject.get(context, 9045827404276417370L);
            if (field != null) {
               VerticalFieldManager vfm = locateMeetingInfoFields(field.getScreen());
               if (vfm != null) {
                  if (eventViewer != null) {
                     eventViewer.getViewerSubject();
                  } else {
                     String subject = "";
                  }

                  Field attendeeField = locateAttendeeField(field);
                  if (ContextObject.getFlag(context, 0)) {
                     Array.resize(verbs, verbs.length + 1);
                     verbs[verbs.length - 1] = new InviteAttendeeVerb(vfm);
                     if (attendeeField != null) {
                        Array.resize(verbs, verbs.length + 2);
                        verbs[verbs.length - 2] = new ChangeAttendeeVerb(attendeeField);
                        verbs[verbs.length - 1] = new DeleteAttendeeVerb(attendeeField);
                     }
                  }
               }
            }
         }

         return defaultVerb;
      }
   }

   @Override
   public int match(Object criteria) {
      SearchCriterion crit = (SearchCriterion)criteria;
      if (crit.getType() != 5) {
         return -1;
      }

      for (int i = this.getAttendeeCount() - 1; i >= 0; i--) {
         if (AddressMatch.match((AddressReference)this._attendees[i], crit) == 1) {
            return 1;
         }
      }

      return 0;
   }

   @Override
   public void addAttendee(Attendee attendee) {
      if (this._attendees == null) {
         this._attendees = new Object[0];
      } else {
         synchronized (this._attendees) {
            if (Arrays.getIndex(this._attendees, attendee) != -1) {
               return;
            }
         }
      }

      synchronized (this._attendees) {
         Arrays.add(this._attendees, attendee);
      }
   }

   @Override
   public void removeAttendee(Attendee attendee) {
      if (this._attendees != null) {
         Arrays.remove(this._attendees, attendee);
         if (this._attendees.length == 0) {
            this._attendees = null;
         }
      }
   }

   @Override
   public void removeAttendees() {
      this._attendees = null;
   }

   @Override
   public Enumeration getAttendees() {
      return (Enumeration)(this._attendees != null ? new Object(this._attendees) : null);
   }

   @Override
   public Attendee getAttendee(String emailAddress) {
      if (this._attendees != null) {
         synchronized (this._attendees) {
            for (int i = this._attendees.length - 1; i >= 0; i--) {
               Attendee attendee = this._attendees[i];
               if (StringUtilities.compareToIgnoreCase(attendee.getAddress().toString(), emailAddress) == 0) {
                  return attendee;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public Attendee getOrganizer() {
      if (this._attendees != null) {
         synchronized (this._attendees) {
            for (int i = this._attendees.length - 1; i >= 0; i--) {
               Attendee attendee = this._attendees[i];
               if (attendee.getType() == 0) {
                  return attendee;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean hasOrganizer() {
      return this.getOrganizer() != null;
   }

   @Override
   public boolean meetingCanBeModified() {
      return (this._capabilities & 1) != 0;
   }

   @Override
   public long getChecksum(Object context) {
      long checksum = 0;
      if (this._attendees != null) {
         synchronized (this._attendees) {
            for (int i = this._attendees.length - 1; i >= 0; i--) {
               Attendee attendee = this._attendees[i];
               checksum ^= AddressBookServices.getReverseLookupCode(attendee.getAddress().toString());
            }

            return checksum;
         }
      } else {
         return checksum;
      }
   }

   @Override
   public Object copy() {
      MeetingInfoModel theCopy = new MeetingInfoModel();
      Enumeration attendees = this.getAttendees();
      if (attendees != null) {
         while (attendees.hasMoreElements()) {
            Attendee attendee = (Attendee)attendees.nextElement();
            if (attendee instanceof Object) {
               theCopy.addAttendee((Attendee)((Copyable)attendee).copy());
            }
         }
      }

      theCopy.setCapabilities(this.getCapabilities());
      return theCopy;
   }

   @Override
   public int getOrder(Object context) {
      return 2000;
   }

   @Override
   public Field getField(Object context) {
      Attendee[] attendees = this._attendees;
      int count = attendees != null ? attendees.length : 0;
      int[] ordering = new int[count];
      Field[] fields = new Object[count];
      int fieldCount = 0;

      for (int i = 0; i < count; i++) {
         Attendee attendee = attendees[i];
         Field field = MeetingUtilities.createAttendeeField(attendee, context);
         int order = MeetingUtilities.getAttendeeFieldOrder(attendee, context);
         if (field != null) {
            ordering[fieldCount] = order;
            fields[fieldCount] = field;
            fieldCount++;
         }
      }

      if (fieldCount > 0) {
         Array.resize(ordering, fieldCount);
         Array.resize(fields, fieldCount);
         Arrays.sort(ordering, 0, ordering.length, fields);
      }

      Manager vfm = new MeetingInfoModel$NotifyVerticalFieldManager();

      for (int i = 0; i < fieldCount; i++) {
         vfm.add(fields[i]);
      }

      vfm.setCookie(this);
      return vfm;
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      if (field instanceof Object && this.meetingCanBeModified()) {
         Manager manager = (Manager)field;
         int fieldCount = manager.getFieldCount();
         this.removeAttendees();

         for (int i = 0; i < fieldCount; i++) {
            Field attendeeField = manager.getField(i);
            Object cookie = attendeeField.getCookie();
            if (cookie instanceof Object) {
               Attendee attendee = (Attendee)cookie;
               if (attendee.getType() != 0) {
                  attendee.setType(1);
               }

               this.addAttendee(attendee);
            }
         }
      }

      return true;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public byte getCapabilities() {
      return this._capabilities;
   }

   @Override
   public String getStringForField(long field) {
      return null;
   }

   @Override
   public String getStringForField(long field, long data) {
      return null;
   }

   @Override
   public int getIconsForField(long field, IconCollection[] icons, int[][][] indices) {
      if (field != 7380487202915104824L) {
         return 0;
      }

      if (this._attendees == null) {
         return 0;
      }

      int numIcons = icons.length;
      Array.resize(icons, numIcons + 1);
      Array.resize(indices, numIcons + 1);
      icons[numIcons] = STATUS_ICONS;
      indices[numIcons] = (int[][])(new int[]{0, -804651005, 0, 1});
      return 1;
   }

   @Override
   public byte getProperties() {
      return 0;
   }

   @Override
   public void setCapabilities(byte capabilities) {
      this._capabilities = capabilities;
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      if (this._attendees == null) {
         return true;
      }

      int numAttendees = this._attendees.length;

      for (int i = 0; i < numAttendees; i++) {
         Attendee attendee = this._attendees[i];
         if (attendee instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)attendee;
            if (!encryptable.checkCrypt(compress, encrypt)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      if (this._attendees == null) {
         return null;
      }

      int numAttendees = this._attendees.length;

      for (int i = 0; i < numAttendees; i++) {
         Attendee attendee = this._attendees[i];
         if (attendee instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)attendee;
            Object newObject = encryptable.reCrypt(compress, encrypt);
            if (newObject != null) {
               this._attendees[i] = (Attendee)newObject;
            }
         }
      }

      return null;
   }

   @Override
   public int getAttendeeCount() {
      return this._attendees != null ? this._attendees.length : 0;
   }

   private static Field locateAttendeeField(Field field) {
      while (field != null) {
         Manager manager = field.getManager();
         if ((manager == null || !(manager.getCookie() instanceof Object)) && field.getCookie() instanceof Object) {
            return field;
         }

         field = manager;
      }

      return null;
   }

   private static VerticalFieldManager locateMeetingInfoFields(Field field) {
      if (field instanceof Object && field.getCookie() instanceof MeetingInfoModel) {
         return (VerticalFieldManager)field;
      }

      if (field instanceof Object) {
         Manager manager = (Manager)field;
         int fieldCount = manager.getFieldCount();

         for (int i = fieldCount - 1; i >= 0; i--) {
            Field subField = manager.getField(i);
            VerticalFieldManager vfm = locateMeetingInfoFields(subField);
            if (vfm != null) {
               return vfm;
            }
         }
      }

      return null;
   }

   private String getCalendarResource(int id) {
      ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      return rb.getString(id);
   }
}
