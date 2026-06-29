package net.rim.device.apps.internal.docview.gui;

public class DocViewHyperlinkInfo {
   public int _linkStartOffset = -1;
   public int _linkEndOffset = -1;
   public byte _linkType = -1;
   public static final byte DOCVIEW_INVALIDLINK = -1;
   public static final byte DOCVIEW_TEXTLINK = 0;
   public static final byte DOCVIEW_SHEETLINK = 1;
   public static final byte DOCVIEW_EXTERNALLINK = 2;
   public static final byte DOCVIEW_NOTELINK = 3;
   public static final byte DOCVIEW_COMMENTLINK = 4;
   public static final byte DOCVIEW_TEXTBOXLINK = 5;
   public static final byte DOCVIEW_IMAGELINK = 6;

   protected DocViewHyperlinkInfo() {
   }

   DocViewHyperlinkInfo cloneObject() {
      throw new Object("Base class cannot be cloned");
   }

   public boolean identicalAndSuccesive(DocViewHyperlinkInfo otherLink, int tolerance) {
      return false;
   }
}
