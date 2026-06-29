package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

import net.rim.plazmic.internal.mediaengine.event.EventLogic;

public interface Interactor {
   void fireEvent(int var1, int var2, long var3);

   EventLogic getEventLogic();
}
