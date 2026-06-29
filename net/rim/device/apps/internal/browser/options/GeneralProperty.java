package net.rim.device.apps.internal.browser.options;

import com.sun.cldc.i18n.Helper;
import java.util.Hashtable;
import net.rim.device.api.browser.util.UserAgent;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.RibbonManagerThread;
import net.rim.device.apps.internal.browser.javascript.JavaScriptRegistry;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.ui.BrowserChoiceField;
import net.rim.device.apps.internal.browser.util.FontCache;
import net.rim.device.apps.internal.browser.verbs.SavePropertyVerb;
import net.rim.device.apps.internal.browser.wml.WMLScript;
import net.rim.device.internal.browser.wap.WAPServiceRecord;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;
import net.rim.vm.Array;
import net.rim.vm.PersistentInteger;
import net.rim.vm.WeakReference;

public final class GeneralProperty extends BrowserProperty implements FieldChangeListener {
   private CheckboxField _confirmQuitOnEscapeField;
   private CheckboxField _confirmExecuteScriptsField;
   private CheckboxField _javascriptLocationEnabledField;
   private CheckboxField _doNotCacheSecurePagesField;
   private CheckboxField _clearCacheOnCloseField;
   private CheckboxField _confirmLeaveModifiedPageField;
   private ObjectChoiceField _animationCountField;
   private ObjectChoiceField _defaultFontFamilyField;
   private ObjectChoiceField _defaultFontSizeField;
   private ObjectChoiceField _minimumFontSizeField;
   private ObjectChoiceField _minimumFontStyleField;
   private ObjectChoiceField _imageQualityField;
   private ObjectChoiceField _defaultViewField;
   private CheckboxField _mobileCursorField;
   private BrowserChoiceField _defaultBrowserField;
   private FontFamily[] _fontFamilies;
   private Integer[] _ptFontSizes;
   public static final int URLS_PER_MORE_REQUEST_DEFAULT = 5;
   public static final int URLS_PER_MORE_REQUEST_ALL = 10;
   private static final long CONFIRM_QUIT_ON_ESCAPE_KEY = 7423466083446598127L;
   private static final int CONFIRM_QUIT_ON_ESCAPE_DEFAULT = 0;
   private static final long CONFIRM_EXECUTE_SCRIPTS_KEY = -5753227574626190370L;
   private static final int CONFIRM_EXECUTE_SCRIPTS_DEFAULT = 0;
   private static final long CLEAR_CACHE_ON_CLOSE_KEY = 4988251933819705505L;
   private static final int CLEAR_CACHE_ON_CLOSE_DEFAULT = 0;
   private static final long DELETE_BROWSER_SERVICE_RECORDS_KEY = 1811994284924603625L;
   private static final int DELETE_BROWSER_SERVICE_RECORDS_DEFAULT = 1;
   private static final long JAVASCRIPT_ENABLED_OVERRIDES_KEY = 2753124139214990482L;
   private static final long ALLOW_POPUP_ENABLED_OVERRIDES_KEY = 7456321715485278933L;
   private static final long JAVASCRIPT_LOCATION_ENABLED_KEY = -7627500914324187861L;
   private static final int JAVASCRIPT_LOCATION_ENABLED_DEFAULT = 0;
   private static final long DEFAULT_CHARSET_KEY = 8288869949763761940L;
   private static final long DEFAULT_CHARSET_MODE_KEY = 4252401799533787592L;
   public static final int DEFAULT_CHARSET_MODE_AUTO = 0;
   public static final int DEFAULT_CHARSET_MODE_MANUAL = 1;
   private static final long DEFAULT_FONT_FAMILY_KEY = 5135993918761058926L;
   private static final long DEFAULT_FONT_SIZE_KEY = -4436752438281299535L;
   private static final long MINIMUM_FONT_SIZE_KEY = 5055303530972575550L;
   private static final int FONT_SIZE_MINIMUM = 6;
   private static final int FONT_SIZE_MAXIMUM = 14;
   public static final int MINIMUM_FONT_SIZE_LOCALE_ZH = 9;
   private static final long MINIMUM_FONT_STYLE_KEY = 4256969707186071607L;
   private static final int[] MINIMUM_FONT_STYLES = new int[]{
      0, 1, 64, 51, 1963524352, -447731706, 1704354115, 712179968, 527827200, 16810638, -2104615050, -143040180
   };
   private static final long DO_NOT_CACHE_SECURE_PAGES_KEY = 4546604513431963091L;
   private static final int DO_NOT_CACHE_SECURE_PAGES_DEFAULT = 0;
   private static final long CONFIRM_LEAVE_MODIFIED_PAGE_KEY = -8340817021720941990L;
   private static final int CONFIRM_LEAVE_MODIFIED_PAGE_DEFAULT = 1;
   private static final long ANIMATION_COUNT_KEY = 4610658888840416077L;
   public static final int ANIMATION_COUNT_NEVER = 0;
   public static final int ANIMATION_COUNT_ONCE = 1;
   public static final int ANIMATION_COUNT_10_TIMES = 2;
   public static final int ANIMATION_COUNT_100_TIMES = 3;
   public static final int ANIMATION_COUNT_IMAGE_SPECIFIED = 4;
   private static final int ANIMATION_COUNT_DEFAULT = 3;
   private static final long IMAGE_QUALITY_KEY = -2542665004339389283L;
   private static final long DEFAULT_VIEW_KEY = 8322570537492212114L;
   private static final long FOREGROUND_BACKGROUND_COLOR_OVERRIDES_KEY = 1026877747155840461L;
   private static final long BACKGROUND_IMAGES_OVERRIDES_KEY = -8350120175087682342L;
   private static final long USE_HTML_TABLES_OVERRIDES_KEY = 3823659508711675681L;
   private static final long EMBEDDED_RICH_CONTENT_OVERRIDES_KEY = 1001800610454161964L;
   private static final long HOME_PAGE_URL_OVERRIDES_KEY = 8006690060151025103L;
   private static final long SHOW_IMAGES_OVERRIDES_KEY = 980267727318495258L;
   private static final long SHOW_IMAGE_PLACEHOLDERS_OVERRIDES_KEY = -4096932205753179896L;
   private static final long CONTENT_MODE_OVERRIDES_KEY = 3302849756421362309L;
   private static final long EMULATION_MODE_OVERRIDES_KEY = 3578154629096263419L;
   private static final long ENABLE_BSM_OVERRIDES_KEY = 4306424196075488382L;
   private static final long CONFIG_VALUES_EDITABLE_OVERRIDES_KEY = -7567025871435005713L;
   private static final long PREVIOUS_SIM_ID_KEY = 3464760717660140268L;
   private static final long AUTHENTICATION_CREDENTIALS_KEY = -973863083351750684L;
   private static final long ENABLE_CSS_OVERRIDES_KEY = -8239863488275138003L;
   private static final long CSS_MEDIA_TYPE_OVERRIDES_KEY = 7141793851573097063L;
   private static final long STARTUP_PAGE_OVERRIDES_KEY = 1793886603480711262L;
   private static final long DEFAULT_BROWSER_CONFIG_CHOOSER_KEY = -5295635671561083971L;
   private static final long DEFAULT_BROWSER_CONFIG_CHOOSER_KEY_MDS = 8711594917130453278L;
   private static final long DEFAULT_BROWSER_CONFIG_CHOOSER_KEY_WAP = 8807721636387799133L;
   private static final long DEFAULT_BROWSER_CONFIG_CHOOSER_KEY_WPTCP = 3887878695310535785L;
   private static final long PREFERRED_BROWSER_CONFIG_CHOOSER_KEY = -6983330969469877897L;
   private static final long PREFERRED_BROWSER_CONFIG_CHOOSER_KEY_MDS = 4864038587272185491L;
   private static final long PREFERRED_BROWSER_CONFIG_CHOOSER_KEY_WAP = 6597912215271916471L;
   private static final long SHOW_FULL_SCREEN_KEY = -2530822912849957228L;
   private static final int SHOW_FULL_SCREEN_DEFAULT = 0;
   private static final long ACCEPT_COOKIES_KEY = 3101209072466186167L;
   private static final int DEFAULT_ACCEPT_COOKIES = 1;
   private static final long RAW_DATA_CACHE_SIZE_KEY = -7550178878175683098L;
   private static final int RAW_DATA_CACHE_DEFAULT_SIZE = 2048;
   private static final long PAGE_CACHE_SIZE_KEY = -2742488999105498644L;
   private static final int PAGE_CACHE_DEFAULT_SIZE = 1;
   private static final long DEFAULT_WAP_CONFIG_DETERMINED_KEY = -5579567576086342582L;
   private static final long DEFAULT_MDS_CONFIG_DETERMINED_KEY = 7207860161686815871L;
   private static final int DEFAULT_CONFIG_DETERMINED_DEFAULT = 0;
   private static final long MENU_DELAY_TIME_KEY = -1191945014529594429L;
   private static final int MENU_DELAY_TIME_DEFAULT = InternalServices.isReducedFormFactor() ? 500 : 300;
   private static final long AUDIO_PLAYER_VOLUME_KEY = -3164916047754673306L;
   private static final int AUDIO_PLAYER_VOLUME_DEFAULT = 70;
   public static final long AUDIO_PLAYER_VOLUME_HANDSET_KEY = -3140942203161326241L;
   public static final long AUDIO_PLAYER_VOLUME_HANDSFREE_KEY = 2631578963231385528L;
   public static final long AUDIO_PLAYER_VOLUME_BLUETOOTH_KEY = -9057183150407874039L;
   public static final long AUDIO_PLAYER_VOLUME_HEADSET_KEY = 7806606057975565055L;
   public static final long AUDIO_PLAYER_VOLUME_HEADSET_HANDSFREE_KEY = -7231668217665147568L;
   public static final long AUDIO_PLAYER_VOLUME_BLUETOOTH_A2DP_KEY = -8832867053176672756L;
   public static final long ENABLE_MOBILE_CURSOR_KEY = 2111923133048289738L;
   public static final int ENABLE_MOBILE_CURSOR_DEFAULT = 1;
   public static final int CONFIRM_QUIT_ON_EXIT = 0;
   public static final int CONFIRM_EXECUTE_SCRIPTS = 1;
   public static final int ANIMATION_COUNT = 2;
   public static final int CLEAR_CACHE_ON_CLOSE = 3;
   public static final int USE_HTML_TABLE_SUPPORT_OVERRIDE = 4;
   public static final int AUTHENTICATION_CREDENTIALS = 5;
   public static final int ENABLE_CSS_OVERRIDE = 6;
   public static final int USE_BACKGROUND_IMAGES_OVERRIDE = 7;
   public static final int USE_FOREGROUND_BACKGROUND_COLOR_OVERRIDE = 8;
   public static final int UA_PROF_URI = 9;
   public static final int DO_NOT_CACHE_SECURE_PAGES = 10;
   public static final int CSS_MEDIA_TYPE_OVERRIDE = 11;
   public static final int DELETE_BROWSER_SERVICE_RECORDS = 12;
   public static final int DEFAULT_CHARSET = 13;
   public static final int DEFAULT_CHARSET_MODE = 14;
   public static final int HOME_PAGE_URL_OVERRIDE = 15;
   public static final int SHOW_IMAGES_OVERRIDE = 16;
   public static final int SHOW_IMAGES_PLACEHOLDERS_OVERRIDE = 17;
   public static final int CONTENT_MODE_OVERRIDE = 18;
   public static final int EMULATION_OVERRIDE = 19;
   public static final int SIM_ID = 20;
   public static final int JAVASCRIPT_ENABLED_OVERRIDE = 21;
   public static final int ALLOW_POPUP_ENABLED_OVERRIDE = 22;
   public static final int JAVASCRIPT_LOCATION_ENABLED = 23;
   public static final int CONFIRM_LEAVE_MODIFIED_PAGE = 24;
   public static final int ENABLE_EMBEDDED_RICH_CONTENT_OVERRIDE = 25;
   public static final int DEFAULT_FONT_FACE = 26;
   public static final int DEFAULT_FONT_SIZE = 27;
   public static final int DEFAULT_BROWSER_CONFIGS = 28;
   public static final int SHOW_FULL_SCREEN = 29;
   public static final int ACCEPT_COOKIES = 30;
   public static final int RAW_DATA_CACHE_SIZE = 31;
   public static final int DEFAULT_WAP_CONFIG_DETERMINED = 32;
   public static final int DEFAULT_MDS_CONFIG_DETERMINED = 33;
   public static final int PAGE_CACHE_SIZE = 34;
   public static final int MENU_DELAY_TIME = 35;
   public static final int MINIMUM_FONT_SIZE = 36;
   public static final int MINIMUM_FONT_STYLE = 37;
   public static final int ENABLE_BSM_OVERRIDE = 38;
   public static final int CONFIG_VALUES_EDITABLE_OVERRIDE = 39;
   public static final int AUDIO_PLAYER_VOLUME = 40;
   public static final int STARTUP_PAGE_OVERRIDE = 41;
   public static final int IMAGE_QUALITY = 42;
   public static final int DEFAULT_VIEW = 43;
   public static final int AUDIO_PLAYER_VOLUME_HANDSET = 44;
   public static final int AUDIO_PLAYER_VOLUME_HANDSFREE = 45;
   public static final int AUDIO_PLAYER_VOLUME_BLUETOOTH = 46;
   public static final int AUDIO_PLAYER_VOLUME_HEADSET = 47;
   public static final int ENABLE_MOBILE_CURSOR = 48;
   public static final int AUDIO_PLAYER_VOLUME_HEADSET_HANDSFREE = 49;
   public static final int AUDIO_PLAYER_VOLUME_BLUETOOTH_A2DP = 50;
   public static final BitSet BACKUP_RESTORE_MASK;
   public static final BitSet ALL_OPTIONS_SET_MASK;
   private static GeneralProperty$GeneralPropertyData _configData;

