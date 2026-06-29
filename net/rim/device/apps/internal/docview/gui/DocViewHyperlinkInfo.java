package net.rim.device.apps.internal.docview.gui;

public class DocViewHyperlinkInfo {
   public int _linkStartOffset = -1;
   public int _linkEndOffset = -1;
   public byte _linkType = -1;
   public static final byte DOCVIEW_INVALIDLINK;
   public static final byte DOCVIEW_TEXTLINK;
   public static final byte DOCVIEW_SHEETLINK;
   public static final byte DOCVIEW_EXTERNALLINK;
   public static final byte DOCVIEW_NOTELINK;
   public static final byte DOCVIEW_COMMENTLINK;
   public static final byte DOCVIEW_TEXTBOXLINK;
   public static final byte DOCVIEW_IMAGELINK;

   protected DocViewHyperlinkInfo() {
   }

   DocViewHyperlinkInfo cloneObject() {
      throw new Object("Base class cannot be cloned");
   }

   public boolean identicalAndSuccesive(DocViewHyperlinkInfo otherLink, int tolerance) {
      return false;
   }
}
