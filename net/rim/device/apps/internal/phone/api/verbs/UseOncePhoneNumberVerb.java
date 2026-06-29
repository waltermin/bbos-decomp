package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.ui.text.PhoneTextFilter;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.editor.UseOnceEditorScreen;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class UseOncePhoneNumberVerb extends Verb implements PhoneVerb {
   private Verb _composeVerb;

   public UseOncePhoneNumberVerb() {
      this(null);
   }

   public UseOncePhoneNumberVerb(Verb composeVerb) {
      super(327973, PhoneResources.getResourceBundle(), 108);
      this._composeVerb = composeVerb;
   }

   @Override
   public final Object invoke(Object param) {
      String initialValue = (String)ContextObject.get(param, 253);
      if (initialValue != null) {
         PhoneTextFilter filter = new PhoneTextFilter();
         AbstractStringWrapper asw = AbstractStringWrapper.createInstance(initialValue);
         if (!filter.validate(asw)) {
            initialValue = "";
         }
      } else {
         initialValue = "";
      }

      ContextObject context = new ContextObject(20);
      PhoneUtilities.setPrivateFlag(context, 83);
      String fieldLabel = PhoneResources.getString(6033);
      ContextObject.put(context, 3986845832244503196L, fieldLabel);
      Object result = UseOnceEditorScreen.showUseOnceScreen(PhoneResources.getString(109), 3797587162219887872L, initialValue, context);
      if (result != null && this._composeVerb != null) {
         this._composeVerb.invoke(result);
         return new ContextObject(39);
      } else {
         return result;
      }
   }
}
