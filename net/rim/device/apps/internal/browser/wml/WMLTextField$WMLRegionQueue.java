package net.rim.device.apps.internal.browser.wml;

import net.rim.device.apps.internal.browser.ui.BrowserTextField$BrowserRegionQueue;
import net.rim.vm.Array;

public final class WMLTextField$WMLRegionQueue extends BrowserTextField$BrowserRegionQueue {
   protected WMLAnchorVerb[] _anchorVerbs;

   WMLTextField$WMLRegionQueue(int initMaxRegions, int initMaxFonts) {
      super(initMaxRegions, initMaxFonts);
   }

   @Override
   protected final void resizeBuffers(int newCapacity) {
      super.resizeBuffers(newCapacity);
      if (newCapacity == 0) {
         this._anchorVerbs = null;
      } else if (this._anchorVerbs == null) {
         this._anchorVerbs = new WMLAnchorVerb[newCapacity];
      } else {
         Array.resize(this._anchorVerbs, newCapacity);
      }
   }

   final boolean appendRegion(int offset, byte attribute, long id, WMLAnchorVerb anchorVerb) {
      int index = super._numregions;
      long[] ids = super.cookieID == null ? null : (long[])super.cookieID.get(index - 1);
      if (index > 0 && attribute == super.attributes[index - 1] && ids != null && id == ids[0] && anchorVerb == this._anchorVerbs[index - 1]) {
         super.offsets[index - 1] = offset;
         return true;
      }

      if (!super.appendRegion(offset, attribute, id)) {
         return false;
      }

      this._anchorVerbs[index] = anchorVerb;
      return true;
   }
}
