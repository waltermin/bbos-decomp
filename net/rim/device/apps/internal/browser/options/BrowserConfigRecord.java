package net.rim.device.apps.internal.browser.options;

import java.io.ByteArrayInputStream;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.i18n.HashResourceBundle;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.messaging.util.SortedCollection;
import net.rim.device.apps.internal.browser.bookmark.BookmarksFolderList;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.cldc.io.waphttp.WAPConnectionRegistry;
import net.rim.device.internal.browser.wap.WAPServiceRecord;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;
import net.rim.vm.Memory;

public final class BrowserConfigRecord {
   private String _uid;
   private String _homePageUrl;
   private int _startupPageValue;
   private String _transportServiceCID;
   private String _transportServiceUID;
   private int _configValuesEditable;
   private int _timeout;
   private int _constrainedNavigation;
   private String _uaProfUri;
   private int _constrainedContentMode;
   private int _defaultContentMode;
   private int _defaultEmulationMode;
   private String _ribbonTitle;
   private int _configType;
   private int _wtlsMode;
   String _icon;
   private int _wtlsClientIdType;
   private String _wtlsClientIdValue;
   private int _showImagesValue;
   private int _showImagePlaceholdersValue;
   private int _autoStart;
   private int _brandingIcon;
   private String _vendorId;
   private int _sendPhoneNumberValue;
   private String[] _bookmarks;
   private String _provisionedBookmarksFolderName;
   private int _ribbonPosition;
   private int _httpsEncryptionMode;
   private int _constrainedSaving;
   private String _appDownloadModuleName;
   private String _appDownloadModuleVersion;
   private String _iconUrl;
   private int _hiddenFromRibbon;
   private String _browserKeyUrl;
   private int _provisionedBookmarksInteraction;
   private boolean _cssEnabled;
   private boolean _useForegroundBackgroundColors;
   private boolean _useBackgroundImages;
   private boolean _htmlTablesEnabled;
   private boolean _useEmbeddedMedia;
   private boolean _javaScriptEnabled;
   private boolean _javaScriptPopupsAllowed;
   private String _localizedStrings;
   private String _bookmarksFolderName;
   private boolean _bsmEnabled;
   private String _helpLinks;
   private String _helpGroupLabel;
   private String _moreImagesUrl;
   private String _moreTunesUrl;
   private String _moreThemesUrl;
   private boolean _rimBranded;
   private boolean _sendProfileDiffs;
   private boolean _promptEnableJavascript;
   private String _ribbonIconType;
   private boolean _useSeparateIcon;
   private String _domains;
   private int _provisionedBookmarksExpandFolder;
   private int _javascriptWatchdogTimeout;
   public static final byte ENCODED_TYPE_HOME_PAGE_URL;
   public static final byte ENCODED_TYPE_STARTUP_PAGE_VALUE;
   public static final byte ENCODED_TYPE_TRANSPORT_SERVICE_CID;
   public static final byte ENCODED_TYPE_TRANSPORT_SERVICE_UID;
   public static final byte ENCODED_TYPE_CONFIG_VALUES_EDITABLE;
   public static final byte ENCODED_TYPE_TIMEOUT;
   public static final byte ENCODED_TYPE_CONSTRAINED_NAVIGATION;
   public static final byte ENCODED_TYPE_UAPROF_URI;
   public static final byte ENCODED_TYPE_CONSTRAINED_CONTENT_MODE;
   public static final byte ENCODED_TYPE_DEFAULT_CONTENT_MODE;
   public static final byte ENCODED_TYPE_RIBBON_TITLE;
   public static final byte ENCODED_TYPE_CONFIG_TYPE;
   public static final byte ENCODED_TYPE_WTLS_MODE;
   public static final byte ENCODED_TYPE_ICON;
   public static final byte ENCODED_TYPE_WTLS_CLIENT_ID_TYPE;
   public static final byte ENCODED_TYPE_WTLS_CLIENT_ID_VALUE;
   public static final byte ENCODED_TYPE_DEFAULT_EMULATION_MODE;
   public static final byte ENCODED_TYPE_SHOW_IMAGES_VALUE;
   public static final byte ENCODED_TYPE_SHOW_IMAGE_PLACEHOLDERS_VALUE;
   public static final byte ENCODED_TYPE_AUTO_START_VALUE;
   public static final byte ENCODED_TYPE_BRANDING_ICON_VALUE;
   public static final byte ENCODED_TYPE_VENDOR_ID_VALUE;
   public static final byte ENCODED_TYPE_SEND_PHONE_NUMBER_VALUE;
   public static final byte ENCODED_TYPE_PROVISIONED_BOOKMARKS_FOLDER_NAME;
   public static final byte ENCODED_TYPE_RIBBON_POSITION;
   public static final byte ENCODED_TYPE_HTTPS_ENCRYPTION;
   public static final byte ENCODED_TYPE_CONSTRAINED_SAVING;
   public static final byte ENCODED_TYPE_APP_DOWNLOAD_MODULE_NAME;
   public static final byte ENCODED_TYPE_ICON_URL;
   public static final byte ENCODED_TYPE_HIDDEN_FROM_RIBBON;
   public static final byte ENCODED_TYPE_BROWSER_KEY_URL;
   public static final byte ENCODED_TYPE_PROVISIONED_BOOKMARKS_INTERACTION;
   public static final byte ENCODED_TYPE_CSS_ENABLED;
   private static final byte ENCODED_TYPE_CSS_MEDIA_TYPE;
   public static final byte ENCODED_TYPE_USE_FOREGROUND_BACKGROUND_COLORS;
   public static final byte ENCODED_TYPE_HTML_TABLES_ENABLED;
   public static final byte ENCODED_TYPE_EMBEDDED_MEDIA_ENABLED;
   public static final byte ENCODED_TYPE_JAVASCRIPT_ENABLED;
   public static final byte ENCODED_TYPE_ALLOW_JAVASCRIPT_POPUPS;
   public static final byte ENCODED_TYPE_USE_BACKGROUND_IMAGES;
   public static final byte ENCODED_TYPE_LOCALIZED_STRINGS;
   public static final byte ENCODED_TYPE_BOOKMARKS_FOLDER_NAME;
   public static final byte ENCODED_TYPE_BSM_ENABLED;
   public static final byte ENCODED_TYPE_HELP_LINKS;
   public static final byte ENCODED_TYPE_HELP_GROUP_LABEL;
   public static final byte ENCODED_TYPE_MORE_IMAGES_URL;
   public static final byte ENCODED_TYPE_MORE_THEMES_URL;
   public static final byte ENCODED_TYPE_MORE_TUNES_URL;
   public static final byte ENCODED_TYPE_APP_DOWNLOAD_MODULE_VERSION;
   public static final byte ENCODED_TYPE_APP_DOWNLOAD_UPGRADE;
   public static final byte ENCODED_TYPE_RIM_BRANDED;
   public static final byte ENCODED_TYPE_SEND_PROFILE_DIFFS;
   public static final byte ENCODED_TYPE_PROMPT_ENABLE_JAVASCRIPT;
   public static final byte ENCODED_TYPE_RIBBON_ICON_TYPE;
   public static final byte ENCODED_TYPE_USE_SEPARATE_ICON;
   public static final byte ENCODED_TYPE_DOMAINS;
   public static final byte ENCODED_TYPE_PROVISIONED_BOOKMARKS_EXPAND_FOLDER;
   public static final byte ENCODED_TYPE_JAVASCRIPT_WATCHDOG_TIMEOUT;
   public static final byte ENCODED_TYPE_BOOKMARK_LOW;
   public static final byte ENCODED_TYPE_BOOKMARK;
   public static final byte ENCODED_TYPE_BOOKMARK_HIGH;
   public static final int TIMEOUT_NONE;
   public static final int TIMEOUT_DEFAULT;
   public static final int BOOLEAN_FALSE_VALUE;
   public static final int BOOLEAN_TRUE_VALUE;
   public static final int STARTUP_BOOKMARKS_PAGE;
   public static final int STARTUP_HOME_PAGE;
   public static final int STARTUP_LAST_PAGE;
   public static final int STARTUP_STARTUP_PAGE;
   public static final int STARTUP_PAGE_VALUE_DEFAULT;
   public static String IPPP_SERVICE_CID = "IPPP";
   public static String TCP_SERVICE_CID = "WPTCP";
   public static String WAP_SERVICE_CID = "WAP";
   public static final int CONFIG_VALUES_EDITABLE_BD_BD;
   public static final int CONFIG_VALUES_EDITABLE_ED_ED;
   public static final int CONFIG_VALUES_EDITABLE_ED_BD;
   public static final int CONFIG_VALUES_EDITABLE_ED_NA;
   public static final int CONFIG_VALUES_EDITABLE_BD_NA;
   public static final int CONFIG_VALUES_EDITABLE_NA_NA;
   public static final int CONFIG_VALUES_EDITABLE_DEFAULT;
   public static final int CONSTRAINED_CONTENT_MODE_UNCONSTRAINED;
   public static final int CONSTRAINED_CONTENT_MODE_WML_ONLY;
   public static int CONSTRAINED_CONTENT_MODE_DEFAULT = -1;
   public static final int CONTENT_MODE_WML_ONLY;
   public static final int CONTENT_MODE_WML_AND_HTML;
   public static final int CONTENT_MODE_HTML_ONLY;
   public static final int CONTENT_MODE_DEFAULT;
   public static final int CONFIG_TYPE_WAP;
   public static final int CONFIG_TYPE_MDS_PRIVATE;
   public static final int CONFIG_TYPE_BWC;
   public static final int CONFIG_TYPE_DEVICE_WPTCP;
   public static final int CONFIG_TYPE_MDS_PUBLIC;
   public static final int CONFIG_TYPE_TEAMON;
   public static final int CONFIG_TYPE_APP_DOWNLOAD;
   public static final int CONFIG_TYPE_WAP_WPTCP;
   public static final int CONFIG_TYPE_MDS_PUBLIC_WPTCP;
   public static final int DEFAULT_EMULATION_MODE_RIM;
   public static final int DEFAULT_EMULATION_MODE_OPENWAVE;
   public static final int DEFAULT_EMULATION_MODE_OPENWAVE_GATEWAY;
   public static final int DEFAULT_EMULATION_MODE_IE;
   public static final int DEFAULT_EMULATION_MODE_POCKET_IE;
   public static final int DEFAULT_EMULATION_MODE_NETSCAPE;
   public static final int DEFAULT_EMULATION_MODE_DEFAULT;
   public static final int SHOW_IMAGES_NONE;
   public static final int SHOW_IMAGES_WML_ONLY;
   public static final int SHOW_IMAGES_WML_AND_HTML;
   public static final int SHOW_IMAGE_PLACEHOLDERS_DEFAULT;
   public static final int SEND_PHONE_NUMBER_NEVER;
   public static final int SEND_PHONE_NUMBER_OTA_REPORTS_ONLY;
   public static final int SEND_PHONE_NUMBER_DEFAULT;
   public static final int RIBBON_POSITION_DEFAULT;
   public static final int HTTPS_ENCRYPTION_REQUIRED_END_TO_END;
   public static final int HTTPS_ENCRYPTION_DESIRED_ALLOW_GAP_END_TO_END;
   public static final int HTTPS_ENCRYPTION_REQUIRED_ALLOW_GAP;
   public static final int HTTPS_ENCRYPTION_NOT_REQUIRED;
   public static final int CONSTRAINED_SAVING_NONE;
   public static final int CONSTRAINED_SAVING_IMAGES;
   public static final int CONSTRAINED_SAVING_AUDIO;
   public static final int PROVISIONED_BOOKMARKS_ALWAYS_SHOW;
   public static final int PROVISIONED_BOOKMARKS_SHOW_IF_NO_OTHERS;
   public static final int PROVISIONED_BOOKMARKS_SHOW_ONLY_IN_THIS_BROWSER;
   public static final int PROVISIONED_BOOKMARKS_INTERACTION_DEFAULT;
   public static final int CSS_MEDIA_TYPE_HANDHELD;
   public static final int CSS_MEDIA_TYPE_SCREEN;
   public static final int CSS_MEDIA_TYPE_DEFAULT;
   public static final int CONSTRAINED_NAVIGATION_NONE;
   public static final int CONSTRAINED_NAVIGATION_RESTRICTED;
   public static final int CONSTRAINED_NAVIGATION_LOCKED_DOWN;
   public static final int CONSTRAINED_NAVIGATION_DEFAULT;
   public static final int PROVISIONED_BOOKMARKS_EXPAND_FOLDER_ALWAYS;
   public static final int PROVISIONED_BOOKMARKS_EXPAND_FOLDER_NEVER;
   public static final int PROVISIONED_BOOKMARKS_EXPAND_FOLDER_ONLY_IN_THIS_BROWSER;
   public static final int PROVISIONED_BOOKMARKS_EXPAND_FOLDER_DEFAULT;
   public static final int JAVASCRIPT_WATCHDOG_TIMEOUT_DEFAULT;
   private static final byte VERSION;
   public static String SERVICE_CID = "BrowserConfig";
   private static final String APP_DOWNLOAD_PREFIX;
   public static int INVALID_VALUE = -1;
   public static final String DEFAULT_BLACKBERRY_BROWSER_HOME_PAGE;
   public static final String DEFAULT_BLACKBERRY_BROWSER_RIBBON_TITLE;
   public static final String BLACKBERRY_BROWSER_CONFIG_DESCRIPTION;
   private static final boolean JAVASCRIPT_ENABLED_DEFAULT;
   private static final boolean JAVASCRIPT_POPUPS_ALLOWED_DEFAULT;
   private static final boolean EMBEDDED_MEDIA_ENABLED_DEFAULT;
   private static final boolean FOREGROUND_BACKGROUND_COLORS_DEFAULT = Graphics.isColor();
   public static final int SHOW_IMAGES_DEFAULT = Graphics.isColor() ? 2 : 1;
   private static final char BOOKMARK_SEPARATOR;
   public static final int BRANDING_ICON_NONE;
   public static final String VENDOR_ID_NONE = null;
   private static final long BUNDLE_ID;
   public static final String BUNDLE_NAME;
   private static Object _bundleSyncObject = new Object();
   private static final int STATE_LOCALE_OR_TYPECODE;
   private static final int STATE_LOCALE;
   private static final int STATE_TYPECODE;
   private static final int STATE_TRANSLATION;

