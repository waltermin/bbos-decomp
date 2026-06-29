package net.rim.device.apps.internal.bis.launch.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.HorizontalFieldManager;

final class TitleBar$TitleAndHelpManager extends HorizontalFieldManager {
   private Field _titleField;
   private Field _helpField;

   public TitleBar$TitleAndHelpManager() {
      super(1153062242095202304L);
   }

   public final void setTitleField(Field title) {
      this._titleField = title;
      this.add(this._titleField);
   }

   @Override
   protected final void sublayout(int width, int height) {
      int helpFieldWidth = 0;
      if (this._helpField != null) {
         this.layoutChild(this._helpField, Display.getWidth(), Display.getHeight());
         helpFieldWidth = this._helpField.getExtent().width;
         this.setPositionChild(this._helpField, width - helpFieldWidth, 0);
         this.layoutChild(this._helpField, helpFieldWidth, height);
      }

      int titleFieldHeight = 0;
      if (this._titleField != null) {
         this.layoutChild(this._titleField, width - helpFieldWidth, height);
         titleFieldHeight = this._titleField.getExtent().height;
         this.setPositionChild(this._titleField, 0, 0);
      }

      this.setExtent(width, titleFieldHeight);
   }
}
