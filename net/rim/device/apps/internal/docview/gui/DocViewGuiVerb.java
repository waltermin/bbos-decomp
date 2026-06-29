package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class DocViewGuiVerb extends Verb {
   private int _behaviour;
   private BaseMenuModel _baseMenuModel;
   private Object _cookie;
   static final int FIND_VERB;
   static final int FIND_NEXT_VERB;
   static final int DISPLAY_SCREEN_TOC;
   static final int DISPLAY_SCREEN_NEXT_ITEM;
   static final int DISPLAY_SCREEN_PREV_ITEM;
   static final int SHEET_SCREEN_VIEW_CELL;
   static final int SHEET_SCREEN_GOTO_CELL;
   static final int SHEET_SCREEN_COPY_CELL;
   static final int SHEET_SCREEN_SHOW_HIDE;
   static final int DISPLAY_SCREEN_MORE;
   static final int DISPLAY_SCREEN_JUMP;
   static final int DISPLAY_SCREEN_TRACKCHANGES;
   static final int OPTIONS_VERB;
   static final int IMAGE_SCREEN_ZOOMIN;
   static final int IMAGE_SCREEN_ZOOMOUT;
   static final int IMAGE_SCREEN_ROTATE;
   static final int IMAGE_SCREEN_ZOOMALL;
   static final int IMAGE_SCREEN_ZOOM1TO1;
   static final int DISPLAY_SCREEN_SAVE;
   static final int IMAGE_SCREEN_VIEWENLARGEALL;
   static final int IMAGE_SCREEN_ENLARGEALL;
   static final int TOC_SCREEN_MORE;
   static final int TEXT_SCREEN_RETRIEVESLIDE;
   static final int DISPLAY_SCREEN_RETRIEVEBLOCK;
   static final int DISPLAY_SCREEN_EMBRETRIEVELARGECHUNK;
   static final int DISPLAY_SCREEN_EMBRETRIEVEDEFCHUNK;
   static final int IMAGE_SCREEN_GETDETAILVIEW;
   static final int IMAGE_SCREEN_MORE;
   static final int TEXT_SCREEN_VIEWPAGES;
   static final int TEXT_SCREEN_VIEWTEXT;
   static final int TEXT_SCREEN_VIEWBOTH;
   static final int TEXT_SCREEN_PLAY;
   static final int IMAGE_SCREEN_SHOWORIGINAL;
   static final int DISPLAY_SCREEN_DOCINFO;

   DocViewGuiVerb(int behaviour, BaseMenuModel baseMenuModel) {
      super(0);
      this._behaviour = behaviour;
      this._baseMenuModel = baseMenuModel;
      switch (behaviour) {
         case 0:
         default:
            super._ordering = 196624;
            return;
         case 1:
            super._ordering = 196640;
         case -1:
      }
   }

   DocViewGuiVerb(int behaviour, int ordering, ResourceBundleFamily rb, int rbKey, BaseMenuModel baseMenuModel) {
      super(ordering, rb, rbKey);
      this._behaviour = behaviour;
      this._baseMenuModel = baseMenuModel;
   }

   @Override
   public final Object invoke(Object context) {
      this._baseMenuModel.perform(this._behaviour, this._cookie);
      return null;
   }

   final int getBehaviour() {
      return this._behaviour;
   }

   final void setCookie(Object cookie) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final String toString() {
      switch (this._behaviour) {
         case -1:
            return super.toString();
         case 0:
         default:
            return CommonResources.getString(9023);
         case 1:
            return CommonResources.getString(9024);
      }
   }
}
