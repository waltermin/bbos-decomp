package net.rim.device.apps.internal.bis.session;

import net.rim.device.apps.internal.bis.data.BrandingInfo;
import net.rim.device.apps.internal.bis.data.Filter;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.SecretQuestion;
import net.rim.device.apps.internal.bis.data.UserInfo;

public final class ClientSessionState {
   private BrandingInfo _brandingInfo;
   private UserInfo _userInfo;
   private String _accountSetupUserName;
   private String[] _accountSetupSuggestions;
   private Mailbox _mailboxToModify;
   private String _integrationEmail;
   private String _integrationPassword;
   private String _termsAndConditions;
   private String _termsVersion;
   private String _tempPassword;
   private String _tempUsername;
   private boolean _tempRememberCredentials;
   private String _tempCurrentPin;
   private Filter _filterToModify;
   private boolean _synchMailboxEnabled;
   private boolean _synchAddressBook;
   private boolean _synchCalendar;
   private boolean _autoAuth = false;
   private SecretQuestion[] _secretQuestions;
   private static ClientSessionState _instance;

   public static final void initialize() {
      _instance = new ClientSessionState();
   }

   public static final void shutdown() {
      _instance = null;
   }

   public static final ClientSessionState getInstance() {
      return _instance;
   }

   public final boolean getSynchAddressBook() {
      return this._synchAddressBook;
   }

   public final boolean getSynchCalendar() {
      return this._synchCalendar;
   }

   public final boolean getSynchMailboxEnabled() {
      return this._synchMailboxEnabled;
   }

   public final void setSynchAddressBook(boolean synchAddressBook) {
      this._synchAddressBook = synchAddressBook;
   }

   public final void setSynchCalendar(boolean synchCalendar) {
      this._synchCalendar = synchCalendar;
   }

   public final String getTempCurrentPin() {
      return this._tempCurrentPin;
   }

   public final void setTempCurrentPin(String currentPin) {
      this._tempCurrentPin = currentPin;
   }

   public final boolean getTempRememberCredentials() {
      return this._tempRememberCredentials;
   }

   public final void setTempRememberCredentials(boolean rememberCredentials) {
      this._tempRememberCredentials = rememberCredentials;
   }

   public final String getTempUsername() {
      return this._tempUsername;
   }

   public final void setTempUsername(String username) {
      this._tempUsername = username;
   }

   public final String getTempPassword() {
      return this._tempPassword;
   }

   public final void setTempPassword(String password) {
      this._tempPassword = password;
   }

   public final BrandingInfo getBrandingInfo() {
      return this._brandingInfo;
   }

   public final void setBrandingInfo(BrandingInfo brandingInfo) {
      this._brandingInfo = brandingInfo;
   }

   public final UserInfo getUserInfo() {
      return this._userInfo;
   }

   public final void setUserInfo(UserInfo userInfo) {
      this._userInfo = userInfo;
   }

   public final String getAccountSetupUserName() {
      return this._accountSetupUserName;
   }

   public final void setAccountSetupUserName(String userName) {
      this._accountSetupUserName = userName;
   }

   public final String[] getAccountSetupSuggestions() {
      return this._accountSetupSuggestions;
   }

   public final void setAccountSetupSuggestions(String[] userNameSuggestions) {
      this._accountSetupSuggestions = userNameSuggestions;
   }

   public final Mailbox getMailboxToModify() {
      return this._mailboxToModify;
   }

   public final void setMailboxToModify(Mailbox mailboxToModify) {
      this._mailboxToModify = mailboxToModify;
   }

   public final String getIntegrationEmail() {
      return this._integrationEmail;
   }

   public final void setIntegrationEmail(String integrationEmail) {
      this._integrationEmail = integrationEmail;
   }

   public final String getIntegrationPassword() {
      return this._integrationPassword;
   }

   public final void setIntegrationPassword(String integrationPassword) {
      this._integrationPassword = integrationPassword;
   }

   public final String getTermsAndConditions() {
      return this._termsAndConditions;
   }

   public final void setTermsAndConditions(String termsAndConditions) {
      this._termsAndConditions = termsAndConditions;
   }

   public final String getTermsVersion() {
      return this._termsVersion;
   }

   public final void setTermsVersion(String version) {
      this._termsVersion = version;
   }

   public final Filter getFilterToModify() {
      return this._filterToModify;
   }

   public final void setFilterToModify(Filter filterToModify) {
      this._filterToModify = filterToModify;
   }

   public final boolean isAutoAuth() {
      return this._autoAuth;
   }

   public final void setAutoAuth(boolean autoAuth) {
      this._autoAuth = autoAuth;
   }

   public final SecretQuestion[] getSecretQuestions() {
      return this._secretQuestions;
   }

   public final void setSecretQuestions(SecretQuestion[] questions) {
      this._secretQuestions = questions;
   }
}
