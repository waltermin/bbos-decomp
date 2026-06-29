package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.ui.VolumeIndicator;

final class StandardVolumeGaugePopup extends BaseVolumeGaugePopup {
   private VolumeIndicator _gauge = new VolumeIndicator(PhoneUtilities.smallScreen());
   private static final int INDICATOR_FIELD;
   private static final int INDICATOR_LABEL;

   public StandardVolumeGaugePopup() {
      super(new StandardGaugeFieldLayoutManager());
      this.getDelegate().add(this._gauge);
   }

   @Override
   protected final void sublayout(int width, int height) {
      super.sublayout(width, height);
      int x = width - this.getDelegate().getWidth() - 8;
      int y = 0 + (PhoneUtilities.smallScreen() ? 0 : 6);
      this.setPosition(x, y);
   }
}
