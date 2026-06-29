package net.rim.device.apps.internal.docview.core;

public interface ArznSheetData {
   void beforeCellParsing(int var1, int var2, boolean var3);

   void afterCellParsing();

   void setTableBgColor(int var1);

   void setTableRowBgColor(int var1, int var2);

   void setTableColBgColor(int var1, int var2);

   void setRowHidden(int var1);

   void setColumnHidden(int var1);

   void setCellBgColor(int var1, int var2, int var3);

   void beforeParagraphParsing(boolean var1);

   void addParagraphInfo(byte var1, int var2, int var3, int var4);

   void addTextContent(StringBuffer var1, ArznRichTextInfo var2, boolean var3);

   void addInternalHyperlink(int var1);

   void addExternalHyperlink(StringBuffer var1);

   void afterParagraphParsing(boolean var1, boolean var2);

   void addBookmarkInfo(int var1);

   void markLastHyperlinkElementEndOffset();

   void addTrackChange(String var1, String var2);
}
