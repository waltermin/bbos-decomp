package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.mms.resources.MMSResources;

class VideoPresentationElementField$VideoButtonField extends ButtonField {
   private Verb _deleteVerb;
   private final VideoPresentationElementField this$0;

   VideoPresentationElementField$VideoButtonField(VideoPresentationElementField _1, Verb deleteVerb) {
      this.this$0 = _1;
      this.setAction();
      this._deleteVerb = deleteVerb;
   }

   void setAction() {
      String label = MMSResources.getString(97);
      if (label != null) {
         Application app = this.getApplication();
         if (app != null) {
            synchronized (app.getAppEventLock()) {
               this.setLabel(label);
               return;
            }
         }

         this.setLabel(label);
      }
   }

   private Application getApplication() {
      try {
         return this.getScreen().getApplication();
      } finally {
         ;
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      this.performAction();
      return true;
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.performAction();
            return true;
         default:
            return super.invokeAction(action);
      }
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
   protected void fieldChangeNotify(int context) {
      super.fieldChangeNotify(context);
      this.performAction();
   }

   private void performAction() {
      this.this$0.startTune();
   }
}
