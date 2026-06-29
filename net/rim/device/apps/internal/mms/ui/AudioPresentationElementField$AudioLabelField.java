package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.VerbMenuItem;

class AudioPresentationElementField$AudioLabelField extends LabelField {
   private Verb _deleteVerb;
   private final AudioPresentationElementField this$0;

   AudioPresentationElementField$AudioLabelField(AudioPresentationElementField _1, String label, Verb deleteVerb) {
      super(label, deleteVerb == null ? 36028797018963968L : 18014398509481984L);
      this.this$0 = _1;
      this._deleteVerb = deleteVerb;
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (this._deleteVerb != null) {
         VerbMenuItem verbMenuItem = (VerbMenuItem)(new Object(null, this._deleteVerb.getOrdering(), 15, this._deleteVerb, null));
         contextMenu.addItem(verbMenuItem);
      }
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (this.this$0.getFlag(2)) {
         MMSPresentationField.drawMoveFocus(graphics, this);
      } else {
         super.drawFocus(graphics, on);
      }
   }
}
