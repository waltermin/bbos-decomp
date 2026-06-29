package net.rim.device.api.ui.container;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.theme.Tag;

class TitleStatusManager extends Manager {
   private VerticalFieldManager _vfm;
   private VerticalFieldManager _bannerManager;
   private VerticalFieldManager _titleManager;
   private VerticalFieldManager _statusManager;
   private Field _title;
   private static Tag BANNER_TAG = Tag.create("banner");
   private static Tag TITLE_TAG = Tag.create("title");
   private static Tag CLIENT_TAG = Tag.create("client");
   private static Tag STATUS_TAG = Tag.create("status");

   public TitleStatusManager(long style) {
      super(validateStyle(style));
      this._vfm = new VerticalFieldManager(validateStyleVFM(style));
      this._vfm.setTag(CLIENT_TAG);
      super.add(this._vfm);
   }

   @Override
   public void setId(String idName) {
      super.setId(idName);
      if (this._titleManager != null) {
         this._titleManager.setId(idName);
      }

      this._vfm.setId(idName);
      if (this._statusManager != null) {
         this._statusManager.setId(idName);
      }
   }

   public Manager getMainManager() {
      return this._vfm;
   }

   @Override
   public void add(Field field) {
      this.getMainManager().add(field);
   }

   @Override
   public void delete(Field field) {
      this.getMainManager().delete(field);
   }

   @Override
   public void deleteRange(int start, int count) {
      this.getMainManager().deleteRange(start, count);
   }

   @Override
   public String getAccessibleName() {
      return this._title != null ? this._title.getAccessibleName() : null;
   }

   @Override
   public void insert(Field field, int index) {
      this.getMainManager().insert(field, index);
   }

   @Override
   public void replace(Field oldField, Field newField) {
      this.getMainManager().replace(oldField, newField);
   }

   @Override
   public void setHorizontalQuantization(int horizontalQuanta) {
      this.getMainManager().setHorizontalQuantization(horizontalQuanta);
   }

   public void setBanner(Field banner) {
      if (this._bannerManager != null) {
         Manager old = this._bannerManager;
         this._bannerManager = null;
         super.delete(old);
         old.deleteAll();
      }

      if (banner != null) {
         this._bannerManager = new VerticalFieldManager(1152921504606846976L);
         this._bannerManager.setTag(BANNER_TAG);
         this._bannerManager.setId(this._vfm.getId());
         if (banner instanceof LabelField && (banner.getTag() == null || banner.getTag().toString().equals("object")) && banner.getId() == null) {
            banner.setId("banner");
         }

         this._bannerManager.add(banner);
         super.insert(this._bannerManager, 0);
      }
   }

   public void setStatus(Field status) {
      if (this._statusManager != null) {
         Manager old = this._statusManager;
         this._statusManager = null;
         super.delete(old);
         old.deleteAll();
      }

      if (status != null) {
         this._statusManager = new VerticalFieldManager(1152921504606846976L);
         this._statusManager.setTag(STATUS_TAG);
         this._statusManager.setId(this._vfm.getId());
         if (!this.isStyle(1073741824)) {
            SeparatorField separator = new SeparatorField(8388608);
            separator.setId("status");
            this._statusManager.insert(separator, 0);
         }

         this._statusManager.add(status);
         super.add(this._statusManager);
      }
   }

   public void setTitle(Field title) {
      if (this._titleManager != null) {
         Manager old = this._titleManager;
         this._titleManager = null;
         super.delete(old);
         old.deleteAll();
      }

      this._title = title;
      if (title != null) {
         this._titleManager = new VerticalFieldManager(1153484454560268288L);
         this._titleManager.setTag(TITLE_TAG);
         this._titleManager.setId(this._vfm.getId());
         if (title instanceof LabelField && (title.getTag() == null || title.getTag().toString().equals("object")) && title.getId() == null) {
            title.setId("title");
         }

         this._titleManager.add(title);
         if (!this.isStyle(2147483648L)) {
            SeparatorField separator = new SeparatorField(8388608);
            separator.setId("title");
            this._titleManager.insert(separator, 1);
         }

         super.insert(this._titleManager, this._bannerManager == null ? 0 : 1);
      }
   }

   public void setTitle(String text) {
      if (!(this._title instanceof LabelField)) {
         this.setTitle(new LabelField(text, 64));
      } else {
         LabelField label = (LabelField)this._title;
         label.setText(text);
      }
   }

   public void setTitle(ResourceBundleFamily family, int id) {
      if (!(this._title instanceof LabelField)) {
         LabelField label = new LabelField("", 64);
         label.setText(family, id);
         this.setTitle(label);
      } else {
         LabelField label = (LabelField)this._title;
         label.setText(family, id);
      }
   }

   @Override
   public void setVerticalQuantization(int verticalQuanta) {
      this.getMainManager().setVerticalQuantization(verticalQuanta);
   }

   @Override
   protected void sublayout(int width, int height) {
      this.setExtent(width, height);
      int bottom = height;
      int bfmheight = 0;
      if (this._bannerManager != null) {
         this.layoutChild(this._bannerManager, width, height);
         this.setPositionChild(this._bannerManager, 0, 0);
         bfmheight = this._bannerManager.getHeight();
      }

      height -= bfmheight;
      int tfmheight = 0;
      if (this._titleManager != null) {
         this.layoutChild(this._titleManager, width, height);
         this.setPositionChild(this._titleManager, 0, bfmheight);
         tfmheight = this._titleManager.getHeight();
      }

      height -= tfmheight;
      int sfmheight = 0;
      if (this._statusManager != null) {
         this.layoutChild(this._statusManager, width, height);
         sfmheight = this._statusManager.getHeight();
         this.setPositionChild(this._statusManager, 0, bottom - sfmheight);
      }

      height -= sfmheight;
      this.setPositionChild(this._vfm, 0, tfmheight + bfmheight);
      this.layoutChild(this._vfm, width, height);
   }

   private static long validateStyle(long style) {
      style &= -4486007441326081L;
      if (Graphics.isColor()) {
         style |= 3221225472L;
      }

      return style;
   }

   private static long validateStyleVFM(long style) {
      if ((style & 844424930131968L) == 0) {
         style |= 281474976710656L;
      }

      if ((style & 52776558133248L) == 0) {
         style |= 17592186044416L;
      }

      if ((style & 3377699720527872L) == 0) {
         style |= 2251799813685248L;
      }

      if ((style & 211106232532992L) == 0) {
         style |= 140737488355328L;
      }

      style &= 292716448017547264L;
      return style | 3458764513820540928L;
   }
}
