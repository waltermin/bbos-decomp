package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.internal.crypto.EncryptionUtilities;
import net.rim.vm.Array;

final class EmailInvitation implements PersistableRIMModel, FieldProvider {
   boolean _inbound;
   boolean _isPin;
   Object _replyTo;
   int _replyToHash;
   int _stage;
   int _cookie;
   Object _question;
   boolean _passcodeFlag;
   byte[] _cryptokey;
   byte[] _pin;
   byte[] _confirm;
   byte[] _password;
   public boolean _backwards = false;
   String _originalEmailText;
   private static final int FIELD_ELEMENT_BIT_LENGTH = 160;
   private static final int FIELD_ELEMENT_BYTE_LENGTH = 20;
   public static final int PRIVATE_KEY_BYTE_LENGTH = 21;
   private static final int COMPRESSED_POINT_BYTE_LENGTH = 21;
   private static final byte HIGH_BYTE_MASK = 0;
   private static final byte LOW_BYTE_MASK = 2;
   public static final int PUBLIC_KEY_BYTE_LENGTH = 21;
   static final String STAGE_2_SUBJECT = "<$RemoveOnDelivery,SuppressSaveInSentItems> BLACKBERRYMESSENGERINVITESTAGE2";
   static final String STAGE_2_BODY = "<<------------>>";
   public static final int VERSION = 1;
   public static final int TYPE_INVITE = 1;
   static final int TLE_STAGE = 1;
   static final int TLE_COOKIE = 2;
   static final int TLE_PASSCODE_FLAG = 3;
   static final int TLE_CRYPTO_KEY = 4;
   static final int TLE_PIN = 5;
   static final int TLE_CONFIRM = 6;
   static final int TLE_PASSWORD = 7;
   static final int TLE_QUESTION = 8;
   static final int TLE_BODY_HASH = 9;
   static final int TLE_END = 80;
   static final int TLE_INBOUND = 39;
   static final int TLE_REPLYTO = 40;
   static final int TLE_ISPIN = 41;

   private EmailInvitation() {
   }

   static final EmailInvitation makeOutbound(
      int stage, int cookie, String question, boolean passcodeFlag, byte[] cryptokey, byte[] pin, byte[] confirm, byte[] password
   ) {
      EmailInvitation email = new EmailInvitation();
      email._inbound = false;
      email._stage = stage;
      email._cookie = cookie;
      email._question = PersistentContent.encode(question, true, true);
      email._passcodeFlag = passcodeFlag;
      email._cryptokey = cryptokey;
      email._pin = pin;
      email._confirm = confirm;
      email._password = password;
      return email;
   }

   final String getReplyTo() {
      try {
         return PersistentContent.decodeString(this._replyTo);
      } finally {
         ;
      }
   }

   final String getQuestion() {
      try {
         return PersistentContent.decodeString(this._question);
      } finally {
         ;
      }
   }

   static final EmailInvitation makeInbound() {
      EmailInvitation email = new EmailInvitation();
      email._inbound = true;
      return email;
   }

   public final void setIsPin(boolean isPin) {
      this._isPin = isPin;
   }

   public final boolean isPin() {
      return this._isPin;
   }

   public final IntHashtable getPersistentData() {
      IntHashtable data = (IntHashtable)(new Object(9));
      data.put(1, new Object(this._stage));
      data.put(2, new Object(this._cookie));
      data.put(3, new Object(this._passcodeFlag ? 1 : 0));
      data.put(4, this._cryptokey);
      data.put(5, this._pin);
      data.put(6, this._confirm);
      data.put(7, this._password);
      data.put(39, new Object(this._passcodeFlag ? 1 : 0));
      data.put(40, this._replyTo);
      data.put(8, this._question);
      data.put(41, new Object(this._isPin ? 1 : 0));
      return data;
   }

   public final void initialize(IntHashtable data) {
      IntEnumeration keys = data.keys();

      while (keys.hasMoreElements()) {
         int type = keys.nextElement();
         switch (type) {
            case 1:
               this._stage = data.get(1);
               break;
            case 2:
               this._cookie = data.get(2);
               break;
            case 3:
               this._passcodeFlag = data.get(3) != 0;
               break;
            case 4:
               this._cryptokey = (byte[])data.get(4);
               break;
            case 5:
               this._pin = (byte[])data.get(5);
               break;
            case 6:
               this._confirm = (byte[])data.get(6);
               break;
            case 7:
               this._password = (byte[])data.get(7);
               break;
            case 8:
               this._question = data.get(8);
               break;
            case 39:
               this._inbound = data.get(39) != 0;
               break;
            case 40:
               this._replyTo = data.get(40);
               break;
            case 41:
               this._isPin = data.get(41) != 0;
         }
      }
   }

