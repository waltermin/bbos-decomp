package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.theme.ThemeManager;

final class MailboxList$HeaderField extends LabelField {
   private final MailboxList this$0;

   public MailboxList$HeaderField(MailboxList _1, String text) {
      super(text, 1152921504606846976L);
      this.this$0 = _1;
   }

   @Override
   public final void paint(Graphics graphics) {
      int width = this.getScreen().getWidth();
      int themeGeneration = ThemeManager.getGeneration();
      if (themeGeneration != this.this$0._themeGeneration) {
         this.this$0._themeGeneration = themeGeneration;
         this.this$0._themeAttributesHeader = ThemeManager.getActiveTheme().getAttributeSet(MailboxList.TAG_HEADER);
      }

      this.setThemeAttributesSpecialClear(true);
      graphics.pushRegion(0, 0, width, this.getHeight(), 0, 0);
      this.setThemeAttributesSpecial(this.this$0._themeAttributesHeader, graphics);
      graphics.setFont(this.getFont());
      String text = this.getText();
      graphics.drawText(text, 0, text.length(), 0, 0, 68, width);
      this.setThemeAttributesSpecial(null, null);
      graphics.popContext();
   }
}
