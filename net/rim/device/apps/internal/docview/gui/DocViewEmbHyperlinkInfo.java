package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.api.util.StringPatternEnumerator;
import net.rim.device.api.util.StringPatternRepository$Internal;
import net.rim.device.apps.api.framework.model.ContextObject;

public final class DocViewEmbHyperlinkInfo extends DocViewHyperlinkInfo implements CookieProvider {
   public String _linkTargetString;
   private StringPattern$Match _match;
   private ContextObject _invokeContext;
   private static final StringPatternContainer _patterns = StringPatternRepository$Internal.getStringPatterns();

   DocViewEmbHyperlinkInfo() {
   }

   @Override
   final DocViewHyperlinkInfo cloneObject() {
      DocViewEmbHyperlinkInfo cloneObj = new DocViewEmbHyperlinkInfo();
      cloneObj._linkTargetString = this._linkTargetString;
      cloneObj._linkType = super._linkType;
      cloneObj._linkStartOffset = super._linkStartOffset;
      cloneObj._linkEndOffset = super._linkEndOffset;
      return cloneObj;
   }

   @Override
   public final boolean identicalAndSuccesive(DocViewHyperlinkInfo otherLink, int tolerance) {
      if (!(otherLink instanceof DocViewEmbHyperlinkInfo)) {
         return false;
      }

      DocViewEmbHyperlinkInfo otherEmbLink = (DocViewEmbHyperlinkInfo)otherLink;
      return super._linkType == otherEmbLink._linkType
         && super._linkEndOffset + 1 + tolerance >= otherEmbLink._linkStartOffset
         && this._linkTargetString != null
         && otherEmbLink._linkTargetString != null
         && this._linkTargetString.compareTo(otherEmbLink._linkTargetString) == 0;
   }

   @Override
   public final Object getCookieWithFocus() {
      Object cookie = null;
      if (this._linkTargetString != null) {
         StringPatternEnumerator patternEnum = (StringPatternEnumerator)(new Object(this._linkTargetString, _patterns));
         if (patternEnum.hasMoreMatches()) {
            if (this._match == null) {
               this._match = (StringPattern$Match)(new Object());
            }

            patternEnum.nextMatch(this._match);
            if (this._invokeContext == null) {
               this._invokeContext = (ContextObject)(new Object());
            }

            this._invokeContext.put(253, this._linkTargetString.substring(this._match.beginIndex, this._match.endIndex));
            cookie = FactoryUtil.createInstance(this._match.id, this._invokeContext);
         }
      }

      return cookie;
   }
}
