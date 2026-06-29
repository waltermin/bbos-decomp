package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class ContactsScreenTitleField extends TitleField {
   ContactsScreenTitleField() {
      super(false);
      this.update();
   }

   @Override
   final int drawIcon(Graphics graphics, int x, int y) {
      return ((ContactStatus)PeerApplication.getInstance()._presenceManager).drawIcon(graphics, x, y, false);
   }

   @Override
   final int getIconHeight() {
      return PeerResources.getIconHeight(this.getFont());
   }

   final void update() {
      String displayName = PeerApplication.getSession().getDisplayName();
      if (displayName != null) {
         if (Locale.getCLDCLocaleString().startsWith("en") && displayName.charAt(displayName.length() - 1) == 's') {
            displayName = ((StringBuffer)(new Object())).append(displayName).append("' ").append(QmResources.getString(19)).toString();
         } else {
            displayName = PeerResources.format(888, displayName);
         }
      } else {
         displayName = QmResources.getString(19);
      }

      this.setTitle(displayName);
      this.invalidate();
   }
}
