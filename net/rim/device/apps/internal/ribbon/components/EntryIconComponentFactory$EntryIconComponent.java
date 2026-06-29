package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager$EntryChangeListener;
import net.rim.device.apps.internal.ribbon.launcher.RibbonIconField;

final class EntryIconComponentFactory$EntryIconComponent implements SimpleRibbonComponent, RibbonComponentInitializer, HierarchyManager$EntryChangeListener {
   RibbonComponent$RibbonComponentChangeListener _listener;
   protected int _height;
   protected int _width;
   protected String _id;
   protected String _folder;
   protected int _position;
   protected boolean _lookUsingOrdinal;
   private RibbonIconField _image;
   private RibbonIconField _imageScaled;
   protected boolean _focus;
   protected int _size;

   final void getImage() {
      if (this._id == null) {
         this._image = null;
      } else {
         ApplicationEntry entry = this.getCurrentApplicationEntry(this._id);
         if (entry != null) {
            RibbonIconField imageOld = this._image;
            this._image = entry.getRibbonIcon();
            if (this._image != null) {
               Theme theme = ThemeManager.getActiveTheme();
               int applicationIconWidth = theme.getRibbonIconWidth();
               int applicationIconHeight = theme.getRibbonIconHeight();
               if (applicationIconWidth > this._size && applicationIconHeight > this._size) {
                  if (this._imageScaled == null || imageOld != this._image) {
                     this._imageScaled = new RibbonIconField(entry);
                  }

                  this._imageScaled.setSize(applicationIconWidth, this._size);
                  this._imageScaled.setBitmap();
               }

               this._image.setSize(applicationIconWidth, applicationIconHeight);
               this._image.setBitmap();
            }
         }
      }
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
   public final void applyTheme() {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void initialize(Hashtable params, Object context) {
      this._id = (String)params.get("id");
      if (this._id.startsWith("slot")) {
         this._lookUsingOrdinal = true;
         boolean var5 = false /* VF: Semaphore variable */;

         label41:
         try {
            var5 = true;
            this._position = Integer.parseInt(this._id.substring(4));
            var5 = false;
         } finally {
            if (var5) {
               System.out.println("Invalid number format parsing position in EntryComponentFactory");
               break label41;
            }
         }
      }

      this._focus = params.get("focus") != null;
      this._folder = (String)params.get("folder");
      if (this._folder == null) {
         this._folder = "";
      }

      String size = (String)params.get("size");
      if (size != null) {
         this._size = Integer.parseInt(size);
      }

      this.getImage();
   }

   @Override
   public final int paintComponent(Graphics g, int x, int y, int width, int height, Object context) {
      if (this._image == null) {
         return 0;
      }

      if (this._focus) {
         g.setDrawingStyle(8, true);
      } else {
         g.setDrawingStyle(8, false);
      }

      RibbonIconField paintIcon = null;
      if (this._imageScaled != null) {
         paintIcon = this._imageScaled;
      } else {
         paintIcon = this._image;
      }

      int bmWidth = paintIcon.getPreferredWidth();
      int bmHeight = paintIcon.getPreferredHeight();
      x += (this._width - bmWidth) / 2;
      y += (this._height - bmHeight) / 2;
      g.pushRegion(x, y, width, height, 0, 0);
      paintIcon.setForcePaintVisible(true);
      paintIcon.paintIconOnly(g);
      paintIcon.setForcePaintVisible(false);
      g.popContext();
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
         ApplicationEntry thisEntry = this.getCurrentApplicationEntry(this._id);
         if (thisEntry == null) {
            this._image = null;
         } else {
            if (entry == null || entry.getUniqueName().equals(thisEntry.getUniqueName())) {
               this.getImage();
               if (this._listener != null) {
                  this._listener.ribbonComponentChanged(null);
               }
            }
         }
      }
   }

   private final ApplicationEntry getCurrentApplicationEntry(String id) {
      if (this._lookUsingOrdinal && this._folder != null) {
         try {
            ApplicationEntry entry = HierarchyManager.getInstance().getAppByIndexInFolder(this._folder, this._position);
            if (entry != null) {
               id = entry.getPropertiesName();
            }
         } finally {
            ;
         }
      }

      return HierarchyManager.getInstance().getApplicationEntry(id);
   }
}
