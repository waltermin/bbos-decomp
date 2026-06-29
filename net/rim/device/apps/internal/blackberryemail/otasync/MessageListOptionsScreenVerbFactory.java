package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.vm.Array;

final class MessageListOptionsScreenVerbFactory implements VerbFactory {
   private OTAFMConfigurationManagerImpl _configManager;
   private Verb[] _verbs;

   MessageListOptionsScreenVerbFactory(OTAFMConfigurationManagerImpl configManager) {
      this._configManager = configManager;
      this._verbs = new Object[0];
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      synchronized (this._verbs) {
         int verbCount = 0;
         Array.resize(this._verbs, 1);
         ServiceRecord[] serviceRecords = ServiceBook.getSB().findRecordsByCid("CMIME");

         for (int i = serviceRecords.length - 1; i >= 0; i--) {
            ServiceRecord serviceRecord = serviceRecords[i];
            if (this._configManager.getConfiguration(serviceRecord).getWirelessPurgeDeletedMessagesEnabled()) {
               this._verbs[verbCount++] = new PurgeDeletedMessagesVerb(this._configManager);
               break;
            }
         }

         Array.resize(this._verbs, verbCount);
         return this._verbs;
      }
   }
}
