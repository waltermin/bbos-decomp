package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;

final class AbstractFilterScreen$FilterChoicesChangeListener implements FieldChangeListener {
   private BasicScreen _screen;
   private final AbstractFilterScreen this$0;

   public AbstractFilterScreen$FilterChoicesChangeListener(AbstractFilterScreen _1, BasicScreen screen) {
      this.this$0 = _1;
      this._screen = screen;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      String filterOperatorChoice = (String)this.this$0._filterOptions.getChoice(this.this$0._filterOptions.getSelectedIndex());
      String filterOperator = null;
      if (!filterOperatorChoice.equals(ApplicationResources.getString(62)) && !filterOperatorChoice.equals(ApplicationResources.getString(299))) {
         if (this.this$0._containsArea != null && this.this$0._containsArea.getManager() == null) {
            this._screen.insertAt(this.this$0._containsArea, this.this$0._containsIndex);
         }
      } else if (this.this$0._containsArea != null && this.this$0._containsArea.getManager() != null) {
         this.this$0._containsIndex = this._screen.deleteField(this.this$0._containsArea);
         return;
      }
   }
}
