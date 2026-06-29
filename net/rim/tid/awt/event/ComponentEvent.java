package net.rim.tid.awt.event;

import net.rim.tid.awt.Event;
import net.rim.tid.itie.IComponent;

public class ComponentEvent extends Event {
   public static final int COMPONENT_MOVED;
   public static final int COMPONENT_RESIZED;

   public ComponentEvent(IComponent source, int id, int eMask) {
      super(source, id, eMask | Event.COMPONENT_EVENT_MASK);
   }

   public IComponent getComponent() {
      return this.getSource();
   }
}
