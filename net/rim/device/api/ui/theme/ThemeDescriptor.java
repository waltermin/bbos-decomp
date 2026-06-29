package net.rim.device.api.ui.theme;

import java.util.Hashtable;

class ThemeDescriptor {
   private String _name;
   private int _appIconWidth;
   private int _appIconHeight;
   private Hashtable _compatibleThemes = new Hashtable();
   private static Hashtable _valuesByName = new Hashtable();
   public static final ThemeDescriptor BLACKBERRY = new ThemeDescriptor("BlackBerry", OSVersion.OS_VERSION_4_1, 36, 36);
   public static final ThemeDescriptor BLACKBERRY_240X260 = new ThemeDescriptor("Blackberry_240x260", OSVersion.OS_VERSION_4_1, 51, 51);
   public static final ThemeDescriptor BLACKBERRY_ICON_240X260 = new ThemeDescriptor("Blackberry_icon_240x260", OSVersion.OS_VERSION_4_1, 60, 56);
   public static final ThemeDescriptor BLACKBERRY_320X240 = new ThemeDescriptor("Blackberry_320x240", OSVersion.OS_VERSION_4_1, 64, 48);
   public static final ThemeDescriptor BB_INSIGHT_LIST_320X240 = new ThemeDescriptor("Bb_insight_list_320x240", OSVersion.OS_VERSION_4_2, 51, 51);
   public static final ThemeDescriptor BB_INSIGHT_ICON_320X240 = new ThemeDescriptor("Bb_insight_icon_320x240", OSVersion.OS_VERSION_4_2, 53, 49);
   public static final ThemeDescriptor BB_DIMENSION_ICON_240X260 = new ThemeDescriptor("Bb_dimension_icon_240x260", OSVersion.OS_VERSION_4_2, 60, 55);
   public static final ThemeDescriptor BB_DIMENSION_ZEN_240X260 = new ThemeDescriptor("Bb_dimension_zen_240x260", OSVersion.OS_VERSION_4_2, 48, 36);
   public static final ThemeDescriptor BB_DIMENSION_TODAY_240X260 = new ThemeDescriptor("Bb_dimension_today_240x260", OSVersion.OS_VERSION_4_2, 48, 36);
   public static final ThemeDescriptor BB_DIMENSION_LIST_320X240 = new ThemeDescriptor("Bb_dimension_list_320x240", OSVersion.OS_VERSION_4_2, 51, 51);
   public static final ThemeDescriptor BB_DIMENSION_ZEN_320X240 = new ThemeDescriptor("Bb_dimension_zen_320x240", OSVersion.OS_VERSION_4_2, 48, 36);
   public static final ThemeDescriptor BB_DIMENSION_TODAY_320X240 = new ThemeDescriptor("Bb_dimension_today_320x240", OSVersion.OS_VERSION_4_2, 48, 36);
   public static final ThemeDescriptor BB_DIMENSION_ICON_240X260_W = new ThemeDescriptor("Bbdimension_icon_240x260_w", OSVersion.OS_VERSION_4_2_1, 60, 55);
   public static final ThemeDescriptor BB_DIMENSION_ZEN_240X260_W = new ThemeDescriptor("Bbdimension_zen_240x260_w", OSVersion.OS_VERSION_4_2_1, 48, 36);
   public static final ThemeDescriptor BB_DIMENSION_ICON_240X260_B = new ThemeDescriptor("Bbdimension_icon_240x260_b", OSVersion.OS_VERSION_4_2_1, 60, 55);
   public static final ThemeDescriptor BB_DIMENSION_ZEN_240X260_B = new ThemeDescriptor("Bbdimension_zen_240x260_b", OSVersion.OS_VERSION_4_2_1, 48, 36);
   public static final ThemeDescriptor BB_DIMENSION_ICON_320X240_W = new ThemeDescriptor("Bbdimension_icon_320x240_w", OSVersion.OS_VERSION_4_2_1, 53, 48);
   public static final ThemeDescriptor BB_DIMENSION_ICON_320X240_B = new ThemeDescriptor("Bbdimension_icon_320x240_b", OSVersion.OS_VERSION_4_2_1, 53, 48);

   private ThemeDescriptor(String name, OSVersion osVersion, int appIconWidth, int appIconHeight) {
      this._name = name;
      this._appIconWidth = appIconWidth;
      this._appIconHeight = appIconHeight;
      this.putCompatibleTheme(osVersion, this);
      _valuesByName.put(name.toLowerCase(), this);
   }

