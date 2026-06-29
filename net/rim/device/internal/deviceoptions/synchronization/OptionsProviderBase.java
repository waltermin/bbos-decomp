package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.deviceoptions.OptionsProvider;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeListener;

class OptionsProviderBase implements OptionsProvider {
   private OptionsProviderChangeListener _listener;

   OptionsProviderBase(OptionsProviderChangeListener listener) {
      this._listener = listener;
   }

   protected void optionsProviderChanged() {
      this._listener.optionsProviderChanged(this);
   }

   @Override
   public void setOptionsData(DataBuffer _1) {
      throw null;
   }

   @Override
   public void getOptionsData(DataBuffer _1) {
      throw null;
   }

   @Override
   public int getUID() {
      throw null;
   }
}
