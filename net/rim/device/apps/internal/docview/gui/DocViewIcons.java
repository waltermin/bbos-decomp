package net.rim.device.apps.internal.docview.gui;

import net.rim.device.internal.ui.IconCollection;

public final class DocViewIcons {
   public static final int RIM_DOCVIEW_ARCHIVE_COLLAPSED;
   public static final int RIM_DOCVIEW_ARCHIVE_EXPANDED;
   private static final int RIM_DOCVIEW_DOCICONS;
   public static final int RIM_DOCVIEW_WORD;
   public static final int RIM_DOCVIEW_SHEET;
   public static final int RIM_DOCVIEW_PPT;
   public static final int RIM_DOCVIEW_PDF;
   public static final int RIM_DOCVIEW_WP;
   public static final int RIM_DOCVIEW_TEXT;
   public static final int RIM_DOCVIEW_HTML;
   public static final int RIM_DOCVIEW_IMAGE;
   public static final int RIM_DOCVIEW_AUDIO;
   private static final int ICON_COLUMNS;
   private static final int ICON_ROWS;
   private static final IconCollection _icons = IconCollection.get("net_rim_DocView", 9, 2);

   private DocViewIcons() {
   }

   public static final IconCollection getIcons() {
      return _icons;
   }
}
