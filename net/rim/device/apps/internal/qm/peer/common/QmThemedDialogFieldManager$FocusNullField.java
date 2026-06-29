package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.component.NullField;

final class QmThemedDialogFieldManager$FocusNullField extends NullField {
   private boolean _canFocus = true;

   private QmThemedDialogFieldManager$FocusNullField() {
      super(18014398509481984L);
   }

   @Override
   public final boolean isFocusable() {
      return this._canFocus;
   }

   QmThemedDialogFieldManager$FocusNullField(QmThemedDialogFieldManager$1 x0) {
      this();
   }

   static final boolean access$102(QmThemedDialogFieldManager$FocusNullField x0, boolean x1) {
      return x0._canFocus = x1;
   }
}
