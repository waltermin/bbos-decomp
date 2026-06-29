package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.quickcontact.QuickContactItem;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.PhoneLogger;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.api.verbs.UseOncePhoneNumberVerb;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class SpeedDialItem extends QuickContactItem implements PersistableRIMModel, ConversionProvider {
   private CallerIDInfo _cidi;
   public static final long FACTORY_GUID = 4046126975918546978L;
   static final long SPEED_DIAL_ITEM_TYPE = -7443913519806541253L;

   SpeedDialItem(int uid, char key, CallerIDInfo cidi) {
      super(uid, key);
      this._cidi = cidi;
   }

   SpeedDialItem(char key, CallerIDInfo cidi) {
      super(key);
      this._cidi = cidi;
   }

   @Override
   public final Object getAddress() {
      return this._cidi.isVoicemailCallerIDInfo() ? PhoneUtilities.getVoiceMailNumber() : this._cidi.getNumber();
   }

   @Override
   public final boolean isValidItem() {
      if (this._cidi == null) {
         return false;
      }

      if (this._cidi.isSpecial()) {
         return this._cidi.getUid() != 0;
      }

      PhoneNumberModel pnm = (PhoneNumberModel)this._cidi.getNumber();
      return pnm != null && pnm.getValue().length() > 0;
   }

   public final CallerIDInfo getCallerIDInfo() {
      return this._cidi;
   }

   @Override
   public final boolean matchAddress(Object address, Object context) {
      Object number = this._cidi.getNumber();
      if (number != null) {
         if (number instanceof Object) {
            boolean typeIndependent = ContextObject.getFlag(context, 93);
            return ((AbstractPhoneNumberModel)number).equals(address, typeIndependent);
         } else {
            return number.equals(address);
         }
      } else {
         return false;
      }
   }

   @Override
   public final String getCommTypeString() {
      return PhoneResources.getString(411);
   }

   @Override
   public final String getFriendlyNameString() {
      if (this._cidi.displayCompanyInfo()) {
         return this._cidi.getDisplayableString(2);
      }

      String name = this._cidi.getDisplayableString(1);
      if (name == null || name.length() == 0) {
         name = this._cidi.getDisplayableString(2);
      }

      return name;
   }

   @Override
   public final String getAddressTypeString() {
      return CommonResources.getString(9119);
   }

   @Override
   public final String getRawAddressString() {
      String numberString = null;
      if (this._cidi.isSpecial()) {
         if (this._cidi.getUid() == -7117173429217454741L) {
            numberString = PhoneUtilities.getVoiceMailNumber();
            if (numberString == null) {
               numberString = "";
            }

            return PhoneUtilities.splitNumberAtFirstSpecialCharacter(numberString)[0];
         } else {
            return "";
         }
      } else {
         AbstractPhoneNumberModel pnm = (AbstractPhoneNumberModel)this._cidi.getNumber();
         int flags = 6;
         numberString = pnm.getDisplayablePhoneNumber();
         int type = pnm.getType();
         if (type != 0 && this._cidi.getAddress() != null) {
            String typeString = AbstractPhoneNumberModel.getTypeString(pnm.getType(), flags);
            return ((StringBuffer)(new Object())).append(typeString).append(" ").append(numberString).toString();
         } else {
            return numberString;
         }
      }
   }

   @Override
   public final boolean invoke() {
      if (this._cidi == null) {
         return false;
      }

      if (!ITPolicy.getBoolean(1, true)) {
         PhoneLogger.log("SDI: IT Policy disabled phone");
         return false;
      }

      Verb[] verbs = new Object[0];
      ContextObject context = (ContextObject)(new Object());
      if (!PhoneUtilities.isQuietProfileOn() && !PhoneUtilities.isDiscreetProfileOn()) {
         context.put(2848872683723475070L, "speed_dial.mp3");
      }

      PhoneUtilities.setPrivateFlag(context, 66);
      Verb defaultVerb = this._cidi.getVerbs(context, verbs);
      PhoneLogger.log(((StringBuffer)(new Object("SDI.invoke key = "))).append(this.getKey()).toString());
      return defaultVerb != null ? this.invokeVerb(defaultVerb) : this.invokeVerb(new DialVerb(this._cidi.getNumber(), this._cidi.getAddress(), context));
   }

   private final boolean invokeVerb(Verb verb) {
      Backlight.enable(true);
      ContextObject context = (ContextObject)(new Object());
      PhoneUtilities.setPrivateFlag(context, 66);
      Object result = verb.invoke(context);
      return ContextObject.getFlag(result, 39);
   }

   @Override
   public final String toString() {
      return this._cidi.toString();
   }

   @Override
   public final void view() {
      Dialog.inform("TODO: view details");
   }

   @Override
   public final void edit() {
      Object addressInfo = QuickContactList.selectFromAddressBook(null, null, CommonResources.getString(8), new Object[]{new UseOncePhoneNumberVerb()}, true);
      if (addressInfo != null) {
         Object number = ContextObject.get(addressInfo, 247);
         Object address = ContextObject.get(addressInfo, 252);
         this._cidi = new CallerIDInfo((PersistableRIMModel)number, (PersistableRIMModel)address, false, true);
         QuickContactList.getInstance().commit();
      }
   }

   @Override
   public final String getInvokeVerbString() {
      ContextObject co = (ContextObject)(new Object());
      co.put(5898398779440734986L, this._cidi);
      return new DialVerb(this._cidi.getNumber(), this._cidi.getAddress(), co).toString();
   }

   @Override
   public final boolean addressBookUpdated(int updateEventId, Object element) {
      return this._cidi.addressBookUpdated(updateEventId, element);
   }

   public static final Object createSpeedDialDataFromHotlistItem(HotlistItem item, ContextObject context) {
      if (item == null) {
         return null;
      }

      ContextObject co = ContextObject.castOrCreate(context);
      CallerIDInfo cidi = (CallerIDInfo)item.getAddress();
      Object address = cidi.getAddress();
      Object number = cidi.getNumber();
      if (address != null) {
         co.put(252, address);
      }

      if (number != null) {
         co.put(247, number);
      }

      return co;
   }

   public final boolean isVoiceMail() {
      return this._cidi.getUid() == -7117173429217454741L;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean convert(Object context, Object target) {
      if (!this.convertTypeAndKey(-7443913519806541253L, context, target)) {
         return false;
      }

      boolean phoneFlagSet = ContextObject.getFlag(context, 20);
      if (!phoneFlagSet) {
         ContextObject.setFlag(context, 20);
      }

      boolean success = false;
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         success = this._cidi.convert(context, target);
         var7 = false;
      } finally {
         if (var7) {
            if (!phoneFlagSet) {
               ContextObject.clearFlag(context, 20);
            }
         }
      }

      if (!phoneFlagSet) {
         ContextObject.clearFlag(context, 20);
      }

      return success;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return this._cidi.checkCrypt(compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._cidi.reCrypt(compress, encrypt);
      return null;
   }
}
