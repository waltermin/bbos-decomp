package net.rim.plazmic.internal.mediaengine.service;

import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.mediaengine.MediaListener;

public interface EventResolver extends MediaListener {
   String ID = "EventResolver";

   void setEventLogic(Object var1);

   Object getEventLogic();

   void setEventListener(int var1, Object var2);

   void removeAllEventListeners();

   void setEngine(EventEngine var1);
}
