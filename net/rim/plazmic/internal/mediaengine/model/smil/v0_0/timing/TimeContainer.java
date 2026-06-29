package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing;

import java.util.Vector;
import net.rim.plazmic.internal.mediaengine.event.EventLogic;

public class TimeContainer extends TimingObject {
   private Vector _children = new Vector();

   public TimeContainer(int id, TimeContainer parent) {
      super(id, parent);
   }

   protected Vector getChildren() {
      return this._children;
   }

   public void addChildElement(TimingObject child) {
      this._children.addElement(child);
   }

   protected void notifyChildStarted(TimingObject child) {
   }

   public void setEndLogic(EventLogic _1) {
      throw null;
   }
}
