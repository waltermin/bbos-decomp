package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.mms.resources.MMSResources;

final class MMSManualDialVerb extends Verb implements Copyable, SetParameter {
   private AbstractComposeVerb _composeVerb;
   private String _phoneNumber;

   public MMSManualDialVerb(AbstractComposeVerb composeVerb) {
      super(1267024);
      this._composeVerb = composeVerb;
   }

   @Override
   public final Object invoke(Object param) {
      if (this._phoneNumber != null) {
         ContextObject contextObject = ContextObject.clone(param);
         contextObject.put(253, this._phoneNumber);
         Factory factory = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(3797587162219887872L);
         Object result = factory.createInstance(contextObject);
         if (result != null && this._composeVerb != null) {
            contextObject.remove(253);
            contextObject.put(254, result);
            return this._composeVerb.invoke(contextObject);
         }
      }

      return null;
   }

   @Override
   public final String toString() {
      String formatString = MMSResources.getString(11);
      return MessageFormat.format(formatString, new String[]{this._phoneNumber});
   }

   @Override
   public final Object copy() {
      return new MMSManualDialVerb((AbstractComposeVerb)this._composeVerb.copy());
   }

   @Override
   public final void setParameter(Object parameter) {
      this._phoneNumber = (String)ContextObject.get(parameter, 253);
   }
}
