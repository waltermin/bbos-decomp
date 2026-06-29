package net.rim.device.api.browser.field;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.LongHashtable;

public final class RenderingOptions {
   private LongHashtable _guidTable = new LongHashtable();
   public static final int VALUE_THRESHOLD = 32000;
   public static final long CORE_OPTIONS_GUID = 4550690918222697397L;
   public static final long IMAGE_OPTIONS_GUID = -2413443615265356506L;
   public static final long MULTIMEDIA_OPTIONS_GUID = 9094571315961484757L;
   public static final int OVERWRITE_CHARSET_MODE = 1;
   public static final boolean OVERWRITE_CHARSET_MODE_DEFAULT = false;
   public static final int JAVASCRIPT_ENABLED = 2;
   public static final boolean JAVASCRIPT_ENABLED_DEFAULT = false;
   public static final int USE_FOREGROUND_BACKGROUND_COLOR = 3;
   public static final boolean USE_FOREGROUND_BACKGROUND_COLOR_DEFAULT = Graphics.isColor();
   public static final int USE_BACKGROUND_IMAGES = 4;
   public static final boolean USE_BACKGROUND_IMAGES_DEFAULT = Graphics.isColor();
   public static final int SHOW_IMAGES_IN_HTML = 5;
   public static final boolean SHOW_IMAGES_IN_HTML_DEFAULT = true;
   public static final int SHOW_IMAGES_IN_WML = 6;
   public static final boolean SHOW_IMAGES_IN_WML_DEFAULT = true;
   public static final int SHOW_IMAGE_PLACEHOLDERS = 7;
   public static final boolean SHOW_IMAGE_PLACEHOLDERS_DEFAULT = true;
   public static final int DEFAULT_CHARSET_VALUE = 8;
   public static final String DEFAULT_CHARSET_VALUE_DEFAULT = "us-ascii";
   public static final int CONFIRM_WMLSCRIPT_EXECUTION = 9;
   public static final boolean CONFIRM_WMLSCRIPT_EXECUTION_DEFAULT = false;
   public static final int SECONDARY_URLS_PER_REQUEST = 10;
   public static final int SECONDARY_URLS_PER_REQUEST_DEFAULT = 5;
   public static final int SECONDARY_URLS_PER_MORE_REQUEST_ALL = 11;
   public static final int SECONDARY_URLS_PER_MORE_REQUEST_ALL_DEFAULT = 10;
   public static final int ANIMATION_COUNT_VALUE = 12;
   public static final int ANIMATION_COUNT_VALUE_DEFAULT = 10;
   public static final int ALLOW_POPUPS = 13;
   public static final boolean ALLOW_POPUPS_DEFAULT = false;
   public static final int MAX_INPUT_CHARS = 14;
   public static final int MAX_INPUT_CHARS_DEFAULT = 512;
   public static final int HOME_PAGE_URL = 15;
   public static final String HOME_PAGE_URL_DEFAULT = null;
   public static final int JAVASCRIPT_LOCATION_ENABLED = 16;
   public static final boolean JAVASCRIPT_LOCATION_ENABLED_DEFAULT = false;
   public static final int SHOW_TABLES_IN_HTML = 17;
   public static final boolean SHOW_TABLES_IN_HTML_DEFAULT = true;
   public static final int ENABLE_CSS = 18;
   public static final boolean ENABLE_CSS_DEFAULT = true;
   public static final int CSS_MEDIA_TYPE = 19;
   public static final String CSS_MEDIA_TYPE_DEFAULT = "handheld";
   public static final int ENABLE_EMBEDDED_RICH_CONTENT = 20;
   public static final boolean ENABLE_EMBEDDED_RICH_CONTENT_DEFAULT = true;
   public static final int ENABLE_WML = 21;
   public static final boolean ENABLE_WML_DEFAULT = true;
   public static final int ENABLE_HTML = 22;
   public static final boolean ENABLE_HTML_DEFAULT = true;
   public static final int APP_VERSION = 23;
   public static final String APP_VERSION_DEFAULT = null;
   public static final int USER_AGENT = 24;
   public static final String USER_AGENT_DEFAULT = null;
   public static final int WAP_MODE = 25;
   public static final boolean WAP_MODE_DEFAULT = false;
   public static final int ENABLE_IMAGE_SAVING = 26;
   public static final boolean ENABLE_IMAGE_SAVING_DEFAULT = true;
   public static final int ENABLE_AUDIO_SAVING = 27;
   public static final boolean ENABLE_AUDIO_SAVING_DEFAULT = true;
   public static final int AUTO_SAVE_DRM_CONTENT = 28;
   public static final int UAPROF_URI = 29;
   public static final boolean AUTO_SAVE_DRM_CONTENT_DEFAULT = false;
   public static final int ADD_IMAGE_ADDRESS_MENU_ITEM = 30;
   public static final boolean ADD_IMAGE_ADDRESS_MENU_ITEM_DEFAULT = false;
   public static final int DEFAULT_FONT_FACE = 31;
   public static final String DEFAULT_FONT_FACE_DEFAULT = "BBMillbank";
   public static final int DEFAULT_FONT_SIZE = 32;
   public static final int DEFAULT_FONT_SIZE_DEFAULT = 8;
   public static final int HMAC = 33;
   public static final int ADD_LINK_ADDRESS_MENU_ITEM = 34;
   public static final boolean ADD_LINK_ADDRESS_MENU_ITEM_DEFAULT = false;
   public static final int MINIMUM_FONT_SIZE = 35;
   public static final int MINIMUM_FONT_SIZE_DEFAULT = 6;
   public static final int MINIMUM_FONT_STYLE = 36;
   public static final int MINIMUM_FONT_STYLE_DEFAULT = 0;
   public static final int PROMPT_ENABLE_JAVASCRIPT = 37;
   public static final boolean PROMPT_ENABLE_JAVASCRIPT_DEFAULT = true;
   public static final int ADD_FULL_IMAGE_MENU_ITEM = 38;
   public static final boolean ADD_FULL_IMAGE_MENU_ITEM_DEFAULT = false;
   public static final int MINIMAL_MENU_MODE = 39;
   public static final boolean MINIMAL_MENU_MODE_DEFAULT = false;
   public static final int ENABLE_FILE_BASED_MENU_ITEMS = 40;
   public static final boolean ENABLE_FILE_BASED_MENU_ITEMS_DEFAULT = true;
   public static final int IMAGE_OPTION_ENABLE_HOTKEYS = 1;
   public static final boolean IMAGE_OPTION_ENABLE_HOTKEYS_DEFAULT = false;
   public static final int ENABLE_IMAGE_EDITING = 41;
   public static final boolean ENABLE_IMAGE_EDITING_DEFAULT = true;
   public static final int RESET_JAVASCRIPT_OPTION_FOR_NEXT_SITE = 42;
   public static final boolean RESET_JAVASCRIPT_OPTION_FOR_NEXT_SITE_DEFAULT = false;
   public static final int IMAGE_QUALITY_VALUE = 43;
   public static final int IMAGE_QUALITY_VALUE_LOW = 0;
   public static final int IMAGE_QUALITY_VALUE_MEDIUM = 1;
   public static final int IMAGE_QUALITY_VALUE_HIGH = 2;
   public static final int IMAGE_QUALITY_VALUE_DEFAULT = 1;
   public static final int VIEW_MODE = 44;
   public static final int VIEW_MODE_VALUE_MOBILE = 0;
   public static final int VIEW_MODE_VALUE_DESKTOP = 1;
   public static final int VIEW_MODE_VALUE_AUTO = 2;
   public static final int VIEW_MODE_VALUE_DEFAULT = 0;
   public static final int RESET_VIEW_MODE_OPTION_FOR_NEXT_SITE = 45;
   public static final boolean RESET_VIEW_MODE_OPTION_FOR_NEXT_SITE_DEFAULT = false;
   public static final int JAVASCRIPT_TIMEOUT = 46;
   public static final int JAVASCRIPT_TIMEOUT_DEFAULT = 0;
   public static final int CURSOR_IN_MOBILE_VIEW = 47;
   public static final boolean CURSOR_IN_MOBILE_VIEW_DEFAULT = false;
   public static final int USE_CONTENT_LOCATION_AS_BASE_URI = 49;
   public static final boolean USE_CONTENT_LOCATION_AS_BASE_URI_DEFAULT = false;
   public static final int MULTIMEDIA_AUDIO_OPTION_SOURCE = 1;
   public static final int MULTIMEDIA_AUDIO_OPTION_SOURCE_DEFAULT = -1;
   public static final int MULTIMEDIA_OPTIONS_AUTOBOOKMARK = 2;
   public static final int MULTIMEDIA_OPTIONS_TRACK_INDEX = 3;
   public static final int MULTIMEDIA_OPTIONS_NO_ADVANCE_TRACK = 4;
   public static final int MULTIMEDIA_OPTIONS_MEDIA_PLAYER_APP = 5;
   public static final Object DEFAULT_HMAC = new byte[0];
   private static final Object NULL = new Object();

