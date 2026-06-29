package net.rim.device.apps.internal.manageconnections;

import net.rim.device.apps.api.ribbon.ImageProviderRibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;

final class MySignalComponentField extends ImageField implements RibbonComponent$RibbonComponentChangeListener {
   private ImageProviderRibbonComponent _component;

   public MySignalComponentField(ImageProviderRibbonComponent component, long style) {
      super(style);
      this._component = component;
      this.setImage((Image)this._component.getImage());
   }

   @Override
   public final void ribbonComponentChanged(RibbonComponent component) {
      this.setImage((Image)this._component.getImage());
   }
}
