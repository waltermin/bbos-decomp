package net.rim.device.apps.internal.calendar.meeting;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressReference;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.AttendeeFactory;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.AttendeeUtilities$EmailAddressComparator;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.framework.model.CompoundRecognizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.address.UseOnceAddressVerb;
import net.rim.device.apps.internal.blackberryemail.email.EmailComposeVerb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailBuilderApi;
import net.rim.vm.Array;

public final class MeetingUtilities {
   public static final long MEETING_LISTENER_SINGLETON = 8503322669810003080L;
   private static final long DONT_NOTIFY_ATTENDEES_LUID_LIST = 7708406907057731816L;
   public static final String ATTENDEE_LIST_TRUNCATED = "Truncated";

   private MeetingUtilities() {
   }

   public static final Field createAttendeeField(Attendee attendee, Object context) {
      int resId;
      switch (attendee.getType()) {
         case -1:
         case 1:
            resId = 604;
            break;
         case 0:
         default:
            resId = 600;
            break;
         case 2:
            resId = 601;
            break;
         case 3:
            resId = 602;
            break;
         case 4:
            resId = 603;
      }

      String labelString = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(resId);
      Field field = null;
      if (!(attendee instanceof Object)) {
         Object address = attendee.getAddress();
         if (address instanceof Object) {
            FieldProvider fieldProvider = (FieldProvider)address;
            ContextObject newContext = ContextObject.clone(context);
            newContext.setFlag(1, 9);
            newContext.setFlag(17, 45);
            Field addressField = fieldProvider.getField(newContext);
            if (addressField != null) {
               String displayableAddress = address.toString();
               if (displayableAddress != null && displayableAddress.indexOf("Truncated") >= 0) {
                  ResourceBundle rb = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA");
                  String text = rb.getString(503);
                  return (Field)(new Object(text));
               }
            }

            field = (Field)(new Object((Field)(new Object(labelString)), addressField));
         }
      } else {
         AddressReference addressRef = (AddressReference)attendee;
         field = (Field)(new Object(addressRef, labelString, 8589934592L, context));
      }

      if (field != null) {
         field.setCookie(attendee);
      }

      return field;
   }

   static final int getAttendeeFieldOrder(Attendee attendee, Object newContext) {
      switch (attendee.getType()) {
         case -1:
         case 1:
            return 140;
         case 0:
         default:
            return 100;
         case 2:
            return 110;
         case 3:
            return 130;
         case 4:
            return 120;
      }
   }

   static final void composeEmailToAttendees(ServiceRecord sr, String subject, Manager manager, Field attendeeField, Object context) {
      EmailMessageModel message = (EmailMessageModel)FactoryUtil.createInstance(-6822293833372928884L, null);
      message.setType((byte)32);
      if (subject == null) {
         subject = "";
      }

      EmailBuilderApi.addSubjectLine(message, subject);
      EmailBuilderApi.addMessageBody(message, "");
      ContextObject co = ContextObject.castOrCreate(context);
      if (attendeeField != null) {
         AttendeeModel attendeeModel = (AttendeeModel)attendeeField.getCookie();
         EmailBuilderApi.addRecipient(message, 0, attendeeModel.getInsideModel());
      } else {
         int size = manager.getFieldCount();
         String senderAddress = EventUtilities.getEmailAddress(sr);
         AttendeeUtilities$EmailAddressComparator emailAddressComparator = (AttendeeUtilities$EmailAddressComparator)(new Object());

         for (int i = 0; i < size; i++) {
            Field f = manager.getField(i);
            AttendeeModel attendeeModel = (AttendeeModel)f.getCookie();
            String displayableAddress = attendeeModel.getAddress().toString();
            if (displayableAddress != null && displayableAddress.indexOf("Truncated") >= 0) {
               ResourceBundle rb = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA");
               String text = rb.getString(611);
               int result = Dialog.ask(3, text);
               if (result == -1) {
                  return;
               }
            } else if (senderAddress == null || displayableAddress == null || emailAddressComparator.compare(senderAddress, displayableAddress) != 0) {
               EmailBuilderApi.addRecipient(message, 0, attendeeModel.getInsideModel());
            }
         }
      }

      co.setFlag(31);
      co.setFlag(39);
      co.setFlag(0);
      ContextObject.put(co, -6095803566992128485L, sr);
      EmailComposeVerb.showEditorScreen(co, co, message);
   }

