package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;

final class SlideBreakPresentationElementField extends VerticalFieldManager implements PresentationElement {
   private int DURATION_MINIMUM = 1;
   private int DURATION_MAXIMUM = 60;
   private int _duration;
   private boolean _isEditable;
   private NumericChoiceField _choiceField;

   public SlideBreakPresentationElementField(boolean isEditable) {
      this(5, isEditable);
   }

   public SlideBreakPresentationElementField(int duration, boolean isEditable) {
      this._duration = duration;
      this._isEditable = isEditable;
      boolean addChoiceField = this._isEditable;
      if (RadioInfo.getNetworkType() == 5) {
         addChoiceField = false;
      }

      if (addChoiceField) {
         String label = MMSResources.getString(98);
         this.DURATION_MINIMUM = Math.min(this.DURATION_MINIMUM, duration);
         this.DURATION_MAXIMUM = Math.max(this.DURATION_MAXIMUM, duration);
         this._choiceField = (NumericChoiceField)(new Object(label, this.DURATION_MINIMUM, this.DURATION_MAXIMUM, 1, duration - this.DURATION_MINIMUM));
         this.add(this._choiceField);
      }

      this.add((Field)(new Object()));
   }

   @Override
   public final void copyTo(MMSPresentationModel target) {
      int duration = this._duration;
      if (this._choiceField != null) {
         duration = this._choiceField.getSelectedIndex() + this.DURATION_MINIMUM;
      }

      target.addSlideBreak(duration, this._isEditable);
   }

   @Override
   public final boolean canMove() {
      return false;
   }

   @Override
   public final void move(boolean mode) {
      throw new Object();
   }
}
