package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.ota.CICALEmailAttachment;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.vm.Array;

class MeetingEmailDataModel
   implements PersistableRIMModel,
   ConversionProvider,
   CloneProvider,
   FieldProvider,
   VerbProvider,
   EncryptableProvider,
   CICALEmailAttachment {
   private Persistable _event;
   private long _eventLUID;
   Object _attachment;
   int _type;

   Event getEvent() {
      CalDB calendar = CalendarServiceManager.getInstance().findCalendarDatabase(this._eventLUID);
      if (calendar != null) {
         Event event = (Event)calendar.get(this._eventLUID);
         if (event != null) {
            return event;
         }
      }

      return (Event)this._event;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Array.resize(verbs, 0);
      if (!ContextObject.getFlag(context, 45)
         && (ContextObject.getFlag(context, 44) || !ContextObject.getFlag(context, 2) || !ContextObject.getFlag(context, 43))) {
         return null;
      }

      Array.resize(verbs, 1);
      verbs[0] = new OpenMeetingEmailDataVerb(this);
      return verbs[0];
   }

   public byte[] getAttachmentData() {
      try {
         return PersistentContent.decodeByteArray(this._attachment);
      } finally {
         ;
      }
   }

   @Override
   public Field getField(Object context) {
      if (ContextObject.getFlag(context, 0)) {
         return null;
      }

      int resourceId = 0;
      short var6;
      switch (this._type) {
         case 0:
            return null;
         case 1:
         default:
            var6 = 700;
            break;
         case 2:
            var6 = 701;
            break;
         case 3:
            var6 = 702;
      }

      long flags = 0;
      if (!ContextObject.getFlag(context, 26)) {
         flags |= 18014398509481984L;
      }

      if (ContextObject.getFlag(context, 17)) {
         flags |= 64;
      }

      LabelField field = new LabelField(
         ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(var6), flags
      );
      field.setCookie(this);
      return field;
   }

   @Override
   public int getOrder(Object context) {
      return 6500;
   }

   @Override
   public boolean convert(Object context, Object target) {
      byte[] attachmentData = this.getAttachmentData();
      if (target instanceof RIMMessagingOutgoingMessage && attachmentData != null) {
         RIMMessagingOutgoingMessage message = (RIMMessagingOutgoingMessage)target;
         message.addAttachment(attachmentData, null, "application/x-rimdevicecalendar");
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Object clone(Object context) {
      return this;
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return false;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      boolean result = true;
      if (this._event instanceof EncryptableProvider) {
         EncryptableProvider ep = (EncryptableProvider)this._event;
         result = ep.checkCrypt(compress, encrypt);
      }

      if (!PersistentContent.checkEncoding(this._attachment, compress, encrypt)) {
         result = false;
      }

      return result;
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._attachment = PersistentContent.reEncode(this._attachment, compress, encrypt);
      if (this._event instanceof EncryptableProvider) {
         EncryptableProvider encryptable = (EncryptableProvider)this._event;
         if (!encryptable.checkCrypt(compress, encrypt)) {
            if (ObjectGroup.isInGroup(this._event)) {
               this._event = (Event)ObjectGroup.expandGroup(this._event);
            }

            if (!ObjectGroup.isInGroup(this._event)) {
               this._event = (Event)((EncryptableProvider)this._event).reCrypt(compress, encrypt);

               try {
                  ObjectGroup.createGroup(this._event);
                  return this;
               } finally {
                  ;
               }
            }

            CICALEventLogger.logEvent(1128481095, 2, null, this._eventLUID);
         }
      }

      return this;
   }

   MeetingEmailDataModel(Event event, int type, byte[] data) {
      Event e = event;
      if (ObjectGroup.isInGroup(e)) {
         e = (Event)ObjectGroup.expandGroup(e);
      } else {
         e = (Event)((Copyable)e).copy();
         e.setUID(e.getUID());
         if (event.isRecurring()) {
            Recur r = event.getReadOnlyRecurrence();
            Recur recurClone = (Recur)((CloneProvider)r).clone(null);
            e.setRecurrence(recurClone);
         }
      }

      label27:
      try {
         ObjectGroup.createGroup(e);
      } finally {
         break label27;
      }

      this._event = e;
      this._eventLUID = event.getLUID();
      this._type = type;
      this._attachment = data;
      this.reCrypt(true, true);
   }
}
