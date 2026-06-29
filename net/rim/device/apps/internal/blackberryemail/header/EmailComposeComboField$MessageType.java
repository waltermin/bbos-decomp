package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.addressbook.PINAddressModel;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.registration.VerbCombinerRepository;
import net.rim.device.apps.api.framework.verb.VerbCombiner;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

class EmailComposeComboField$MessageType {
   private int _tag;
   private StringBuffer _tempStringBuffer;
   private TextFilter _pinAddressFilter;
   private final EmailComposeComboField this$0;

   EmailComposeComboField$MessageType(EmailComposeComboField _1, int tag) {
      this.this$0 = _1;
      this._tempStringBuffer = new StringBuffer();
      this._pinAddressFilter = TextFilter.get(9);
      this._tag = tag;
      this.checkValidity();
   }

   private void checkValidity() {
      switch (this._tag) {
         case -1:
            throw new IllegalStateException("EmailComposeComboField: Illegal message type");
         case 0:
         case 1:
      }
   }

   @Override
   public String toString() {
      switch (this._tag) {
         case -1:
            return "";
         case 0:
         default:
            return EmailResources.getString(187);
         case 1:
            return EmailResources.getString(34);
      }
   }

   public String getPrefixString() {
      switch (this._tag) {
         case -1:
            return "";
         case 0:
         default:
            return EmailResources.getString(40);
         case 1:
            return EmailResources.getString(35);
      }
   }

   public PersistableRIMModel createAddress(String address) {
      switch (this._tag) {
         case -1:
            return null;
         case 0:
         default:
            return EmailHeaderModel.createEmailFreeFormAddress(address);
         case 1:
            return EmailHeaderModel.createPINFreeFormAddress(address);
      }
   }

   public boolean isAppropriateInnerModel(Object obj) {
      switch (this._tag) {
         case -1:
            return false;
         case 0:
         default:
            if (!(obj instanceof EmailAddressModel) && !(obj instanceof GroupAddressCardModel)) {
               return false;
            }

            return true;
         case 1:
            return obj instanceof PINAddressModel || obj instanceof GroupAddressCardModel;
      }
   }

   public int getVerbGroupId() {
      switch (this._tag) {
         case -1:
            return 0;
         case 0:
         default:
            return 15556151;
         case 1:
            return 13685231;
      }
   }

   public VerbCombiner createVerbCombiner() {
      long combinerId = 0;
      switch (this._tag) {
         case -1:
            return null;
         case 0:
         default:
            combinerId = 6552349874078558230L;
            break;
         case 1:
            combinerId = -4100019750034398199L;
      }

      VerbCombiner[] combiners = VerbCombinerRepository.getCombiners(combinerId);
      return combiners != null && combiners.length != 0 ? combiners[0] : null;
   }

   public boolean shouldTranslateEntry() {
      return true;
   }

   public boolean shouldPutFocusOnTranslation() {
      return this._tag != 0 ? false : this.this$0.getText().indexOf(64) != -1;
   }

   public boolean shouldPutFocusOnLookup() {
      if (this._tag != 0) {
         return false;
      }

      int spaceIndex = -1;
      return (spaceIndex = this.this$0.getText().indexOf(32)) == -1 ? true : this.this$0.getText().indexOf(32, spaceIndex + 1) == -1;
   }

   public String translate(String text) {
      switch (this._tag) {
         case -1:
            return null;
         case 0:
         default:
            return this.emailTranslate(text);
         case 1:
            return this.pinTranslate(text);
      }
   }

   private String emailTranslate(String text) {
      return this.this$0.getEmailEditable().getTranslated();
   }

   private String pinTranslate(String text) {
      this._tempStringBuffer.setLength(0);
      int textSize = text.length();

      for (int i = 0; i < textSize; i++) {
         char ch = CharacterUtilities.toUpperCase(text.charAt(i), 1701707776);
         if (this._pinAddressFilter.validate(ch)) {
            this._tempStringBuffer.append(ch);
         }
      }

      return this._tempStringBuffer.toString();
   }
}
