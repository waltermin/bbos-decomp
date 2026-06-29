package net.rim.device.apps.internal.browser.channel;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.utility.props.IntegerProps;
import net.rim.device.apps.api.utility.props.ObjectProps;
import net.rim.device.apps.api.utility.props.StringProps;

public final class ChannelApplicationEntryPoint implements EntryPointDescriptor, IntegerProps, StringProps, ObjectProps, Runnable {
   private ApplicationDescriptor _applicationDescriptor;
   private ChannelModel _model;
   private Integer _order;
   private String _uniqueName;
   private Object _customIcon;

   public ChannelApplicationEntryPoint(ApplicationDescriptor ad, ChannelModel model) {
      this._model = model;
      this._applicationDescriptor = ad;
      String moduleName = ad.getModuleName();
      String name = ad.getName();
      StringBuffer buff = new StringBuffer(moduleName.length() + name.length() + 1);
      buff.append(moduleName).append('.').append(name);
      this._uniqueName = buff.toString();
      int order = ad.getPosition();
      if (order != 0) {
         this._order = new Integer(order);
      }
   }

   public final ApplicationDescriptor getApplicationDescriptor() {
      return this._applicationDescriptor;
   }

   @Override
   public final String get(long propID, String defaultReturned) {
      if (propID == 1) {
         return this._uniqueName;
      } else if (propID == 3) {
         return this._model.getTitle();
      } else if (propID == 9) {
         return this._uniqueName;
      } else {
         return propID == 2 && this._model.getStatus() == 1 ? "unread" : defaultReturned;
      }
   }

   @Override
   public final void set(long propID, String valueToSet) {
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      if (propID == 5) {
         Bitmap icon = this._applicationDescriptor.getIcon();
         if (icon != null) {
            return icon;
         }
      } else if (propID == 4) {
         if (this._customIcon != null) {
            return this._customIcon;
         }
      } else if (propID == -8880124975077471920L) {
         return this._applicationDescriptor;
      }

      return defaultReturned;
   }

   @Override
   public final Integer get(long propID, Integer defaultReturned) {
      return propID == 6 && this._order != null ? this._order : defaultReturned;
   }

   @Override
   public final void set(long propID, Integer valueToSet) {
   }

   @Override
   public final void set(long propID, Object valueToSet) {
      if (propID == 4) {
         this._customIcon = valueToSet;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      try {
         ApplicationManager.getApplicationManager().runApplication(this._applicationDescriptor);
      } catch (Throwable var3) {
         throw new RuntimeException(e.getMessage());
      }
   }
}
