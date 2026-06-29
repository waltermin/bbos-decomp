package net.rim.device.apps.internal.docview.core;

final class ArznAudioContainer extends ArznObject {
   ArznAudioData _audioData;

   @Override
   final void parse(ArznParsingInfo parsingData) {
      this._audioData = this.addAudio(parsingData);
      super.parse(parsingData);
   }

   @Override
   final void initialize(UCSParser docParser, ArznClassFactory classFactory, ArznObject parent) {
      super.initialize(docParser, classFactory, parent);
      this._audioData = null;
   }

   @Override
   protected final int parseContainerData(ArznParsingInfo parsingData) {
      int offset = 0;
      switch (super._ucsParser.getCurrentCommandCode()) {
         case 85:
            if (this._audioData != null) {
               byte[] header = new byte[super._ucsParser.getCurrentCommandSize()];
               if (header != null) {
                  super._ucsParser.readByteArray(header);
                  offset += header.length;
                  this._audioData.addAudioHeader(header);
                  Object var6 = null;
                  return offset;
               }
            }
            break;
         case 87:
            if (this._audioData != null) {
               int riffSize = super._ucsParser.readUnsignedInt();
               offset += 4;
               this._audioData.addRiffSize(riffSize);
            }
      }

      return offset;
   }

   @Override
   protected final void addAudioContents(byte[] rawAudio) {
      if (this._audioData != null) {
         this._audioData.addContents(rawAudio);
      }
   }
}