   private BrowserConfigRecord() {
      this._configType = INVALID_VALUE;
      this.reset();
   }

   private BrowserConfigRecord(String transportCid, String transportUid, int type) {
      this._configType = type;
      this.reset();
      this._transportServiceCID = transportCid;
      this._transportServiceUID = transportUid;
      this._homePageUrl = "http://mobile.blackberry.com/";
   }

   private final void reset() {
      this._uid = null;
      this._cssEnabled = getDefaultPropertyAsBoolean(this._configType, 33);
      this._useBackgroundImages = getDefaultPropertyAsBoolean(this._configType, 40);
      this._htmlTablesEnabled = getDefaultPropertyAsBoolean(this._configType, 36);
      this._bsmEnabled = getDefaultPropertyAsBoolean(this._configType, 43);
      this._startupPageValue = 3;
      this._timeout = 300;
      this._ribbonTitle = "";
      this._ribbonPosition = 0;
      this._configValuesEditable = 1;
      this._constrainedNavigation = 0;
      this._constrainedContentMode = CONSTRAINED_CONTENT_MODE_DEFAULT;
      this._defaultContentMode = 1;
      this._defaultEmulationMode = 1;
      this._icon = null;
      this._showImagesValue = SHOW_IMAGES_DEFAULT;
      this._showImagePlaceholdersValue = 0;
      this._sendPhoneNumberValue = 0;
      this._autoStart = 0;
      this._brandingIcon = -1;
      this._vendorId = VENDOR_ID_NONE;
      this._constrainedSaving = 0;
      this._hiddenFromRibbon = 0;
      this._provisionedBookmarksInteraction = 0;
      this._provisionedBookmarksExpandFolder = 0;
      this._useForegroundBackgroundColors = FOREGROUND_BACKGROUND_COLORS_DEFAULT;
      this._useEmbeddedMedia = false;
      this._javaScriptEnabled = false;
      this._javaScriptPopupsAllowed = false;
      this._sendProfileDiffs = false;
      this._uaProfUri = null;
      this._homePageUrl = null;
      this._transportServiceCID = null;
      this._transportServiceUID = null;
      this._appDownloadModuleName = null;
      this._appDownloadModuleVersion = null;
      this._iconUrl = null;
      this._browserKeyUrl = null;
      this._localizedStrings = null;
      this._bookmarksFolderName = null;
      this._helpLinks = null;
      this._helpGroupLabel = null;
      this._moreImagesUrl = null;
      this._moreTunesUrl = null;
      this._moreThemesUrl = null;
      this._bookmarks = null;
      this._provisionedBookmarksFolderName = null;
      this._rimBranded = false;
      this._promptEnableJavascript = true;
      this._ribbonIconType = null;
      this._useSeparateIcon = getDefaultPropertyAsBoolean(this._configType, 55);
      this._domains = null;
      this._javascriptWatchdogTimeout = 5000;
      if (this._configType == 2) {
         this._httpsEncryptionMode = 0;
      } else if (this._configType == 4) {
         this._httpsEncryptionMode = 1;
      } else {
         this._httpsEncryptionMode = 2;
      }

      this._wtlsMode = 0;
      this._wtlsClientIdType = -1;
      this._wtlsClientIdValue = null;
   }

