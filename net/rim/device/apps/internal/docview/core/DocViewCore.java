package net.rim.device.apps.internal.docview.core;

public final class DocViewCore {
   private UCSDocumentData _docData;
   public static final byte PARSE_SUCCESS = 0;
   public static final byte PARSE_UNRECOGNIZEDINPUT = 1;
   public static final byte PARSE_INCOMPLETEDATA = 2;
   public static final byte PARSE_INTERRUPTEDBYUSER = 3;
   public static final byte PARSE_INPROGRESS = 4;
   public static final byte PARSE_IDLE = 5;
   public static final byte PARSER_CONTINUE = 0;
   public static final byte PARSER_PAUSE = 1;
   public static final byte PARSER_STOP = 2;

   public DocViewCore(ArznParseCallback parseItf) {
      this._docData = new UCSDocumentData(parseItf);
   }

   public final synchronized void parseDocument(DocViewInputStream ucsData) {
      this._docData.parseDocument(ucsData);
   }

   public final int getCurrentParsePercentage() {
      return this._docData.getCurrentParsePercentage();
   }

   public final byte getLastParsingStatus() {
      return this._docData._parseStatus;
   }

   public final void resumeParsing() {
      this._docData.resumeParsing();
   }

   public final int getMoreAvailableBytes() {
      return this._docData.getMoreAvailableBytes();
   }
}
