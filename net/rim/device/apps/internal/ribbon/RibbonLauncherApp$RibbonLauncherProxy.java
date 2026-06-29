package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ribbon.RibbonListener;

final class RibbonLauncherApp$RibbonLauncherProxy extends RibbonLauncher {
   private RibbonLauncher _rl;

   RibbonLauncherApp$RibbonLauncherProxy(RibbonLauncher rl) {
      this._rl = rl;
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(-8849441768144918583L, this);
   }

   @Override
   public final void addRibbonListener(RibbonListener listener) {
      this._rl.addRibbonListener(listener);
   }

   @Override
   public final void removeRibbonListener(RibbonListener listener) {
      this._rl.removeRibbonListener(listener);
   }

   @Override
   public final void registerAction(String name, EntryPointDescriptor descriptor) {
      this._rl.registerAction(name, descriptor);
   }

   @Override
   public final void unregisterAction(String name) {
      this._rl.unregisterAction(name);
   }

   @Override
   public final EntryPointDescriptor getRegisteredAction(String name) {
      return this._rl.getRegisteredAction(name);
   }

   @Override
   public final EntryPointDescriptor getEntryPointDescriptor(String name) {
      return this._rl.getEntryPointDescriptor(name);
   }

   @Override
   public final void updateRegisteredAction(String name) {
      this._rl.updateRegisteredAction(name);
   }

   @Override
   public final void disableHotKeys(boolean disable) {
      this._rl.disableHotKeys(disable);
   }

   @Override
   public final void launch(String url) {
      this._rl.launch(url);
   }

   @Override
   public final void setBackgroundImage(Object object, String[] params) {
      this._rl.setBackgroundImage(object, params);
   }

   @Override
   public final void showRootFolder(boolean moveFocusToFirst) {
      this._rl.showRootFolder(moveFocusToFirst);
   }

   @Override
   public final void setNetworkImmediateDisplayString(String string) {
      this._rl.setNetworkImmediateDisplayString(string);
   }

   @Override
   public final void setLongIdleModeText(String string) {
      this._rl.setLongIdleModeText(string);
   }

   @Override
   public final void setLongImmediateCellBroadcastText(String string) {
      this._rl.setLongImmediateCellBroadcastText(string);
   }

   @Override
   public final void setApplicationEntryPointProperty(String uniqueName, long propertyId, Object property) {
      this._rl.setApplicationEntryPointProperty(uniqueName, propertyId, property);
   }

   @Override
   public final boolean isFirstBoot() {
      return this._rl.isFirstBoot();
   }
}
