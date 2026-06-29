package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.qm.peer.common.FixedHeightManager;
import net.rim.device.apps.internal.qm.peer.common.OkCancelDialog;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class SelectContactDialog extends OkCancelDialog implements ListFieldCallback {
   private ListField _listField;
   private PeerContact[] _contacts;

   final PeerContact getSelection() {
      int index = this._listField.getSelectedIndex();
      return index >= 0 && index < this._contacts.length ? this._contacts[index] : null;
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return index >= 0 && index < this._contacts.length ? this._contacts[index] : null;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      graphics.drawText(this._contacts[index].getDisplayName(), 0, y);
   }

   SelectContactDialog(PeerContact[] contacts) {
      this(contacts, 0);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.close(false);
         return true;
      } else if (key == '\n' && this.getLeafFieldWithFocus() == this._listField) {
         this.close(true);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (this.getLeafFieldWithFocus() == this._listField) {
         this.close(true);
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   SelectContactDialog(PeerContact[] contacts, int selectedIndex) {
      if (contacts == null) {
         this._contacts = new PeerContact[0];
      } else {
         this._contacts = contacts;
      }

      PeerContact selectedContact = null;
      if (selectedIndex >= 0 && selectedIndex < this._contacts.length) {
         selectedContact = this._contacts[selectedIndex];
      }

      Arrays.sort(this._contacts, new StringComparator());
      this.addTitle(QmResources.getString(12));
      int count = this._contacts.length;
      this._listField = (ListField)(new Object(count));
      this._listField.setCallback(this);
      this._listField.setSelectedIndex(0);
      FixedHeightManager fhm = new FixedHeightManager(this._listField, 4);
      this.add(fhm);
      this.addCancelButton();
      if (selectedContact != null) {
         int newIndex = Arrays.getIndex(this._contacts, selectedContact);
         if (newIndex >= 0) {
            this._listField.setSelectedIndex(newIndex);
         }
      }
   }
}
