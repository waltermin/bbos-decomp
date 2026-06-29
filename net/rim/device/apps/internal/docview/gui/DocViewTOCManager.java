package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.ui.UiInternal;

public final class DocViewTOCManager extends VerticalFieldManager {
   private DocViewDisplayField _delegateMgr;
   private byte _styleInfo = 0;
   private DocViewTOCManager$DocViewObjectList _listField;
   private boolean _isListDirty;
   private DocViewTOCManager$DocViewMoreAvailableLine _morePreview;
   private VerbMenuItem _moreVerb;
   private short _savedMenuFlags;
   static final byte DEFAULT_STYLE = 0;
   static final byte CUSTOM_STYLE = 1;

   public DocViewTOCManager(DocViewDisplayField delegate, Font initialFont) {
      this(delegate, (byte)0, initialFont);
   }

   DocViewTOCManager(DocViewDisplayField delegate, byte style, Font initialFont) {
      super(281474976710656L);
      if (delegate == null) {
         throw new Object("Null delegate manager for TOC.");
      }

      this._styleInfo = style;
      this._delegateMgr = delegate;
      if (style == 0) {
         this._listField = new DocViewTOCManager$DocViewObjectList(this);
         if (initialFont != null) {
            this._listField.setFont(initialFont);
            this._isListDirty = true;
         }

         this.add(this._listField);
      }
   }

   private final void createMoreVerb() {
      if (this._moreVerb == null) {
         this._moreVerb = (VerbMenuItem)(new Object(new DocViewGuiVerb(21, 344064, EmailResources.getResourceBundle(), 80, this._delegateMgr), 0));
      }
   }

   public final void setItems(DocViewDisplayField$ItemInfo[] items, int selectedIndex, boolean addRetrievePreview, boolean preserveSelectedIndex) {
      if (this._styleInfo == 0) {
         if (items == null || items.length == 0) {
            throw new Object("Invalid items parameter for TOC.");
         }

         int prevSelectedIndex = this._listField.getSelectedIndex();
         this._listField.set(items);
         if (preserveSelectedIndex) {
            if (prevSelectedIndex >= 0) {
               this._listField.setSelectedIndex(prevSelectedIndex);
            }
         } else if (selectedIndex > 0) {
            this._listField.setSelectedIndex(selectedIndex);
         }

         if (addRetrievePreview) {
            if (this._delegateMgr.isMoreSupported()) {
               if (this._morePreview == null) {
                  this._morePreview = new DocViewTOCManager$DocViewMoreAvailableLine(this);
                  this._morePreview.setFont(this._listField.getFont());
                  this.add(this._morePreview);
               } else if (this._morePreview.getIndex() == -1) {
                  this.add(this._morePreview);
               }

               this.createMoreVerb();
               return;
            }
         } else {
            if (this._morePreview != null && this._morePreview.getIndex() != -1) {
               this.delete(this._morePreview);
            }

            this._morePreview = null;
            this._moreVerb = null;
         }
      }
   }

   @Override
   protected final void onDisplay() {
      this._savedMenuFlags = this._delegateMgr.getMenuFlags();
      if (this._styleInfo == 0) {
         this._delegateMgr.processMenuFlag(false, (short)1);
      } else {
         this._delegateMgr.processMenuFlag(true, (short)1);
      }
   }

   @Override
   protected final void onUndisplay() {
      this._delegateMgr.setMenuFlags(this._savedMenuFlags);
      super.onUndisplay();
   }

   protected final void init() {
      if (this._isListDirty) {
         try {
            this.getScreen().getUiEngine().relayout();
            this._isListDirty = false;
         } finally {
            return;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void setFont(Font font) {
      super.setFont(font);
      if (this._styleInfo == 0) {
         if (this._listField != null) {
            this._listField.setFont(font);
            boolean var4 = false /* VF: Semaphore variable */;

            label30:
            try {
               var4 = true;
               this.getScreen().getUiEngine().relayout();
               var4 = false;
            } finally {
               if (var4) {
                  this._isListDirty = true;
                  break label30;
               }
            }
         }

         if (this._morePreview != null) {
            this._morePreview.setFont(font);
         }
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      this._delegateMgr.addCustomMenuVerbs(menu, instance);
      if (this._styleInfo == 0 && this._morePreview != null && this._delegateMgr.isMoreSupported() && this._morePreview.getIndex() != -1) {
         menu.add(this._moreVerb);
      }
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int retValue = super.moveFocus(amount, status, time);
      if (retValue != amount) {
         if (this._delegateMgr instanceof DocViewTextDisplayField) {
            ((DocViewTextDisplayField)this._delegateMgr).checkTrackChanges();
         }
      } else if (amount > 0 && this._morePreview != null && this._morePreview.getIndex() != -1 && AttachmentViewerFactory.isAutoMoreEnabled()) {
         this._moreVerb.run();
      }

      return retValue;
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               handled = this._delegateMgr.invokeAction(action);
         }
      }

      return handled;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (UiInternal.map(keycode) == 27) {
         if (!this.isSelecting()) {
            this._delegateMgr.toggleDisplayMode();
            return true;
         } else {
            return super.keyDown(keycode, time);
         }
      } else {
         return this._delegateMgr.keyDown(keycode, time) ? true : super.keyDown(keycode, time);
      }
   }

   @Override
   public final void setVerticalScroll(int position) {
      label30:
      try {
         if (Application.getApplication() != this._delegateMgr._application) {
            synchronized (this._delegateMgr._application.getAppEventLock()) {
               super.setVerticalScroll(position);
               return;
            }
         }
      } finally {
         break label30;
      }

      super.setVerticalScroll(position);
   }
}
