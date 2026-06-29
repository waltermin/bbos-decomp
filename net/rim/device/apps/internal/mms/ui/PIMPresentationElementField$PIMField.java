package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SelfDrawingListField;
import net.rim.device.apps.api.ui.VerbMenuItem;

class PIMPresentationElementField$PIMField extends SelfDrawingListField {
   private String _unsupportedLabel;
   private Object _pimObject;
   private Verb _deleteVerb;
   private final PIMPresentationElementField this$0;

   PIMPresentationElementField$PIMField(PIMPresentationElementField _1, Object pimObject, String unsupportedLabel, Verb deleteVerb) {
      super(1, 27021597764222976L);
      this.this$0 = _1;
      this._unsupportedLabel = unsupportedLabel;
      this._pimObject = pimObject;
      this._deleteVerb = deleteVerb;
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (!(this._pimObject instanceof Object)) {
         String var7 = this._unsupportedLabel;
         graphics.drawText(var7, 0, y);
      } else {
         PaintProvider paintProvider = (PaintProvider)this._pimObject;
         paintProvider.paint(graphics, 0, y, width, this.getHeight(), this._unsupportedLabel);
      }
   }

   @Override
   public Object get(ListField listField, int index) {
      return this._pimObject;
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (this._pimObject instanceof Object) {
         VerbProvider verbProvider = (VerbProvider)this._pimObject;
         ContextObject context = (ContextObject)(new Object(45, 54));
         Verb[] verbs = new Object[0];
         Verb defaultVerb = verbProvider.getVerbs(context, verbs);
         if (verbs != null) {
            for (int idx = 0; idx < verbs.length; idx++) {
               Verb verb = verbs[idx];
               int priority;
               if (verb == defaultVerb) {
                  priority = 500;
               } else {
                  priority = 15;
               }

               VerbMenuItem verbMenuItem = (VerbMenuItem)(new Object(null, verb.getOrdering(), priority, verb, null));
               contextMenu.addItem(verbMenuItem);
               if (verb == defaultVerb) {
                  contextMenu.setDefaultItem(verbMenuItem);
               }
            }
         }

         if (this._deleteVerb != null) {
            VerbMenuItem verbMenuItem = (VerbMenuItem)(new Object(null, this._deleteVerb.getOrdering(), 15, this._deleteVerb, null));
            contextMenu.addItem(verbMenuItem);
         }
      }
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (this.this$0._moveMode) {
         MMSPresentationField.drawMoveFocus(graphics, this);
      } else {
         super.drawFocus(graphics, on);
      }
   }
}
