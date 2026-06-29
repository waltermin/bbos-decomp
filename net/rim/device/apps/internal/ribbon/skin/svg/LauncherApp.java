package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.internal.ribbon.skin.svg.manager.DirectionalSkinManagerFactory;
import net.rim.device.apps.internal.ribbon.skin.svg.manager.FiveSkinManagerFactory;
import net.rim.device.apps.internal.ribbon.skin.svg.manager.TodaySkinManagerFactory;
import net.rim.device.apps.internal.ribbon.skin.svg.manager.ZenPlusSkinManagerFactory;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.registry.Registry;

class LauncherApp {
   static Class class$net$rim$device$apps$internal$ribbon$skin$svg$FontManagerProvider;
   static Class class$net$rim$device$apps$internal$ribbon$skin$svg$layout$LayoutManagerProvider;

   public static void libMain(String[] args) {
      ThemeManager.addLayoutFactory(new LauncherApp$1());
      FiveSkinManagerFactory.init();
      TodaySkinManagerFactory.init();
      ZenPlusSkinManagerFactory.init();
      DirectionalSkinManagerFactory.init();
      Registry registry = MediaFactory.getRegistry();
      registry.setValue(new String[]{"CONTENT", "x-object:/FontManager"}, "1");
      registry.setValue(
         new String[]{"CONTENT", "x-object:/FontManager", "RESOURCE_PROVIDER"},
         (class$net$rim$device$apps$internal$ribbon$skin$svg$FontManagerProvider == null
               ? (
                  class$net$rim$device$apps$internal$ribbon$skin$svg$FontManagerProvider = class$(
                     "net.rim.device.apps.internal.ribbon.skin.svg.FontManagerProvider"
                  )
               )
               : class$net$rim$device$apps$internal$ribbon$skin$svg$FontManagerProvider)
            .getName()
      );
      registry.setValue(new String[]{"CONTENT", "x-object:/layout"}, "1");
      registry.setValue(
         new String[]{"CONTENT", "x-object:/layout", "RESOURCE_PROVIDER"},
         (class$net$rim$device$apps$internal$ribbon$skin$svg$layout$LayoutManagerProvider == null
               ? (
                  class$net$rim$device$apps$internal$ribbon$skin$svg$layout$LayoutManagerProvider = class$(
                     "net.rim.device.apps.internal.ribbon.skin.svg.layout.LayoutManagerProvider"
                  )
               )
               : class$net$rim$device$apps$internal$ribbon$skin$svg$layout$LayoutManagerProvider)
            .getName()
      );
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
