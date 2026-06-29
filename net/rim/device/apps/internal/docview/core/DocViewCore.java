package net.rim.device.apps.internal.docview.core;

public final class DocViewCore {
   private UCSDocumentData _docData;
   public static final byte PARSE_SUCCESS;
   public static final byte PARSE_UNRECOGNIZEDINPUT;
   public static final byte PARSE_INCOMPLETEDATA;
   public static final byte PARSE_INTERRUPTEDBYUSER;
   public static final byte PARSE_INPROGRESS;
   public static final byte PARSE_IDLE;
   public static final byte PARSER_CONTINUE;
   public static final byte PARSER_PAUSE;
   public static final byte PARSER_STOP;

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
