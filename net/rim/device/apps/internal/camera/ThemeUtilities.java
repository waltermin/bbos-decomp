package net.rim.device.apps.internal.camera;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.ribbon.RibbonBanner;

public final class ThemeUtilities {
   static final Tag TAG_CAMERA_CAPTURE = Tag.create("camera-capture");
   static final Tag TAG_CAMERA_PREVIEW = Tag.create("camera-preview");
   static final Tag TAG_CAMERA_HINT = Tag.create("camera-hint");
   static final Tag TAG_CAMERA_BANNER = Tag.create("camera-banner");
   static final Tag TAG_CAMERA_TITLE_BAR = Tag.get("media-library-banner");
   static final Tag TAG_CAMERA_SCREEN = Tag.get("media-library-screen");
   static final Tag TAG_CAMERA_SELECTABLE_TEXT = Tag.get("media-options-text");
   static final Tag TAG_CAMERA_HEADING = Tag.get("media-options-heading-text");

   private ThemeUtilities() {
   }

   public static final Field getTitleField(String title) {
      Field banner = RibbonBanner.getInstance().getStatusBanner(title, 3);
      banner.setTag(TAG_CAMERA_TITLE_BAR);
      return banner;
   }
}
