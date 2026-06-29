package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.editor.UseOnceEditorScreen;
import net.rim.device.apps.internal.blackberryemail.email.EmailComposeVerb;
import net.rim.device.apps.internal.blackberryemail.email.PINComposeVerb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public final class UseOnceAddressVerb extends Verb {
   private Verb _composeVerb;
   private boolean _doCompose;
   private int _promptResourceId;
   private long _objectType;

   private UseOnceAddressVerb(int menuOrdering, int menuResourceId, int promptResourceId, long objectType, boolean doCompose, Verb composeVerb) {
      super(menuOrdering, EmailResources.getResourceBundle(), menuResourceId);
      this._promptResourceId = promptResourceId;
      this._objectType = objectType;
      this._doCompose = doCompose;
      this._composeVerb = composeVerb;
   }

   public static final Verb newUseOnceEmailAddressVerb(boolean doCompose) {
      return new UseOnceAddressVerb(327952, 43, 42, -2985347935260258684L, doCompose, null);
   }

   public static final Verb newUseOncePINAddressVerb(Verb composeVerb) {
      return new UseOnceAddressVerb(327968, 39, 38, 4246852237058296601L, composeVerb != null, composeVerb);
   }

   @Override
   public final Object invoke(Object param) {
      String initialValue = (String)ContextObject.get(param, 253);
      if (this._objectType == 4246852237058296601L && initialValue != null) {
         TextFilter tf = TextFilter.get(9);
         if (!tf.validate(AbstractStringWrapper.createInstance(initialValue))) {
            initialValue = "";
         }
      }

      Object result = UseOnceEditorScreen.showUseOnceScreen(EmailResources.getString(this._promptResourceId), this._objectType, initialValue, null);
      if (result != null) {
         Verb composeVerb = this._composeVerb;
         if (this._doCompose && composeVerb == null) {
            for (Verb verb : VerbRepository.getVerbRepository(-7881764549058890736L).getVerbs(this._objectType)) {
               if (verb instanceof EmailComposeVerb || verb instanceof PINComposeVerb) {
                  composeVerb = verb;
                  break;
               }
            }
         }

         if (composeVerb != null) {
            ContextObject contextObject = ContextObject.clone(param);
            contextObject.remove(253);
            contextObject.put(254, result);
            result = composeVerb.invoke(contextObject);
         }
      }

      if (this._doCompose) {
         AddressBookOptions options = AddressBookServices.getAddressBookOptions();
         if (options != null) {
            options.setComposePreference((byte)1);
         }
      }

      return result;
   }
}
