package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.javascript.JavaScriptRegistry;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.ui.BrowserChoiceField;
import net.rim.device.apps.internal.browser.verbs.SavePropertyVerb;
import net.rim.device.cldc.io.ippp.IPPPTransportBase;
import net.rim.vm.Array;

public final class BrowserConfigProperty extends BrowserProperty {
   private BrowserConfigRecord _config;
   private BrowserChoiceField _browserChoiceField;
   private VerticalFieldManager _browserConfigSubScreen;
   private EditField _homePageUrlField;
   private ObjectChoiceField _showImagesField;
   private ObjectChoiceField _showImagePlaceholdersField;
   private ObjectChoiceField _sendPhoneNumberField;
   private ObjectChoiceField _startupPageField;
   private EditField _transportServiceCIDField;
   private EditField _transportServiceUIDField;
   private ObjectChoiceField _configValuesEditableField;
   private TransportConfigProperty _transportConfigProperty;
   private EditField _timeoutField;
   private ObjectChoiceField _constrainedNavigationField;
   private ObjectChoiceField _constrainedContentModeField;
   private ObjectChoiceField _defaultContentModeField;
   private ObjectChoiceField _defaultEmulationModeField;
   private ObjectChoiceField _configTypeField;
   private ObjectChoiceField _autoStartField;
   private EditField _ribbonTitleField;
   private EditField _ribbonPositionField;
   private EditField _brandingIconField;
   private EditField _vendorIdField;
   private HostRoutingTable _hrt;
   private BitSet _overriddenSet;
   private BrowserConfigButtonField _useCurrentAsHomePageField;
   private ObjectChoiceField _httpsEncryptionField;
   private ObjectChoiceField _constrainedSavingField;
   private EditField _appDownloadModuleNameField;
   private EditField _appDownloadModuleVersionField;
   private EditField _iconUrlField;
   private EditField _localizedStringsField;
   private EditField _bookmarksFolderNameField;
   private EditField _helpLinksField;
   private EditField _helpGroupLabelField;
   private CheckboxField _javascriptEnabledField;
   private CheckboxField _allowPopupsEnabledField;
   private CheckboxField _javascriptWatchdogEnabledField;
   private CheckboxField _foregroundBackgroundColorField;
   private CheckboxField _backgroundImagesField;
   private CheckboxField _useHtmlTablesField;
   private CheckboxField _enableCssField;
   private CheckboxField _enableEmbeddedRichContentField;
   private CheckboxField _enableBSMField;
   private EditField _moreImagesUrlField;
   private EditField _moreTunesUrlField;
   private EditField _moreThemesUrlField;
   private CheckboxField _rimBrandedField;
   private CheckboxField _sendProfileDiffsField;
   private EditField _uaprofURIField;
   private CheckboxField _promptEnabledJavascriptField;
   private CheckboxField _useSeparateIconField;
   private EditField _ribbonIconTypeField;
   private EditField _domainsField;
   private ObjectChoiceField _provisionedBookmarksInteractionField;
   private ObjectChoiceField _provisionedBookmarksExpandFolderField;
   private NumericChoiceField _interactivePingPacketTimer;
   private boolean _itPolicyChanged;
   private static final int HOME_PAGE_OVERRIDDEN;
   private static final int SHOW_IMAGES_OVERRIDDEN;
   private static final int SHOW_IMAGE_PLACEHOLDERS_OVERRIDDEN;
   private static final int CONTENT_MODE_OVERRIDDEN;
   private static final int EMULATION_MODE_OVERRIDDEN;
   private static final int JAVASCRIPT_ENABLED_OVERRIDDEN;
   private static final int JAVASCRIPT_POPUPS_ALLOWED_OVERRIDDEN;
   private static final int BACKGROUND_IMAGES_OVERRIDDEN;
   private static final int HTML_TABLES_ENABLED_OVERRIDDEN;
   private static final int CSS_ENABLED_OVERRIDDEN;
   private static final int EMBEDDED_MEDIA_ENABLED_OVERRIDDEN;
   private static final int FOREGROUND_BACKGROUND_COLORS_OVERRIDDEN;
   private static final int BSM_ENABLED_OVERRIDDEN;
   private static final int CONFIG_VALUES_EDITABLE_OVERRIDDEN;
   private static final int STARTUP_PAGE_OVERRIDDEN;

   @Override
   public final String getLabel() {
      return BrowserResources.getString(184);
   }

   @Override
   public final Screen getScreen(boolean restrictedAccess) {
      this._itPolicyChanged = false;
      super._restrictedAccess = restrictedAccess;
      Screen screen = this.generateScreen(BrowserResources.getString(184));
      String currentBrowserConfigUID = null;
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null) {
         currentBrowserConfigUID = session.getConfig().getUid();
      }

