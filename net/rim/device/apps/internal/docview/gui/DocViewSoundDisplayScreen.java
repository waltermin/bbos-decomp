package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.util.IntHashtable;

final class DocViewSoundDisplayScreen extends DocViewDisplayScreen {
   DocViewSoundDisplayScreen(IntHashtable paramsHash) {
      super(paramsHash);
      super._displayField = new DocViewSoundDisplayField(this, this, null, super._docData);
   }

   @Override
   protected final boolean hasAutoLoad() {
      return false;
   }
}