   public static final void addListener(BrowserOptionsChangeListener listener) {
      synchronized (_configData) {
         if (_configData._generalPropertyListeners == null) {
            _configData._generalPropertyListeners = new WeakReference[0];
         }

         for (int i = _configData._generalPropertyListeners.length - 1; i >= 0; i--) {
            if (_configData._generalPropertyListeners[i].get() == null) {
               Arrays.removeAt(_configData._generalPropertyListeners, i);
            }
         }

         Arrays.add(_configData._generalPropertyListeners, new WeakReference(listener));
      }
   }

   public static final void removeListener(BrowserOptionsChangeListener listener) {
      synchronized (_configData) {
         if (_configData._generalPropertyListeners != null) {
            for (int i = _configData._generalPropertyListeners.length - 1; i >= 0; i--) {
               Object obj = _configData._generalPropertyListeners[i].get();
               if (obj == null || obj == listener) {
                  Arrays.removeAt(_configData._generalPropertyListeners, i);
               }
            }
         }
      }
   }

   public static final void notifyListeners() {
      synchronized (_configData) {
         if (_configData._changedOptions.getNumSet() > 0) {
            if (_configData._generalPropertyListeners != null) {
               for (int i = _configData._generalPropertyListeners.length - 1; i >= 0; i--) {
                  Object obj = _configData._generalPropertyListeners[i].get();
                  if (obj == null) {
                     Arrays.removeAt(_configData._generalPropertyListeners, i);
                  } else {
                     ((BrowserOptionsChangeListener)obj).optionsChanged(_configData._changedOptions);
                  }
               }
            }

            _configData._changedOptions.reset();
         }
      }
   }

