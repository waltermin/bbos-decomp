package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.qm.peer.common.FixedHeightManager;
import net.rim.device.apps.internal.qm.peer.common.OkCancelDialog;

final class SelectServiceDialog extends OkCancelDialog implements ListFieldCallback {
   private ListField _listField;
   private ServiceRunnable[] _services;

   final ServiceRunnable getSelection() {
      int index = this._listField.getSelectedIndex();
      return this._services != null && index >= 0 && index < this._services.length ? this._services[index] : null;
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this._services != null && index >= 0 && index < this._services.length ? this._services[index] : null;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (this._services != null) {
         graphics.drawText(this._services[index].getName(), 0, y);
      }
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

   SelectServiceDialog(ServiceRunnable[] services) {
      this.addTitle(PeerResources.getString(2049));
      this._services = services;
      int count = 0;
      if (this._services != null) {
         count = this._services.length;
         Arrays.sort(this._services, new SelectServiceDialog$1(this));
      }

      this._listField = (ListField)(new Object(count));
      this._listField.setCallback(this);
      this._listField.setSelectedIndex(0);
      FixedHeightManager fhm = new FixedHeightManager(this._listField, 4);
      this.add(fhm);
      this.addCancelButton();
   }
}
