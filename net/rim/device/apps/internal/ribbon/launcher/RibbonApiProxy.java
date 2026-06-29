package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.ApplicationFolder;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.ribbon.RibbonApi;
import net.rim.device.apps.api.utility.props.ObjectProps;
import net.rim.device.apps.api.utility.props.StringProps;

public final class RibbonApiProxy extends RibbonApi {
   private RibbonApiProxy$EntryPointDescriptionStore _entryPointDescriptionStore = new RibbonApiProxy$EntryPointDescriptionStore(null);

   public static final void register() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      RibbonApiProxy rap = (RibbonApiProxy)ar.getOrWaitFor(RibbonApi.ID);
      if (rap == null) {
         rap = new RibbonApiProxy();
         ar.put(RibbonApi.ID, rap);
      }
   }

   private RibbonApiProxy() {
   }

   private final ApplicationEntry getApplicationEntry(int moduleHandle, int index) {
      String moduleName = CodeModuleManager.getModuleName(moduleHandle);
      HierarchyManager hm = HierarchyManager.getInstance();
      InternalApplicationHierarchy iah = hm.getActiveHierarchy();
      ApplicationEntry ae = null;
      ApplicationFolder[] folders = iah.getFolders();

      for (int i = folders.length - 1; i >= 0; i--) {
         String name = folders[i].getName();
         InternalApplicationFolder af = hm.getFolder(name);
         ApplicationEntry[] aearray = af.getApplications();

         for (int j = aearray.length - 1; j >= 0; j--) {
            ApplicationEntry tempae = aearray[j];
            EntryPointDescriptor epd = tempae.getDescriptor();
            if (epd instanceof Object) {
               ApplicationEntryPoint tempaep = (ApplicationEntryPoint)epd;
               ApplicationDescriptor ad = tempaep.getApplicationDescriptor();
               if (moduleName.equals(ad.getModuleName()) && ad.getIndex() == index) {
                  ae = tempae;
                  return ae;
               }
            }
         }
      }

      return ae;
   }

   @Override
   public final String getName(int moduleHandle, int index) {
      ApplicationEntry ae = this.getApplicationEntry(moduleHandle, index);
      if (ae == null) {
         throw new Object(
            ((StringBuffer)(new Object("Module with handle [")))
               .append(moduleHandle)
               .append("] and index [")
               .append(index)
               .append("] has no application entry point")
               .toString()
         );
      } else {
         return ae.getDescription(true);
      }
   }

   @Override
   public final void setIcon(int moduleHandle, int index, Bitmap newicon) {
      this.setIcon(4, moduleHandle, newicon, index);
   }

   private final void setIcon(long property, int moduleHandle, Bitmap icon, int index) {
      ApplicationEntry ae = this.getApplicationEntry(moduleHandle, index);
      if (ae == null) {
         throw new Object(
            ((StringBuffer)(new Object("Module with handle [")))
               .append(moduleHandle)
               .append("] and index [")
               .append(index)
               .append("] has no application entry point")
               .toString()
         );
      }

      ApplicationEntryPoint aep = (ApplicationEntryPoint)ae.getDescriptor();
      if (aep instanceof Object) {
         ObjectProps oprops = aep;
         oprops.set(property, icon);
         this._entryPointDescriptionStore.putBitmap(moduleHandle, index, property, icon);
      }

      RibbonIconField rif = ae.getRibbonIcon();
      if (null == rif) {
         throw new Object(
            ((StringBuffer)(new Object("Module with handle [")))
               .append(moduleHandle)
               .append("] and index [")
               .append(index)
               .append("] has no ribbon icon")
               .toString()
         );
      }

      rif.setBitmap();
   }

   @Override
   public final void setName(int moduleHandle, int index, String name) {
      ApplicationEntry ae = this.getApplicationEntry(moduleHandle, index);
      if (ae == null) {
         throw new Object(
            ((StringBuffer)(new Object("Module with handle [")))
               .append(moduleHandle)
               .append("] and index [")
               .append(index)
               .append("] has no application entry point")
               .toString()
         );
      }

      this._entryPointDescriptionStore.putString(moduleHandle, index, 3, name);
      ApplicationEntryPoint aep = (ApplicationEntryPoint)ae.getDescriptor();
      if (aep instanceof Object) {
         StringProps oprops = aep;
         oprops.set(3, name);
         this._entryPointDescriptionStore.putString(moduleHandle, index, 3, name);
      }

      RibbonIconField rif = ae.getRibbonIcon();
      if (null == rif) {
         throw new Object(
            ((StringBuffer)(new Object("Module with handle [")))
               .append(moduleHandle)
               .append("] and index [")
               .append(index)
               .append("] has no ribbon icon")
               .toString()
         );
      }

      ae.clearDescription();
   }

   @Override
   public final void setRolloverIcon(int moduleHandle, int index, Bitmap rollovericon) {
      this.setIcon(10, moduleHandle, rollovericon, index);
   }

   public final void applyTo(int moduleHandle, ApplicationEntry ae) {
      if (this._entryPointDescriptionStore._moduleLookup.containsKey(moduleHandle)) {
         EntryPointDescriptor epd = ae.getDescriptor();
         if (epd instanceof Object) {
            ApplicationEntryPoint aep = (ApplicationEntryPoint)epd;
            if (aep instanceof Object) {
               ObjectProps oprops = aep;
               ApplicationDescriptor ad = aep.getApplicationDescriptor();
               int index = ad.getIndex();
               Bitmap icon = this._entryPointDescriptionStore.getBitmap(moduleHandle, index, 4);
               if (icon != null) {
                  oprops.set(4, icon);
               }

               icon = this._entryPointDescriptionStore.getBitmap(moduleHandle, index, 10);
               if (icon != null) {
                  oprops.set(10, icon);
               }

               String name = this._entryPointDescriptionStore.getString(moduleHandle, index, 3);
               if (name != null) {
                  oprops.set(3, name);
               }
            }
         }
      }
   }
}
