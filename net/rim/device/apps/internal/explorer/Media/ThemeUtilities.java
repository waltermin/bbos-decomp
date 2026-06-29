package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.ribbon.RibbonBanner;

public final class ThemeUtilities {
   public static final Tag MEDIA_HUB_TAG = Tag.create("media-hub");
   public static final Tag BANNER_TAG = Tag.create("media-library-banner");
   public static final Tag LIST_TAG = Tag.create("media-list");
   public static final Tag SCREEN_TAG = Tag.create("media-library-screen");
   public static final Tag TITLE_TAG = Tag.create("media-track-title");
   public static final Tag TEXT_TAG = Tag.create("media-property");
   public static final Tag SELECTABLE_TEXT_TAG = Tag.create("media-options-text");
   public static final Tag HEADING_TEXT_TAG = Tag.create("media-options-heading-text");
   public static final Tag FIND_TAG = Tag.create("media-find");
   public static final Tag ALBUM_TITLE_TAG = Tag.create("media-library-album-title");
   public static final Tag SCROLL_BAR_TAG = Tag.create("media-library-scrollbar");

   private ThemeUtilities() {
   }

   public static final Field getTitleField(String title) {
      Field banner = RibbonBanner.getInstance().getStatusBanner(title, 3);
      banner.setTag(BANNER_TAG);
      return banner;
   }
}
