package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.internal.qm.peer.common.OkCancelDialog;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class PickContactListDialog extends OkCancelDialog implements ListFieldCallback {
   private ListField _listField;
   SimpleSortingVector _contactLists;
   private PeerContactList _skipList;
   private int _skipIndex = -1;

   final PeerContactList getSelection() {
      int index = this._listField.getSelectedIndex();
      if (this._skipIndex != -1 && index >= this._skipIndex) {
         index++;
      }

      return (PeerContactList)this._contactLists.getAt(index);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return null;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (this._skipList != null && this._skipIndex == -1 && this._contactLists.getAt(index) == this._skipList) {
         this._skipIndex = index;
      }

      if (this._skipIndex != -1 && index >= this._skipIndex) {
         index++;
      }

      graphics.drawText(((PeerContactList)this._contactLists.getAt(index)).getDisplayName(), 0, y);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.close(false);
         return true;
      }

      if (key == '\n') {
         Field field = this.getFieldWithFocus();
         if (field instanceof Manager) {
            field = ((Manager)field).getFieldWithFocus();
         }

         if (field == this._listField) {
            this.close(true);
            return true;
         }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      Field field = this.getFieldWithFocus();
      if (field instanceof Manager) {
         field = ((Manager)field).getFieldWithFocus();
      }

      if (field == this._listField) {
         this.close(true);
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   PickContactListDialog(PeerContactListCollection contactListCollection, PeerContactList skip) {
      this._contactLists = contactListCollection.getContactListsAsVector();
      this.addTitle(QmResources.getString(60));
      int count = this._contactLists.size();
      if (skip != null) {
         count--;
      }

      this._skipList = skip;
      this._listField = new ListField(count);
      this._listField.setCallback(this);
      this._listField.setSelectedIndex(count / 2);
      this.add(this._listField);
      this.addCancelButton();
   }
}