   public static final String getDefaultFontFamily() {
      PersistentObject store = RIMPersistentStore.getPersistentObject(5135993918761058926L);
      Object contents = store.getContents();
      return !(contents instanceof String) ? FontCache.getInstance().getDefaultFontFamily() : (String)contents;
   }

   public static final void setDefaultFontFamily(String value) {
      PersistentObject store = RIMPersistentStore.getPersistentObject(5135993918761058926L);
      store.setContents(value, 51);
      store.commit();
      _configData._changedOptions.set(26);
   }

   public static final boolean getCurrentPropertyAsBoolean(int id) {
      return PersistentInteger.get(getBooleanHandle(id)) == 1;
   }

   public static final boolean setCurrentProperty(int id, boolean value) {
      int handle = getBooleanHandle(id);
      if (PersistentInteger.get(handle) != (value ? 1 : 0)) {
         PersistentInteger.set(handle, value ? 1 : 0);
         _configData._changedOptions.set(id);
      }

      return true;
   }

   public static final int getCurrentPropertyAsInt(int id) {
      return PersistentInteger.get(getIntegerHandle(id));
   }

   public static final boolean setCurrentProperty(int id, int value) {
      int handle = getIntegerHandle(id);
      if (PersistentInteger.get(handle) != value) {
         PersistentInteger.set(handle, value);
         _configData._changedOptions.set(id);
      }

      return true;
   }

