package net.rim.device.api.browser.field;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.LongHashtable;

public final class RenderingOptions {
   private LongHashtable _guidTable = (LongHashtable)(new Object());
   public static final int VALUE_THRESHOLD;
   public static final long CORE_OPTIONS_GUID;
   public static final long IMAGE_OPTIONS_GUID;
   public static final long MULTIMEDIA_OPTIONS_GUID;
   public static final int OVERWRITE_CHARSET_MODE;
   public static final boolean OVERWRITE_CHARSET_MODE_DEFAULT;
   public static final int JAVASCRIPT_ENABLED;
   public static final boolean JAVASCRIPT_ENABLED_DEFAULT;
   public static final int USE_FOREGROUND_BACKGROUND_COLOR;
   public static final boolean USE_FOREGROUND_BACKGROUND_COLOR_DEFAULT = Graphics.isColor();
   public static final int USE_BACKGROUND_IMAGES;
   public static final boolean USE_BACKGROUND_IMAGES_DEFAULT = Graphics.isColor();
   public static final int SHOW_IMAGES_IN_HTML;
   public static final boolean SHOW_IMAGES_IN_HTML_DEFAULT;
   public static final int SHOW_IMAGES_IN_WML;
   public static final boolean SHOW_IMAGES_IN_WML_DEFAULT;
   public static final int SHOW_IMAGE_PLACEHOLDERS;
   public static final boolean SHOW_IMAGE_PLACEHOLDERS_DEFAULT;
   public static final int DEFAULT_CHARSET_VALUE;
   public static final String DEFAULT_CHARSET_VALUE_DEFAULT;
   public static final int CONFIRM_WMLSCRIPT_EXECUTION;
   public static final boolean CONFIRM_WMLSCRIPT_EXECUTION_DEFAULT;
   public static final int SECONDARY_URLS_PER_REQUEST;
   public static final int SECONDARY_URLS_PER_REQUEST_DEFAULT;
   public static final int SECONDARY_URLS_PER_MORE_REQUEST_ALL;
   public static final int SECONDARY_URLS_PER_MORE_REQUEST_ALL_DEFAULT;
   public static final int ANIMATION_COUNT_VALUE;
   public static final int ANIMATION_COUNT_VALUE_DEFAULT;
   public static final int ALLOW_POPUPS;
   public static final boolean ALLOW_POPUPS_DEFAULT;
   public static final int MAX_INPUT_CHARS;
   public static final int MAX_INPUT_CHARS_DEFAULT;
   public static final int HOME_PAGE_URL;
   public static final String HOME_PAGE_URL_DEFAULT = null;
   public static final int JAVASCRIPT_LOCATION_ENABLED;
   public static final boolean JAVASCRIPT_LOCATION_ENABLED_DEFAULT;
   public static final int SHOW_TABLES_IN_HTML;
   public static final boolean SHOW_TABLES_IN_HTML_DEFAULT;
   public static final int ENABLE_CSS;
   public static final boolean ENABLE_CSS_DEFAULT;
   public static final int CSS_MEDIA_TYPE;
   public static final String CSS_MEDIA_TYPE_DEFAULT;
   public static final int ENABLE_EMBEDDED_RICH_CONTENT;
   public static final boolean ENABLE_EMBEDDED_RICH_CONTENT_DEFAULT;
   public static final int ENABLE_WML;
   public static final boolean ENABLE_WML_DEFAULT;
   public static final int ENABLE_HTML;
   public static final boolean ENABLE_HTML_DEFAULT;
   public static final int APP_VERSION;
   public static final String APP_VERSION_DEFAULT = null;
   public static final int USER_AGENT;
   public static final String USER_AGENT_DEFAULT = null;
   public static final int WAP_MODE;
   public static final boolean WAP_MODE_DEFAULT;
   public static final int ENABLE_IMAGE_SAVING;
   public static final boolean ENABLE_IMAGE_SAVING_DEFAULT;
   public static final int ENABLE_AUDIO_SAVING;
   public static final boolean ENABLE_AUDIO_SAVING_DEFAULT;
   public static final int AUTO_SAVE_DRM_CONTENT;
   public static final int UAPROF_URI;
   public static final boolean AUTO_SAVE_DRM_CONTENT_DEFAULT;
   public static final int ADD_IMAGE_ADDRESS_MENU_ITEM;
   public static final boolean ADD_IMAGE_ADDRESS_MENU_ITEM_DEFAULT;
   public static final int DEFAULT_FONT_FACE;
   public static final String DEFAULT_FONT_FACE_DEFAULT;
   public static final int DEFAULT_FONT_SIZE;
   public static final int DEFAULT_FONT_SIZE_DEFAULT;
   public static final int HMAC;
   public static final int ADD_LINK_ADDRESS_MENU_ITEM;
   public static final boolean ADD_LINK_ADDRESS_MENU_ITEM_DEFAULT;
   public static final int MINIMUM_FONT_SIZE;
   public static final int MINIMUM_FONT_SIZE_DEFAULT;
   public static final int MINIMUM_FONT_STYLE;
   public static final int MINIMUM_FONT_STYLE_DEFAULT;
   public static final int PROMPT_ENABLE_JAVASCRIPT;
   public static final boolean PROMPT_ENABLE_JAVASCRIPT_DEFAULT;
   public static final int ADD_FULL_IMAGE_MENU_ITEM;
   public static final boolean ADD_FULL_IMAGE_MENU_ITEM_DEFAULT;
   public static final int MINIMAL_MENU_MODE;
   public static final boolean MINIMAL_MENU_MODE_DEFAULT;
   public static final int ENABLE_FILE_BASED_MENU_ITEMS;
   public static final boolean ENABLE_FILE_BASED_MENU_ITEMS_DEFAULT;
   public static final int IMAGE_OPTION_ENABLE_HOTKEYS;
   public static final boolean IMAGE_OPTION_ENABLE_HOTKEYS_DEFAULT;
   public static final int ENABLE_IMAGE_EDITING;
   public static final boolean ENABLE_IMAGE_EDITING_DEFAULT;
   public static final int RESET_JAVASCRIPT_OPTION_FOR_NEXT_SITE;
   public static final boolean RESET_JAVASCRIPT_OPTION_FOR_NEXT_SITE_DEFAULT;
   public static final int IMAGE_QUALITY_VALUE;
   public static final int IMAGE_QUALITY_VALUE_LOW;
   public static final int IMAGE_QUALITY_VALUE_MEDIUM;
   public static final int IMAGE_QUALITY_VALUE_HIGH;
   public static final int IMAGE_QUALITY_VALUE_DEFAULT;
   public static final int VIEW_MODE;
   public static final int VIEW_MODE_VALUE_MOBILE;
   public static final int VIEW_MODE_VALUE_DESKTOP;
   public static final int VIEW_MODE_VALUE_AUTO;
   public static final int VIEW_MODE_VALUE_DEFAULT;
   public static final int RESET_VIEW_MODE_OPTION_FOR_NEXT_SITE;
   public static final boolean RESET_VIEW_MODE_OPTION_FOR_NEXT_SITE_DEFAULT;
   public static final int JAVASCRIPT_TIMEOUT;
   public static final int JAVASCRIPT_TIMEOUT_DEFAULT;
   public static final int CURSOR_IN_MOBILE_VIEW;
   public static final boolean CURSOR_IN_MOBILE_VIEW_DEFAULT;
   public static final int USE_CONTENT_LOCATION_AS_BASE_URI;
   public static final boolean USE_CONTENT_LOCATION_AS_BASE_URI_DEFAULT;
   public static final int MULTIMEDIA_AUDIO_OPTION_SOURCE;
   public static final int MULTIMEDIA_AUDIO_OPTION_SOURCE_DEFAULT;
   public static final int MULTIMEDIA_OPTIONS_AUTOBOOKMARK;
   public static final int MULTIMEDIA_OPTIONS_TRACK_INDEX;
   public static final int MULTIMEDIA_OPTIONS_NO_ADVANCE_TRACK;
   public static final int MULTIMEDIA_OPTIONS_MEDIA_PLAYER_APP;
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

      if (!(object instanceof Object)) {
         if (guid == 4550690918222697397L) {
            switch (propertyId) {
               case 15:
               case 24:
                  object = PersistentContent.decodeString(object);
                  if (object instanceof Object) {
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
