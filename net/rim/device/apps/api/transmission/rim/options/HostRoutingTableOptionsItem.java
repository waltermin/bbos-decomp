package net.rim.device.apps.api.transmission.rim.options;

import net.rim.device.api.hrt.HRUtils;
import net.rim.device.apps.api.options.OptionsListItem;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;

public final class HostRoutingTableOptionsItem extends OptionsListItem {
   public HostRoutingTableOptionsItem() {
      super(RIMMessagingService.getResourceString(2), -1514481539159318190L);
   }

   @Override
   protected final void open() {
      HRUtils.getThunks().displayEditor(HRUtils.getDefaultHRT(), 1);
   }
}
