package net.rim.device.api.ui.theme;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.XYDimension;
import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;
import net.rim.device.resources.Resource;
import net.rim.plazmic.app.themereader.ThemeEventLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ParameterizedThemeManager {
   private Vector _discoveredThemes = (Vector)(new Object());
   private static final int MAX_ALTERNATE_BASE_SEARCH_SLEEP = 6000;
   private static Hashtable _stockThemes;

   public void discoverTheme(
      InputStream themeDescription, String themeModuleName, Resource themeResource, String manifestVersion, String createdBy, String osVersion
   ) {
      this._discoveredThemes.addElement(new ThemeModuleDescriptor(themeDescription, themeModuleName, themeResource, manifestVersion, createdBy, osVersion));
   }

   public void registerThemes(ThemeEventLogger logger) {
      Enumeration e = this._discoveredThemes.elements();

      while (e.hasMoreElements()) {
         ThemeModuleDescriptor d = (ThemeModuleDescriptor)e.nextElement();
         Theme$Factory factory = newInstance(d, logger);
         if (factory != null) {
            registerTheme(factory);
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static Theme$Factory newInstance(ThemeModuleDescriptor d, ThemeEventLogger logger) {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setAllowUndefinedNamespaces(true);

      DocumentBuilder builder;
      try {
         builder = factory.newDocumentBuilder();
      } catch (Throwable var22) {
         throw new Object(pce.getMessage());
      }

      Document document;
      try {
         document = builder.parse(d.getDescription());
      } catch (Throwable var21) {
         throw new Object(se.getMessage());
      }

      Element root = document.getDocumentElement();
      if (root == null) {
         logger.emptyDescription(d.getModuleName());
         return null;
      }

      String themeName = ParameterizedFactory.getThemeName(root);
      String parentThemeName = ParameterizedFactory.getParentThemeName(root);
      Integer alternateBaseFirstRedundant = ParameterizedFactory.getAlternateBaseFirstRedundant(root);
      Integer alternateBaseSearchSleep = ParameterizedFactory.getAlternateBaseSearchSleep(root);
      XYDimension adjustedAppIconSize = null;
      OSVersion currentOs = OSVersion.getCurrentOs();
      OSVersion manifestOs = OSVersion.forName(d.getOsVersion());
      if (manifestOs != null && manifestOs.compareTo(currentOs) <= 0) {
         String baseCandidate = findBaseCandidate(
            parentThemeName, ParameterizedFactory.getAlternateBase(root), alternateBaseFirstRedundant, alternateBaseSearchSleep, logger, d.getModuleName()
         );
         if (baseCandidate != null) {
            parentThemeName = baseCandidate;
         } else {
            ThemeDescriptor parentTheme = ThemeDescriptor.forNameIgnoreCase(parentThemeName);
            if (parentTheme == null) {
               logger.descriptorNotFound(d.getModuleName(), parentThemeName);
               return null;
            }

            ThemeDescriptor compatibleTheme = parentTheme.getCompatibleTheme(currentOs);
            if (compatibleTheme == null) {
               logger.themeVersionSupportDropped(d.getModuleName());
               return null;
            }

            String candidateParent = compatibleTheme.getName();
            logger.themeNameMapped(d.getModuleName(), parentTheme.getName(), candidateParent);
            if (!isSuitableParent(candidateParent, logger, d.getModuleName())) {
               return null;
            }

            parentThemeName = candidateParent;
            adjustedAppIconSize = getAppIconSize(parentTheme, compatibleTheme);
         }

         String createdBy = d.getCreatedBy();
         boolean mangleFolderNames = createdBy.indexOf("dontMangleFolderNames") == -1;
         return new ParameterizedFactory(
            d.getModuleName(),
            d.getResource(),
            root,
            themeName,
            parentThemeName,
            adjustedAppIconSize,
            mangleFolderNames,
            manifestOs.equals(OSVersion.OS_VERSION_4_1)
         );
      } else {
         logger.themeVersionExceedsReader(d.getModuleName());
         return null;
      }
   }

   private static String findBaseCandidate(
      String parentThemeName,
      String alternateBase,
      Integer alternateBaseFirstRedundant,
      Integer alternateBaseSearchSleep,
      ThemeEventLogger logger,
      String moduleName
   ) {
      String result = attemptSingleBaseSearch(parentThemeName, alternateBase, alternateBaseFirstRedundant, logger, moduleName);
      boolean isUiThread = Application.getApplication().isEventThread();
      if (!isUiThread && result == null && alternateBaseSearchSleep != null) {
         label25:
         try {
            Thread.sleep(Math.max(0, Math.min(alternateBaseSearchSleep, 6000)));
         } finally {
            break label25;
         }

         result = attemptSingleBaseSearch(parentThemeName, alternateBase, alternateBaseFirstRedundant, logger, moduleName);
      }

      return result;
   }

   private static String attemptSingleBaseSearch(
      String parentThemeName, String alternateBase, Integer alternateBaseFirstRedundant, ThemeEventLogger logger, String moduleName
   ) {
      BaseThemeIterator iter = new BaseThemeIterator(parentThemeName, alternateBase, alternateBaseFirstRedundant);

      while (iter.hasNextBaseTheme()) {
         String baseCandidate = iter.nextBaseTheme();
         if (baseCandidate != null && baseCandidate.length() > 0 && isSuitableParent(baseCandidate, logger, moduleName)) {
            return baseCandidate;
         }
      }

      return null;
   }

   private static boolean isSuitableParent(String candidateParent, ThemeEventLogger logger, String moduleName) {
      boolean result = false;
      Theme$Factory factory = ThemeManager.getThemeFactory(candidateParent);
      if (factory == null) {
         logger.candidateParentNotRegistered(moduleName, candidateParent);
         return result;
      } else if (factory instanceof ParameterizedFactory) {
         logger.candidateParentThirdParty(moduleName, candidateParent);
         return result;
      } else if (!factory.isActivatable()) {
         logger.candidateParentNotActivatable(moduleName, candidateParent);
         return result;
      } else {
         return true;
      }
   }

   private static XYDimension getAppIconSize(ThemeDescriptor parentTheme, ThemeDescriptor compatibleTheme) {
      int parentWidth = parentTheme.getApplicationIconWidth();
      int parentHeight = parentTheme.getApplicationIconHeight();
      int compatibleWidth = compatibleTheme.getApplicationIconWidth();
      int compatibleHeight = compatibleTheme.getApplicationIconHeight();
      if (parentWidth == compatibleWidth && parentHeight == compatibleHeight) {
         return null;
      }

      int width = Math.max(parentWidth, compatibleWidth);
      int height = Math.max(parentHeight, compatibleHeight);
      return (XYDimension)(new Object(width, height));
   }

   private static void registerTheme(Theme$Factory factory) {
      try {
         ThemeManager.add(factory);
      } finally {
         return;
      }
   }

   public static synchronized Theme getTheme(String name) {
      if (_stockThemes == null) {
         _stockThemes = (Hashtable)(new Object());
      }

      Theme result;
      if (!_stockThemes.containsKey(name)) {
         result = ThemeManager.getTheme(name);
         if (result != null) {
            _stockThemes.put(name, result);
            return result;
         }
      } else {
         result = (Theme)_stockThemes.get(name);
      }

      return result;
   }
}