   protected RenderingOptions() {
   }

   private final RenderingOptions$GUIDSettings getOrCreateTable(long guid) {
      synchronized (this._guidTable) {
         RenderingOptions$GUIDSettings settings = (RenderingOptions$GUIDSettings)this._guidTable.get(guid);
         if (settings == null) {
            settings = new RenderingOptions$GUIDSettings();
            this._guidTable.put(guid, settings);
         }

         return settings;
      }
   }

   private final RenderingOptions$GUIDSettings getTable(long guid) {
      return (RenderingOptions$GUIDSettings)this._guidTable.get(guid);
   }

   public final boolean getPropertyWithBooleanValue(long guid, int propertyId, boolean defaultValue) {
      RenderingOptions$GUIDSettings table = this.getTable(guid);
      if (table == null) {
         return defaultValue;
      } else {
         return !table._intIntHashtable.containsKey(propertyId) ? defaultValue : table._intIntHashtable.get(propertyId) == 1;
      }
   }

   public final int getPropertyWithIntValue(long guid, int propertyId, int defaultValue) {
      RenderingOptions$GUIDSettings table = this.getTable(guid);
      if (table == null) {
         return defaultValue;
      } else {
         return !table._intIntHashtable.containsKey(propertyId) ? defaultValue : table._intIntHashtable.get(propertyId);
      }
   }

