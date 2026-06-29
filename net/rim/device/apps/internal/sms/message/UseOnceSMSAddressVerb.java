package net.rim.device.apps.internal.sms.message;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.editor.UseOnceEditorScreen;
import net.rim.device.apps.internal.sms.resources.SMSResources;

final class UseOnceSMSAddressVerb extends Verb {
   private Verb _composeVerb;
   private int _type;
   static final int PHONE;
   static final int EMAIL;

   UseOnceSMSAddressVerb() {
      this(null, 0);
   }

   UseOnceSMSAddressVerb(Verb composeVerb) {
      this(composeVerb, 0);
   }

   UseOnceSMSAddressVerb(Verb composeVerb, int type) {
      super(type == 0 ? 327984 : 327985, SMSResources.getResourceBundle(), 190);
      this._type = type;
      this._composeVerb = composeVerb;
   }

   @Override
   public final Object invoke(Object param) {
      String initialValue = (String)ContextObject.get(param, 253);
      Object result = null;
      ContextObject context = (ContextObject)(new Object(55));
      switch (this._type) {
         case -1:
            break;
         case 0:
            if (initialValue != null) {
               TextFilter filter = TextFilter.get(12);
               if (!filter.validate(AbstractStringWrapper.createInstance(initialValue))) {
                  initialValue = null;
               }
            }

            String phoneNumber = (String)ContextObject.get(param, 247);
            if (phoneNumber != null) {
               ContextObject contextObject = ContextObject.clone(param);
               contextObject.put(253, phoneNumber);
               Factory factory = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(3797587162219887872L);
               result = factory.createInstance(contextObject);
            } else {
               result = UseOnceEditorScreen.showUseOnceScreen(SMSResources.getString(191), 3797587162219887872L, initialValue, context);
            }
            break;
         case 1:
         default:
            if (initialValue != null) {
               TextFilter filter = TextFilter.get(8);
               if (!filter.validate(AbstractStringWrapper.createInstance(initialValue))) {
                  initialValue = null;
               }
            }

            result = UseOnceEditorScreen.showUseOnceScreen(SMSResources.getString(191), -2985347935260258684L, initialValue, context);
      }

      if (result != null && this._composeVerb != null) {
         ContextObject contextObject = ContextObject.clone(param);
         contextObject.remove(253);
         contextObject.put(254, result);
         result = this._composeVerb.invoke(contextObject);
      }

      return result;
   }
}
