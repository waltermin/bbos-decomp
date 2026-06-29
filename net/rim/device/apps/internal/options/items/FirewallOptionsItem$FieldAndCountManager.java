package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;

final class FirewallOptionsItem$FieldAndCountManager extends Manager {
   private Field _field;
   private LabelField _countLabel;

   FirewallOptionsItem$FieldAndCountManager(Field field, LabelField countLabel) {
      super(0);
      this._field = field;
      this._countLabel = countLabel;
      this.add(this._field);
      this.add(this._countLabel);
   }

   @Override
   protected final void sublayout(int maxWidth, int maxHeight) {
      int countLabelWidth = this._countLabel.getPreferredWidth();
      this.layoutChild(this._field, maxWidth - countLabelWidth - 18, maxHeight);
      this.setPositionChild(this._field, 0, 0);
      this.layoutChild(this._countLabel, countLabelWidth + 1, maxHeight);
      this.setPositionChild(this._countLabel, maxWidth - countLabelWidth - 1, 0);
      this.setExtent(maxWidth, Math.max(this._field.getHeight(), this._countLabel.getHeight()));
   }
}
