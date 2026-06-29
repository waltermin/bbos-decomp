package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.apps.api.ui.SelfDrawingListField;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

class SSOptionListField extends SelfDrawingListField {
   private SSOptionProfile[] _profiles;

   public SSOptionListField(SSOptionProfile[] profiles) {
      super(0, 0);
      if (profiles != null) {
         this.setSize(profiles.length);
         this._profiles = profiles;
      }

      this.setEmptyString(PhoneResources.getResourceBundle(), 6013, 0);
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (this._profiles != null) {
         SSOptionProfile profile = this._profiles[index];
         if (profile != null) {
            profile.paint(graphics, 0, y, width);
         }
      }
   }
}
