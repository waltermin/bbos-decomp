package net.rim.device.apps.internal.docview.gui;

import net.rim.device.internal.ui.IconCollection;

public final class DocViewIcons {
   public static final int RIM_DOCVIEW_ARCHIVE_COLLAPSED = 0;
   public static final int RIM_DOCVIEW_ARCHIVE_EXPANDED = 1;
   private static final int RIM_DOCVIEW_DOCICONS = 65536;
   public static final int RIM_DOCVIEW_WORD = 65536;
   public static final int RIM_DOCVIEW_SHEET = 65537;
   public static final int RIM_DOCVIEW_PPT = 65538;
   public static final int RIM_DOCVIEW_PDF = 65539;
   public static final int RIM_DOCVIEW_WP = 65540;
   public static final int RIM_DOCVIEW_TEXT = 65541;
   public static final int RIM_DOCVIEW_HTML = 65542;
   public static final int RIM_DOCVIEW_IMAGE = 65543;
   public static final int RIM_DOCVIEW_AUDIO = 65544;
   private static final int ICON_COLUMNS = 9;
   private static final int ICON_ROWS = 2;
   private static final IconCollection _icons = IconCollection.get("net_rim_DocView", 9, 2);

   private DocViewIcons() {
   }

   public static final IconCollection getIcons() {
      return _icons;
   }
}