      this._overriddenSet = (BitSet)(new Object());
      if (currentBrowserConfigUID == null) {
         this._hrt = null;
         screen.add((Field)(new Object(BrowserResources.getString(233))));
         return screen;
      } else {
         this._config = BrowserConfigRecord.getDecodedConfig(currentBrowserConfigUID, -1, null);
         if (this._config == null) {
            this._hrt = null;
            screen.add((Field)(new Object(BrowserResources.getString(201))));
            return screen;
         } else {
            this._browserChoiceField = new BrowserChoiceField(BrowserResources.getString(819), currentBrowserConfigUID);
            this._browserChoiceField.setChangeListener(this);
            screen.add(this._browserChoiceField);
            screen.add((Field)(new Object()));
            this._browserConfigSubScreen = this.getBrowserConfigSubScreen();
            screen.add(this._browserConfigSubScreen);
            this._browserConfigSubScreen.setFocus();
            return screen;
         }
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (context != Integer.MIN_VALUE) {
         if (field == this._useCurrentAsHomePageField) {
            Page page = BrowserDaemonRegistry.getInstance().getCurrentPage();
            String url = page.getURL();
            if (this._homePageUrlField.isEditable() && url != null) {
               this._homePageUrlField.setText(url);
               return;
            }
         } else if (field == this._browserChoiceField) {
            this._browserChoiceField.setDirty(false);
            BrowserConfigRecord newConfig = this._browserChoiceField.getSelectedBrowser();
            if (newConfig != null && !StringUtilities.strEqualIgnoreCase(newConfig.getUid(), this._config.getUid(), 1701729619)) {
               if (!this.confirm(null, null)) {
                  this._browserChoiceField.setSelectedBrowser(this._config.getUid());
                  return;
               }

               Manager mgr = this._browserConfigSubScreen.getManager();
               if (mgr != null) {
                  mgr.delete(this._browserConfigSubScreen);
                  this._itPolicyChanged = false;
                  this._overriddenSet.reset();
                  this._config = newConfig;
                  this._browserConfigSubScreen = this.getBrowserConfigSubScreen();
                  mgr.add(this._browserConfigSubScreen);
                  this._browserConfigSubScreen.setFocus();
                  return;
               }
            }
         } else {
            this.showOrHideOptionalFields(field);
         }
      }
   }

   private final void showOrHideOptionalFields(Field field) {
      if (field == this._javascriptEnabledField) {
         Manager mgr = this._javascriptEnabledField.getManager();
         if (mgr != null) {
            boolean checked = this._javascriptEnabledField.getChecked();
            if (checked && this._allowPopupsEnabledField.getManager() == null) {
               int indexToAddAt = this._javascriptEnabledField.getIndex() + 1;
               mgr.delete(this._promptEnabledJavascriptField);
               mgr.insert(this._allowPopupsEnabledField, indexToAddAt);
               mgr.insert(this._javascriptWatchdogEnabledField, indexToAddAt + 1);
               return;
            }

            if (!checked && this._allowPopupsEnabledField.getManager() == mgr) {
               mgr.delete(this._allowPopupsEnabledField);
               mgr.delete(this._javascriptWatchdogEnabledField);
               mgr.insert(this._promptEnabledJavascriptField, this._javascriptEnabledField.getIndex() + 1);
               return;
            }
         }
      } else if (field == this._showImagesField) {
         Manager mgr = this._showImagesField.getManager();
         if (mgr != null) {
            int showImages = this._showImagesField.getSelectedIndex();
            if (showImages != 2 && this._showImagePlaceholdersField.getManager() == null) {
               mgr.insert(this._showImagePlaceholdersField, this._showImagesField.getIndex() + 1);
               return;
            }

            if (showImages == 2 && this._showImagePlaceholdersField.getManager() == mgr) {
               mgr.delete(this._showImagePlaceholdersField);
            }
         }
      }
   }

