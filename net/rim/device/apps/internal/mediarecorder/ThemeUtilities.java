package net.rim.device.apps.internal.mediarecorder;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.ribbon.RibbonBanner;

public class ThemeUtilities {
   static final Tag TAG_RECORDER_SCREEN = Tag.create("recorder-screen");
   static final Tag TAG_RECORDER_RECORDBAR = Tag.create("recorder-recordbar");
   static final Tag TAG_RECORDER_CONTROLBAR = Tag.create("recorder-controlbar");
   static final Tag TAG_RECORDER_HINT = Tag.create("recorder-hint");
   public static final Tag TAG_RECORDER_BANNER = Tag.create("camera-banner");
   public static final Tag TAG_RECORDER_TITLE_BAR = Tag.get("media-library-banner");
   public static final Tag TAG_RECORDER_OPTION_SCREEN = Tag.get("media-library-screen");
   public static final Tag TAG_RECORDER_SELECTABLE_TEXT = Tag.get("media-options-text");
   public static final Tag TAG_RECORDER_HEADING = Tag.get("media-options-heading-text");

   private ThemeUtilities() {
   }

   public static Field getTitleField(String title) {
      Field banner = RibbonBanner.getInstance().getStatusBanner(title, 3);
      banner.setTag(TAG_RECORDER_TITLE_BAR);
      return banner;
   }
}
