package net.rim.device.apps.internal.mms;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.apps.internal.mms.options.MMSOptions;
import net.rim.device.apps.internal.mms.plugin.MMSBrowserPlugin;
import net.rim.device.apps.internal.mms.service.MMSService;
import net.rim.device.apps.internal.mms.verbs.MMSVerbFactory;
import net.rim.device.internal.io.file.MetaDataProvider;

final class MMSRegistrationRunnable implements Runnable {
   private static final String MESSAGING_MODULE_NAME;

   @Override
   public final void run() {
      MMSGlobalEventListener.initializeUnreadCountMergeWithText();
      MMSOptions.registerOnceOnSystemStart();
      NotificationsManager.registerSource(8609386677418041260L, new MMSRegistrationRunnable$1(this), 2);
      MMSVerbFactory.registerOnceOnSystemStart();
      MMSService.registerOnceOnSystemStart();
      MMSBrowserPlugin.registerOnceOnSystemStart();
      MIMETypeAssociations.registerType("mms", "application/vnd.wap.mms-message", 0);
      MetaDataProvider.registerProvider("application/vnd.wap.mms-message", new MMSThumbnailProvider());
   }
}