   public final String getPropertyWithStringValue(long guid, int propertyId, String defaultValue) {
      RenderingOptions$GUIDSettings table = this.getTable(guid);
      if (table == null) {
         return defaultValue;
      }

      Object object = table._intHashtable.get(propertyId);
      if (object == null) {
         return defaultValue;
      }

      if (object == NULL) {
         return null;
      }

      if (!(object instanceof String)) {
         if (guid == 4550690918222697397L) {
            switch (propertyId) {
               case 15:
               case 24:
                  object = PersistentContent.decodeString(object);
                  if (object instanceof String) {
                     return (String)object;
                  }
            }
         }

         return defaultValue;
      } else {
         return (String)object;
      }
   }

   public final Object getPropertyWithObjectValue(long guid, int propertyId, Object defaultValue) {
      RenderingOptions$GUIDSettings table = this.getTable(guid);
      if (table == null) {
         return defaultValue;
      } else {
         Object object = table._intHashtable.get(propertyId);
         if (object == null) {
            return defaultValue;
         } else {
            return object == NULL ? null : object;
         }
      }
   }

   public final void setProperty(long guid, int propertyId, boolean value) {
      this.getOrCreateTable(guid)._intIntHashtable.put(propertyId, value ? 1 : 2);
   }

   public final void setProperty(long guid, int propertyId, int value) {
      this.getOrCreateTable(guid)._intIntHashtable.put(propertyId, value);
   }

   public final void setProperty(long guid, int propertyId, String value) {
      Object obj = value;
      if (guid == 4550690918222697397L) {
         switch (propertyId) {
            case 15:
            case 24:
               obj = PersistentContent.encode(value);
         }
      }

      this.getOrCreateTable(guid)._intHashtable.put(propertyId, value == null ? NULL : obj);
   }

   public final void setProperty(long guid, int propertyId, Object value) {
      this.getOrCreateTable(guid)._intHashtable.put(propertyId, value == null ? NULL : value);
   }
}
