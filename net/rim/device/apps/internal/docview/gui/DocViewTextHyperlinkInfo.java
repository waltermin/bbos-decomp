package net.rim.device.apps.internal.docview.gui;

public final class DocViewTextHyperlinkInfo extends DocViewHyperlinkInfo {
   public int _iLinkTargetBookmark = -1;
   public int _iLinkTargetChunkHint = -1;

   DocViewTextHyperlinkInfo() {
      super._linkType = 0;
   }

   @Override
   final DocViewHyperlinkInfo cloneObject() {
      DocViewTextHyperlinkInfo cloneObj = new DocViewTextHyperlinkInfo();
      cloneObj._iLinkTargetBookmark = this._iLinkTargetBookmark;
      cloneObj._iLinkTargetChunkHint = this._iLinkTargetChunkHint;
      cloneObj._linkType = super._linkType;
      cloneObj._linkStartOffset = super._linkStartOffset;
      cloneObj._linkEndOffset = super._linkEndOffset;
      return cloneObj;
   }

   @Override
   public final boolean identicalAndSuccesive(DocViewHyperlinkInfo otherLink, int tolerance) {
      if (!(otherLink instanceof DocViewTextHyperlinkInfo)) {
         return false;
      }

      DocViewTextHyperlinkInfo otherTextLink = (DocViewTextHyperlinkInfo)otherLink;
      return this._iLinkTargetBookmark != -1
         && this._iLinkTargetBookmark == otherTextLink._iLinkTargetBookmark
         && super._linkEndOffset + 1 + tolerance >= otherTextLink._linkStartOffset;
   }
}
