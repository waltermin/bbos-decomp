package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.XYRect;

public final class ClientRequest {
   public String _commandCode;
   public int _partIndex = -2;
   public String _domID;
   public String _nextDOMID;
   public String _arbDOMID;
   public String _password;
   public String _archiveIndicator;
   public int _blockIndex = -1;
   public int _chunkHint = -1;
   public byte _srcType = -1;
   public String _findPattern;
   public String _findTargetChunks;
   public boolean _findCaseSensitive;
   public boolean _findNext;
   public int _chunkSize = -1;
   public boolean _ftr;
   public boolean _imageRequest;
   public XYRect _areaToEnlarge;
   public int _imageWidth;
   public int _imageHeight;
   public int _reqChunkSizeForImages = -1;
   public int _serverAudioCodec = -1;

   public ClientRequest() {
   }

   public ClientRequest(
      String commandCode,
      int partIndex,
      String domID,
      String nextDOMID,
      String arbDOMID,
      int chunkHint,
      String pwd,
      int blockIndex,
      byte srcType,
      String archiveIndicator,
      String findPattern,
      String findTargetChunks,
      boolean findSearchSensitive,
      boolean findNext,
      int chunkSize,
      boolean ftr,
      boolean imageRequest,
      XYRect areaToEnlarge,
      int imageWidth,
      int imageHeight,
      int reqChunkSizeForImages,
      int serverAudioCodec
   ) {
      this._commandCode = commandCode;
      this._partIndex = partIndex;
      this._domID = domID;
      this._nextDOMID = nextDOMID;
      this._arbDOMID = arbDOMID;
      this._chunkHint = chunkHint;
      this._password = pwd;
      this._blockIndex = blockIndex;
      this._srcType = srcType;
      this._archiveIndicator = archiveIndicator;
      this._findPattern = findPattern;
      this._findTargetChunks = findTargetChunks;
      this._findCaseSensitive = findSearchSensitive;
      this._findNext = findNext;
      this._chunkSize = chunkSize;
      this._ftr = ftr;
      this._imageRequest = imageRequest;
      this._areaToEnlarge = areaToEnlarge;
      this._imageWidth = imageWidth;
      this._imageHeight = imageHeight;
      this._reqChunkSizeForImages = reqChunkSizeForImages;
      this._serverAudioCodec = serverAudioCodec;
   }

   public ClientRequest(ClientRequest copyRequest) {
      this.setRequest(copyRequest);
   }

   public final void setRequest(ClientRequest request) {
      this._commandCode = request._commandCode;
      this._partIndex = request._partIndex;
      this._domID = request._domID;
      this._nextDOMID = request._nextDOMID;
      this._arbDOMID = request._arbDOMID;
      this._chunkHint = request._chunkHint;
      this._password = request._password;
      this._blockIndex = request._blockIndex;
      this._srcType = request._srcType;
      this._archiveIndicator = request._archiveIndicator;
      this._findPattern = request._findPattern;
      this._findTargetChunks = request._findTargetChunks;
      this._findCaseSensitive = request._findCaseSensitive;
      this._findNext = request._findNext;
      this._chunkSize = request._chunkSize;
      this._ftr = request._ftr;
      this._imageRequest = request._imageRequest;
      this._areaToEnlarge = request._areaToEnlarge;
      this._imageWidth = request._imageWidth;
      this._imageHeight = request._imageHeight;
      this._reqChunkSizeForImages = request._reqChunkSizeForImages;
      this._serverAudioCodec = request._serverAudioCodec;
   }

   final void setCustomRequest(MoreDescriptor descriptor) {
      if (descriptor._commandCode != null) {
         this._commandCode = descriptor._commandCode;
      }

      if (descriptor._partIndex != -2) {
         this._partIndex = descriptor._partIndex;
      }

      if (descriptor._embeddedomID != null) {
         this._domID = descriptor._embeddedomID;
         this._srcType = 1;
         this._arbDOMID = null;
      }

      if (descriptor._arbDomID != null) {
         this._arbDOMID = descriptor._arbDomID;
      }

      this._blockIndex = descriptor._targetBlockIndex;
      this._chunkHint = descriptor._chunkHint;
      if (descriptor._isImageRequest || descriptor._rectToEnlarge != null) {
         this._imageRequest = true;
         this._imageWidth = descriptor._reqImageWidth > 0 ? descriptor._reqImageWidth : DocViewGUIInternalConstants.SCREEN_WIDTH;
         this._imageHeight = descriptor._reqImageHeight > 0 ? descriptor._reqImageHeight : DocViewGUIInternalConstants.SCREEN_HEIGHT;
         this._areaToEnlarge = descriptor._rectToEnlarge;
      }

      this._chunkSize = descriptor._chunkSize;
      if (descriptor._findPattern != null) {
         this._findPattern = descriptor._findPattern;
         this._findTargetChunks = descriptor._findBlockList;
         this._findCaseSensitive = descriptor._findCaseSensitive;
         this._findNext = descriptor._findNext;
      }
   }
}