   public final void commitChangesToServiceBook() {
      ServiceBook serviceBook = ServiceBook.getSB();
      ServiceRecord serviceRecord = serviceBook.getRecordByUidAndCid(this._uid, SERVICE_CID);
      if (serviceRecord != null) {
         try {
            serviceRecord.setApplicationData(this.getEncodedData());
            serviceBook.commit();
            BrowserDaemonRegistry.notifyBrowserConfigChangeListeners(true);
         } finally {
            return;
         }
      }
   }

   public static final BrowserConfigRecord getDecodedConfig(ServiceRecord sr) {
      if (sr != null && StringUtilities.strEqualIgnoreCase(sr.getCid(), SERVICE_CID, 1701707776)) {
         try {
            return getDecodedConfig(sr.getUid(), sr.getApplicationData());
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   public static final boolean getDefaultPropertyAsBoolean(int configType, int property) {
      boolean isMDS = configType == 1 || configType == 4 || configType == 8;
      switch (property) {
         case 33:
         case 35:
         case 36:
            return true;
         case 37:
            return false;
         case 38:
            return false;
         case 39:
            return false;
         case 40:
            if (!isMDS) {
               return true;
            }

            return false;
         case 43:
            return isMDS;
         case 55:
            if (configType != 2 && configType != 6) {
               return false;
            }

            return true;
         default:
            throw new Object();
      }
   }

   public static final BrowserConfigRecord getDecodedConfig(String uid, byte[] data) {
      BrowserConfigRecord browserConfig = new BrowserConfigRecord();
      browserConfig._uid = uid;
      boolean cssEnabledSet = false;
      boolean htmlTablesEnabledSet = false;
      boolean useBackgroundImagesSet = false;
      boolean bsmEnabledSet = false;
      boolean useSeparateIconSet = false;
      if (data != null && data.length != 0) {
         DataBuffer _tmpDataBuffer = (DataBuffer)(new Object(data, 0, data.length, true));
         SyncBuffer _tmpSyncBuffer = (SyncBuffer)(new Object(_tmpDataBuffer, 0, 0));
         _tmpDataBuffer.readByte();

         while (!_tmpSyncBuffer.isEmpty()) {
            int fieldType = _tmpSyncBuffer.getFieldType(true);
            switch (fieldType) {
               case 0:
               case 34:
               case 50:
                  if (fieldType >= 60 && fieldType <= 99) {
                     String encodedBookmark = _tmpSyncBuffer.getString();
                     if (encodedBookmark != null && encodedBookmark.length() > 2) {
                        if (browserConfig._bookmarks == null) {
                           browserConfig._bookmarks = new Object[40];
                        }

                        browserConfig._bookmarks[fieldType - 60] = encodedBookmark;
                     }
                     break;
                  }

                  _tmpSyncBuffer.skipField();
                  break;
               case 1:
               default:
                  browserConfig._homePageUrl = _tmpSyncBuffer.getString();
                  if (browserConfig._homePageUrl == null) {
                     browserConfig._homePageUrl = "";
                  }
                  break;
               case 2:
                  browserConfig._startupPageValue = _tmpSyncBuffer.getInt();
                  if (browserConfig._startupPageValue > 0 && browserConfig._startupPageValue <= 3) {
                     break;
                  }

                  browserConfig._startupPageValue = 3;
                  if (browserConfig._startupPageValue != 0) {
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 3:
                  browserConfig._transportServiceCID = _tmpSyncBuffer.getString();
                  if (browserConfig._transportServiceCID != null) {
                     browserConfig._transportServiceCID = Memory.stringIntern(browserConfig._transportServiceCID);
                  }
                  break;
               case 4:
                  browserConfig._transportServiceUID = _tmpSyncBuffer.getString();
                  if (browserConfig._transportServiceUID != null) {
                     browserConfig._transportServiceUID = Memory.stringIntern(browserConfig._transportServiceUID);
                  }
                  break;
               case 5:
                  browserConfig._configValuesEditable = _tmpSyncBuffer.getInt();
                  if (browserConfig._configValuesEditable < 0 || browserConfig._configValuesEditable > 5) {
                     browserConfig._configValuesEditable = 1;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 6:
                  browserConfig._timeout = _tmpSyncBuffer.getInt();
                  if (browserConfig._timeout < 0) {
                     browserConfig._timeout = 300;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 7:
                  browserConfig._constrainedNavigation = _tmpSyncBuffer.getInt();
                  if (browserConfig._constrainedNavigation < 0 || browserConfig._constrainedNavigation > 2) {
                     browserConfig._constrainedNavigation = 0;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 8:
                  browserConfig._uaProfUri = _tmpSyncBuffer.getString();
                  break;
               case 9:
                  browserConfig._constrainedContentMode = _tmpSyncBuffer.getInt();
                  if (browserConfig._constrainedContentMode < -1 || browserConfig._constrainedContentMode > 0) {
                     browserConfig._constrainedContentMode = CONSTRAINED_CONTENT_MODE_DEFAULT;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 10:
                  browserConfig._defaultContentMode = _tmpSyncBuffer.getInt();
                  if (browserConfig._defaultContentMode < 0 || browserConfig._defaultContentMode > 2) {
                     browserConfig._defaultContentMode = 1;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 11:
                  browserConfig._ribbonTitle = _tmpSyncBuffer.getString();
                  if (browserConfig._ribbonTitle == null) {
                     browserConfig._ribbonTitle = "";
                  }
                  break;
               case 12:
                  browserConfig._configType = _tmpSyncBuffer.getInt();
                  if (browserConfig._configType < 0 || browserConfig._configType > 8) {
                     browserConfig._configType = INVALID_VALUE;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }

                  browserConfig._cssEnabled = cssEnabledSet ? browserConfig._cssEnabled : getDefaultPropertyAsBoolean(browserConfig._configType, 33);
                  browserConfig._useBackgroundImages = useBackgroundImagesSet
                     ? browserConfig._useBackgroundImages
                     : getDefaultPropertyAsBoolean(browserConfig._configType, 40);
                  browserConfig._htmlTablesEnabled = htmlTablesEnabledSet
                     ? browserConfig._htmlTablesEnabled
                     : getDefaultPropertyAsBoolean(browserConfig._configType, 36);
                  browserConfig._bsmEnabled = bsmEnabledSet ? browserConfig._bsmEnabled : getDefaultPropertyAsBoolean(browserConfig._configType, 43);
                  browserConfig._useSeparateIcon = useSeparateIconSet
                     ? browserConfig._useSeparateIcon
                     : getDefaultPropertyAsBoolean(browserConfig._configType, 55);
                  break;
               case 13:
                  browserConfig._wtlsMode = _tmpSyncBuffer.getInt();
                  if (browserConfig._wtlsMode < 0 || browserConfig._wtlsMode > 1) {
                     browserConfig._wtlsMode = 0;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 14:
                  browserConfig._icon = _tmpSyncBuffer.getString();
                  break;
               case 15:
                  browserConfig._wtlsClientIdType = _tmpSyncBuffer.getInt();
                  if (browserConfig._wtlsClientIdType < 0 || browserConfig._wtlsClientIdType > 7) {
                     browserConfig._wtlsClientIdType = -1;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 16:
                  browserConfig._wtlsClientIdValue = _tmpSyncBuffer.getString();
                  if (browserConfig._wtlsClientIdValue == null) {
                     browserConfig._wtlsClientIdValue = "";
                  }
                  break;
               case 17:
                  browserConfig._defaultEmulationMode = _tmpSyncBuffer.getInt();
                  if (browserConfig._defaultEmulationMode < 1 || browserConfig._defaultEmulationMode > 6) {
                     browserConfig._defaultEmulationMode = 1;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 18:
                  browserConfig._showImagesValue = _tmpSyncBuffer.getInt();
                  if (browserConfig._showImagesValue < 0 || browserConfig._showImagesValue > 2) {
                     browserConfig._showImagesValue = SHOW_IMAGES_DEFAULT;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 19:
                  browserConfig._showImagePlaceholdersValue = _tmpSyncBuffer.getInt();
                  if (browserConfig._showImagePlaceholdersValue < 0 || browserConfig._showImagePlaceholdersValue > 1) {
                     browserConfig._showImagePlaceholdersValue = 0;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 20:
                  browserConfig._autoStart = _tmpSyncBuffer.getInt();
                  if (browserConfig._autoStart < 0 || browserConfig._autoStart > 1) {
                     browserConfig._autoStart = 0;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 21:
                  browserConfig._brandingIcon = _tmpSyncBuffer.getInt();
                  if (browserConfig._brandingIcon < -1) {
                     browserConfig._brandingIcon = -1;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 22:
                  browserConfig._vendorId = _tmpSyncBuffer.getString();
                  break;
               case 23:
                  browserConfig._sendPhoneNumberValue = _tmpSyncBuffer.getInt();
                  if (browserConfig._sendPhoneNumberValue < 0 || browserConfig._sendPhoneNumberValue > 1) {
                     browserConfig._sendPhoneNumberValue = 0;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 24:
                  browserConfig._provisionedBookmarksFolderName = _tmpSyncBuffer.getString();
                  break;
               case 25:
                  browserConfig._ribbonPosition = _tmpSyncBuffer.getInt();
                  if (browserConfig._ribbonPosition < 0) {
                     browserConfig._ribbonPosition = 0;
                     EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
                  }
                  break;
               case 26:
                  browserConfig._httpsEncryptionMode = _tmpSyncBuffer.getInt();
                  break;
               case 27:
                  browserConfig._constrainedSaving = _tmpSyncBuffer.getInt();
                  break;
               case 28:
                  browserConfig._appDownloadModuleName = _tmpSyncBuffer.getString();
                  break;
               case 29:
                  browserConfig._iconUrl = _tmpSyncBuffer.getString();
                  break;
               case 30:
                  browserConfig._hiddenFromRibbon = _tmpSyncBuffer.getInt();
                  if (browserConfig._hiddenFromRibbon < 0 || browserConfig._hiddenFromRibbon > 1) {
                     browserConfig._hiddenFromRibbon = 0;
                  }
                  break;
               case 31:
                  browserConfig._browserKeyUrl = _tmpSyncBuffer.getString();
                  break;
               case 32:
                  browserConfig._provisionedBookmarksInteraction = _tmpSyncBuffer.getInt();
                  if (browserConfig._provisionedBookmarksInteraction < 0 || browserConfig._provisionedBookmarksInteraction > 2) {
                     browserConfig._provisionedBookmarksInteraction = 0;
                  }
                  break;
               case 33:
                  browserConfig._cssEnabled = _tmpSyncBuffer.getInt() != 0;
                  cssEnabledSet = true;
                  break;
               case 35:
                  browserConfig._useForegroundBackgroundColors = _tmpSyncBuffer.getInt() != 0;
                  break;
               case 36:
                  browserConfig._htmlTablesEnabled = _tmpSyncBuffer.getInt() != 0;
                  htmlTablesEnabledSet = true;
                  break;
               case 37:
                  browserConfig._useEmbeddedMedia = _tmpSyncBuffer.getInt() != 0;
                  break;
               case 38:
                  browserConfig._javaScriptEnabled = _tmpSyncBuffer.getInt() != 0;
                  break;
               case 39:
                  browserConfig._javaScriptPopupsAllowed = _tmpSyncBuffer.getInt() != 0;
                  break;
               case 40:
                  browserConfig._useBackgroundImages = _tmpSyncBuffer.getInt() != 0;
                  useBackgroundImagesSet = true;
                  break;
               case 41:
                  browserConfig._localizedStrings = _tmpSyncBuffer.getString();
                  break;
               case 42:
                  browserConfig._bookmarksFolderName = _tmpSyncBuffer.getString();
                  break;
               case 43:
                  browserConfig._bsmEnabled = _tmpSyncBuffer.getInt() != 0;
                  bsmEnabledSet = true;
                  break;
               case 44:
                  browserConfig._helpLinks = _tmpSyncBuffer.getString();
                  break;
               case 45:
                  browserConfig._helpGroupLabel = _tmpSyncBuffer.getString();
                  break;
               case 46:
                  browserConfig._moreImagesUrl = _tmpSyncBuffer.getString();
                  break;
               case 47:
                  browserConfig._moreThemesUrl = _tmpSyncBuffer.getString();
                  break;
               case 48:
                  browserConfig._moreTunesUrl = _tmpSyncBuffer.getString();
                  break;
               case 49:
                  browserConfig._appDownloadModuleVersion = _tmpSyncBuffer.getString();
                  break;
               case 51:
                  browserConfig._rimBranded = _tmpSyncBuffer.getInt() != 0;
                  break;
               case 52:
                  browserConfig._sendProfileDiffs = _tmpSyncBuffer.getInt() != 0;
                  break;
               case 53:
                  browserConfig._promptEnableJavascript = _tmpSyncBuffer.getInt() != 0;
                  break;
               case 54:
                  browserConfig._ribbonIconType = _tmpSyncBuffer.getString();
                  break;
               case 55:
                  browserConfig._useSeparateIcon = _tmpSyncBuffer.getInt() != 0;
                  useSeparateIconSet = true;
                  break;
               case 56:
                  browserConfig._domains = _tmpSyncBuffer.getString();
                  break;
               case 57:
                  browserConfig._provisionedBookmarksExpandFolder = _tmpSyncBuffer.getInt();
                  if (browserConfig._provisionedBookmarksExpandFolder < 0 || browserConfig._provisionedBookmarksExpandFolder > 2) {
                     browserConfig._provisionedBookmarksExpandFolder = 0;
                  }
                  break;
               case 58:
                  browserConfig._javascriptWatchdogTimeout = Math.max(0, _tmpSyncBuffer.getInt());
            }
         }

         if (browserConfig._homePageUrl != null && browserConfig._transportServiceCID != null && browserConfig._transportServiceUID != null) {
            if (browserConfig._constrainedNavigation == 1 && browserConfig._startupPageValue == 0) {
               browserConfig._startupPageValue = 1;
            }

            if (browserConfig._constrainedContentMode == 0 && browserConfig._defaultContentMode != 0) {
               browserConfig._defaultContentMode = 0;
            }

            if (browserConfig._configType == INVALID_VALUE) {
               if (StringUtilities.strEqualIgnoreCase(browserConfig._transportServiceCID, IPPP_SERVICE_CID, 1701707776)) {
                  browserConfig._configType = 1;
               } else {
                  browserConfig._configType = 0;
               }
            } else if (browserConfig._configType == 6 && StringUtilities.regionMatches(browserConfig._transportServiceUID, true, 0, "AD_", 0, 3, 1701707776)) {
               browserConfig._transportServiceUID = browserConfig._transportServiceUID.substring(3);
            }

            if (browserConfig._httpsEncryptionMode == INVALID_VALUE) {
               if (browserConfig._configType == 2) {
                  browserConfig._httpsEncryptionMode = 0;
                  return browserConfig;
               }

               if (browserConfig._configType == 4) {
                  browserConfig._httpsEncryptionMode = 1;
                  return browserConfig;
               }

               browserConfig._httpsEncryptionMode = 2;
            }

            return browserConfig;
         } else {
            EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
            return null;
         }
      } else {
         EventLogger.logEvent(1907089860548946979L, 1114207074, 3);
         return null;
      }
   }

   public static final BrowserConfigRecord getDecodedConfig(String browserConfigUID, int browserConfigType, String transportServiceCID) {
      BrowserConfigRecord browserConfig = null;
      ServiceBook sb = ServiceBook.getSB();
      if (browserConfigUID != null) {
         ServiceRecord sr = sb.getRecordByUidAndCid(browserConfigUID, SERVICE_CID);
         if (sr != null) {
            browserConfig = getDecodedConfig(sr);
         }
      }

      if (browserConfig == null) {
         boolean checkCID = transportServiceCID != null && transportServiceCID.length() > 0;
         if (browserConfigType != INVALID_VALUE) {
            ServiceRecord[] records = sb.findRecordsByCid(SERVICE_CID);

            for (int i = 0; i < records.length; i++) {
               BrowserConfigRecord tempRecord = getDecodedConfig(records[i]);
               if (tempRecord != null
                  && tempRecord.getPropertyAsInt(12) == browserConfigType
                  && (!checkCID || StringUtilities.strEqualIgnoreCase(tempRecord.getPropertyAsString(3), transportServiceCID, 1701707776))) {
                  browserConfig = tempRecord;
                  break;
               }
            }
         }

         if (browserConfig == null && checkCID) {
            BrowserSession session = BrowserSession.getCurrentSession();
            if (session != null && StringUtilities.strEqualIgnoreCase(transportServiceCID, session.getConfig().getPropertyAsString(3), 1701707776)) {
               browserConfigUID = session.getConfig().getUid();
            } else if (StringUtilities.strEqualIgnoreCase(transportServiceCID, WAPServiceRecord.SERVICE_CID, 1701707776)) {
               browserConfigUID = GeneralProperty.getDefaultWapBrowserConfigServiceUID();
            } else if (StringUtilities.strEqualIgnoreCase(transportServiceCID, IPPP_SERVICE_CID, 1701707776)) {
               browserConfigUID = GeneralProperty.getDefaultMdsBrowserConfigServiceUID();
            } else if (StringUtilities.strEqualIgnoreCase(transportServiceCID, WPTCPServiceRecord.SERVICE_CID, 1701707776)) {
               browserConfigUID = GeneralProperty.getDefaultWptcpBrowserConfigServiceUID();
            } else if (session != null) {
               browserConfigUID = session.getConfig().getUid();
            }

            if (browserConfigUID != null) {
               ServiceRecord sr = sb.getRecordByUidAndCid(browserConfigUID, SERVICE_CID);
               if (sr != null) {
                  browserConfig = getDecodedConfig(sr);
               }
            }
         }
      }

      return browserConfig;
   }

   public static final BrowserConfigRecord getNewConfig(String transportCid, String transportUid, int type) {
      return new BrowserConfigRecord(transportCid, transportUid, type);
   }

   public final byte[] getEncodedData() {
      DataBuffer tmpDataBuffer = (DataBuffer)(new Object());
      SyncBuffer tmpSyncBuffer = (SyncBuffer)(new Object(tmpDataBuffer, 0, 0));
      tmpDataBuffer.writeByte(2);
      tmpSyncBuffer.addField(1, this._homePageUrl);
      tmpSyncBuffer.addInt(2, this._startupPageValue, 1);
      tmpSyncBuffer.addField(3, this._transportServiceCID);
      String transportServiceUID = this._transportServiceUID;
      if (this._configType == 6) {
         transportServiceUID = ((StringBuffer)(new Object("AD_"))).append(this._transportServiceUID).toString();
      }

      tmpSyncBuffer.addField(4, transportServiceUID);
      tmpSyncBuffer.addInt(5, this._configValuesEditable, 1);
      tmpSyncBuffer.addInt(6, this._timeout, 4);
      tmpSyncBuffer.addInt(7, this._constrainedNavigation, 1);
      tmpSyncBuffer.addInt(9, this._constrainedContentMode, 1);
      tmpSyncBuffer.addInt(10, this._defaultContentMode, 1);
      tmpSyncBuffer.addField(11, this._ribbonTitle);
      tmpSyncBuffer.addInt(25, this._ribbonPosition, 1);
      tmpSyncBuffer.addInt(12, this._configType, 1);
      tmpSyncBuffer.addInt(26, this._httpsEncryptionMode, 1);
      tmpSyncBuffer.addInt(27, this._constrainedSaving, 1);
      tmpSyncBuffer.addInt(30, this._hiddenFromRibbon, 1);
      tmpSyncBuffer.addInt(32, this._provisionedBookmarksInteraction, 1);
      tmpSyncBuffer.addInt(57, this._provisionedBookmarksExpandFolder, 1);
      if (this._browserKeyUrl != null && this._browserKeyUrl.length() > 0) {
         tmpSyncBuffer.addField(31, this._browserKeyUrl);
      }

      if (this._wtlsMode != 0) {
         tmpSyncBuffer.addInt(13, this._wtlsMode, 1);
      }

      if (this._icon != null) {
         tmpSyncBuffer.addField(14, this._icon);
      }

      if (this._appDownloadModuleName != null) {
         tmpSyncBuffer.addField(28, this._appDownloadModuleName);
      }

      if (this._appDownloadModuleVersion != null) {
         tmpSyncBuffer.addField(49, this._appDownloadModuleVersion);
      }

      if (this._iconUrl != null) {
         tmpSyncBuffer.addField(29, this._iconUrl);
      }

      if (this._wtlsClientIdType != -1) {
         tmpSyncBuffer.addInt(15, this._wtlsClientIdType, 1);
         tmpSyncBuffer.addField(16, this._wtlsClientIdValue);
      }

      tmpSyncBuffer.addInt(17, this._defaultEmulationMode, 1);
      tmpSyncBuffer.addInt(18, this._showImagesValue, 1);
      tmpSyncBuffer.addInt(19, this._showImagePlaceholdersValue, 1);
      tmpSyncBuffer.addInt(20, this._autoStart, 1);
      tmpSyncBuffer.addInt(21, this._brandingIcon, 1);
      if (this._vendorId != VENDOR_ID_NONE) {
         tmpSyncBuffer.addField(22, this._vendorId);
      }

      tmpSyncBuffer.addInt(23, this._sendPhoneNumberValue, 1);
      if (this._provisionedBookmarksFolderName != null) {
         tmpSyncBuffer.addField(24, this._provisionedBookmarksFolderName);
      }

      tmpSyncBuffer.addInt(33, this._cssEnabled ? 1 : 0, 1);
      tmpSyncBuffer.addInt(35, this._useForegroundBackgroundColors ? 1 : 0, 1);
      tmpSyncBuffer.addInt(40, this._useBackgroundImages ? 1 : 0, 1);
      tmpSyncBuffer.addInt(36, this._htmlTablesEnabled ? 1 : 0, 1);
      tmpSyncBuffer.addInt(38, this._javaScriptEnabled ? 1 : 0, 1);
      tmpSyncBuffer.addInt(39, this._javaScriptPopupsAllowed ? 1 : 0, 1);
      tmpSyncBuffer.addInt(51, this._rimBranded ? 1 : 0, 1);
      tmpSyncBuffer.addInt(52, this._sendProfileDiffs ? 1 : 0, 1);
      tmpSyncBuffer.addInt(53, this._promptEnableJavascript ? 1 : 0, 1);
      tmpSyncBuffer.addInt(55, this._useSeparateIcon ? 1 : 0, 1);
      tmpSyncBuffer.addInt(58, this._javascriptWatchdogTimeout, 4);
      String[] bookmarks = this._bookmarks;
      if (bookmarks != null) {
         int length = Math.min(bookmarks.length, 40);

         for (int i = 0; i < length; i++) {
            tmpSyncBuffer.addField(60 + i, bookmarks[i]);
         }
      }

      if (this._localizedStrings != null) {
         tmpSyncBuffer.addField(41, this._localizedStrings);
      }

      if (this._bookmarksFolderName != null) {
         tmpSyncBuffer.addField(42, this._bookmarksFolderName);
      }

      tmpSyncBuffer.addInt(43, this._bsmEnabled ? 1 : 0, 1);
      if (this._helpLinks != null) {
         tmpSyncBuffer.addField(44, this._helpLinks);
      }

      if (this._helpGroupLabel != null) {
         tmpSyncBuffer.addField(45, this._helpGroupLabel);
      }

      if (this._moreImagesUrl != null && this._moreImagesUrl.length() > 0) {
         tmpSyncBuffer.addField(46, this._moreImagesUrl);
      }

      if (this._moreTunesUrl != null && this._moreTunesUrl.length() > 0) {
         tmpSyncBuffer.addField(48, this._moreTunesUrl);
      }

      if (this._moreThemesUrl != null && this._moreThemesUrl.length() > 0) {
         tmpSyncBuffer.addField(47, this._moreThemesUrl);
      }

      if (this._uaProfUri != null) {
         tmpSyncBuffer.addField(8, this._uaProfUri);
      }

      if (this._ribbonIconType != null && this._ribbonIconType.length() > 0) {
         tmpSyncBuffer.addField(54, this._ribbonIconType);
      }

      if (this._domains != null && this._domains.length() > 0) {
         tmpSyncBuffer.addField(56, this._domains);
      }

      return tmpDataBuffer.toArray();
   }

   public final String getUid() {
      return this._uid;
   }

   public final boolean getPropertyAsBoolean(int property) {
      switch (property) {
         case 33:
            return this._cssEnabled;
         case 35:
            return this._useForegroundBackgroundColors;
         case 36:
            return this._htmlTablesEnabled;
         case 37:
            return this._useEmbeddedMedia;
         case 38:
            return this._javaScriptEnabled;
         case 39:
            return this._javaScriptPopupsAllowed;
         case 40:
            return this._useBackgroundImages;
         case 43:
            return this._bsmEnabled;
         case 51:
            return this._rimBranded;
         case 52:
            return this._sendProfileDiffs;
         case 53:
            return this._promptEnableJavascript;
         case 55:
            return this._useSeparateIcon;
         default:
            throw new Object();
      }
   }

   public final int getPropertyAsInt(int property) {
      switch (property) {
         case 2:
            return this._startupPageValue;
         case 5:
            return this._configValuesEditable;
         case 6:
            return this._timeout;
         case 7:
            return this._constrainedNavigation;
         case 9:
            return this._constrainedContentMode;
         case 10:
            return this._defaultContentMode;
         case 12:
            return this._configType;
         case 13:
            return this._wtlsMode;
         case 15:
            return this._wtlsClientIdType;
         case 17:
            return this._defaultEmulationMode;
         case 18:
            return this._showImagesValue;
         case 19:
            return this._showImagePlaceholdersValue;
         case 20:
            return this._autoStart;
         case 21:
            return this._brandingIcon;
         case 23:
            return this._sendPhoneNumberValue;
         case 25:
            return this._ribbonPosition;
         case 26:
            return this._httpsEncryptionMode;
         case 27:
            return this._constrainedSaving;
         case 30:
            return this._hiddenFromRibbon;
         case 32:
            return this._provisionedBookmarksInteraction;
         case 57:
            return this._provisionedBookmarksExpandFolder;
         case 58:
            return this._javascriptWatchdogTimeout;
         default:
            throw new Object();
      }
   }

   public final String getPropertyAsString(int property) {
      switch (property) {
         case 1:
            return this._homePageUrl;
         case 3:
            return this._transportServiceCID;
         case 4:
            return this._transportServiceUID;
         case 8:
            return this._uaProfUri;
         case 11:
            return this._ribbonTitle;
         case 16:
            return this._wtlsClientIdValue;
         case 22:
            return this._vendorId;
         case 24:
            return this._provisionedBookmarksFolderName;
         case 28:
            return this._appDownloadModuleName;
         case 29:
            return this._iconUrl;
         case 31:
            return this._browserKeyUrl;
         case 41:
            return this._localizedStrings;
         case 42:
            return this._bookmarksFolderName;
         case 44:
            return this._helpLinks;
         case 45:
            return this._helpGroupLabel;
         case 46:
            return this._moreImagesUrl;
         case 47:
            return this._moreThemesUrl;
         case 48:
            return this._moreTunesUrl;
         case 49:
            return this._appDownloadModuleVersion;
         case 54:
            return this._ribbonIconType;
         case 56:
            return this._domains;
         default:
            throw new Object();
      }
   }

   public final boolean isPropertyAsStringSet(int property) {
      switch (property) {
         case 1:
            if (this._homePageUrl != null) {
               return true;
            }

            return false;
         default:
            throw new Object();
      }
   }

   public static final String encodeBookmark(String url, String title) {
      return ((StringBuffer)(new Object())).append(url).append('|').append(title).toString();
   }

   public final SortedCollection getProvisionedBookmarks() {
      String[] bookmarks = this._bookmarks;
      if (bookmarks != null && bookmarks.length > 0) {
         SortedCollection bookmarksCollection = (SortedCollection)(new Object());
         bookmarksCollection.initialize(0, BookmarksFolderList.getProvisionedFolderLUID(this._uid), (LongKeyProviderAdaptor)(new Object()), null);

         for (int i = 0; i < bookmarks.length; i++) {
            if (bookmarks[i] != null && bookmarks[i].length() > 2) {
               StringTokenizer tokenizer = (StringTokenizer)(new Object(bookmarks[i], '|'));
               String uri = tokenizer.nextToken();
               if (uri != null && uri.length() > 0) {
                  String title = tokenizer.nextToken();
                  if (title != null && title.length() > 0) {
                     ModelResult modelResult = new ModelResult(uri, 8449, null);
                     bookmarksCollection.add(new BrowserPageModel(BrowserPageModel.makeLUID(), i, 4, title, modelResult));
                  }
               }
            }
         }

         if (bookmarksCollection.size() > 0) {
            return bookmarksCollection;
         }
      }

      return null;
   }

   public final void setProvisionedBookmarks(String[] bookmarks) {
      this._bookmarks = bookmarks;
   }

   public final Object getPropertyAsObject(int property) {
      switch (property) {
         case 14:
            return this.decodeIconData(this._icon);
         default:
            throw new Object();
      }
   }

   private final String getPrivateMDSHomePageWithITPolicy() {
      boolean readonly = ITPolicy.getBoolean(18, false);
      if (!readonly && this._uid != null) {
         String homePageUrl = GeneralProperty.getHomePageUrlOverrideValue(this._uid);
         if (homePageUrl != null) {
            return homePageUrl;
         }
      }

      return this._homePageUrl;
   }

   public final String getHomePageWithOverride() {
      if (this._configType == 1) {
         return this.getPrivateMDSHomePageWithITPolicy();
      }

      if (this._uid != null) {
         String homePageUrl = GeneralProperty.getHomePageUrlOverrideValue(this._uid);
         if (homePageUrl != null) {
            return homePageUrl;
         }
      }

      return this._homePageUrl;
   }

   public final int getPropertyAsIntWithOverride(byte property) {
      if (this._uid != null) {
         int key = 0;
         byte var4;
         switch (property) {
            case 2:
               var4 = 41;
               break;
            case 5:
               var4 = 39;
               break;
            case 17:
               var4 = 19;
               break;
            case 18:
               var4 = 16;
               break;
            case 19:
               var4 = 17;
               break;
            default:
               throw new Object();
         }

         if (var4 != 0) {
            int value = GeneralProperty.getOverridePropertyAsInt(var4, this._uid);
            if (value != -1) {
               return value;
            }
         }
      }

      return this.getPropertyAsInt(property);
   }

   public final boolean getPropertyAsBooleanWithOverride(byte property) {
      if (this._uid != null) {
         int key = 0;
         byte var4;
         switch (property) {
            case 32:
            case 34:
            case 41:
            case 42:
               throw new Object();
            case 33:
            default:
               var4 = 6;
               break;
            case 35:
               var4 = 8;
               break;
            case 36:
               var4 = 4;
               break;
            case 37:
               var4 = 25;
               break;
            case 38:
               var4 = 21;
               break;
            case 39:
               var4 = 22;
               break;
            case 40:
               var4 = 7;
               break;
            case 43:
               var4 = 38;
         }

         if (var4 != 0) {
            int value = GeneralProperty.getOverridePropertyAsInt(var4, this._uid);
            if (value != -1) {
               if (value == 1) {
                  return true;
               }

               return false;
            }
         }
      }

      return this.getPropertyAsBoolean(property);
   }

   public final void setBooleanPropertyWithOverride(boolean value, int configKey, int generalOptionsKey, String uid) {
      if (value == this.getPropertyAsBoolean(configKey)) {
         GeneralProperty.removeOverrideProperty(generalOptionsKey, uid);
      } else {
         GeneralProperty.setOverrideProperty(generalOptionsKey, uid, value);
      }
   }

   public final void setIntPropertyWithOverride(int value, int configKey, int generalOptionsKey, String uid) {
      if (value == this.getPropertyAsInt(configKey)) {
         GeneralProperty.removeOverrideProperty(generalOptionsKey, uid);
      } else {
         GeneralProperty.setOverrideProperty(generalOptionsKey, uid, value);
      }
   }

   public final int getContentModeWithOverride() {
      if (this._constrainedContentMode != -1) {
         return this._constrainedContentMode;
      }

      if (this._uid != null) {
         int contentMode = GeneralProperty.getOverridePropertyAsInt(18, this._uid);
         if (contentMode != -1) {
            return contentMode;
         }
      }

      return this._defaultContentMode;
   }

   public final String getLocalizedString(int property) {
      if (property == 11 || this._localizedStrings != null && this._localizedStrings.length() > 0) {
         try {
            return getResourceBundleFamily().getString(this.getResourceID(property));
         } finally {
            return this.getPropertyAsString(property);
         }
      } else {
         return this.getPropertyAsString(property);
      }
   }

   public final int getResourceID(int property) {
      return (int)StringUtilities.stringHashToLong(((StringBuffer)(new Object())).append(this._uid).append((char)95).append(property).toString());
   }

   private static final ResourceBundleFamily getResourceBundleFamily() {
      synchronized (_bundleSyncObject) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         ResourceBundleFamily bundleFamily = (ResourceBundleFamily)applicationRegistry.getOrWaitFor(1637489727423389893L);
         if (bundleFamily == null) {
            bundleFamily = ResourceBundle.getBundle(1637489727423389893L, "net.rim.device.apps.internal.browser.options.BrowserConfigRecord.BUNDLE_ID");
            applicationRegistry.put(1637489727423389893L, bundleFamily);
         }

         return bundleFamily;
      }
   }

   private final void addRootString(HashResourceBundle bundle, int property) {
      String str = this.getPropertyAsString(property);
      bundle.put(this.getResourceID(property), str != null && str.length() > 0 ? str : null);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void addLocalizedStrings(ServiceRecord serviceRecord) {
      ResourceBundleFamily bundleFamily = getResourceBundleFamily();
      Locale locale = Locale.get(0);
      HashResourceBundle bundle = getOrCreateBundle(locale, bundleFamily);
      int ribbonTitleResourceID = this.getResourceID(11);
      String ribbonTitle = this._ribbonTitle;
      if (ribbonTitle == null || ribbonTitle.length() == 0) {
         ribbonTitle = serviceRecord.getName();
      }

      bundle.put(ribbonTitleResourceID, ribbonTitle);
      this.addRootString(bundle, 24);
      this.addRootString(bundle, 42);
      this.addRootString(bundle, 45);
      if (this._localizedStrings != null && this._localizedStrings.length() > 0) {
         try {
            String currentToken = null;
            int state = 1;
            int typecode = -1;
            StringTokenizer tokenizer = (StringTokenizer)(new Object(this._localizedStrings, '|'));

            while (tokenizer.hasMoreTokens()) {
               currentToken = tokenizer.nextToken();
               if (state == 0) {
                  if (currentToken.length() <= 0) {
                     break;
                  }

                  char firstChar = currentToken.charAt(0);
                  state = firstChar >= '0' && firstChar <= '9' ? 2 : 1;
               }

               switch (state) {
                  case 0:
                     throw new Object();
                  case 1:
                  default:
                     boolean var16 = false /* VF: Semaphore variable */;

                     label164:
                     try {
                        var16 = true;
                        locale = Locale.parse(currentToken);
                        bundle = getOrCreateBundle(locale, bundleFamily);
                        var16 = false;
                     } finally {
                        if (var16) {
                           Locale var19 = null;
                           bundle = null;
                           break label164;
                        }
                     }

                     state = 2;
                     break;
                  case 2:
                     typecode = Integer.parseInt(currentToken);
                     state = 3;
                     break;
                  case 3:
                     int resourceID = -1;
                     switch (typecode) {
                        case 11:
                           resourceID = ribbonTitleResourceID;
                           break;
                        case 24:
                        case 42:
                        case 45:
                           resourceID = this.getResourceID(typecode);
                     }

                     if (resourceID != -1 && bundle != null) {
                        bundle.put(resourceID, currentToken);
                     }

                     state = 0;
               }
            }
         } finally {
            return;
         }
      }
   }

   private static final HashResourceBundle getOrCreateBundle(Locale locale, ResourceBundleFamily bundleFamily) {
      ResourceBundle existingBundle = bundleFamily.getBundle(locale);
      if (existingBundle instanceof Object && locale != null && locale.equals(existingBundle.getLocale())) {
         return (HashResourceBundle)existingBundle;
      }

      HashResourceBundle bundle = (HashResourceBundle)(new Object(locale));
      if (locale != null) {
         bundleFamily.put(locale, bundle);
      }

      return bundle;
   }

   public final boolean isITEnabled() {
      switch (this._configType) {
         case -1:
         case 3:
         case 5:
            return true;
         case 0:
         case 7:
         default:
            return ITPolicy.getBoolean(19, true);
         case 1:
            return ITPolicy.getBoolean(2, true);
         case 2:
            return ServiceBook.getSB().isAllowedRecord(null, 0, null, "*", "CMIME");
         case 4:
         case 8:
            return ITPolicy.getBoolean(30, 3, true);
         case 6:
            return ITPolicy.getBoolean(30, 14, true);
      }
   }

   private final byte[] decodeIconData(String base64EncodedString) {
      byte[] iconData = null;
      if (base64EncodedString != null) {
         try {
            byte[] b64Data = base64EncodedString.getBytes();
            ByteArrayInputStream bais = (ByteArrayInputStream)(new Object(b64Data));
            Base64InputStream b64is = (Base64InputStream)(new Object(bais));
            DataBuffer dataBuffer = (DataBuffer)(new Object());
            byte[] byteBuffer = new byte[20];
            int bytesRead = 0;

            while (bytesRead >= 0) {
               bytesRead = b64is.read(byteBuffer);
               if (bytesRead > 0) {
                  dataBuffer.write(byteBuffer, 0, bytesRead);
               }
            }

            return dataBuffer.toArray();
         } finally {
            return iconData;
         }
      } else {
         return iconData;
      }
   }

   public final boolean doesBrandingVendorIdMatch() {
      boolean match = true;
      if (this._vendorId != VENDOR_ID_NONE) {
         int brandingVendorId = Branding.getVendorId();
         StringTokenizer tokens = (StringTokenizer)(new Object(this._vendorId, ' '));

         while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();

            try {
               int vendorId = Integer.parseInt(token);
               if (vendorId == brandingVendorId) {
                  return true;
               }

               match = false;
            } finally {
               continue;
            }
         }
      }

      return match;
   }

   public final void setPropertyAsInt(int property, int value) {
      switch (property) {
         case 2:
            this._startupPageValue = value;
            return;
         case 5:
            this._configValuesEditable = value;
            return;
         case 6:
            this._timeout = value;
            return;
         case 7:
            this._constrainedNavigation = value;
            return;
         case 9:
            this._constrainedContentMode = value;
            return;
         case 10:
            this._defaultContentMode = value;
            return;
         case 12:
            this._configType = value;
            return;
         case 13:
            this._wtlsMode = value;
            return;
         case 15:
            this._wtlsClientIdType = value;
            return;
         case 17:
            this._defaultEmulationMode = value;
            return;
         case 18:
            this._showImagesValue = value;
            return;
         case 19:
            this._showImagePlaceholdersValue = value;
            return;
         case 20:
            this._autoStart = value;
            return;
         case 21:
            this._brandingIcon = value;
            return;
         case 23:
            this._sendPhoneNumberValue = value;
            return;
         case 25:
            this._ribbonPosition = value;
            return;
         case 26:
            this._httpsEncryptionMode = value;
            return;
         case 27:
            this._constrainedSaving = value;
            return;
         case 30:
            this._hiddenFromRibbon = value;
            return;
         case 32:
            this._provisionedBookmarksInteraction = value;
            return;
         case 57:
            this._provisionedBookmarksExpandFolder = value;
            return;
         case 58:
            this._javascriptWatchdogTimeout = value;
            return;
         default:
            throw new Object();
      }
   }

   public final void setPropertyAsString(int property, String value) {
      switch (property) {
         case 1:
            this._homePageUrl = value;
            return;
         case 3:
            this._transportServiceCID = value;
            return;
         case 4:
            this._transportServiceUID = value;
            return;
         case 8:
            this._uaProfUri = value;
            return;
         case 11:
            this._ribbonTitle = value;
            return;
         case 16:
            this._wtlsClientIdValue = value;
            return;
         case 22:
            this._vendorId = value;
            return;
         case 24:
            this._provisionedBookmarksFolderName = value;
            return;
         case 28:
            this._appDownloadModuleName = value;
            return;
         case 29:
            this._iconUrl = value;
            return;
         case 31:
            this._browserKeyUrl = value;
            return;
         case 41:
            this._localizedStrings = value;
            return;
         case 42:
            this._bookmarksFolderName = value;
            return;
         case 44:
            this._helpLinks = value;
            return;
         case 45:
            this._helpGroupLabel = value;
            return;
         case 46:
            this._moreImagesUrl = value;
            return;
         case 47:
            this._moreThemesUrl = value;
            return;
         case 48:
            this._moreTunesUrl = value;
            return;
         case 49:
            this._appDownloadModuleVersion = value;
            return;
         case 54:
            this._ribbonIconType = value;
            return;
         case 56:
            this._domains = value;
            return;
         default:
            throw new Object();
      }
   }

   public final void setPropertyAsBoolean(int property, boolean value) {
      switch (property) {
         case 33:
            this._cssEnabled = value;
            return;
         case 35:
            this._useForegroundBackgroundColors = value;
            return;
         case 36:
            this._htmlTablesEnabled = value;
            return;
         case 37:
            this._useEmbeddedMedia = value;
            return;
         case 38:
            this._javaScriptEnabled = value;
            return;
         case 39:
            this._javaScriptPopupsAllowed = value;
            return;
         case 40:
            this._useBackgroundImages = value;
            return;
         case 43:
            this._bsmEnabled = value;
            return;
         case 51:
            this._rimBranded = value;
            return;
         case 52:
            this._sendProfileDiffs = value;
            return;
         case 53:
            this._promptEnableJavascript = value;
            return;
         case 55:
            this._useSeparateIcon = value;
            return;
         default:
            throw new Object();
      }
   }

   public static final BrowserConfigRecord[] getValidBrowserConfigRecords() {
      return getValidBrowserConfigRecords(false);
   }

   public static final BrowserConfigRecord[] getValidBrowserConfigRecords(boolean onlyUnconstrainedConfigs) {
      boolean wapInstalled = WAPConnectionRegistry.isWAPInstalled();
      ServiceRecord[] serviceRecords = ServiceBook.getSB().findRecordsByCid(SERVICE_CID);
      int length = serviceRecords.length;
      BrowserConfigRecord[] records = new BrowserConfigRecord[length];

      for (int i = 0; i < length; i++) {
         BrowserConfigRecord rec = records[i] = getDecodedConfig(serviceRecords[i]);
         if (rec == null) {
            Arrays.removeAt(serviceRecords, i);
            Arrays.removeAt(records, i);
            length--;
            i--;
         } else {
            int configType = rec.getPropertyAsInt(12);
            if (!onlyUnconstrainedConfigs || rec.getPropertyAsInt(30) != 1 && rec.getPropertyAsInt(7) == 0 && configType != 2 && configType != 6) {
               String transportCid = rec.getPropertyAsString(3);
               String transportUid = rec.getPropertyAsString(4);
               ServiceRecord transportSr = getTransportServiceRecord(transportCid, transportUid);
               if (transportSr == null) {
                  Arrays.removeAt(serviceRecords, i);
                  Arrays.removeAt(records, i);
                  length--;
                  i--;
               } else if (configType == 0) {
                  if (!wapInstalled || !ITPolicy.getBoolean(19, true)) {
                     Arrays.removeAt(serviceRecords, i);
                     Arrays.removeAt(records, i);
                     length--;
                     i--;
                  }
               } else if (configType == 7) {
                  if (!ITPolicy.getBoolean(19, true)) {
                     Arrays.removeAt(serviceRecords, i);
                     Arrays.removeAt(records, i);
                     length--;
                     i--;
                  }
               } else if (configType == 1) {
                  if (!ITPolicy.getBoolean(2, true)) {
                     Arrays.removeAt(serviceRecords, i);
                     Arrays.removeAt(records, i);
                     length--;
                     i--;
                  }
               } else if ((configType == 4 || configType == 8 || configType == 2) && !ITPolicy.getBoolean(30, 3, true)) {
                  Arrays.removeAt(serviceRecords, i);
                  Arrays.removeAt(records, i);
                  length--;
                  i--;
               }
            } else {
               Arrays.removeAt(serviceRecords, i);
               Arrays.removeAt(records, i);
               length--;
               i--;
            }
         }
      }

      return records;
   }

   public static final boolean isValidBrowserConfigRecord(String uid) {
      if (uid != null && uid.length() != 0) {
         BrowserConfigRecord[] configs = getValidBrowserConfigRecords();
         int numConfigs = configs.length;

         for (int i = 0; i < numConfigs; i++) {
            String configUid = configs[i].getUid();
            if (StringUtilities.strEqualIgnoreCase(uid, configUid, 1701707776)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static final ServiceRecord getTransportServiceRecord(String transportServiceCID, String transportServiceUID) {
      ServiceRecord browserTransport = null;
      if (transportServiceCID != null && transportServiceUID != null) {
         browserTransport = ServiceBook.getSB().getRecordByUidAndCid(transportServiceUID, transportServiceCID);
      }

      return browserTransport;
   }

   public static final String mapTransportUIDToConfigUID(String transportCID, String transportUID) {
      if (transportUID == null) {
         return null;
      }

      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] records = sb.findRecordsByCid(SERVICE_CID);

      for (int i = 0; i < records.length; i++) {
         ServiceRecord record = records[i];

         try {
            BrowserConfigRecord browserConfig = getDecodedConfig(record.getUid(), record.getApplicationData());
            if (browserConfig != null
               && StringUtilities.strEqualIgnoreCase(transportCID, browserConfig.getPropertyAsString(3), 1701707776)
               && StringUtilities.strEqualIgnoreCase(transportUID, browserConfig.getPropertyAsString(4), 1701707776)) {
               return record.getUid();
            }
         } finally {
            continue;
         }
      }

      return null;
   }
}