   private final VerticalFieldManager getBrowserConfigSubScreen() {
      boolean otherFieldsEditable = this.canEditOtherFields();
      VerticalFieldManager screen = (VerticalFieldManager)(new Object());
      int fontHeight = Font.getDefault().getHeight();
      int configType = this._config.getPropertyAsInt(12);
      boolean mdsConfig = configType == 1 || configType == 4;
      String homePageUrl = this._config.getHomePageWithOverride();
      if (homePageUrl != null && !homePageUrl.equals(this._config.getPropertyAsString(1))) {
         this._overriddenSet.set(0);
      }

      this._homePageUrlField = (EditField)(new Object(BrowserResources.getString(185), homePageUrl, 1000000, 117440512));
      if (!this.canEditUrlField()) {
         this._homePageUrlField.setEditable(false);
      }

      if (this.canEditUrlField() && BrowserDaemonRegistry.getInstance().getCurrentPage() != null) {
         this._useCurrentAsHomePageField = new BrowserConfigButtonField(BrowserResources.getString(642), 65536);
         this._useCurrentAsHomePageField.setChangeListener(this);
      } else {
         this._useCurrentAsHomePageField = null;
      }

      int showImages = this._config.getPropertyAsIntWithOverride((byte)18);
      if (showImages != -1 && showImages != this._config.getPropertyAsInt(18)) {
         this._overriddenSet.set(1);
      }

      this._showImagesField = (ObjectChoiceField)(new Object(BrowserResources.getString(596), BrowserResources.getStringArray(529), showImages));
      int showImagePlaceholders = this._config.getPropertyAsIntWithOverride((byte)19);
      if (showImagePlaceholders != -1 && showImagePlaceholders != this._config.getPropertyAsInt(19)) {
         this._overriddenSet.set(2);
      }

      this._showImagePlaceholdersField = (ObjectChoiceField)(new Object(
         BrowserResources.getString(295), CommonResources.getYesNoArray(1), showImagePlaceholders
      ));
      int contentMode = this._config.getContentModeWithOverride();
      if (contentMode != -1 && contentMode != this._config.getPropertyAsInt(10)) {
         this._overriddenSet.set(3);
      }

      this._defaultContentModeField = (ObjectChoiceField)(new Object(BrowserResources.getString(398), BrowserResources.getStringArray(594), contentMode));
      if (this._config.getPropertyAsInt(9) != -1) {
         this._defaultContentModeField.setEditable(false);
      }

      int emulationMode = this._config.getPropertyAsIntWithOverride((byte)17);
      if (emulationMode != -1 && emulationMode != this._config.getPropertyAsInt(17)) {
         this._overriddenSet.set(4);
      }

      this._defaultEmulationModeField = (ObjectChoiceField)(new Object(BrowserResources.getString(420), BrowserResources.getStringArray(591), emulationMode - 1));
      if (super._restrictedAccess && !otherFieldsEditable && !mdsConfig) {
         this._defaultEmulationModeField.setEditable(false);
      }

      int configValuesEditable = this._config.getPropertyAsIntWithOverride((byte)5);
      if (configValuesEditable != -1 && configValuesEditable != this._config.getPropertyAsInt(5)) {
         this._overriddenSet.set(13);
      }

      this._configValuesEditableField = (ObjectChoiceField)(new Object(
         BrowserResources.getString(203), BrowserResources.getStringArray(489), configValuesEditable
      ));
      if (!otherFieldsEditable) {
         this._configValuesEditableField.setEditable(false);
      }

      this._ribbonTitleField = (EditField)(new Object(BrowserResources.getString(440), this._config.getPropertyAsString(11)));
      if (!otherFieldsEditable) {
         this._ribbonTitleField.setEditable(false);
      }

      this._ribbonPositionField = (EditField)(new Object(BrowserResources.getString(654), String.valueOf(this._config.getPropertyAsInt(25))));
      this._ribbonPositionField.setFilter(TextFilter.get(1));
      if (!otherFieldsEditable) {
         this._ribbonPositionField.setEditable(false);
      }

      this._configTypeField = (ObjectChoiceField)(new Object(
         BrowserResources.getString(450), BrowserResources.getStringArray(531), this._config.getPropertyAsInt(12)
      ));
      if (!otherFieldsEditable) {
         this._configTypeField.setEditable(false);
      }

      this._autoStartField = (ObjectChoiceField)(new Object(
         BrowserResources.getString(544), CommonResources.getYesNoArray(1), this._config.getPropertyAsInt(20)
      ));
      if (!otherFieldsEditable) {
         this._autoStartField.setEditable(false);
      }

      this._sendPhoneNumberField = (ObjectChoiceField)(new Object(
         BrowserResources.getString(636), BrowserResources.getStringArray(637), this._config.getPropertyAsInt(23)
      ));
      if (!otherFieldsEditable) {
         this._sendPhoneNumberField.setEditable(false);
      }

      this._brandingIconField = (EditField)(new Object(BrowserResources.getString(561), String.valueOf(this._config.getPropertyAsInt(21))));
      this._brandingIconField.setFilter(TextFilter.get(5));
      if (!otherFieldsEditable) {
         this._brandingIconField.setEditable(false);
      }

      this._vendorIdField = (EditField)(new Object(BrowserResources.getString(564), this._config.getPropertyAsString(22)));
      if (!otherFieldsEditable) {
         this._vendorIdField.setEditable(false);
      }

      int startupPage = this._config.getPropertyAsIntWithOverride((byte)2);
      if (startupPage != -1 && startupPage != this._config.getPropertyAsInt(2)) {
         this._overriddenSet.set(14);
      }

      this._startupPageField = (ObjectChoiceField)(new Object(BrowserResources.getString(195), BrowserResources.getStringArray(592), startupPage));
      if (!this.canEditUrlField() || this._config.getPropertyAsInt(7) != 0) {
         this._startupPageField.setEditable(false);
      }

      String transportCID = this._config.getPropertyAsString(3);
      this._transportServiceCIDField = (EditField)(new Object(BrowserResources.getString(198), transportCID));
      if (!otherFieldsEditable) {
         this._transportServiceCIDField.setEditable(false);
      }

      this._transportServiceUIDField = (EditField)(new Object(BrowserResources.getString(199), this._config.getPropertyAsString(4)));
      if (!otherFieldsEditable) {
         this._transportServiceUIDField.setEditable(false);
      }

      this._timeoutField = (EditField)(new Object(BrowserResources.getString(202), String.valueOf(this._config.getPropertyAsInt(6))));
      this._timeoutField.setFilter(TextFilter.get(1));
      if (!otherFieldsEditable) {
         this._timeoutField.setEditable(false);
      }

      this._constrainedNavigationField = (ObjectChoiceField)(new Object(
         BrowserResources.getString(427), new String[]{"No", "Restricted", "Locked Down"}, this._config.getPropertyAsInt(7)
      ));
      if (!otherFieldsEditable) {
         this._constrainedNavigationField.setEditable(false);
      }

      this._constrainedContentModeField = (ObjectChoiceField)(new Object(
         BrowserResources.getString(443), BrowserResources.getStringArray(488), this._config.getPropertyAsInt(9) + 1
      ));
      if (!otherFieldsEditable) {
         this._constrainedContentModeField.setEditable(false);
      }

      this._httpsEncryptionField = (ObjectChoiceField)(new Object(
         BrowserResources.getString(666), BrowserResources.getStringArray(667), this._config.getPropertyAsInt(26)
      ));
      if (!otherFieldsEditable) {
         this._httpsEncryptionField.setEditable(false);
      }

      this._constrainedSavingField = (ObjectChoiceField)(new Object(
         "Constrained Saving:", new String[]{"None", "Images", "Sounds", "Images and Sounds"}, MathUtilities.clamp(0, this._config.getPropertyAsInt(27), 3)
      ));
      if (!otherFieldsEditable) {
         this._constrainedSavingField.setEditable(false);
      }

      this._provisionedBookmarksInteractionField = (ObjectChoiceField)(new Object(
         "Show Provisioned Bookmarks:",
         new String[]{"Always", "Only if no others", "Only in this browser"},
         MathUtilities.clamp(0, this._config.getPropertyAsInt(32), 2)
      ));
      if (!otherFieldsEditable) {
         this._provisionedBookmarksInteractionField.setEditable(false);
      }

      this._provisionedBookmarksExpandFolderField = (ObjectChoiceField)(new Object(
         "Expand Provisioned Bookmarks Folder:",
         new String[]{"Always", "Never", "Only in this browser"},
         MathUtilities.clamp(0, this._config.getPropertyAsInt(57), 2)
      ));
      if (!otherFieldsEditable) {
         this._provisionedBookmarksExpandFolderField.setEditable(false);
      }

      this._appDownloadModuleNameField = (EditField)(new Object("App Download module name:", this._config.getPropertyAsString(28)));
      if (!otherFieldsEditable) {
         this._appDownloadModuleNameField.setEditable(false);
      }

      this._appDownloadModuleVersionField = (EditField)(new Object("App Download module version:", this._config.getPropertyAsString(49)));
      if (!otherFieldsEditable) {
         this._appDownloadModuleVersionField.setEditable(false);
      }

      this._iconUrlField = (EditField)(new Object("Icon Url:", this._config.getPropertyAsString(29)));
      if (!otherFieldsEditable) {
         this._iconUrlField.setEditable(false);
      }

      this._ribbonIconTypeField = (EditField)(new Object("Home Screen Icon Type: ", this._config.getPropertyAsString(54)));
      if (!otherFieldsEditable) {
         this._ribbonIconTypeField.setEditable(false);
      }

      this._useSeparateIconField = (CheckboxField)(new Object("Use Separate Icon", this._config.getPropertyAsBoolean(55)));
      if (!otherFieldsEditable) {
         this._useSeparateIconField.setEditable(false);
      }

      this._domainsField = (EditField)(new Object("Domains: ", this._config.getPropertyAsString(56)));
      if (!otherFieldsEditable) {
         this._domainsField.setEditable(false);
      }

      this._rimBrandedField = (CheckboxField)(new Object("RIM Branded", this._config.getPropertyAsBoolean(51)));
      if (!otherFieldsEditable) {
         this._rimBrandedField.setEditable(false);
      }

      this._sendProfileDiffsField = (CheckboxField)(new Object("Send Profile Diffs", this._config.getPropertyAsBoolean(52)));
      if (!otherFieldsEditable) {
         this._sendProfileDiffsField.setEditable(false);
      }

      this._localizedStringsField = (EditField)(new Object("Localized strings: ", this._config.getPropertyAsString(41)));
      if (!otherFieldsEditable) {
         this._localizedStringsField.setEditable(false);
      }

      this._bookmarksFolderNameField = (EditField)(new Object("Bookmarks Folder Name: ", this._config.getPropertyAsString(42)));
      if (!otherFieldsEditable) {
         this._bookmarksFolderNameField.setEditable(false);
      }

      this._helpLinksField = (EditField)(new Object("Help Links: ", this._config.getPropertyAsString(44)));
      if (!otherFieldsEditable) {
         this._helpLinksField.setEditable(false);
      }

      this._helpGroupLabelField = (EditField)(new Object("Help Group Label: ", this._config.getPropertyAsString(45)));
      if (!otherFieldsEditable) {
         this._helpGroupLabelField.setEditable(false);
      }

      this._moreImagesUrlField = (EditField)(new Object("Download Images URL: ", this._config.getPropertyAsString(46), 1000000, 117440512));
      if (!otherFieldsEditable) {
         this._moreImagesUrlField.setEditable(false);
      }

      this._moreTunesUrlField = (EditField)(new Object("Download Tunes URL: ", this._config.getPropertyAsString(48), 1000000, 117440512));
      if (!otherFieldsEditable) {
         this._moreTunesUrlField.setEditable(false);
      }

      this._moreThemesUrlField = (EditField)(new Object("Download Themes URL: ", this._config.getPropertyAsString(47), 1000000, 117440512));
      if (!otherFieldsEditable) {
         this._moreThemesUrlField.setEditable(false);
      }

      boolean value = this._config.getPropertyAsBooleanWithOverride((byte)38);
      if (value != this._config.getPropertyAsBoolean(38)) {
         this._overriddenSet.set(5);
      }

      this._javascriptEnabledField = (CheckboxField)(new Object(BrowserResources.getString(621), value));
      value = this._config.getPropertyAsBooleanWithOverride((byte)39);
      if (value != this._config.getPropertyAsBoolean(39)) {
         this._overriddenSet.set(6);
      }

      this._allowPopupsEnabledField = (CheckboxField)(new Object(BrowserResources.getString(634), value));
      value = this._config.getPropertyAsBoolean(53);
      this._promptEnabledJavascriptField = (CheckboxField)(new Object(BrowserResources.getString(754), value));
      value = this._config.getPropertyAsBooleanWithOverride((byte)33);
      if (value != this._config.getPropertyAsBoolean(33)) {
         this._overriddenSet.set(9);
      }

      this._enableCssField = (CheckboxField)(new Object(BrowserResources.getString(673), value));
      value = this._config.getPropertyAsInt(58) != 0;
      this._javascriptWatchdogEnabledField = (CheckboxField)(new Object(BrowserResources.getString(902), value));
      value = this._config.getPropertyAsBooleanWithOverride((byte)35);
      if (value != this._config.getPropertyAsBoolean(35)) {
         this._overriddenSet.set(11);
      }

      this._foregroundBackgroundColorField = (CheckboxField)(new Object(BrowserResources.getString(659), value));
      value = this._config.getPropertyAsBooleanWithOverride((byte)40);
      if (value != this._config.getPropertyAsBoolean(40)) {
         this._overriddenSet.set(7);
      }

      this._backgroundImagesField = (CheckboxField)(new Object(BrowserResources.getString(660), value));
      value = this._config.getPropertyAsBooleanWithOverride((byte)36);
      if (value != this._config.getPropertyAsBoolean(36)) {
         this._overriddenSet.set(8);
      }

      this._useHtmlTablesField = (CheckboxField)(new Object(BrowserResources.getString(668), value));
      value = this._config.getPropertyAsBooleanWithOverride((byte)37);
      if (value != this._config.getPropertyAsBoolean(37)) {
         this._overriddenSet.set(10);
      }

      this._enableEmbeddedRichContentField = (CheckboxField)(new Object(BrowserResources.getString(679), value));
      value = this._config.getPropertyAsBooleanWithOverride((byte)43);
      if (value != this._config.getPropertyAsBoolean(43)) {
         this._overriddenSet.set(12);
      }

      this._enableBSMField = (CheckboxField)(new Object(BrowserResources.getString(697), value));
      this._uaprofURIField = (EditField)(new Object("UAProf URI: ", this._config.getPropertyAsString(8)));
      if (!otherFieldsEditable) {
         this._uaprofURIField.setEditable(false);
      }

      this._interactivePingPacketTimer = (NumericChoiceField)(new Object("Interactive Ping Timer", 0, 600000, 1000));
      this._interactivePingPacketTimer.setSelectedValue(IPPPTransportBase.getTransportInteractivePingPacketTimeout());
      ServiceRecord sr = BrowserConfigRecord.getTransportServiceRecord(this._config.getPropertyAsString(3), this._config.getPropertyAsString(4));
      this._hrt = sr != null ? sr.getAttachedHrt() : null;
      if (JavaScriptRegistry.isInstalled() && !ITPolicy.getBoolean(30, 2, false)) {
         screen.add(this._javascriptEnabledField);
         this._javascriptEnabledField.setChangeListener(this);
         if (this._javascriptEnabledField.getChecked()) {
            screen.add(this._allowPopupsEnabledField);
            screen.add(this._javascriptWatchdogEnabledField);
         } else {
            screen.add(this._promptEnabledJavascriptField);
         }
      }

      screen.add(this._useHtmlTablesField);
      if (Graphics.isColor()) {
         screen.add(this._foregroundBackgroundColorField);
         screen.add(this._backgroundImagesField);
         screen.add(this._enableEmbeddedRichContentField);
      }

      if (mdsConfig && !super._restrictedAccess) {
         screen.add(this._enableBSMField);
      }

      screen.add(this._enableCssField);
      screen.add(this._showImagesField);
      this._showImagesField.setChangeListener(this);
      if (this._showImagesField.getSelectedIndex() != 2) {
         screen.add(this._showImagePlaceholdersField);
      }

      screen.add(this._defaultEmulationModeField);
      screen.add(this._defaultContentModeField);
      if (this._startupPageField.isEditable()) {
         screen.add(this._startupPageField);
      }

      screen.add((Field)(new Object(fontHeight >> 1)));
      screen.add(this._homePageUrlField);
      if (this._useCurrentAsHomePageField != null) {
         screen.add(this._useCurrentAsHomePageField);
      }

      screen.add((Field)(new Object()));
      if (!super._restrictedAccess) {
         screen.add((Field)(new Object(fontHeight >> 1)));
         screen.add(this._configValuesEditableField);
         screen.add(this._ribbonTitleField);
         screen.add(this._ribbonPositionField);
         screen.add(this._configTypeField);
         screen.add(this._ribbonIconTypeField);
         screen.add(this._useSeparateIconField);
         screen.add(this._rimBrandedField);
         screen.add(this._sendProfileDiffsField);
         screen.add(this._autoStartField);
         screen.add(this._sendPhoneNumberField);
         screen.add(this._brandingIconField);
         screen.add(this._vendorIdField);
         screen.add(this._transportServiceCIDField);
         screen.add(this._transportServiceUIDField);
         if (!"WPTCP".equalsIgnoreCase(transportCID)) {
            screen.add(this._timeoutField);
         }

         screen.add(this._constrainedNavigationField);
         screen.add(this._constrainedContentModeField);
         screen.add(this._appDownloadModuleNameField);
         screen.add(this._appDownloadModuleVersionField);
         screen.add(this._iconUrlField);
         if (!super._restrictedAccess && mdsConfig) {
            screen.add(this._httpsEncryptionField);
         }

         screen.add(this._constrainedSavingField);
         screen.add(this._domainsField);
         screen.add(this._bookmarksFolderNameField);
         screen.add(this._localizedStringsField);
         screen.add(this._helpLinksField);
         screen.add(this._helpGroupLabelField);
         screen.add(this._moreImagesUrlField);
         screen.add(this._moreTunesUrlField);
         screen.add(this._moreThemesUrlField);
         screen.add(this._uaprofURIField);
         screen.add(this._provisionedBookmarksInteractionField);
         screen.add(this._provisionedBookmarksExpandFolderField);
         screen.add(this._interactivePingPacketTimer);
         screen.add((Field)(new Object()));
      }

      this._transportConfigProperty = TransportConfigProperty.getInstance(
         super._restrictedAccess, this._config.getPropertyAsString(3), this._config.getPropertyAsString(4)
      );
      if (this._transportConfigProperty != null) {
         this._transportConfigProperty
            .addFields(
               screen,
               otherFieldsEditable,
               this._config.getPropertyAsString(3),
               this._config.getPropertyAsString(4),
               this._config.getPropertyAsInt(13),
               this._config.getPropertyAsInt(15),
               this._config.getPropertyAsString(16)
            );
      }

      return screen;
   }

