package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ribbon.ImageProviderRibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager$EntryChangeListener;
import net.rim.device.internal.ui.ScaleBitmap;

final class EntryBitmapComponentFactory$EntryBitmapComponent
   implements SimpleRibbonComponent,
   RibbonComponentInitializer,
   HierarchyManager$EntryChangeListener,
   ImageProviderRibbonComponent {
   RibbonComponent$RibbonComponentChangeListener _listener;
   protected int _height;
   protected int _width;
   protected String _id;
   protected String _folder;
   protected int _position = -1;
   protected boolean _focus;
   protected int _size;
   protected Bitmap _bitmap;
   int _ox;

   final void resolveImage() {
      if (this._id == null) {
         this._bitmap = null;
      } else {
         ApplicationEntry entry = this.getCurrentApplicationEntry();
         if (entry != null) {
            Object object = this._focus ? entry.getBitmapFocus() : entry.getBitmap();
            if (object instanceof Object) {
               Bitmap bitmap = (Bitmap)object;
               int bmWidth = bitmap.getWidth();
               int bmHeight = bitmap.getHeight();
               if (this._size != 0) {
                  int height = this._size;
                  int width = bmWidth * this._size / bmHeight;
                  this._ox = 0;
                  if (width == bitmap.getWidth() && height == bitmap.getHeight()) {
                     this._bitmap = bitmap;
                     return;
                  }

                  this._bitmap = ScaleBitmap.scaleBitmap(bitmap, width, height);
                  return;
               }

               this._bitmap = bitmap;
            }
         }
      }
   }

   @Override
   public final void applyTheme() {
   }

   @Override
   public final int getComponentWidth() {
      return this._width;
   }

   @Override
   public final int getComponentHeight() {
      return this._height;
   }

   @Override
   public final Object getImage() {
      return this._bitmap;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void initialize(Hashtable params, Object context) {
      this._id = (String)params.get("id");
      if (this._id.startsWith("slot")) {
         boolean var5 = false /* VF: Semaphore variable */;

         label33:
         try {
            var5 = true;
            this._position = Integer.parseInt(this._id.substring(4));
            var5 = false;
         } finally {
            if (var5) {
               System.out.println("Invalid number format parsing position in EntryComponentFactory");
               break label33;
            }
         }
      }

      this._focus = params.get("focus") != null;
      this._folder = (String)params.get("folder");
      String size = (String)params.get("size");
      if (size != null) {
         this._size = Integer.parseInt(size);
      }

      this.resolveImage();
   }

   @Override
   public final int paintComponent(Graphics g, int x, int y, int width, int height, Object context) {
      if (this._bitmap != null) {
         g.drawBitmap(x + this._ox, y, width, height, this._bitmap, 0, 0);
      }

      return 0;
   }

   @Override
   public final synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }

   @Override
   public final void setDimensionsAvailable(int width, int height) {
      this._height = height;
      this._width = width;
   }

   @Override
   public final void uninitialize() {
   }

   @Override
   public final void onEntryChange(ApplicationEntry entry) {
      if (this._id != null) {
         if (entry == null || entry.getUniqueName().equals(this._id)) {
            this.resolveImage();
            if (this._listener != null) {
               this._listener.ribbonComponentChanged(null);
            }
         }
      }
   }

   private static final String getApplicationEntry(String folder, int position) {
      try {
         ApplicationEntry entry = HierarchyManager.getInstance().getAppByIndexInFolder(folder != null ? folder : "", position);
         if (entry != null) {
            return entry.getUniqueName();
         }
      } finally {
         return null;
      }

      return null;
   }

   private final ApplicationEntry getCurrentApplicationEntry() {
      if (this._position != -1) {
         this._id = getApplicationEntry(this._folder, this._position);
         if (this._id == null) {
            throw new Object("Application ID not found");
         }
      }

      return HierarchyManager.getInstance().getApplicationEntry(this._id);
   }
}
