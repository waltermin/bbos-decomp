package net.rim.device.apps.internal.browser.pme;

import net.rim.device.api.browser.plugin.BrowserContentProviderRegistry;
import net.rim.device.apps.internal.browser.pme.form.PMEFormControlHandler;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.registry.Registry;

public final class PMELoaderApp {
   static Class class$net$rim$device$apps$internal$browser$pme$form$PMEFormHandler;
   static Class class$net$rim$device$apps$internal$browser$pme$form$PMEFormControlHandler;

   public static final void register() {
      Registry registry = MediaFactory.getRegistry();
      registry.setValue(new String[]{"CONTENT", "pme://xforms"}, "1");
      registry.setValue(
         new String[]{"CONTENT", "pme://xforms", "RESOURCE_PROVIDER"},
         (class$net$rim$device$apps$internal$browser$pme$form$PMEFormHandler == null
               ? (class$net$rim$device$apps$internal$browser$pme$form$PMEFormHandler = class$("net.rim.device.apps.internal.browser.pme.form.PMEFormHandler"))
               : class$net$rim$device$apps$internal$browser$pme$form$PMEFormHandler)
            .getName()
      );

      for (int i = PMEFormControlHandler.EXTENSION_IDS.length - 1; i >= 0; i--) {
         registry.setValue(new String[]{"CONTENT", PMEFormControlHandler.EXTENSION_IDS[i]}, "1");
         registry.setValue(
            new String[]{"CONTENT", PMEFormControlHandler.EXTENSION_IDS[i], "RESOURCE_PROVIDER"},
            (class$net$rim$device$apps$internal$browser$pme$form$PMEFormControlHandler == null
                  ? (
                     class$net$rim$device$apps$internal$browser$pme$form$PMEFormControlHandler = class$(
                        "net.rim.device.apps.internal.browser.pme.form.PMEFormControlHandler"
                     )
                  )
                  : class$net$rim$device$apps$internal$browser$pme$form$PMEFormControlHandler)
               .getName()
         );
      }

      BrowserContentProviderRegistry converterRegistry = BrowserContentProviderRegistry.getInstance();
      if (converterRegistry != null) {
         converterRegistry.register(new PMERenderingConverter());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