   public static ThemeDescriptor forNameIgnoreCase(String name) {
      return (ThemeDescriptor)_valuesByName.get(name.toLowerCase());
   }

   public String getName() {
      return this._name;
   }

   public int getApplicationIconWidth() {
      return this._appIconWidth;
   }

   public int getApplicationIconHeight() {
      return this._appIconHeight;
   }

   public ThemeDescriptor getCompatibleTheme(OSVersion osVersion) {
      Object value = this._compatibleThemes.get(osVersion);
      return !(value instanceof ThemeDescriptor$DeviceSwitch) ? (ThemeDescriptor)value : ((ThemeDescriptor$DeviceSwitch)value).getThemeDescriptor();
   }

   private ThemeDescriptor putCompatibleTheme(OSVersion osVersion, ThemeDescriptor compatibleTheme) {
      this._compatibleThemes.put(osVersion, compatibleTheme);
      return this;
   }

   private ThemeDescriptor putCompatibleTheme(OSVersion osVersion, ThemeDescriptor trackWheelTheme, ThemeDescriptor rollerBallTheme) {
      this._compatibleThemes.put(osVersion, new ThemeDescriptor$DeviceSwitch(trackWheelTheme, rollerBallTheme));
      return this;
   }

   static {
      BLACKBERRY_ICON_240X260.putCompatibleTheme(OSVersion.OS_VERSION_4_2, BB_DIMENSION_ICON_240X260)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_2_1, BB_DIMENSION_ICON_240X260)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_2_2, BB_DIMENSION_ICON_240X260)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_2_3, BB_DIMENSION_ICON_240X260);
      BB_DIMENSION_ICON_240X260_B.putCompatibleTheme(OSVersion.OS_VERSION_4_2_1, BB_DIMENSION_ICON_240X260_W, BB_DIMENSION_ICON_240X260_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_2_2, BB_DIMENSION_ICON_240X260_W, BB_DIMENSION_ICON_240X260_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_2_3, BB_DIMENSION_ICON_240X260_W, BB_DIMENSION_ICON_240X260_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_3, BB_DIMENSION_ICON_240X260_W, BB_DIMENSION_ICON_240X260_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_3_1, BB_DIMENSION_ICON_240X260_W, BB_DIMENSION_ICON_240X260_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_3_2, BB_DIMENSION_ICON_240X260_W, BB_DIMENSION_ICON_240X260_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_3_3, BB_DIMENSION_ICON_240X260_W, BB_DIMENSION_ICON_240X260_B);
      BB_DIMENSION_ZEN_240X260_B.putCompatibleTheme(OSVersion.OS_VERSION_4_2_1, BB_DIMENSION_ZEN_240X260_W, BB_DIMENSION_ZEN_240X260_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_2_2, BB_DIMENSION_ZEN_240X260_W, BB_DIMENSION_ZEN_240X260_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_2_3, BB_DIMENSION_ZEN_240X260_W, BB_DIMENSION_ZEN_240X260_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_3, BB_DIMENSION_ZEN_240X260_W, BB_DIMENSION_ZEN_240X260_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_3_1, BB_DIMENSION_ZEN_240X260_W, BB_DIMENSION_ZEN_240X260_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_3_2, BB_DIMENSION_ZEN_240X260_W, BB_DIMENSION_ZEN_240X260_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_3_3, BB_DIMENSION_ZEN_240X260_W, BB_DIMENSION_ZEN_240X260_B);
      BLACKBERRY_320X240.putCompatibleTheme(OSVersion.OS_VERSION_4_2, BLACKBERRY_320X240)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_2_1, BB_DIMENSION_ICON_320X240_W, BB_DIMENSION_ICON_320X240_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_2_2, BB_DIMENSION_ICON_320X240_W, BB_DIMENSION_ICON_320X240_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_2_3, BB_DIMENSION_ICON_320X240_W, BB_DIMENSION_ICON_320X240_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_3, BB_DIMENSION_ICON_320X240_W, BB_DIMENSION_ICON_320X240_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_3_1, BB_DIMENSION_ICON_320X240_W, BB_DIMENSION_ICON_320X240_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_3_2, BB_DIMENSION_ICON_320X240_W, BB_DIMENSION_ICON_320X240_B)
         .putCompatibleTheme(OSVersion.OS_VERSION_4_3_3, BB_DIMENSION_ICON_320X240_W, BB_DIMENSION_ICON_320X240_B);
   }
}