   private static final int getIntegerHandle(int id) {
      switch (id) {
         case 2:
            return _configData._animationCountHandle;
         case 14:
            return _configData._defaultCharsetModeHandle;
         case 27:
            return _configData._defaultFontSizeHandle;
         case 31:
            return _configData._rawDataCacheSizeHandle;
         case 34:
            return _configData._pageCacheSizeHandle;
         case 35:
            return _configData._menuDelayTimeHandle;
         case 36:
            return _configData._minimumFontSizeHandle;
         case 37:
            return _configData._minimumFontStyleHandle;
         case 40:
            return _configData._audioPlayerVolumeHandle;
         case 42:
            return _configData._imageQualityHandle;
         case 43:
            return _configData._defaultViewHandle;
         case 44:
            return _configData._audioPathHandsetHandle;
         case 45:
            return _configData._audioPathHandsfreeHandle;
         case 46:
            return _configData._audioPathBluetoothHandle;
         case 47:
            return _configData._audioPathHeadsetHandle;
         case 49:
            return _configData._audioPathHeadsetHandle;
         case 50:
            return _configData._audioPathBluetootha2dpHandle;
         default:
            throw new RuntimeException();
      }
   }

   private static final int getBooleanHandle(int id) {
      switch (id) {
         case 0:
            return _configData._quitHandle;
         case 1:
            return _configData._executeHandle;
         case 3:
            return _configData._clearHandle;
         case 10:
            return _configData._doNotCacheSecurePagesHandle;
         case 12:
            return _configData._deleteHandle;
         case 23:
            return _configData._javascriptLocationHandle;
         case 24:
            return _configData._confirmLeaveModifiedPageHandle;
         case 29:
            return _configData._showFullScreenHandle;
         case 30:
            return _configData._cookieAcceptHandle;
         case 32:
            return _configData._defaultWapConfigDeterminedHandle;
         case 33:
            return _configData._defaultMdsConfigDeterminedHandle;
         case 48:
            return _configData._enableMobileCursorHandle;
         default:
            throw new RuntimeException();
      }
   }

   public static final String getPreviousSimId() {
      PersistentObject store = RIMPersistentStore.getPersistentObject(3464760717660140268L);
      Object contents = store.getContents();
      return !(contents instanceof String) ? null : (String)contents;
   }

   public static final String getDefaultCharsetValue() {
      PersistentObject store = RIMPersistentStore.getPersistentObject(8288869949763761940L);
      Object contents = store.getContents();
      return !(contents instanceof String) ? Helper.getSuggestedEncoding(Locale.getDefault().getCode()) : (String)contents;
   }

   public static final int getDefaultCharsetModeValue() {
      return PersistentInteger.get(_configData._defaultCharsetModeHandle);
   }

   public static final String getHomePageUrlOverrideValue(String uid) {
      return (String)getHomePageUrlOverrides().get(StringUtilities.toLowerCase(uid, 1701707776));
   }

