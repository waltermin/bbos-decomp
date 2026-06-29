package net.rim.device.apps.api.transmission.rim.options;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.apps.api.options.OptionsListItem;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;

public final class ServiceBookOptionsItem extends OptionsListItem {
   public ServiceBookOptionsItem() {
      super(RIMMessagingService.getResourceString(3), -1514481539159318190L);
   }

   @Override
   protected final void open() {
      ServiceBook.getSB().displayEditor(1);
   }
}
