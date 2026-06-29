package net.rim.device.api.ui;

class FontRegistry$FontInfo {
   int _index;
   int _count;
   boolean _isPublic;
   int _codFileHandle;
   int _renderingEngineHandle;
   String _typefaceName;

   FontRegistry$FontInfo(int aIndex, int aCount, boolean aIsPublic, int aCodFileHandle, int aRenderingEngineHandle, String aTypefaceName) {
      this._index = aIndex;
      this._count = aCount;
      this._isPublic = aIsPublic;
      this._codFileHandle = aCodFileHandle;
      this._renderingEngineHandle = aRenderingEngineHandle;
      this._typefaceName = aTypefaceName;
   }
}
