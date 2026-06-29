package net.rim.device.apps.internal.sms;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.utility.props.ObjectProps;
import net.rim.device.apps.api.utility.props.StringProps;
import net.rim.device.apps.internal.sms.resources.SMSResources;

final class SetupMenuEntryPoint implements EntryPointDescriptor, Runnable, StringProps, ObjectProps {
   private Application _app;
   private String _title;
   private Object[] _items;
   private int[] _ids;
   private boolean _helpAvailable;
   private SIMToolkit _stk;
   static String UNIQUE_NAME = "net_rim_SIMToolKit";

   SetupMenuEntryPoint(SIMToolkit stk, Application app, String title, Object[] items, int[] ids, boolean helpAvailable) {
      this._stk = stk;
      this._app = app;
      this._title = title;
      this._items = items;
      this._ids = ids;
      this._helpAvailable = helpAvailable;
   }

   @Override
   public final void run() {
      if (RadioInfo.getState() != 1) {
         Dialog.alert(SMSResources.getString(392));
      } else {
         this._app.invokeLater(new SetupMenuEntryPoint$1(this));
      }
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      return propID == 5 ? Bitmap.getBitmapResource("SIMToolkitMenuIcon.gif") : defaultReturned;
   }

   @Override
   public final String get(long propID, String defaultReturned) {
      if (propID == 1) {
         return UNIQUE_NAME;
      } else {
         return propID == 3 ? this._title : defaultReturned;
      }
   }

   @Override
   public final void set(long propID, Object valueToSet) {
   }

   @Override
   public final void set(long propID, String valueToSet) {
   }
}