   static final Object pickAttendee(Manager manager, Field attendeeField) {
      RIMModel newModel = null;
      Verb addressSelectionVerb = AddressBookServices.getAddressSelectionVerb(-2985347935260258684L);
      if (addressSelectionVerb != null) {
         Verb[] useOnceVerbs = new Object[]{UseOnceAddressVerb.newUseOnceEmailAddressVerb(false)};
         CompoundRecognizer modelRecognizer = (CompoundRecognizer)(new Object());
         Recognizer emailRecognizer = RecognizerRepository.getRecognizers(-2985347935260258684L);
         Recognizer groupRecognizer = RecognizerRepository.getRecognizers(-1326186686655625745L);
         if (emailRecognizer != null) {
            modelRecognizer.addRecognizer(emailRecognizer);
         }

         if (groupRecognizer != null) {
            modelRecognizer.addRecognizer(groupRecognizer);
         }

         AddressSelectionContext selectionContext = (AddressSelectionContext)(new Object(null, null, null, modelRecognizer, useOnceVerbs));
         selectionContext.setUseEntryPrefixes(
            new Object[]{ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(900)}
         );
         newModel = (RIMModel)addressSelectionVerb.invoke(selectionContext);
         if (newModel != null) {
            if (!(newModel instanceof Object)) {
               Attendee attendee = AttendeeFactory.createAttendee(1, newModel);
               Field field = createAttendeeField(attendee, null);
               if (field != null) {
                  if (attendeeField != null) {
                     manager.insert(field, attendeeField.getIndex());
                     manager.delete(attendeeField);
                  } else {
                     manager.add(field);
                  }

                  field.setFocus();
               }
            } else {
               GroupAddressCardModel gacm = (GroupAddressCardModel)newModel;

               for (int i = 0; i < gacm.size(); i++) {
                  if (gacm.getAddressModelTypeAt(i) == 0) {
                     RIMModel addressModel = gacm.getAddressModelAt(i);
                     if (addressModel != null && addressModel instanceof Object) {
                        Attendee attendee = AttendeeFactory.createAttendee(1, addressModel);
                        Field field = createAttendeeField(attendee, null);
                        if (field != null) {
                           if (attendeeField != null) {
                              manager.insert(field, attendeeField.getIndex());
                              manager.delete(attendeeField);
                           } else {
                              manager.add(field);
                           }

                           field.setFocus();
                        }
                     }
                  }
               }
            }
         }
      }

      return newModel;
   }

   private static final synchronized long[] getDontNotifyAttendeesList(CalendarService calendarService) {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      long key = 7708406903981735936L | calendarService.getUniqueServiceID();
      long[] luids = (long[])applicationRegistry.getOrWaitFor(key);
      if (luids == null) {
         luids = new long[0];
         applicationRegistry.put(key, luids);
      }

      return luids;
   }

   public static final synchronized void dontNotifyAttendees(CalendarService calendarService, long eventLUID) {
      long[] luids = getDontNotifyAttendeesList(calendarService);
      synchronized (luids) {
         Array.resize(luids, luids.length + 1);
         luids[luids.length - 1] = eventLUID;
      }
   }

   public static final synchronized boolean getNotifyAttendees(CalendarService calendarService, long eventLUID) {
      long[] luids = getDontNotifyAttendeesList(calendarService);
      synchronized (luids) {
         for (int i = luids.length - 1; i >= 0; i--) {
            if (luids[i] == eventLUID) {
               return false;
            }
         }

         return true;
      }
   }

   public static final synchronized void clearNotifyAttendees(CalendarService calendarService, long eventLUID) {
      long[] luids = getDontNotifyAttendeesList(calendarService);
      synchronized (luids) {
         for (int i = luids.length - 1; i >= 0; i--) {
            if (luids[i] == eventLUID) {
               luids[i] = luids[luids.length - 1];
               Array.resize(luids, luids.length - 1);
            }
         }
      }
   }
}
