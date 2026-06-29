package net.rim.device.apps.internal.docview.gui;

public class DocViewAtomicID {
   public int _partIndex = -2;
   public int _totalBlocks = -1;
   public byte _srcType = -1;
   public String _domID;
   public byte _docType = -1;
   public byte _docSubtype = -1;
   public byte _embObjType = -1;
   public byte _embObjSubtype = -1;
   public String _arbDOMID;
   public int _arbDOMIDStartBlock = -1;
   public int _arbDOMIDEndBlock = -1;

   public DocViewAtomicID() {
   }

   public void set(DocViewAtomicID otherID) {
      this._partIndex = otherID._partIndex;
      this._totalBlocks = otherID._totalBlocks;
      this._srcType = otherID._srcType;
      this._domID = otherID._domID;
      this._docType = otherID._docType;
      this._docSubtype = otherID._docSubtype;
      this._embObjType = otherID._embObjType;
      this._embObjSubtype = otherID._docSubtype;
      this._arbDOMID = otherID._arbDOMID;
      this._arbDOMIDStartBlock = otherID._arbDOMIDStartBlock;
      this._arbDOMIDEndBlock = otherID._arbDOMIDEndBlock;
   }

   public DocViewAtomicID(DocViewAtomicID otherID) {
      this.set(otherID);
   }
}