   private final boolean canEditUrlField() {
      int cve = this._config.getPropertyAsIntWithOverride((byte)5);
      return cve == 1 || cve == 2 || cve == 3 || !super._restrictedAccess && (cve == 0 || cve == 4);
   }

   private final boolean canEditOtherFields() {
      int cve = this._config.getPropertyAsIntWithOverride((byte)5);
      return cve == 1 || !super._restrictedAccess && (cve == 0 || cve == 2);
   }

   @Override
   public final Verb getVerbs(Verb[] verbs) {
      int items = 0;
      if (this._config == null) {
         Array.resize(verbs, items);
         return null;
      }

      Array.resize(verbs, items + 1);
      verbs[items++] = new SavePropertyVerb(this);
      if (this._overriddenSet.getNumSet() > 0) {
         Array.resize(verbs, items + 1);
         verbs[items++] = new BrowserConfigProperty$RestoreDefaultsVerb(this, this._config);
      }

      if (!super._restrictedAccess && this._hrt != null) {
         Array.resize(verbs, items + 1);
         verbs[items++] = new EditHRTVerb(this._hrt);
      }

      return verbs[0];
   }

   public final void itPolicyChanged() {
      this._itPolicyChanged = true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void saveProperty() {
      String currentConfigServiceUID = this._config.getUid();
      if (this._itPolicyChanged) {
         this._itPolicyChanged = false;
         Status.show(BrowserResources.getString(699));
      } else {
         String homePageUrl = this._homePageUrlField.getText().trim();
         int newlineIndex = homePageUrl.indexOf(10);
         if (newlineIndex != -1) {
            homePageUrl = homePageUrl.substring(0, newlineIndex);
         }

         if (homePageUrl.length() != 0) {
            if (homePageUrl.equals(this._config.getPropertyAsString(1))) {
               GeneralProperty.removeHomePageUrlOverrideValue(currentConfigServiceUID);
            } else {
               GeneralProperty.setHomePageUrlOverrideValue(currentConfigServiceUID, homePageUrl);
            }
         }

         int showImages = this._showImagesField.getSelectedIndex();
         this._config.setIntPropertyWithOverride(showImages, 18, 16, currentConfigServiceUID);
         int showImagePlaceholders = this._showImagePlaceholdersField.getSelectedIndex();
         this._config.setIntPropertyWithOverride(showImagePlaceholders, 19, 17, currentConfigServiceUID);
         this._config.setIntPropertyWithOverride(this._startupPageField.getSelectedIndex(), 2, 41, currentConfigServiceUID);
         String newValue = this._transportServiceCIDField.getText();
         newlineIndex = newValue.indexOf(10);
         if (newlineIndex != -1) {
            newValue = newValue.substring(0, newlineIndex);
         }

         this._config.setPropertyAsString(3, newValue);
         newValue = this._transportServiceUIDField.getText();
         newlineIndex = newValue.indexOf(10);
         if (newlineIndex != -1) {
            newValue = newValue.substring(0, newlineIndex);
         }

         this._config.setPropertyAsString(4, newValue);
         int newTime = 300;

         label331:
         try {
            newTime = Integer.parseInt(this._timeoutField.getText());
         } finally {
            break label331;
         }

         this._config.setPropertyAsInt(6, newTime);
         int configValuesEditable = this._configValuesEditableField.getSelectedIndex();
         this._config.setIntPropertyWithOverride(configValuesEditable, 5, 39, currentConfigServiceUID);
         this._config.setPropertyAsInt(7, this._constrainedNavigationField.getSelectedIndex());
         if (this._config.getPropertyAsInt(7) != 0 && this._config.getPropertyAsInt(2) == 0) {
            this._config.setPropertyAsInt(2, 1);
         }

         this._config.setPropertyAsInt(9, this._constrainedContentModeField.getSelectedIndex() - 1);
         this._config.setPropertyAsString(11, this._ribbonTitleField.getText());
         int newPos = 0;

         label325:
         try {
            newPos = Integer.parseInt(this._ribbonPositionField.getText());
         } finally {
            break label325;
         }

         this._config.setPropertyAsInt(25, newPos);
         int contentMode;
         if (this._config.getPropertyAsInt(9) == -1) {
            contentMode = this._defaultContentModeField.getSelectedIndex();
         } else {
            contentMode = this._config.getPropertyAsInt(9);
         }

         this._config.setIntPropertyWithOverride(contentMode, 10, 18, currentConfigServiceUID);
         int emulationMode = this._defaultEmulationModeField.getSelectedIndex() + 1;
         this._config.setIntPropertyWithOverride(emulationMode, 17, 19, currentConfigServiceUID);
         this._config.setPropertyAsInt(12, this._configTypeField.getSelectedIndex());
         this._config.setPropertyAsInt(20, this._autoStartField.getSelectedIndex());
         this._config.setPropertyAsInt(23, this._sendPhoneNumberField.getSelectedIndex());
         int brandingIcon = -1;

         label321:
         try {
            brandingIcon = Integer.parseInt(this._brandingIconField.getText());
         } finally {
            break label321;
         }

         this._config.setPropertyAsInt(21, brandingIcon);
         String vendorId = this._vendorIdField.getText();
         this._config.setPropertyAsString(22, vendorId.length() > 0 ? vendorId : BrowserConfigRecord.VENDOR_ID_NONE);
         this._config.setPropertyAsInt(26, this._httpsEncryptionField.getSelectedIndex());
         String localizedStrings = this._localizedStringsField.getText().trim();
         this._config.setPropertyAsString(41, localizedStrings.length() > 0 ? localizedStrings : null);
         String bookmarksFolderName = this._bookmarksFolderNameField.getText().trim();
         this._config.setPropertyAsString(42, bookmarksFolderName.length() > 0 ? bookmarksFolderName : null);
         String helpLinks = this._helpLinksField.getText().trim();
         this._config.setPropertyAsString(44, helpLinks.length() > 0 ? helpLinks : null);
         String helpGroupLabel = this._helpGroupLabelField.getText().trim();
         this._config.setPropertyAsString(45, helpGroupLabel.length() > 0 ? helpGroupLabel : null);
         String moreImagesUrl = this._moreImagesUrlField.getText().trim();
         this._config.setPropertyAsString(46, moreImagesUrl);
         String moreThemesUrl = this._moreThemesUrlField.getText().trim();
         this._config.setPropertyAsString(47, moreThemesUrl);
         String moreTunesUrl = this._moreTunesUrlField.getText().trim();
         this._config.setPropertyAsString(48, moreTunesUrl);
         String uaProfURI = this._uaprofURIField.getText().trim();
         this._config.setPropertyAsString(8, uaProfURI.length() > 0 ? uaProfURI : null);
         this._config.setPropertyAsInt(27, this._constrainedSavingField.getSelectedIndex());
         this._config.setPropertyAsInt(32, this._provisionedBookmarksInteractionField.getSelectedIndex());
         this._config.setPropertyAsInt(57, this._provisionedBookmarksExpandFolderField.getSelectedIndex());
         this._config.setPropertyAsString(29, this._iconUrlField.getText());
         this._config.setPropertyAsString(28, this._appDownloadModuleNameField.getText());
         this._config.setPropertyAsString(49, this._appDownloadModuleVersionField.getText());
         this._config.setPropertyAsString(54, this._ribbonIconTypeField.getText());
         this._config.setPropertyAsBoolean(55, this._useSeparateIconField.getChecked());
         this._config.setPropertyAsString(56, this._domainsField.getText());
         this._config.setPropertyAsBoolean(51, this._rimBrandedField.getChecked());
         this._config.setPropertyAsBoolean(52, this._sendProfileDiffsField.getChecked());
         this._config.setPropertyAsString(24, this._config.getPropertyAsString(24));
         this._config.setPropertyAsInt(13, 0);
         this._config.setPropertyAsInt(15, -1);
         this._config.setPropertyAsString(16, "");
         this._config.setPropertyAsBoolean(53, this._promptEnabledJavascriptField.getChecked());
         this._config.setBooleanPropertyWithOverride(this._backgroundImagesField.getChecked(), 40, 7, currentConfigServiceUID);
         this._config.setBooleanPropertyWithOverride(this._useHtmlTablesField.getChecked(), 36, 4, currentConfigServiceUID);
         this._config.setBooleanPropertyWithOverride(this._enableCssField.getChecked(), 33, 6, currentConfigServiceUID);
         this._config.setBooleanPropertyWithOverride(this._enableEmbeddedRichContentField.getChecked(), 37, 25, currentConfigServiceUID);
         this._config.setBooleanPropertyWithOverride(this._foregroundBackgroundColorField.getChecked(), 35, 8, currentConfigServiceUID);
         this._config.setBooleanPropertyWithOverride(this._allowPopupsEnabledField.getChecked(), 39, 22, currentConfigServiceUID);
         this._config.setPropertyAsInt(58, this._javascriptWatchdogEnabledField.getChecked() ? 5000 : 0);
         this._config.setBooleanPropertyWithOverride(this._javascriptEnabledField.getChecked(), 38, 21, currentConfigServiceUID);
         this._config.setBooleanPropertyWithOverride(this._enableBSMField.getChecked(), 43, 38, currentConfigServiceUID);
         byte[] data = null;
         boolean var29 = false /* VF: Semaphore variable */;

         try {
            var29 = true;
            data = this._config.getEncodedData();
            var29 = false;
         } finally {
            if (var29) {
               return;
            }
         }

         ServiceBook sb = ServiceBook.getSB();
         ServiceRecord rec = sb.getRecordByUidAndCid(currentConfigServiceUID, BrowserConfigRecord.SERVICE_CID);
         if (rec == null) {
            Status.show(BrowserResources.getString(201));
         } else {
            if (this._transportConfigProperty != null) {
               this._transportConfigProperty.saveProperty(this._config.getPropertyAsString(3), this._config.getPropertyAsString(4));
            }

            rec.setApplicationData(data);
            sb.commit();
            if (this._interactivePingPacketTimer.isDirty()) {
               IPPPTransportBase.setInteractivePingPacketTimeoutOverride(this._interactivePingPacketTimer.getSelectedValue());
            }

            BrowserDaemonRegistry.notifyBrowserConfigChangeListeners(true);
         }
      }
   }
}
