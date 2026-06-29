package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.RadioInternal;

final class NetworkOptionsItem$UMTS_NetworkModeField implements NetworkOptionsItem$NetworkModeFieldProvider {
   ObjectChoiceField _modeField;
   private final NetworkOptionsItem this$0;

   NetworkOptionsItem$UMTS_NetworkModeField(NetworkOptionsItem _1) {
      this.this$0 = _1;
      _1._savedNetMode = RadioInternal.getNetworkMode();
      String[] strs = _1._rb.getStringArray(1970);
      String[] args = new String[0];
      int[] map = new int[0];
      int networkModes = RadioInternal.getAvailableNetworkModes();
      _1.addOption(networkModes, 1, strs, args, map, 0, 0);
      _1.addOption(networkModes, 2, strs, args, map, 1, 1);
      _1.addOption(networkModes, 3, strs, args, map, 2, 2);
      if (args.length <= 1) {
         this._modeField = null;
      } else {
         int umtsIndex = 0;
         int curMode = RadioInternal.getNetworkMode();

         int i;
         for (i = map.length - 1; i >= 0; i--) {
            if (map[i] == 0) {
               umtsIndex = i;
            }

            if (curMode == map[i]) {
               break;
            }
         }

         if (i < 0) {
            this.setNetworkMode(0);
            _1._savedNetMode = 0;
            i = umtsIndex;
         }

         this._modeField = new ObjectChoiceField(OptionsResources.getString(1969), args, i);
         _1._indexToNetMode = map;
         this._modeField.setChangeListener(this);
      }
   }

   @Override
   public final Field getField() {
      return this._modeField;
   }

   @Override
   public final void discard() {
      if (this._modeField != null && RadioInternal.getNetworkMode() != this.this$0._savedNetMode) {
         this.setNetworkMode(this.this$0._savedNetMode);
      }
   }

   @Override
   public final void save() {
      if (RadioInternal.getNetworkMode() != this.this$0._savedNetMode && this._modeField.isDirty()) {
         int newMode = this.this$0._indexToNetMode[this._modeField.getSelectedIndex()];
         if (newMode != RadioInternal.getNetworkMode()) {
            this.setNetworkMode(newMode);
         }

         if (NetworkOptionsItem._isSynchronousOperation) {
            this.this$0._savedNetMode = newMode;
            this._modeField.setDirty(false);
         }
      }
   }

   private final void setNetworkMode(int mode) {
      if (RadioInternal.getNetworkMode() != mode) {
         RadioInternal.setNetworkMode(mode);
      }
   }

   @Override
   public final boolean isDirty() {
      return this._modeField.isDirty();
   }

   @Override
   public final void update() {
      int mode = RadioInternal.getNetworkMode();
      if (mode != this.this$0._indexToNetMode[this._modeField.getSelectedIndex()]) {
         for (int i = this._modeField.getSize() - 1; i >= 0; i--) {
            if (this.this$0._indexToNetMode[i] == mode) {
               this._modeField.setSelectedIndex(i);
               return;
            }
         }
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      this.setNetworkMode(this.this$0._indexToNetMode[this._modeField.getSelectedIndex()]);
   }
}
