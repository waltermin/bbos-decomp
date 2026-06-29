package net.rim.wica.runtime.lifecycle.internal;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.LongHashtable;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.lifecycle.UninstallTaskInfo;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.provisioning.ProvisioningService;
import net.rim.wica.runtime.util.Util;

final class LifecycleServiceImpl$LocalizationTask implements Runnable, EventListener {
   private LongHashtable _pendingInstalls;
   private Locale _locale;
   private final LifecycleServiceImpl this$0;

   LifecycleServiceImpl$LocalizationTask(LifecycleServiceImpl this$0, Locale locale) {
      this.this$0 = this$0;
      this._locale = locale;
   }

   @Override
   public final void run() {
      String newLanguage = this._locale.getLanguage();
      ProvisioningService provisioning = (ProvisioningService)this.this$0
         ._provider
         .getService(
            LifecycleServiceImpl.class$net$rim$wica$runtime$provisioning$ProvisioningService == null
               ? (
                  LifecycleServiceImpl.class$net$rim$wica$runtime$provisioning$ProvisioningService = LifecycleServiceImpl.class$(
                     "net.rim.wica.runtime.provisioning.ProvisioningService"
                  )
               )
               : LifecycleServiceImpl.class$net$rim$wica$runtime$provisioning$ProvisioningService
         );
      Vector apps = this.this$0._manager.getApplications();
      int size = apps.size();
      this.this$0._eventService.addListener(200, this);
      this._pendingInstalls = (LongHashtable)(new Object(size));

      for (int i = 0; i < size; i++) {
         WicletImpl app = (WicletImpl)apps.elementAt(i);
         if (!app.getLanguage().equals(newLanguage) && app.isLanguageSupported(newLanguage)) {
            app.setUninstallTask(new UninstallTaskInfo(32));
            app.uninstall();
            DeploymentDescriptor descriptor = new DeploymentDescriptor();
            String uri = app.getUri();
            descriptor.setUri(uri);
            descriptor.setLanguages(app.getInfo().getLanguages());
            int language = Util.arrayFind(descriptor.getLanguages(), this._locale.getLanguage());
            long applicationId = app.getId();
            this._pendingInstalls.put(applicationId, new Object(applicationId));
            boolean persistPackage = true;
            byte[] appPackage = app.getWicletStore().getPackage();
            if (appPackage == null) {
               persistPackage = false;
               appPackage = this.this$0.getPackage(uri, app.getVersion());
            }

            provisioning.installApplication(descriptor, appPackage, applicationId, language, app.getInfo().isRibbonVisible(), true, 0, persistPackage, null);
         }
      }

      if (this._pendingInstalls.size() > 0) {
         label64:
         try {
            synchronized (this) {
               this.wait();
            }
         } finally {
            break label64;
         }
      }

      this.this$0._eventService.removeListener(this);
      this._pendingInstalls = null;
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      Wiclet application = (Wiclet)data;
      this._pendingInstalls.remove(application.getId());
      if (this._pendingInstalls.isEmpty()) {
         synchronized (this) {
            this.notifyAll();
         }
      }
   }
}
