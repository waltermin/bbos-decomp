package net.rim.device.apps.internal.explorer.file.resource;

import net.rim.device.internal.ui.IconCollection;

public final class ExplorerIcons {
   public static final int FILE_NO_ATTRIBUTES = 0;
   public static final int FILE_ENCRYPTED = 1;
   public static final int FILE_REMOVABLE = 2;
   public static final int FILE_ENCRYPTED_REMOVABLE = 3;
   public static final int RIM_EXPLORE_FOLDER = 0;
   public static final int RIM_EXPLORE_UP_ARROW = 1;
   public static final int FORWARD_LOCKED_GLYPH = 0;
   public static final int REMOVABLE_GLYPH = 1;
   private static final IconCollection FOLDER_ICONS = IconCollection.get("net_rim_file_explorer-folder", 2, 4);
   private static final IconCollection GLYPHS = IconCollection.get("net_rim_file_explorer_glyph", 2);
   public static final int MEDIA_TYPE_COLUMNS = 5;

   private ExplorerIcons() {
   }

   public static final IconCollection getFolderIcon() {
      return FOLDER_ICONS;
   }

   public static final IconCollection getGlyphs() {
      return GLYPHS;
   }
}
