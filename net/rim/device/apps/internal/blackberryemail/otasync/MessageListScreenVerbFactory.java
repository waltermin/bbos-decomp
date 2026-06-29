package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfiguration;

final class MessageListScreenVerbFactory implements VerbFactory {
   private OTAFMConfigurationManagerImpl _configManager;
   private Verb[] _verbs;

   MessageListScreenVerbFactory(OTAFMConfigurationManagerImpl configManager) {
      this._configManager = configManager;
      this._verbs = new Object[1];
      this._verbs[0] = new SyncNowVerb();
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      if (!this._configManager.isOTAFMAvailable()) {
         return null;
      }

      ServiceRecord[] serviceRecords = this._configManager.getServiceRecords();
      synchronized (serviceRecords) {
         for (int i = serviceRecords.length - 1; i >= 0; i--) {
            OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecords[i]);
            if (configuration != null && configuration.getWirelessReconcileEnabled()) {
               return this._verbs;
            }
         }

         return null;
      }
   }
}
