package net.rim.device.apps.internal.security;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.internal.resource.SecurityResource;
import net.rim.device.internal.deviceoptions.Owner;

final class SecurityApp$OwnerInfoManager extends Manager implements SecurityResource {
   private RichTextField _fieldOwnerName;
   private RichTextField _fieldOwnerInfo;
   private boolean _drawInfo;
   private Tag TAG;
   private final SecurityApp this$0;
   private static final int INSIDE_SPACE;

   private SecurityApp$OwnerInfoManager(SecurityApp _1) {
      super(3458764513820540928L);
      this.this$0 = _1;
      this.TAG = Tag.create("owner-info");
      this.setTag(this.TAG);
      String ownerName = Owner.getOwnerName();
      String ownerInfo = Owner.getOwnerInfo();
      if (ownerName != null && ownerName.length() != 0 || ownerInfo != null && ownerInfo.length() != 0) {
         this._drawInfo = true;
      } else {
         this._drawInfo = false;
      }

      this._fieldOwnerName = (RichTextField)(new Object(ownerName, 36028797086072832L));
      this.add(this._fieldOwnerName);
      this._fieldOwnerInfo = (RichTextField)(new Object(ownerInfo, 36028797018963968L));
      this.add(this._fieldOwnerInfo);
   }

   @Override
   protected final void sublayout(int width, int height) {
      int childWidth = width - 8;
      int childHeight = height - 8;
      int numFields = this.getFieldCount();
      int heightUsed = 4;

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         this.setPositionChild(field, 4, heightUsed);
         this.layoutChild(field, childWidth, childHeight - heightUsed);
         heightUsed += field.getHeight();
      }

      this.setExtent(width, height);
   }

   @Override
   protected final void paint(Graphics graphics) {
      int width = this.getWidth();
      int height = this.getHeight();
      graphics.drawRect(0, 1, width, height - 1);
      if (!this._drawInfo) {
         width -= 8;
         int y = 4;
         graphics.drawText(this.this$0._rb.getString(100), 4, y, 52, width);
         y += graphics.getFont().getHeight() + 2;
         graphics.drawText(this.this$0._rb.getString(101), 4, y, 52, width);
         graphics.drawText(this.this$0._rb.getString(103), 4, height - 4, 45, width);
      } else {
         super.paint(graphics);
      }
   }

   public final void updateOwnerInfo() {
      String ownerName = Owner.getOwnerName();
      String ownerInfo = Owner.getOwnerInfo();
      this._drawInfo = ownerName.length() != 0 || ownerInfo.length() != 0;
      this._fieldOwnerName.setText(ownerName);
      this._fieldOwnerInfo.setText(ownerInfo);
   }

   SecurityApp$OwnerInfoManager(SecurityApp x0, SecurityApp$1 x1) {
      this(x0);
   }
}
