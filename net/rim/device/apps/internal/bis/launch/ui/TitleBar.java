package net.rim.device.apps.internal.bis.launch.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;

final class TitleBar extends VerticalFieldManager {
   private LabelField _title;
   private TitleBar$TitleAndHelpManager _titleAndHelpManager;
   private static final int TITLEBAR_FONT_SIZE_PT = 7;

   public TitleBar(String title) {
      this._title = (LabelField)(new Object(title, 4294967360L));
      this._titleAndHelpManager = new TitleBar$TitleAndHelpManager();
      this._titleAndHelpManager.setTitleField(this._title);
      this.add(this._titleAndHelpManager);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      this.setFont(Font.getDefault().derive(1, 7, 3));
   }

   public final void setTitle(String title) {
      this._title.setText(title);
   }
}
