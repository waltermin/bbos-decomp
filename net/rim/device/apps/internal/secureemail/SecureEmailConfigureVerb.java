package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.apps.api.framework.verb.Verb;

public class SecureEmailConfigureVerb extends Verb {
   private String _description;
   private SecureEmailFactory _factory;

   public SecureEmailConfigureVerb(SecureEmailFactory factory) {
      super(332384);
      this._description = MessageFormat.format(SecureEmailResources.getString(31), new Object[]{factory.getEncodingString()});
      this._factory = factory;
   }

   @Override
   public String toString() {
      return this._description;
   }

   @Override
   public Object invoke(Object context) {
      SecureEmailOptionsItem item = this._factory.createOptionsItem();
      item.perform(6099736323056465049L, context);
      return null;
   }
}
