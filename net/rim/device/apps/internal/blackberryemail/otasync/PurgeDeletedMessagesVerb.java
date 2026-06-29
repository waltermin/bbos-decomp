package net.rim.device.apps.internal.blackberryemail.otasync;

import java.util.Vector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.ui.CommonResources;

final class PurgeDeletedMessagesVerb extends Verb {
   private OTAFMConfigurationManagerImpl _configManager;

   PurgeDeletedMessagesVerb(OTAFMConfigurationManagerImpl configManager) {
      super(16987648, MessageResources.getBundle(), 163);
      this._configManager = configManager;
   }

   @Override
   public final Object invoke(Object parameter) {
      Vector choices = new Vector();
      ServiceRecord[] serviceRecords = ServiceBook.getSB().findRecordsByCid("CMIME");

      for (int i = serviceRecords.length - 1; i >= 0; i--) {
         ServiceRecord serviceRecord = serviceRecords[i];
         if (this._configManager.getConfiguration(serviceRecord).wirelessPurgeDeletedMessagesAllowed()) {
            choices.addElement(serviceRecord);
         }
      }

      ServiceRecord serviceRecord = null;
      int count = choices.size();
      if (count > 1) {
         ServiceRecord[] services = new ServiceRecord[count];
         String[] names = new String[count];

         for (int i = count - 1; i >= 0; i--) {
            services[i] = (ServiceRecord)choices.elementAt(i);
            names[i] = services[i].getName();
         }

         Arrays.sort(names, 0, names.length, services, new PurgeDeletedMessagesVerb$StringComparator());
         Dialog dialog = new Dialog(MessageResources.getString(168), names, null, 0, Bitmap.getPredefinedBitmap(1));
         dialog.doModal();
         if (dialog.getSelectedValue() != -1) {
            serviceRecord = services[dialog.getSelectedValue()];
         }
      } else if (count == 1) {
         serviceRecord = (ServiceRecord)choices.elementAt(0);
      }

      if (serviceRecord != null) {
         String[] arguments = new String[]{serviceRecord.getName()};
         String prompt = MessageFormat.format(MessageResources.getString(169), arguments);
         Dialog dialog = new Dialog(prompt, CommonResources.getYesNoArray(1), null, 0, Bitmap.getPredefinedBitmap(2));
         dialog.doModal();
         if (dialog.getSelectedValue() == 1) {
            OTAMessageSync.getInstance().purgeDeletedMessages(serviceRecord);
         }
      }

      return null;
   }
}
