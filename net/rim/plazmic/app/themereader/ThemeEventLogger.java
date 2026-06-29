package net.rim.plazmic.app.themereader;

import java.io.IOException;
import net.rim.device.api.system.EventLogger;

public class ThemeEventLogger {
   private long _appGuid;
   private static final long THEME_READER_GUID;
   private static final String THEME_READER_NAME;
   private static ThemeEventLogger _logger;

   private ThemeEventLogger(long appGuid) {
      this._appGuid = appGuid;
   }

   private static ThemeEventLogger createThemeEventLogger(long appGuid, String appName) {
      EventLogger.register(appGuid, appName, 2);
      return new ThemeEventLogger(appGuid);
   }

   public static ThemeEventLogger getThemeEventLogger() {
      if (_logger == null) {
         _logger = createThemeEventLogger(8451140947859979436L, "ThemeReader");
      }

      return _logger;
   }

   void startThemeSearch() {
      this.info("Start theme search");
   }

   void endThemeSearch() {
      this.info("End theme search");
   }

   void unexpectedGeneralFailure(Exception e) {
      this.error(((StringBuffer)(new Object("Gen failure "))).append(e).toString());
   }

   void unexpectedReadFailure(IOException ioe) {
      this.error(((StringBuffer)(new Object("Read failure "))).append(ioe.getMessage()).toString());
   }

   void namePrefixMatch(String moduleName) {
      this.info(getModuleString(moduleName, "Name prefix match"));
   }

   void failedApplicationControl(String moduleName) {
      this.warning(getModuleString(moduleName, "Failed AppCtrl"));
   }

   void noResourceClass(String moduleName) {
      this.warning(getModuleString(moduleName, "No resource"));
   }

   void noManifest(String moduleName) {
      this.warning(getModuleString(moduleName, "No manifest"));
   }

   void noCasualHash(String moduleName) {
      this.warning(getModuleString(moduleName, "No hash"));
   }

   void noDescription(String moduleName) {
      this.warning(getModuleString(moduleName, "No desc"));
   }

   void exceedsMaximumSize(String moduleName) {
      this.warning(getModuleString(moduleName, "Exceeds max size"));
   }

   void invalidCasualHash(String moduleName) {
      this.warning(getModuleString(moduleName, "Invalid hash"));
   }

   public void emptyDescription(String moduleName) {
      this.warning(getModuleString(moduleName, "Desc empty"));
   }

   public void themeVersionExceedsReader(String moduleName) {
      this.warning(getModuleString(moduleName, "Version exceeds reader"));
   }

   public void themeVersionSupportDropped(String moduleName) {
      this.warning(getModuleString(moduleName, "Version support dropped"));
   }

   public void descriptorNotFound(String moduleName, String themeName) {
      this.warning(getModuleString(moduleName, ((StringBuffer)(new Object("Cannot find descriptor "))).append(themeName).toString()));
   }

   public void candidateParentNotRegistered(String moduleName, String candidateParent) {
      this.warning(getModuleString(moduleName, ((StringBuffer)(new Object("Parent not registered "))).append(candidateParent).toString()));
   }

   public void candidateParentThirdParty(String moduleName, String candidateParent) {
      this.warning(getModuleString(moduleName, ((StringBuffer)(new Object("Parent is third-party "))).append(candidateParent).toString()));
   }

   public void candidateParentNotActivatable(String moduleName, String candidateParent) {
      this.warning(getModuleString(moduleName, ((StringBuffer)(new Object("Parent not activatable "))).append(candidateParent).toString()));
   }

   public void themeNameMapped(String moduleName, String fromName, String toName) {
      this.info(getModuleString(moduleName, ((StringBuffer)(new Object("Mapped from "))).append(fromName).append(" to ").append(toName).toString()));
   }

   public void activationWarning(String moduleName, Exception e) {
      this.warning(getModuleString(moduleName, ((StringBuffer)(new Object("Activation warning: "))).append(e).toString()));
   }

   public void unknownActivationWarning(String moduleName, Exception e) {
      this.warning(getModuleString(moduleName, ((StringBuffer)(new Object("Unknown activation warning: "))).append(e).toString()));
   }

   private static String getModuleString(String moduleName, String s) {
      return ((StringBuffer)(new Object())).append('(').append(Main.getThemeModuleNameProper(moduleName)).append(')').append(s).toString();
   }

   private void info(String s) {
      EventLogger.logEvent(this._appGuid, s.getBytes(), 4);
   }

   private void warning(String s) {
      EventLogger.logEvent(this._appGuid, s.getBytes(), 3);
   }

   private void error(String s) {
      EventLogger.logEvent(this._appGuid, s.getBytes(), 2);
   }
}
