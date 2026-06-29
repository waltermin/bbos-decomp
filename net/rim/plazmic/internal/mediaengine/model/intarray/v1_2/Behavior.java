package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.service.node.Node;

public interface Behavior extends Node {
   void begin();

   boolean getFirstBeginTrigger(Event var1);

   boolean getNextBeginTrigger(Event var1);
}
