package com.sun.cldc.i18n.j2me;

final class ConversionDataRegistryHelper$EncodingMappingData {
   final int _id;
   int _locale;
   String _typeface;
   final String _encoding;
   byte[][] _binaryData;
   int _encodingDataOffset;
   int _encodingDataLength;

   ConversionDataRegistryHelper$EncodingMappingData(String encoding, int id, int locale, String typeface) {
      this._id = id;
      this._locale = locale;
      this._typeface = typeface;
      this._encoding = encoding;
   }
}
