package net.rim.device.apps.internal.sms;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.SIMCard;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.internal.phone.data.MissedCallIndicator;
import net.rim.device.apps.internal.sms.message.PackageManager;
import net.rim.device.internal.proxy.Proxy;

public final class Register {
   public static final void libMain(String[] args) {
      SMSService smsService = new SMSService();
      PackageManager.registerOnceOnSystemStart();
      SMSGlobalEventListener.initializeUnreadCountMergeWithText();
      Storage.registerOnceOnSystemStart();
      NotificationsManager.registerNotificationsEngineListener(7986617465467730856L, smsService);
      Proxy p = Proxy.getInstance();
      p.addGlobalEventListener(new SMSGlobalEventListener());
      NotificationsManager.registerSource(7986617465467730856L, new Register$1(), 2);
      SyncManager sm = SyncManager.getInstance();
      if (sm != null) {
         sm.enableSynchronization(new SMSSync(), true, 8);
      }

      SIMCard.addListener(p, SIMManager.getInstance());
      SIMToolkit stk = new SIMToolkit();
      SIMCard.addListener(p, stk);
      p.addGlobalEventListener(stk);
      MissedCallIndicator.getInstance();
      VoicemailIconManager.getInstance();
      SMSOptionsScreen.register();
   }
}
