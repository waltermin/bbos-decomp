package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.filters.EmailFilterVerb;
import net.rim.vm.Array;

final class MessageListVerbFactory implements VerbFactory {
   private Verb[] _verbs = new Verb[0];

   @Override
   public final Verb[] getVerbs(Object context) {
      synchronized (this._verbs) {
         int verbCount = 0;
         Array.resize(this._verbs, 3);
         if (context != null) {
            Object savedSelectedItem = ((ContextObject)context).get(250);
            if (savedSelectedItem instanceof EmailMessageModel) {
               EmailMessageModel m = (EmailMessageModel)savedSelectedItem;
               if (m.inbound() && !m.flagsSet(33554432)) {
                  ServiceRecord sr = m.getServiceRecordForMessage();
                  if (sr != null && ServiceBook.getSB().getRecordByCidAndUserId("sync", sr.getUserId()) != null) {
                     this._verbs[verbCount++] = new EmailFilterVerb(context, 1);
                     this._verbs[verbCount++] = new EmailFilterVerb(context, 2);
                  }
               }
            }
         }

         Array.resize(this._verbs, verbCount);
         return this._verbs;
      }
   }
}