   final byte[] pickle(String body) {
      DataBuffer db = (DataBuffer)(new Object(false));
      db.writeByte(1);
      db.writeByte(1);
      ConverterUtilities.convertInt(db, 1, this._stage, 1);
      ConverterUtilities.writeInt(db, 2, this._cookie);
      ConverterUtilities.writeString(db, 8, this.getQuestion());
      ConverterUtilities.convertInt(db, 3, this._passcodeFlag ? 1 : 0, 1);
      ConverterUtilities.writeByteArray(db, 4, this._cryptokey);
      ConverterUtilities.writeByteArray(db, 5, this._pin);
      ConverterUtilities.writeByteArray(db, 6, this._confirm);
      ConverterUtilities.writeByteArray(db, 7, this._password);
      ConverterUtilities.writeEmptyField(db, 80);
      db.trim();
      return db.toArray();
   }

   final boolean unPickle(byte[] data) {
      DataBuffer db = (DataBuffer)(new Object(data, 0, data.length, false));
      if (db.readByte() != 1) {
         return false;
      }

      if (db.readUnsignedByte() != 1) {
         return false;
      }

      int fieldcount = 0;

      try {
         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(db);
            } finally {
               return fieldcount == 9;
            }

            switch (type) {
               case 1:
                  this._stage = ConverterUtilities.readInt(db);
                  fieldcount++;
                  break;
               case 2:
                  this._cookie = ConverterUtilities.readInt(db);
                  fieldcount++;
                  break;
               case 3:
                  this._passcodeFlag = ConverterUtilities.readInt(db) == 1;
                  fieldcount++;
                  break;
               case 4:
                  this._cryptokey = ConverterUtilities.readByteArray(db);
                  fieldcount++;
                  break;
               case 5:
                  this._pin = ConverterUtilities.readByteArray(db);
                  fieldcount++;
                  break;
               case 6:
                  this._confirm = ConverterUtilities.readByteArray(db);
                  fieldcount++;
                  break;
               case 7:
                  this._password = ConverterUtilities.readByteArray(db);
                  fieldcount++;
                  break;
               case 8:
                  this._question = ConverterUtilities.readString(db);
                  fieldcount++;
                  break;
               case 80:
                  ConverterUtilities.skipField(db);
                  fieldcount++;
                  break;
               default:
                  ConverterUtilities.skipField(db);
            }
         }
      } finally {
         ;
      }
   }

   public final int getStage() {
      return this._stage;
   }

   public final void setReplyTo(String email) {
      this._replyToHash = email.hashCode();
      this._replyTo = PersistentContent.encode(email, true, true);
   }

   public static final String stripBody(String emailtext) {
      if (emailtext != null && emailtext.length() > 0) {
         int exist = emailtext.indexOf("-------o-------");
         if (exist == -1) {
            exist = emailtext.indexOf("-------AM-------");
            if (exist == -1) {
               return null;
            }
         }

         return emailtext.substring(0, exist);
      } else {
         return null;
      }
   }

   public static final byte[] stripData(String emailtext) {
      if (emailtext != null && emailtext.length() > 0) {
         int exist = emailtext.indexOf("-------o-------");
         boolean backwards = false;
         if (exist == -1) {
            exist = emailtext.indexOf("-------AM-------");
            if (exist == -1) {
               return null;
            }

            backwards = true;
         }

         int start;
         if (backwards) {
            start = exist + 16;

            for (int i = exist + 16; i < emailtext.length(); i++) {
               char c = emailtext.charAt(i);
               start = i;
               if (c >= '0' && c <= '9') {
                  break;
               }
            }
         } else {
            start = exist + 15;

            for (int i = exist + 15; i < emailtext.length(); i++) {
               char c = emailtext.charAt(i);
               start = i;
               if (c >= '0' && c <= '9') {
                  break;
               }
            }
         }

         String invite = emailtext.substring(start);
         int index = invite.indexOf(58);
         if (index == -1) {
            return null;
         }

         String lengthStr = invite.substring(0, index);
         String invitedataStr = null;

         try {
            int length = Integer.valueOf(lengthStr);
            invitedataStr = invite.substring(index + 1);
            invitedataStr = Utils.stripNonBase64Chars(invitedataStr);
            if (invitedataStr == null) {
               return null;
            }

            invitedataStr = invitedataStr.substring(0, length);
         } finally {
            ;
         }

         byte[] invitedata = invitedataStr.getBytes();
         return Utils.decodeBase64(invitedata, 0, invitedata.length);
      } else {
         return null;
      }
   }

   public final void handleDeny() {
      if (!PeerApplication.isDeviceLocked()) {
         if (this._stage != 5) {
            return;
         }

         PeerContactListCollection contactList = PeerApplication.getInstance().getContactListCollection();
         String replyTo = this.getReplyTo();
         PeerContact c = contactList.findContact(String.valueOf(replyTo));
         if (c != null) {
            PeerApplication.getInstance().deleteContact(c, true);
         }

         String name = Utils.resolveName(replyTo);
         WrongPasscodeNotification request = new WrongPasscodeNotification(replyTo, MessageFormat.format(PeerResources.getString(2006), new Object[]{name}));
         contactList.addNewRequest(request);
      }
   }

   public final void declineInvitation() {
      if (!PeerApplication.isDeviceLocked()) {
         EmailInvitation invite = makeOutbound(5, this._cookie, "", false, "".getBytes(), "".getBytes(), "".getBytes(), "".getBytes());
         if (this.isPin()) {
            invite.setIsPin(true);
         }

         EmailInvitationUtilities.sendEmailMessage(
            this.getReplyTo(), "<$RemoveOnDelivery,SuppressSaveInSentItems> BLACKBERRYMESSENGERINVITESTAGE2", "<< no text >>", invite, false, true
         );
      }
   }

   public final void handleStage1(String sender, String body) {
      NewContactRequest request = new NewContactRequest(sender, body, this);
      PeerApplication.getInstance().getContactListCollection().addNewRequest(request);
   }

   public final void acceptInvitation() {
      if (!PeerApplication.isDeviceLocked()) {
         String answer = "BlackBerry";
         if (this._passcodeFlag) {
            PasscodeQuestionDialog pqd = new PasscodeQuestionDialog(this.getQuestion());
            if (!pqd.doModal()) {
               return;
            }

            answer = pqd.getAnswer();
         }

         PeerApplication app = PeerApplication.getInstance();
         if (app == null) {
            return;
         }

         PeerSession session = PeerApplication.getSession();
         byte[] sharedSecretCurvePoint = EncryptionUtilities.generateCurvePointFromByteArray(0, answer.getBytes());
         byte[] devicePrivateKey = new byte[21];
         RandomSource.getBytes(devicePrivateKey);
         devicePrivateKey[0] = (byte)(devicePrivateKey[0] & 0);
         devicePrivateKey[20] = (byte)(devicePrivateKey[20] | 2);
         byte[] devicePublicKey = new byte[20];
         EncryptionUtilities.calculateKey(0, sharedSecretCurvePoint, devicePrivateKey, devicePublicKey);
         byte[] STPublicKey = new byte[21];
         STPublicKey[0] = 2;
         System.arraycopy(devicePublicKey, 0, STPublicKey, 1, devicePublicKey.length);
         byte[] buffer = new byte[1];
         Array.resize(buffer, 21);
         System.arraycopy(STPublicKey, 0, buffer, 0, STPublicKey.length);
         byte[] key1 = new byte[20];
         EncryptionUtilities.calculateKey(0, this._cryptokey, devicePrivateKey, key1);
         byte[] key = new byte[16];
         System.arraycopy(key1, key1.length - 16, key, 0, 16);
         int length = EncryptionUtilities.getCiphertextLength(session.getMyContactId().length());
         byte[] encryptedPin = new byte[length];
         EncryptionUtilities.encrypt(key, session.getMyContactId().getBytes(), 0, session.getMyContactId().length(), encryptedPin, 0);
         length = EncryptionUtilities.getCiphertextLength(PeerData.getPasswordKey().length());
         byte[] encryptedPassword = new byte[length];
         EncryptionUtilities.encrypt(key, PeerData.getPasswordKey().getBytes(), 0, PeerData.getPasswordKey().length(), encryptedPassword, 0);
         Digest sha1Digest = (Digest)(new Object());
         sha1Digest.update(key1);
         byte[] k_kconf = new byte[sha1Digest.getDigestLength()];
         sha1Digest.getDigest(k_kconf, 0);
         PeerContactListCollection contactList = PeerApplication.getInstance().getContactListCollection();
         String replyTo = this.getReplyTo();
         String name = Utils.resolveName(replyTo);
         PeerContact contact = contactList.findContact(replyTo);
         if (contact == null) {
            contact = PeerContactListCollection.getPendingContact(this._cookie);
         }

         if (contact == null) {
            contact = new PeerContact(name, replyTo, true);
            contact.setCookie(this._cookie);
            contactList.addContact(contact);
         }

         if (!this.isPin()) {
            contact.setOriginalContactInfo(replyTo);
         }

         EmailInvitation invite = makeOutbound(2, this._cookie, "", false, buffer, encryptedPin, k_kconf, encryptedPassword);
         if (this.isPin()) {
            invite.setIsPin(true);
         }

         if (this._backwards) {
            invite._backwards = true;
         }

         EmailInvitationUtilities.sendEmailMessage(
            replyTo,
            "<$RemoveOnDelivery,SuppressSaveInSentItems> BLACKBERRYMESSENGERINVITESTAGE2",
            "<$RemoveOnDelivery,SuppressSaveInSentItems> BLACKBERRYMESSENGERINVITESTAGE2",
            invite,
            false,
            true
         );
         Dialog.inform(PeerResources.getString(2016));
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void handleStage2(String sender, String recipient) {
      if (this._stage == 2) {
         PeerApplication app = PeerApplication.getInstance();
         if (app != null) {
            String _tempPin = null;
            String _tempPassword = null;
            PeerContactListCollection contactList = PeerApplication.getInstance().getContactListCollection();
            PeerContact pending = PeerContactListCollection.getPendingContact(this._cookie);
            if (pending != null) {
               if (!PeerData.isAllowForwardInvite() && pending.getIdHash() != this._replyToHash) {
                  return;
               }

               String name = Utils.resolveName(sender);
               byte[] key1 = null;
               byte[] key = null;
               boolean var15 = false /* VF: Semaphore variable */;

               try {
                  var15 = true;
                  key1 = new byte[20];
                  EncryptionUtilities.calculateKey(0, this._cryptokey, pending.getPrivateKey(), key1);
                  key = new byte[16];
                  System.arraycopy(key1, key1.length - 16, key, 0, 16);
                  byte[] sha1Digest = new byte[8];
                  EncryptionUtilities.decrypt(key, this._pin, 0, this._pin.length, sha1Digest, 0);
                  _tempPin = (String)(new Object(sha1Digest));
                  sha1Digest = new byte[this._password.length];
                  EncryptionUtilities.decrypt(key, this._password, 0, this._password.length, sha1Digest, 0);
                  _tempPassword = (String)(new Object(sha1Digest));
                  _tempPassword.trim();
                  var15 = false;
               } finally {
                  if (var15) {
                     EmailInvitationUtilities.sendEmailMessage(sender, "", PeerResources.getString(895), this, false, false);
                     contactList.removeContact(pending);
                     WrongPasscodeNotification request = new WrongPasscodeNotification(
                        sender, MessageFormat.format(PeerResources.getString(2003), new Object[]{name})
                     );
                     contactList.addNewRequest(request);
                     return;
                  }
               }

               Digest sha1Digest = (Digest)(new Object());
               sha1Digest.update(key1);
               byte[] k_kconf = new byte[sha1Digest.getDigestLength()];
               sha1Digest.getDigest(k_kconf, 0);
               if (!Arrays.equals(k_kconf, this._confirm)) {
                  EmailInvitationUtilities.sendEmailMessage(sender, "", PeerResources.getString(895), this, false, false);
                  contactList.removeContact(pending);
                  WrongPasscodeNotification request = new WrongPasscodeNotification(
                     sender, MessageFormat.format(PeerResources.getString(2003), new Object[]{name})
                  );
                  contactList.addNewRequest(request);
                  return;
               }

               PeerContact contact = contactList.findContact(_tempPin);
               if (contact != null && contact != pending) {
                  contactList.removeContact(pending);
               } else {
                  contact = pending;
               }

               if (contact != null) {
                  contactList.updateContact(contact, _tempPin, name, _tempPassword);
                  app.finalizeInvitation(contact, String.valueOf(this._cookie), recipient);
               }

               contactList.expireNewRequests(sender);
            }
         }
      }
   }

   @Override
   public final Field getField(Object context) {
      ButtonField field = null;
      if (this._inbound && this._stage == 1) {
         field = new EmailInvitation$AcceptButton(this);
      }

      return field;
   }

   @Override
   public final int getOrder(Object context) {
      return 6500;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      return false;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }
}
