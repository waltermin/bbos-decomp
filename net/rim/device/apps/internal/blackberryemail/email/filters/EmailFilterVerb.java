package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public final class EmailFilterVerb extends Verb {
   private int _type;
   public static final int DEFAULT = 0;
   public static final int FILTER_SENDER = 1;
   public static final int FILTER_SUBJECT = 2;

   @Override
   public final String toString() {
      switch (this._type) {
         case -1:
            return "";
         case 0:
         default:
            return EmailResources.getString(160);
         case 1:
            return EmailResources.getString(164);
         case 2:
            return EmailResources.getString(163);
      }
   }

   public EmailFilterVerb(Object context, int type) {
      super(16986368);
      this._type = type;
   }

   @Override
   public final Object invoke(Object obj) {
      if (!(obj instanceof Object)) {
         return null;
      }

      ContextObject context = (ContextObject)obj;
      String userId = null;
      Object savedSelectedItem = context.get(250);
      if (savedSelectedItem instanceof EmailMessageModel) {
         EmailMessageModel m = (EmailMessageModel)savedSelectedItem;
         if (m.inbound()) {
            ServiceRecord sr = m.getServiceRecordForMessage();
            if (sr != null) {
               userId = String.valueOf(sr.getUserId());
               if (ServiceBook.getSB().getRecordByCidAndUserId("sync", sr.getUserId()) != null) {
                  new EmailFilterList(userId).run(m, this._type);
                  return null;
               }
            }
         }
      }

      userId = this.pickFromList();
      if (userId != null) {
         new EmailFilterList(userId).run(null, 0);
      }

      return null;
   }

   protected final String pickFromList() {
      ServiceRecord[] srs = ServiceBook.getSB().findRecordsByCid("SYNC");
      if (srs.length == 0) {
         return null;
      }

      int size = srs.length;
      String[] srNames = new Object[size];
      int[] userIds = new int[size];

      for (int i = 0; i < size; i++) {
         ServiceRecord sr = srs[i];
         srNames[i] = sr.getName();
         userIds[i] = sr.getUserId();
      }

      if (size == 1) {
         return String.valueOf(userIds[0]);
      }

      Arrays.sort(srNames, new EmailFilterVerb$StringComparator());
      int result = Dialog.ask(EmailResources.getString(188), srNames, 0);
      return result != -1 ? String.valueOf(userIds[result]) : null;
   }
}
