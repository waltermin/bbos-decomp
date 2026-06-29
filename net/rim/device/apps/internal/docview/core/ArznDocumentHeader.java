package net.rim.device.apps.internal.docview.core;

public final class ArznDocumentHeader {
   public byte[] _abyUCSVersion = new byte[2];
   public byte _byDocType = -1;

   public ArznDocumentHeader() {
      this._abyUCSVersion[0] = this._abyUCSVersion[1] = 0;
   }
}