   public static final Hashtable getHomePageUrlOverrides() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(8006690060151025103L);
      Hashtable table = (Hashtable)persistentObject.getContents();
      if (table == null) {
         synchronized (persistentObject) {
            table = (Hashtable)persistentObject.getContents();
            if (table == null) {
               table = new Hashtable();
               persistentObject.setContents(table, 51);
               persistentObject.commit();
            }

            return table;
         }
      } else {
         return table;
      }
   }

   private static final long mapPropertyToKey(int property) {
      switch (property) {
         case 4:
            return 3823659508711675681L;
         case 6:
            return -8239863488275138003L;
         case 7:
            return -8350120175087682342L;
         case 8:
            return 1026877747155840461L;
         case 11:
            return 7141793851573097063L;
         case 16:
            return 980267727318495258L;
         case 17:
            return -4096932205753179896L;
         case 18:
            return 3302849756421362309L;
         case 19:
            return 3578154629096263419L;
         case 21:
            return 2753124139214990482L;
         case 22:
            return 7456321715485278933L;
         case 25:
            return 1001800610454161964L;
         case 38:
            return 4306424196075488382L;
         case 39:
            return -7567025871435005713L;
         case 41:
            return 1793886603480711262L;
         default:
            throw new RuntimeException();
      }
   }

   public static final int getOverridePropertyAsInt(int property, String uid) {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(mapPropertyToKey(property));
      ToIntHashtable table = (ToIntHashtable)persistentObject.getContents();
      return table != null ? table.get(StringUtilities.toLowerCase(uid, 1701707776)) : -1;
   }

   public static final boolean getOverridePropertyAsBoolean(int key, String uid, boolean defaultValue) {
      int value = getOverridePropertyAsInt(key, uid);
      return value != -1 ? value == 1 : defaultValue;
   }

   public static final ToIntHashtable getOverridePropertiesAsInts(int property) {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(mapPropertyToKey(property));
      ToIntHashtable table = (ToIntHashtable)persistentObject.getContents();
      if (table == null) {
         synchronized (persistentObject) {
            table = (ToIntHashtable)persistentObject.getContents();
            if (table == null) {
               table = new ToIntHashtable();
               persistentObject.setContents(table, 51);
               persistentObject.commit();
            }

            return table;
         }
      } else {
         return table;
      }
   }

   public static final String getEmulationModeString(int index) {
      index = MathUtilities.clamp(0, index, 5);
      if (index == _configData._emulationModeStringIndex) {
         String string = (String)_configData._emulationModeStringWR.get();
         if (string != null) {
            return string;
         }
      }

      String BLACKBERRY_VERSION = UserAgent.getDefaultUserAgent();
      String string;
      switch (index) {
         case -1:
            throw new ArrayIndexOutOfBoundsException();
         case 0:
         default:
            string = BLACKBERRY_VERSION;
            break;
         case 1:
            string = BLACKBERRY_VERSION + " UP.Browser/5.0.3.3";
            break;
         case 2:
            string = BLACKBERRY_VERSION + " UP.Browser/5.0.3.3 UP.Link/5.1.2.1";
            break;
         case 3:
            string = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0) " + BLACKBERRY_VERSION;
            break;
         case 4:
            string = "Mozilla/2.0 (compatible; MSIE 3.02; Windows CE; PPC; 240x320) " + BLACKBERRY_VERSION;
            break;
         case 5:
            string = "Mozilla/4.8 [en] (Windows NT 5.0; U) " + BLACKBERRY_VERSION;
      }

      _configData._emulationModeStringIndex = index;
      _configData._emulationModeStringWR.set(string);
      return string;
   }

   public static final String[] getAuthenticationCredentialsValue(String realm) {
      return (String[])getAuthenticationCredentials().get(realm);
   }

   public static final Hashtable getAuthenticationCredentials() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-973863083351750684L);
      Hashtable table = (Hashtable)persistentObject.getContents();
      if (table == null) {
         synchronized (persistentObject) {
            table = (Hashtable)persistentObject.getContents();
            if (table == null) {
               table = new Hashtable();
               persistentObject.setContents(table, 51);
               persistentObject.commit();
            }

            return table;
         }
      } else {
         return table;
      }
   }

   @Override
   public final String getLabel() {
      return BrowserResources.getString(180);
   }

   @Override
   public final Screen getScreen(boolean restrictedAccess) {
      super._restrictedAccess = restrictedAccess;
      MainScreen screen = this.generateScreen(BrowserResources.getString(180));
      String defaultConfigUID = getDefaultBrowserConfigServiceUID();
      if (defaultConfigUID == null || defaultConfigUID.length() == 0) {
         defaultConfigUID = determineDefaultBrowserConfigServiceUID();
         setDefaultBrowserConfigServiceUID(defaultConfigUID);
      }

      this._defaultBrowserField = new BrowserChoiceField(BrowserResources.getString(838), defaultConfigUID);
      this._confirmQuitOnEscapeField = new CheckboxField(BrowserResources.getString(434), getCurrentPropertyAsBoolean(0));
      this._confirmLeaveModifiedPageField = new CheckboxField(BrowserResources.getString(641), getCurrentPropertyAsBoolean(24));
      this._confirmExecuteScriptsField = new CheckboxField(BrowserResources.getString(285), getCurrentPropertyAsBoolean(1));
      this._javascriptLocationEnabledField = new CheckboxField(BrowserResources.getString(645), getCurrentPropertyAsBoolean(23));
      this._imageQualityField = new ObjectChoiceField(BrowserResources.getString(860), BrowserResources.getStringArray(859), getCurrentPropertyAsInt(42));
      this._defaultViewField = new ObjectChoiceField(BrowserResources.getString(870), BrowserResources.getStringArray(869), getCurrentPropertyAsInt(43));
      this._mobileCursorField = new CheckboxField(BrowserResources.getString(904), getCurrentPropertyAsBoolean(48));
      this._animationCountField = new ObjectChoiceField(BrowserResources.getString(651), BrowserResources.getStringArray(657), getCurrentPropertyAsInt(2));
      this._doNotCacheSecurePagesField = new CheckboxField(BrowserResources.getString(633), getCurrentPropertyAsBoolean(10));
      this._clearCacheOnCloseField = new CheckboxField(BrowserResources.getString(500), getCurrentPropertyAsBoolean(3));
      this._fontFamilies = FontFamily.getFontFamilies();
      this._ptFontSizes = null;
      String value = getDefaultFontFamily();
      int fontIndex = 0;

      for (int i = 0; i < this._fontFamilies.length; i++) {
         if (StringUtilities.strEqualIgnoreCase(this._fontFamilies[i].getName(), value, 1701707776)) {
            fontIndex = i;
            break;
         }
      }

      this._defaultFontFamilyField = new ObjectChoiceField(BrowserResources.getString(700), this._fontFamilies, fontIndex);
      this._defaultFontFamilyField.setChangeListener(this);
      this._defaultFontSizeField = new ObjectChoiceField(BrowserResources.getString(701), null, 0, 134217728);
      this._minimumFontSizeField = new ObjectChoiceField(BrowserResources.getString(737), null, 0, 134217728);
      this.populateFontSizes();
      this._defaultFontSizeField.setChangeListener(this);
      this._minimumFontSizeField.setChangeListener(this);
      int index = 0;
      switch (getCurrentPropertyAsInt(37)) {
         case 1:
            index = 1;
            break;
         case 64:
            index = 2;
      }

      this._minimumFontStyleField = new ObjectChoiceField(BrowserResources.getString(738), BrowserResources.getStringArray(739), index);
      screen.add(this._defaultBrowserField);
      screen.add(this._defaultFontFamilyField);
      screen.add(this._defaultFontSizeField);
      screen.add(this._minimumFontSizeField);
      screen.add(this._minimumFontStyleField);
      if (Trackball.isSupported()) {
         screen.add(this._defaultViewField);
         screen.add(this._mobileCursorField);
      }

      screen.add(this._imageQualityField);
      screen.add(this._animationCountField);
      if (JavaScriptRegistry.isInstalled()) {
         screen.add(this._javascriptLocationEnabledField);
      }

      if (!super._restrictedAccess) {
         screen.add(new LabelField(""));
         screen.add(this._doNotCacheSecurePagesField);
         screen.add(this._clearCacheOnCloseField);
      }

      VerticalIndentFieldManager indentMgr = new VerticalIndentFieldManager();
      screen.add(indentMgr);
      int indentHeight = Font.getDefault().getHeight();
      indentMgr.add(new LabelField(BrowserResources.getString(681)));
      indentMgr.add(this._confirmQuitOnEscapeField, indentHeight);
      indentMgr.add(this._confirmLeaveModifiedPageField, indentHeight);
      if (WMLScript.isWMLScriptInstalled()) {
         indentMgr.add(this._confirmExecuteScriptsField, indentHeight);
      }

      return screen;
   }

   @Override
   public final Verb getVerbs(Verb[] verbs) {
      Array.resize(verbs, super._restrictedAccess ? 1 : 2);
      verbs[0] = new SavePropertyVerb(this);
      if (!super._restrictedAccess) {
         verbs[1] = new ClearVerb(538);
      }

      return verbs[0];
   }

   @Override
   public final void saveProperty() {
      BrowserConfigRecord record = this._defaultBrowserField.getSelectedBrowser();
      if (record != null) {
         String defaultConfigUID = record.getUid();
         setDefaultBrowserConfigServiceUID(defaultConfigUID);
         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         if (browser.isSingleBrowserMode()) {
            String initialConfigUID = browser.getInitialConfigUID();
            if (!StringUtilities.strEqualIgnoreCase(defaultConfigUID, initialConfigUID, 1701707776)) {
               browser.setInitialConfigUID(defaultConfigUID);
               browser.activateConfig(defaultConfigUID, true);
            }
         }
      }

      setCurrentProperty(0, this._confirmQuitOnEscapeField.getChecked());
      setCurrentProperty(24, this._confirmLeaveModifiedPageField.getChecked());
      setCurrentProperty(1, this._confirmExecuteScriptsField.getChecked());
      setCurrentProperty(3, this._clearCacheOnCloseField.getChecked());
      setCurrentProperty(23, this._javascriptLocationEnabledField.getChecked());
      setCurrentProperty(10, this._doNotCacheSecurePagesField.getChecked());
      setCurrentProperty(2, this._animationCountField.getSelectedIndex());
      setCurrentProperty(42, this._imageQualityField.getSelectedIndex());
      setCurrentProperty(43, this._defaultViewField.getSelectedIndex());
      setCurrentProperty(48, this._mobileCursorField.getChecked());
      if (this._defaultFontFamilyField.isDirty()) {
         setDefaultFontFamily(this._defaultFontFamilyField.getChoice(this._defaultFontFamilyField.getSelectedIndex()).toString());
      }

      setCurrentProperty(27, this._ptFontSizes[this._defaultFontSizeField.getSelectedIndex()]);
      setCurrentProperty(36, this._ptFontSizes[this._minimumFontSizeField.getSelectedIndex()]);
      setCurrentProperty(37, MINIMUM_FONT_STYLES[this._minimumFontStyleField.getSelectedIndex()]);
      notifyListeners();
   }

   public static final void setPreviousSimId(String id) {
      PersistentObject store = RIMPersistentStore.getPersistentObject(3464760717660140268L);
      store.setContents(id, 51);
      store.commit();
      _configData._changedOptions.set(20);
   }

   public static final boolean setDefaultCharsetValue(String encoding) {
      PersistentObject store = RIMPersistentStore.getPersistentObject(8288869949763761940L);
      store.setContents(encoding, 51);
      store.commit();
      _configData._changedOptions.set(13);
      return true;
   }

   public static final boolean setHomePageUrlOverrideValue(String uid, String homePage) {
      Hashtable table = getHomePageUrlOverrides();
      table.put(StringUtilities.toLowerCase(uid, 1701707776), homePage);
      PersistentObject.commit(table);
      _configData._changedOptions.set(15);
      return true;
   }

   public static final boolean removeHomePageUrlOverrideValue(String uid) {
      Hashtable table = getHomePageUrlOverrides();
      table.remove(StringUtilities.toLowerCase(uid, 1701707776));
      PersistentObject.commit(table);
      _configData._changedOptions.set(15);
      return true;
   }

   public static final boolean setOverrideProperty(int key, String uid, boolean value) {
      return setOverrideProperty(key, uid, value ? 1 : 0);
   }

   public static final boolean setOverrideProperty(int key, String uid, int showImages) {
      ToIntHashtable table = getOverridePropertiesAsInts(key);
      table.put(StringUtilities.toLowerCase(uid, 1701707776), showImages);
      PersistentObject.commit(table);
      _configData._changedOptions.set(key);
      if (key == 18) {
         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         browser.refreshAcceptValues();
      }

      return true;
   }

   public static final boolean removeOverrideProperty(int key, String uid) {
      ToIntHashtable table = getOverridePropertiesAsInts(key);
      table.remove(StringUtilities.toLowerCase(uid, 1701707776));
      PersistentObject.commit(table);
      _configData._changedOptions.set(key);
      if (key == 18) {
         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         browser.refreshAcceptValues();
      }

      return true;
   }

   public static final boolean clearPropertyOverrides(int key) {
      ToIntHashtable table = getOverridePropertiesAsInts(key);
      table.clear();
      PersistentObject.commit(table);
      _configData._changedOptions.set(key);
      if (key == 18) {
         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         browser.refreshAcceptValues();
      }

      return true;
   }

   public static final boolean setAuthenticationCredentialsValue(String realm, String[] credentials) {
      Hashtable table = getAuthenticationCredentials();
      table.put(realm, credentials);
      PersistentObject.commit(table);
      _configData._changedOptions.set(5);
      return true;
   }

   public static final boolean clearAuthenticationCredentials() {
      Hashtable table = getAuthenticationCredentials();
      table.clear();
      PersistentObject.commit(table);
      _configData._changedOptions.set(5);
      return true;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (context != Integer.MIN_VALUE) {
         if (field == this._defaultFontFamilyField) {
            this.populateFontSizes();
         } else {
            if (field == this._defaultFontSizeField) {
               int index = this._defaultFontSizeField.getSelectedIndex();
               if (index < this._minimumFontSizeField.getSelectedIndex()) {
                  this._minimumFontSizeField.setSelectedIndex(index);
                  return;
               }
            } else if (field == this._minimumFontSizeField) {
               int index = this._minimumFontSizeField.getSelectedIndex();
               if (index > this._defaultFontSizeField.getSelectedIndex()) {
                  this._defaultFontSizeField.setSelectedIndex(index);
               }
            }
         }
      }
   }

   private final void populateFontSizes() {
      FontFamily ff = this._fontFamilies[this._defaultFontFamilyField.getSelectedIndex()];
      int defaultSize = 0;
      int minimumSize = 0;
      if (this._ptFontSizes == null) {
         defaultSize = getCurrentPropertyAsInt(27);
         minimumSize = getCurrentPropertyAsInt(36);
      } else {
         int currentIndex = this._defaultFontSizeField.getSelectedIndex();
         defaultSize = this._ptFontSizes[currentIndex];
         currentIndex = this._minimumFontSizeField.getSelectedIndex();
         minimumSize = this._ptFontSizes[currentIndex];
      }

      int[] fontSizes = ff.getHeights();
      int numFontSizes = fontSizes.length;
      this._ptFontSizes = new Integer[numFontSizes];
      Arrays.sort(fontSizes, 0, fontSizes.length);
      int newDefaultIndex = 0;
      int newMinimumIndex = 0;
      int lastSize = -1;
      int count = 0;

      for (int i = 0; i < numFontSizes; i++) {
         int size = Ui.convertSize(fontSizes[i], 0, 3);
         if (lastSize != size && size >= 6 && size <= 14) {
            lastSize = size;
            if (defaultSize >= size) {
               newDefaultIndex = count;
            }

            if (minimumSize >= size) {
               newMinimumIndex = count;
            }

            this._ptFontSizes[count++] = new Integer(size);
         }
      }

      Array.resize(this._ptFontSizes, count);
      this._defaultFontSizeField.setChoices(this._ptFontSizes);
      this._defaultFontSizeField.setSelectedIndex(newDefaultIndex);
      this._minimumFontSizeField.setChoices(this._ptFontSizes);
      this._minimumFontSizeField.setSelectedIndex(newMinimumIndex);
   }

   public static final String determineDefaultBrowserConfigServiceUID() {
      String uid = null;
      uid = getPreferredBrowserConfigServiceUID();
      if (BrowserConfigRecord.isValidBrowserConfigRecord(uid)) {
         setPreferredBrowserConfigServiceUID(null);
         return uid;
      }

      uid = ITPolicy.getString(20);
      if (BrowserConfigRecord.isValidBrowserConfigRecord(uid)) {
         return uid;
      }

      BrowserConfigRecord[] records = BrowserConfigRecord.getValidBrowserConfigRecords();
      if (records.length > 0) {
         boolean publicMdsConfig = false;
         boolean wapConfig = false;

         for (int i = 0; i < records.length; i++) {
            BrowserConfigRecord browserConfig = records[i];
            if (browserConfig.getPropertyAsInt(7) == 0) {
               String configUid = browserConfig.getUid();
               String transportCid = browserConfig.getPropertyAsString(3);
               if (StringUtilities.strEqualIgnoreCase(transportCid, BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)) {
                  if (browserConfig.getPropertyAsInt(12) == 1) {
                     uid = configUid;
                     break;
                  }

                  if (!publicMdsConfig) {
                     uid = configUid;
                     publicMdsConfig = true;
                  }
               } else if (browserConfig.getPropertyAsInt(12) != 7
                  && !StringUtilities.strEqualIgnoreCase(WAPServiceRecord.SERVICE_CID, transportCid, 1701707776)) {
                  if (uid == null) {
                     uid = configUid;
                  }
               } else if (uid == null || !publicMdsConfig && (!wapConfig || StringUtilities.compareToIgnoreCase(configUid, uid, 1701707776) < 0)) {
                  uid = configUid;
                  wapConfig = true;
               }
            }
         }
      }

      return uid != null ? uid : "";
   }

   private static final String getPersistentValue(long key) {
      synchronized (_configData._persistentSync) {
         PersistentObject item = RIMPersistentStore.getPersistentObject(key);
         String configUID = (String)item.getContents();
         if (configUID == null) {
            configUID = "";
         }

         return configUID;
      }
   }

   public static final String getOrDetermineDefaultBrowserConfigServiceUID() {
      String configUid = getDefaultBrowserConfigServiceUID();
      if (configUid == null || configUid.length() == 0) {
         configUid = determineDefaultBrowserConfigServiceUID();
         setDefaultBrowserConfigServiceUID(configUid);
      }

      return configUid;
   }

   public static final String getDefaultBrowserConfigServiceUID() {
      return getPersistentValue(-5295635671561083971L);
   }

   public static final String getDefaultMdsBrowserConfigServiceUID() {
      return getPersistentValue(8711594917130453278L);
   }

   public static final String getDefaultWapBrowserConfigServiceUID() {
      return getPersistentValue(8807721636387799133L);
   }

   public static final String getDefaultWptcpBrowserConfigServiceUID() {
      return getPersistentValue(3887878695310535785L);
   }

   public static final String getPreferredBrowserConfigServiceUID() {
      return getPersistentValue(-6983330969469877897L);
   }

   public static final String getPreferredMdsBrowserConfigServiceUID() {
      return getPersistentValue(4864038587272185491L);
   }

   public static final String getPreferredWapBrowserConfigServiceUID() {
      return getPersistentValue(6597912215271916471L);
   }

   private static final void setPersistentValue(long key, String newUID) {
      synchronized (_configData._persistentSync) {
         PersistentObject item = RIMPersistentStore.getPersistentObject(key);
         item.setContents(newUID, 51);
         item.commit();
         _configData._changedOptions.set(28);
         notifyListeners();
      }
   }

   public static final void setDefaultMdsBrowserConfigServiceUID(String newUID) {
      synchronized (_configData._persistentSync) {
         RibbonManagerThread.setHotkey(getDefaultMdsBrowserConfigServiceUID(), '\u0000');
         setPersistentValue(8711594917130453278L, newUID);
         RibbonManagerThread.setHotkey(newUID, BrowserResources.getString(516).charAt(0));
      }
   }

   public static final void setDefaultWapBrowserConfigServiceUID(String newUID) {
      synchronized (_configData._persistentSync) {
         RibbonManagerThread.setHotkey(getDefaultWapBrowserConfigServiceUID(), '\u0000');
         setPersistentValue(8807721636387799133L, newUID);
         RibbonManagerThread.setHotkey(newUID, BrowserResources.getString(517).charAt(0));
      }
   }

   public static final void setDefaultWptcpBrowserConfigServiceUID(String newUID) {
      setPersistentValue(3887878695310535785L, newUID);
   }

   public static final void setDefaultBrowserConfigServiceUID(String newUID) {
      setPersistentValue(-5295635671561083971L, newUID);
   }

   public static final void setPreferredMdsBrowserConfigServiceUID(String newUID) {
      synchronized (_configData._persistentSync) {
         setPersistentValue(4864038587272185491L, newUID);
         if (BrowserConfigRecord.isValidBrowserConfigRecord(newUID)) {
            setDefaultMdsBrowserConfigServiceUID(newUID);
            setCurrentProperty(33, true);
         }
      }
   }

   public static final void setPreferredWapBrowserConfigServiceUID(String newUID) {
      synchronized (_configData._persistentSync) {
         setPersistentValue(6597912215271916471L, newUID);
         if (BrowserConfigRecord.isValidBrowserConfigRecord(newUID)) {
            setDefaultWapBrowserConfigServiceUID(newUID);
            setCurrentProperty(32, true);
         }
      }
   }

   public static final void setPreferredBrowserConfigServiceUID(String newUID) {
      synchronized (_configData._persistentSync) {
         setPersistentValue(-6983330969469877897L, newUID);
         if (BrowserConfigRecord.isValidBrowserConfigRecord(newUID)) {
            setDefaultBrowserConfigServiceUID(newUID);
         }
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _configData = (GeneralProperty$GeneralPropertyData)ar.getOrWaitFor(4417702636629415150L);
      if (_configData == null) {
         _configData = new GeneralProperty$GeneralPropertyData();
         ar.put(4417702636629415150L, _configData);
      }

      BACKUP_RESTORE_MASK = new BitSet();
      BACKUP_RESTORE_MASK.set(0);
      BACKUP_RESTORE_MASK.set(1);
      BACKUP_RESTORE_MASK.set(18);
      BACKUP_RESTORE_MASK.set(15);
      BACKUP_RESTORE_MASK.set(19);
      BACKUP_RESTORE_MASK.set(16);
      BACKUP_RESTORE_MASK.set(17);
      BACKUP_RESTORE_MASK.set(13);
      BACKUP_RESTORE_MASK.set(14);
      BACKUP_RESTORE_MASK.set(21);
      BACKUP_RESTORE_MASK.set(22);
      BACKUP_RESTORE_MASK.set(23);
      BACKUP_RESTORE_MASK.set(8);
      BACKUP_RESTORE_MASK.set(7);
      BACKUP_RESTORE_MASK.set(4);
      BACKUP_RESTORE_MASK.set(5);
      BACKUP_RESTORE_MASK.set(6);
      BACKUP_RESTORE_MASK.set(11);
      BACKUP_RESTORE_MASK.set(24);
      BACKUP_RESTORE_MASK.set(25);
      BACKUP_RESTORE_MASK.set(26);
      BACKUP_RESTORE_MASK.set(27);
      BACKUP_RESTORE_MASK.set(28);
      BACKUP_RESTORE_MASK.set(29);
      BACKUP_RESTORE_MASK.set(36);
      BACKUP_RESTORE_MASK.set(37);
      BACKUP_RESTORE_MASK.set(38);
      BACKUP_RESTORE_MASK.set(39);
      BACKUP_RESTORE_MASK.set(40);
      BACKUP_RESTORE_MASK.set(41);
      BACKUP_RESTORE_MASK.set(42);
      BACKUP_RESTORE_MASK.set(43);
      BACKUP_RESTORE_MASK.set(44);
      BACKUP_RESTORE_MASK.set(45);
      BACKUP_RESTORE_MASK.set(46);
      BACKUP_RESTORE_MASK.set(47);
      BACKUP_RESTORE_MASK.set(49);
      BACKUP_RESTORE_MASK.set(50);
      BACKUP_RESTORE_MASK.set(48);
      ALL_OPTIONS_SET_MASK = new BitSet();
      ALL_OPTIONS_SET_MASK.not();
   }
}
