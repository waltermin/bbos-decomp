package net.rim.device.apps.internal.docview.core;

public interface ArznParseCallback {
   void addTextContent(ArznRichTextInfo var1, StringBuffer var2, boolean var3, boolean var4);

   void addInternalHyperlink(int var1, boolean var2);

   void addExternalHyperlink(StringBuffer var1, boolean var2);

   void markLastHyperlinkElementEndOffset(boolean var1);

   void setLastHyperlinkElementChunkID(int var1, boolean var2);

   void addBookmarkInfo(int var1);

   void addDocumentHeader(ArznDocumentHeader var1);

   void addChunkHint(int var1);

   void addDOMIDHint(String var1);

   void addSummaryDOMID(String var1);

   void addDocInfoValue(int var1, String var2);

   void beforePageParsing();

   void afterPageParsing(int var1);

   EmbeddedHint addEmbeddedObjectHint(byte var1);

   void parsedEmbeddedObjectHint(EmbeddedHint var1);

   void addTrackChange(String var1, String var2, boolean var3);

   ArznImageData addImage();

   ArznAudioData addAudio();

   ArznSheetData addSpreadsheet(int var1, int var2, String var3, boolean var4);

   void parsedEmbeddedObjectHint(ArznSheetData var1, EmbeddedHint var2);

   void beforeParagraphParsing(boolean var1);

   void addParagraphInfo(byte var1, int var2, int var3, int var4, boolean var5);

   void addFontSizes(short[] var1);

   void afterParagraphParsing(boolean var1, boolean var2, boolean var3);

   void setPageCount(int var1);

   byte getStopFlag();

   void endCommandCodeParsing(byte var1);
}
