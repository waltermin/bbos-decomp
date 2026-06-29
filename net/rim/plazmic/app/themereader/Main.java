package net.rim.plazmic.app.themereader;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.theme.ParameterizedThemeManager;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.plazmic.internal.math.HashSum;

public class Main extends Application implements GlobalEventListener {
   private ThemeEventLogger _themeEventLogger = ThemeEventLogger.getThemeEventLogger();
   private static final String THEME_MODULE_NAME_PREFIX = "com_plazmic_theme_";
   private static final int MAX_SIZE = 1572864;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static void libMain(String[] args) {
      Main m = new Main();

      label20:
      try {
         searchThemes(CodeModuleManager.getModuleHandles(), m._themeEventLogger);
      } catch (Throwable var4) {
         m._themeEventLogger.unexpectedGeneralFailure(e);
         break label20;
      }

      m.enterEventDispatcher();
   }

   private Main() {
      this.addGlobalEventListener(this);
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 256826950193107649L) {
         searchThemes(new int[]{data0}, this._themeEventLogger);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static void searchThemes(int[] candidateHandles, ThemeEventLogger themeEventLogger) {
      themeEventLogger.startThemeSearch();
      ParameterizedThemeManager mgr = new ParameterizedThemeManager();
      Enumeration matchingNames = collectModuleNames(candidateHandles, themeEventLogger);

      while (matchingNames.hasMoreElements()) {
         String moduleName = (String)matchingNames.nextElement();
         Resource r = Resource$Internal.getResourceClass(moduleName);
         if (r != null) {
            ThemeManifest themeManifest = getThemeManifest(r);
            byte[] themeDescription = getThemeDescription(themeManifest, r);
            if (isValidTheme(moduleName, themeManifest, themeDescription, themeEventLogger)) {
               mgr.discoverTheme(
                  new ByteArrayInputStream(themeDescription),
                  moduleName,
                  r,
                  themeManifest.getManifestVersion(),
                  themeManifest.getCreatedBy(),
                  themeManifest.getOsVersion()
               );
            }
         } else {
            themeEventLogger.noResourceClass(moduleName);
         }
      }

      label34:
      try {
         mgr.registerThemes(themeEventLogger);
      } catch (Throwable var9) {
         themeEventLogger.unexpectedReadFailure(ioe);
         break label34;
      }

      themeEventLogger.endThemeSearch();
   }

   public static boolean registerTheme(String themeName) {
      boolean success = true;
      int handle = CodeModuleManager.getModuleHandle("com_plazmic_theme_" + themeName);
      if (handle == 0) {
         return false;
      }

      searchThemes(new int[]{handle}, ThemeEventLogger.getThemeEventLogger());
      return success;
   }

   private static Enumeration collectModuleNames(int[] handles, ThemeEventLogger themeEventLogger) {
      Vector names = new Vector();

      for (int i = 0; i < handles.length; i++) {
         String name = null;

         label46:
         try {
            name = CodeModuleManager.getModuleName(handles[i]);
         } finally {
            break label46;
         }

         if (name != null && name.startsWith("com_plazmic_theme_")) {
            themeEventLogger.namePrefixMatch(name);
            if (ApplicationControl.isThemeDataAllowed(handles[i])) {
               names.addElement(name);
            } else {
               themeEventLogger.failedApplicationControl(name);
            }
         }
      }

      return names.elements();
   }

   private static ThemeManifest getThemeManifest(Resource r) {
      ThemeManifest manifest = null;
      byte[] manifestData = r.getResource("theme_manifest.mf");
      if (manifestData != null) {
         try {
            return new ThemeManifest(new ByteArrayInputStream(manifestData));
         } finally {
            return manifest;
         }
      } else {
         return manifest;
      }
   }

   private static byte[] getThemeDescription(ThemeManifest manifest, Resource r) {
      byte[] description = null;
      if (manifest != null && manifest.getDescription() != null) {
         description = r.getResource(manifest.getDescription());
      }

      return description;
   }

   private static boolean isValidTheme(String themeModuleName, ThemeManifest themeManifest, byte[] description, ThemeEventLogger themeEventLogger) {
      if (themeManifest == null) {
         themeEventLogger.noManifest(themeModuleName);
         return false;
      }

      if (themeManifest.getHash() == null) {
         themeEventLogger.noCasualHash(themeModuleName);
         return false;
      }

      if (description == null) {
         themeEventLogger.noDescription(themeModuleName);
         return false;
      }

      if (CodeModuleManager.getModuleCodeSize(CodeModuleManager.getModuleHandle(themeModuleName)) > 1572864) {
         themeEventLogger.exceedsMaximumSize(themeModuleName);
         return false;
      }

      try {
         int suspectHash = Integer.parseInt(themeManifest.getHash());
         if (suspectHash != getActualHash(description)) {
            themeEventLogger.invalidCasualHash(themeModuleName);
            return false;
         } else {
            return true;
         }
      } finally {
         themeEventLogger.invalidCasualHash(themeModuleName);
         return false;
      }
   }

   private static int getActualHash(byte[] b) {
      HashSum hs = new HashSum();
      hs.update(b);
      return hs.getCasualHash();
   }

   static String getThemeModuleNameProper(String moduleName) {
      int startIndex = moduleName.startsWith("com_plazmic_theme_") ? 18 : 0;
      return moduleName.substring(startIndex);
   }
}
