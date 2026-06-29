package net.rim.device.api.smartcard;

import net.rim.device.apps.api.framework.verb.Verb;

class SmartCardOptionsItem$ShowDriverSettingsVerb extends Verb {
   private Object _driver;

   SmartCardOptionsItem$ShowDriverSettingsVerb(Object driver) {
      super(65536, SmartCardOptionsItem._rb.getFamily(), 29);
      this._driver = driver;
   }

   @Override
   public Object invoke(Object parameter) {
      if (!(this._driver instanceof Object)) {
         ((SmartCard)this._driver).displaySettings(null);
         return null;
      } else {
         ((SmartCardReader)this._driver).displaySettings(null);
         return null;
      }
   }
}
