package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing;

import java.util.Vector;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventLogic;

public class Par extends TimeContainer {
   public Par(int id, TimeContainer parent) {
      super(id, parent);
   }

   @Override
   public void setEndLogic(EventLogic logic) {
      if (this.isImplicitDuration()) {
         Event parEvent = new Event();
         parEvent._event = 2;
         parEvent._eventParam = this.getId();
         Event triggerEvent = new Event();
         triggerEvent._event = 2;
         Vector children = this.getChildren();
         if (children.size() == 0) {
            triggerEvent._eventParam = this.getId();
            logic.addEventDependancy(triggerEvent, parEvent, 0);
            return;
         }

         for (int i = 0; i < children.size(); i++) {
            TimingObject child = (TimingObject)children.elementAt(i);
            triggerEvent._eventParam = child.getId();
            logic.addEventDependancy(triggerEvent, parEvent, 0);
         }
      }
   }

   @Override
   protected void removeFill() {
      Vector children = this.getChildren();

      for (int i = 0; i < children.size(); i++) {
         TimingObject child = (TimingObject)children.elementAt(i);
         child.removeFill();
      }
   }

   @Override
   public boolean end(Event triggerEvent) {
      boolean finished = true;
      if (this.isImplicitDuration()) {
         Vector children = this.getChildren();

         for (int i = 0; i < children.size(); i++) {
            TimingObject child = (TimingObject)children.elementAt(i);
            finished = finished && child.getState() != 1;
         }
      }

      if (finished) {
         finished = finished && super.end(triggerEvent);
         if (this.getFill() == 2) {
            this.removeFill();
         }
      }

      return finished;
   }
}
