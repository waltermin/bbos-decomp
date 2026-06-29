package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.blackberryemail.email.EmailBuilder;
import net.rim.device.apps.internal.blackberryemail.email.EmailComposeVerb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

final class SendAsAttachmentVerb extends Verb {
   AddressCardModelImpl _address;

   SendAsAttachmentVerb(AddressCardModelImpl address) {
      super(16864000);
      this._address = address;
   }

   @Override
   public final String toString() {
      return AddressBookResources.getString(1602);
   }

   @Override
   public final Object invoke(Object originalContext) {
      for (Verb verb : VerbRepository.getVerbRepository(-7881764549058890736L).getVerbs(-2985347935260258684L)) {
         if (verb instanceof EmailComposeVerb) {
            ContextObject contextObject = ContextObject.clone(originalContext);
            contextObject.setFlag(31, 0);
            EmailMessageModel message = EmailBuilder.buildMessage(contextObject);
            if (message != null) {
               message.add(this._address);
               contextObject.put(-8485899342890396495L, message);
               return verb.invoke(contextObject);
            }
            break;
         }
      }

      return null;
   }
}
