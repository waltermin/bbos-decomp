package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.qm.resource.QmResources;

public class QmVerb extends Verb {
   private String _label;
   public int _id;
   public static QmMainScreen _screen;

   public QmVerb(int ordering, int id) {
      super(ordering);
      this._id = id;
   }

   public QmVerb(int ordering, int id, String label) {
      super(ordering);
      this._id = id;
      this._label = label;
   }

   public QmVerb(int ordering, String label) {
      super(ordering);
      this._label = label;
   }

   @Override
   public String toString() {
      return this._label != null ? this._label : QmResources.getString(this._id);
   }

   protected void invoke() {
      _screen.invokeVerb(this._id);
   }

   @Override
   public Object invoke(Object context) {
      this.invoke();
      return null;
   }
}
