package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing;

import java.util.Vector;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventLogic;

public class Seq extends TimeContainer {
   private TimingObject _currentActiveChild;

   public Seq(int id, TimeContainer parent) {
      super(id, parent);
   }

   @Override
   public void setEndLogic(EventLogic logic) {
      if (this.isImplicitDuration()) {
         Event seqEvent = (Event)(new Object());
         seqEvent._event = 2;
         seqEvent._eventParam = this.getId();
         Event seqEndTrigger = (Event)(new Object());
         seqEndTrigger._event = 2;
         seqEndTrigger._eventParam = this.getLastChild().getId();
         logic.addEventDependancy(seqEndTrigger, seqEvent, 0);
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

   private TimingObject getLastChild() {
      TimingObject lastChild = (TimingObject)this.getChildren().lastElement();
      if (lastChild == null) {
         lastChild = this;
      }

      return lastChild;
   }

   @Override
   public boolean end(Event triggerEvent) {
      boolean finished = false;
      if (triggerEvent._eventParam == this.getLastChild().getId() || triggerEvent._eventParam == this.getId()) {
         finished = super.end(triggerEvent);
         if (this.getFill() == 2) {
            this.removeFill();
         }
      }

      return finished;
   }

   @Override
   protected void notifyChildStarted(TimingObject child) {
      if (this._currentActiveChild != null) {
         this._currentActiveChild.removeFill();
      }

      this._currentActiveChild = child;
   }
}
