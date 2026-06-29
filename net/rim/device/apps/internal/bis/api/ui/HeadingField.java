package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;

public final class HeadingField extends LabelField {
   public HeadingField() {
   }

   public HeadingField(Object text) {
   }

   @Override
   protected final void paint(Graphics graphics) {
      Tag tag = Tag.create("menu");
      Theme theme = ThemeManager.getActiveTheme();
      ThemeAttributeSet attrSet = theme.getAttributeSet(tag);
      int color = attrSet.getColor(2);
      graphics.setColor(color);
      super.paint(graphics);
   }
}
