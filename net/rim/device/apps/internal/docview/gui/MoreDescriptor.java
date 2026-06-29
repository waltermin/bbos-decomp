package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.XYRect;

final class MoreDescriptor {
   String _commandCode;
   int _partIndex = -2;
   String _embeddedomID;
   String _arbDomID;
   boolean _isImageRequest;
   int _reqImageWidth;
   int _reqImageHeight;
   int _targetBlockIndex = -1;
   int _chunkHint = -1;
   int _chunkSize = -1;
   XYRect _rectToEnlarge;
   String _findPattern;
   String _findBlockList;
   boolean _findCaseSensitive;
   boolean _findNext;
   int _latestBookmarkRequestByRemoteLink = -1;
   int _latestChunkRequestByRemoteLink = -1;

   final void reset() {
      this._commandCode = this._embeddedomID = this._arbDomID = null;
      this._partIndex = -2;
      this._isImageRequest = false;
      this._reqImageWidth = this._reqImageHeight = 0;
      this._targetBlockIndex = this._chunkHint = -1;
      this._chunkSize = -1;
      this._rectToEnlarge = null;
      this._latestBookmarkRequestByRemoteLink = this._latestChunkRequestByRemoteLink = -1;
      this._findPattern = this._findBlockList = null;
      this._findCaseSensitive = this._findNext = false;
   }
}
