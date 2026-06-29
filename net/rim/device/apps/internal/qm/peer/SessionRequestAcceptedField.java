package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;

final class SessionRequestAcceptedField extends SessionInfoField implements FieldChangeListener {
   private ButtonField _end;
   private int _id;

   public SessionRequestAcceptedField(int id) {
      super(null, null);
      this._id = id;
   }

   @Override
   protected final void setText() {
      super._text = "The invitation has been accepted.";
   }

   @Override
   protected final void addFields() {
      super.addFields();
      this._end = new ButtonField("End Session", 12884967424L);
      this._end.setChangeListener(this);
      this.add(this._end);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._end) {
         SessionManager.getInstance().sessionEnded(this._id, false);
         this.delete(this._end);
      }
   }
}
