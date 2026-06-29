package net.rim.device.apps.internal.blackberryemail.properties;

import net.rim.device.api.util.Persistable;

class MessagePropertiesDefaults$MessagePropertiesDefaultsPersistedData implements Persistable {
   long _defaultEncodingUID;
   int _defaultEncodingAction;
   int _defaultMessageClassification;

   MessagePropertiesDefaults$MessagePropertiesDefaultsPersistedData() {
      this.reset();
   }

   void reset() {
      this._defaultEncodingUID = 182808770805039415L;
      this._defaultEncodingAction = 0;
      this._defaultMessageClassification = 0;
   }
}
