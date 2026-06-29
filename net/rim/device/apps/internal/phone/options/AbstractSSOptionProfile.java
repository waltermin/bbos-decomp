package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ui.CommonResources;

class AbstractSSOptionProfile implements SSOptionProfile {
   protected String _profileName;
   protected static final int PROFILE_NAME_PAINT_OFFSET = 0;
   protected static final int HSPACE = 3;

   protected AbstractSSOptionProfile(String profileName) {
      this._profileName = profileName;
   }

   @Override
   public void paint(Graphics graphics, int x, int y, int width) {
      Font font = null;
      boolean enabled = this.isEnabled();
      if (enabled) {
         font = graphics.getFont();
         Font boldFont = font.derive(1);
         graphics.setFont(boldFont);
      }

      String enabledString = CommonResources.getString(115);
      int enabledStringWidth = graphics.getFont().getBounds(enabledString);
      int profileNameAvailableWidth = width - enabledStringWidth - 3;
      graphics.drawText(this._profileName, 0, y, 64, profileNameAvailableWidth);
      if (enabled) {
         graphics.drawText(enabledString, 0, y, 5, width);
      }

      if (font != null) {
         graphics.setFont(font);
      }
   }

   @Override
   public boolean isEnabled() {
      throw null;
   }

   @Override
   public void disable() {
      throw null;
   }

   @Override
   public void enable() {
      throw null;
   }
}
