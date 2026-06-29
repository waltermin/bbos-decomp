package net.rim.device.apps.internal.bis.data;

public final class BrandingInfo {
   private String _hostedMailDomain;
   private int _settings;
   private String _availableLanguages;
   private String _defaultLanguage;
   private String _endUserAgreementURL;
   private String _helpRootURL;
   private boolean _selfCreateEnabled;
   private int _numValidationAttempts;
   private static final int FORCE_UNIQUE_SUB_ID;
   private static final int DEVICE_PIN_CHANGE_ENABLED;
   private static final int IMPORT_CONTROL_ENABLED;
   private static final int END_USER_AGREEMENT_ENABLED;
   private static final int AUTO_LOGIN_ENABLED;
   private static final int AUTO_DETECT_HANDHELD_CHANGE_ENABLED;
   private static final int FORCE_USER_ID_EQUALS_HOSTED_ADDRESS;

   public final String getHostedMailDomain() {
      return this._hostedMailDomain;
   }

   public final void setHostedMailDomain(String hostedMailDomain) {
      this._hostedMailDomain = hostedMailDomain;
   }

   public final void setSelfCreateEnabled(boolean selfCreateEnabled) {
      this._selfCreateEnabled = selfCreateEnabled;
   }

   public final boolean isSelfCreateEnabled() {
      return this._selfCreateEnabled;
   }

   public final void setSettings(int settings) {
      this._settings = settings;
   }

   public final String getAvailableLanguages() {
      return this._availableLanguages;
   }

   public final void setAvailableLanguages(String availableLanguages) {
      this._availableLanguages = availableLanguages;
   }

   public final boolean isDevicePINChangeEnabled() {
      return (this._settings & 8) != 0;
   }

   public final boolean isAutoLoginEnabled() {
      return (this._settings & 1024) != 0;
   }

   public final boolean isForceUserIDEqualHostedAddress() {
      return (this._settings & 4096) != 0;
   }

   public final boolean isImportControlEnabled() {
      return (this._settings & 16) != 0;
   }

   public final String getDefaultLanguage() {
      return this._defaultLanguage;
   }

   public final void setDefaultLanguage(String defaultLanguage) {
      this._defaultLanguage = defaultLanguage;
   }

   public final String getEndUserAgreementURL() {
      return this._endUserAgreementURL;
   }

   public final void setEndUserAgreementURL(String endUserAgreementURL) {
      this._endUserAgreementURL = endUserAgreementURL;
   }

   public final String getHelpRootURL() {
      return this._helpRootURL;
   }

   public final void setHelpRootURL(String helpRootURL) {
      this._helpRootURL = helpRootURL;
   }

   public final int getNumValidationAttempts() {
      return this._numValidationAttempts;
   }

   public final void setNumValidationAttempts(int attempts) {
      this._numValidationAttempts = attempts;
   }
}
