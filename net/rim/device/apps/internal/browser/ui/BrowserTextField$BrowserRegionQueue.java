package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.component.ActiveRichTextField$RegionQueue;
import net.rim.vm.Array;

public class BrowserTextField$BrowserRegionQueue extends ActiveRichTextField$RegionQueue {
   public TextFieldLink[] _links;

   public BrowserTextField$BrowserRegionQueue(int initMaxRegions, int initMaxFonts) {
   }

   @Override
   protected void resizeBuffers(int newCapacity) {
      super.resizeBuffers(newCapacity);
      if (newCapacity == 0) {
         this._links = null;
      } else if (this._links == null) {
         this._links = new TextFieldLink[newCapacity];
      } else {
         Array.resize(this._links, newCapacity);
      }
   }

   public boolean appendRegion(int offset, byte attribute, long id, TextFieldLink link) {
      int index = super._numregions;
      if (!super.appendRegion(offset, attribute, id)) {
         return false;
      }

      this._links[index] = link;
      return true;
   }
}
