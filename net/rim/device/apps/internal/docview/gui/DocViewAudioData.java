package net.rim.device.apps.internal.docview.gui;

import net.rim.device.apps.internal.docview.core.ArznAudioData;

public final class DocViewAudioData extends DocViewObjectData implements ArznAudioData {
   private byte[] _header;
   private int _riffSize;

   public final int getRiffSize() {
      return this._riffSize;
   }

   public final byte[] getAudioHeader() {
      return this._header;
   }

   public final byte[] getAudioContents() {
      return this.getContents();
   }

   @Override
   public final void addContents(byte[] rawAudio) {
      super.addData(rawAudio);
   }

   @Override
   public final void addRiffSize(int size) {
      this._riffSize = size;
   }

   @Override
   public final void addAudioHeader(byte[] header) {
      this._header = header;
   }

   DocViewAudioData() {
   }
}
