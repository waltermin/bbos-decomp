package net.rim.device.apps.internal.browser.debug;

import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.Transport;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.internal.blackberryemail.address.UseOnceAddressVerb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.browser.ui.DialogEnterString;

final class DebugInfoSendVerb extends Verb {
   private String _text;
   private String _subject;

   DebugInfoSendVerb(String text, String subject) {
      super(40000);
      this._text = text;
      this._subject = subject;
   }

   @Override
   public final String toString() {
      return "Send...";
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object invoke(Object context) {
      if (this._text != null && this._text.length() != 0) {
         int length = this._text.length();
         int maxLength = 64000;
         int parts = length / maxLength;
         int remainder = length % maxLength;
         parts += remainder != 0 ? 1 : 0;
         VerbRepository repository = VerbRepository.getVerbRepository(-1789952090272871921L);
         Verb[] verbs = repository.getVerbs(-1789952090272871921L);
         Recognizer[] recognizers = null;
         String[] prefixes = null;
         Verb[] useOnceVerbs = null;
         String emailPrefix = EmailResources.getString(44);
         String pinPrefix = EmailResources.getString(34);
         Verb useOnceEmailVerb = UseOnceAddressVerb.newUseOnceEmailAddressVerb(false);
         Verb useOncePinVerb = UseOnceAddressVerb.newUseOncePINAddressVerb(null);
         if (CMIMEUtilities.canSendEmail()) {
            recognizers = new Object[]{RecognizerRepository.getRecognizers(-2985347935260258684L), RecognizerRepository.getRecognizers(4246852237058296601L)};
            prefixes = new Object[]{emailPrefix, pinPrefix};
            useOnceVerbs = new Object[]{useOnceEmailVerb, useOncePinVerb};
         } else {
            recognizers = new Object[]{RecognizerRepository.getRecognizers(4246852237058296601L)};
            prefixes = new Object[]{pinPrefix};
            useOnceVerbs = new Object[]{useOncePinVerb};
         }

         AddressSelectionContext selectionContext = (AddressSelectionContext)(new Object(null, EmailResources.getString(94), null, recognizers, useOnceVerbs));
         selectionContext.setUseEntryPrefixes(prefixes);
         Object addressModel = verbs[0].invoke(selectionContext);
         Address address = null;
         boolean var30 = false /* VF: Semaphore variable */;

         try {
            var30 = true;
            if (addressModel instanceof Object) {
               FriendlyNameAddressModel model = (FriendlyNameAddressModel)addressModel;
               address = (Address)(new Object(model.getAddress(), model.getFriendlyName()));
               var30 = false;
            } else if (addressModel instanceof Object) {
               FriendlyNameAddressModel model = (FriendlyNameAddressModel)addressModel;
               address = (Address)(new Object(model.getAddress(), model.getFriendlyName()));
               var30 = false;
            } else {
               var30 = false;
            }
         } finally {
            if (var30) {
               Dialog.inform("Invalid address");
               return null;
            }
         }

         if (address == null) {
            return null;
         }

         boolean var27 = false /* VF: Semaphore variable */;

         try {
            var27 = true;
            DialogEnterString var38 = new Object("Enter subject line", this._subject, "Ok");
            if (((Dialog)var38).doModal() == -1) {
               return null;
            }

            String subject = ((DialogEnterString)var38).getResult();
            if (subject == null) {
               subject = "";
            }

            for (int i = 1; i <= parts; i++) {
               Message message = (Message)(new Object());
               message.addRecipients(0, new Object[]{address});
               String subjectText = subject;
               if (parts > 1) {
                  subjectText = ((StringBuffer)(new Object())).append(subjectText).append(" ").append(i).append('/').append(parts).toString();
               }

               message.setSubject(subjectText);
               int partLength = i == parts && remainder != 0 ? remainder : maxLength;
               int startIndex = (i - 1) * maxLength;
               message.setContent(this._text.substring(startIndex, startIndex + partLength));
               Transport.send(message);
            }

            var27 = false;
         } finally {
            if (var27) {
               Dialog.inform("An error occurred while sending the log");
               return null;
            }
         }

         Dialog.inform("Log sent");
         return null;
      } else {
         return null;
      }
   }
}
