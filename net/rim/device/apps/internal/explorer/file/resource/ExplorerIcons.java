package net.rim.device.apps.internal.explorer.file.resource;

import net.rim.device.internal.ui.IconCollection;

public final class ExplorerIcons {
   public static final int FILE_NO_ATTRIBUTES;
   public static final int FILE_ENCRYPTED;
   public static final int FILE_REMOVABLE;
   public static final int FILE_ENCRYPTED_REMOVABLE;
   public static final int RIM_EXPLORE_FOLDER;
   public static final int RIM_EXPLORE_UP_ARROW;
   public static final int FORWARD_LOCKED_GLYPH;
   public static final int REMOVABLE_GLYPH;
   private static final IconCollection FOLDER_ICONS = IconCollection.get("net_rim_file_explorer-folder", 2, 4);
   private static final IconCollection GLYPHS = IconCollection.get("net_rim_file_explorer_glyph", 2);
   public static final int MEDIA_TYPE_COLUMNS;

   private ExplorerIcons() {
   }

   public static final IconCollection getFolderIcon() {
      return FOLDER_ICONS;
   }

   public static final IconCollection getGlyphs() {
      return GLYPHS;
   }
}
