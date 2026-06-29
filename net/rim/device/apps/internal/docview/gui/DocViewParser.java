package net.rim.device.apps.internal.docview.gui;

import java.io.InputStream;
import net.rim.device.apps.internal.docview.core.DocViewCore;
import net.rim.device.apps.internal.docview.core.DocViewInputStream;

public final class DocViewParser {
   private final DocViewCore _coreLib;
   private final DocViewParsingData _parsingData;

   public DocViewParser(boolean trackChangesOn) {
      this._parsingData = new DocViewParsingData(trackChangesOn);
      this._coreLib = new DocViewCore(this._parsingData);
   }

   public final DocViewParsingData getParsingData() {
      return this._parsingData;
   }

   public final synchronized void parseDocument(InputStream in, boolean pausable) {
      this.parseDocument(new DocViewInputStreamImpl(in), true, 0, pausable, true);
      synchronized (in) {
         in.notifyAll();
      }
   }

   public final synchronized void parseDocument(byte[] ucsData, boolean newDocument, int chunkIndex, boolean pausable, boolean isLastChunk) {
      this.parseDocument(new DocViewInputStreamImpl((InputStream)(new Object(ucsData))), newDocument, chunkIndex, pausable, true);
   }

   private final void parseDocument(DocViewInputStream data, boolean newDocument, int chunkIndex, boolean pausable, boolean isLastChunk) {
      this._parsingData.reset(newDocument, chunkIndex, pausable, isLastChunk);
      this._coreLib.parseDocument(data);
      this._parsingData.endParsing(this.getLastParsingStatus());
   }

   public final int getCurrentParsePercentage() {
      return this._coreLib.getCurrentParsePercentage();
   }

   public final byte getLastParsingStatus() {
      return this._coreLib.getLastParsingStatus();
   }

   public final void resumeParsing() {
      this._parsingData.resetForContinue();
      this._coreLib.resumeParsing();
   }

   public final void stopParsing() {
      this._parsingData.setStopFlag((byte)2);
      this._coreLib.resumeParsing();
   }

   public final int getMoreAvailableBytes() {
      return this._coreLib.getMoreAvailableBytes();
   }
}
