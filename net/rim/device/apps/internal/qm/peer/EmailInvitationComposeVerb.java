package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.qm.resource.QmResources;
import net.rim.device.internal.crypto.EncryptionUtilities;

final class EmailInvitationComposeVerb {
   private static byte[] _privateKey;
   private static byte[] _publicKey;

   static final void doInvite(Object addressModel, PeerContactList list) {
      if (addressModel == null) {
         addressModel = getAddress();
      }

      if (addressModel != null) {
         String address = ((FriendlyNameAddressModel)addressModel).getAddress();
         PeerContact contact = PeerApplication.getInstance().getContactListCollection().findContact(address);
         if (!validateContact(contact)) {
            Dialog.alert(PeerResources.getString(2055));
            return;
         }

         if (Utils.isValidString(address)) {
            boolean passcodeFlag = false;
            String question = "";
            String answer = "BlackBerry";
            AddPasscodeDialog apd = new AddPasscodeDialog();
            if (PeerData.isAskInviteQuestion() && apd.doModal()) {
               question = apd.getQuestion();
               answer = apd.getAnswer();
               passcodeFlag = true;
            }

            generateEncryptionKeys(answer);
            int cookie = (int)System.currentTimeMillis();
            if (sendEmailInvitation(address, cookie, question, passcodeFlag, addressModel instanceof Object)) {
               addContact(contact, address, answer, cookie, list);
               return;
            }
         } else {
            Dialog.alert(PeerResources.getString(2008));
         }
      }
   }

   private static final boolean validateContact(PeerContact contact) {
      return contact == null || !contact.isAuthorized();
   }

   private static final void addContact(PeerContact contact, String address, String answer, int cookie, PeerContactList list) {
      String name = Utils.resolveName(address);
      PeerContactListCollection contactList = PeerApplication.getInstance().getContactListCollection();
      if (contact == null) {
         contact = new PeerContact(name, address, true);
         contactList.addContact(contact, list);
      } else if (!contact.isAuthorized()) {
         contact.setAuthorized(true);
         contact.setPending(true);
         contactList.addContact(contact, list);
      }

      if (answer != null) {
         contact.setPasscodeAnswer(answer);
         contact.setPrivateKey(_privateKey);
      }

      contact.setCookie(cookie);
      PeerContactListCollection.addPendingContact(cookie, contact);
      Dialog.inform(QmResources.format(41, name));
   }

   static final Object getAddress() {
      Recognizer[] recognizers = null;
      String[] prefixes = null;
      Verb[] useOnceVerbs = null;
      String emailPrefix = PeerResources.getString(2009);
      String pinPrefix = PeerResources.getString(2011);
      if (EmailInvitationUtilities.canSendEmail()) {
         recognizers = new Object[]{RecognizerRepository.getRecognizers(-2985347935260258684L), RecognizerRepository.getRecognizers(4246852237058296601L)};
         prefixes = new Object[]{emailPrefix, pinPrefix};
         useOnceVerbs = new Object[]{new UseOnceEmailVerb(), new UseOncePINVerb()};
      } else {
         recognizers = new Object[]{RecognizerRepository.getRecognizers(4246852237058296601L)};
         prefixes = new Object[]{pinPrefix};
         useOnceVerbs = new Object[]{new UseOncePINVerb()};
      }

      AddressSelectionContext asc = (AddressSelectionContext)(new Object(
         PeerResources.getString(2012), PeerResources.getString(2013), PeerResources.getString(2014), recognizers, useOnceVerbs
      ));
      asc.setUseEntryPrefixes(prefixes);
      Verb addressSelectionVerb = AddressBookServices.getAddressSelectionVerb(-1789952090272871921L);
      return addressSelectionVerb != null ? addressSelectionVerb.invoke(asc) : null;
   }

   static final boolean sendEmailInvitation(String address, int cookie, String question, boolean passcodeFlag, boolean isPin) {
      String username = PeerApplication.getSession().getDisplayName();
      String subject = PeerResources.format(66, username);
      ContactAddMessageDialog camd = new ContactAddMessageDialog();
      if (camd.doModal()) {
         String body = camd.getMessage();
         body = ((StringBuffer)(new Object())).append(body).append(PeerResources.getString(68)).toString();
         EmailInvitation invite = EmailInvitation.makeOutbound(1, cookie, question, passcodeFlag, _publicKey, ".".getBytes(), ".".getBytes(), ".".getBytes());
         invite.setIsPin(isPin);
         EmailInvitationUtilities.sendEmailMessage(address, subject, body, invite, false, true);
         return true;
      } else {
         return false;
      }
   }

   private static final void generateEncryptionKeys(String source) {
      byte[] sharedSecretCurvePoint = EncryptionUtilities.generateCurvePointFromByteArray(0, source.getBytes());
      _privateKey = new byte[21];
      RandomSource.getBytes(_privateKey);
      _privateKey[0] = (byte)(_privateKey[0] & 0);
      _privateKey[20] = (byte)(_privateKey[20] | 2);
      byte[] devicePublicKey = new byte[20];
      EncryptionUtilities.calculateKey(0, sharedSecretCurvePoint, _privateKey, devicePublicKey);
      byte[] STPublicKey = new byte[21];
      STPublicKey[0] = 2;
      System.arraycopy(devicePublicKey, 0, STPublicKey, 1, devicePublicKey.length);
      _publicKey = new byte[21];
      System.arraycopy(STPublicKey, 0, _publicKey, 0, STPublicKey.length);
   }
}
