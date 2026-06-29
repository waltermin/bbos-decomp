package net.rim.device.internal.EScreens;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

class EScreenSessionManager$MyMenuItem extends MenuItem {
   private int _id;
   private Object _context;
   private final EScreenSessionManager this$0;

   EScreenSessionManager$MyMenuItem(EScreenSessionManager _1, int id, String text, Object context) {
      super(text, _1.getMenuOrdinal(id), _1.getMenuPriority(id));
      this.this$0 = _1;
      this._id = id;
      this._context = context;
   }

   @Override
   public void run() {
      switch (this._id) {
         case 1:
         default:
            this.this$0._listField.invalidate();
            return;
         case 2:
            ((EScreenSession)this._context).stop();
            return;
         case 3:
            this.this$0.removeSession(this._context);
            this.this$0._listField.setSize(this.this$0._sessions.size());
            return;
         case 4:
            String className = (String)this._context;

            try {
               EScreenSession s = (EScreenSession)Class.forName(className).newInstance();
               s.init(this.this$0, this.this$0._sessions.size());
               s.setFont(this.this$0.getFont());
               this.this$0._sessions.addElement(s);
               UiApplication.getUiApplication().pushScreen(s);
               this.this$0._listField.setSize(this.this$0._sessions.size());
               return;
            } finally {
               Dialog.alert("Unable to newInstance session");
               return;
            }
         case 5:
            if (this._context instanceof Object) {
               Integer i = (Integer)this._context;
               EScreenSession s = (EScreenSession)this.this$0._sessions.elementAt(i);
               UiApplication.getUiApplication().pushScreen(s);
            }
         case 0:
      }
   }
}
