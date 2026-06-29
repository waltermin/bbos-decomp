package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class DocViewGuiVerb extends Verb {
   private int _behaviour;
   private BaseMenuModel _baseMenuModel;
   private Object _cookie;
   static final int FIND_VERB = 0;
   static final int FIND_NEXT_VERB = 1;
   static final int DISPLAY_SCREEN_TOC = 2;
   static final int DISPLAY_SCREEN_NEXT_ITEM = 3;
   static final int DISPLAY_SCREEN_PREV_ITEM = 4;
   static final int SHEET_SCREEN_VIEW_CELL = 5;
   static final int SHEET_SCREEN_GOTO_CELL = 6;
   static final int SHEET_SCREEN_COPY_CELL = 7;
   static final int SHEET_SCREEN_SHOW_HIDE = 8;
   static final int DISPLAY_SCREEN_MORE = 9;
   static final int DISPLAY_SCREEN_JUMP = 10;
   static final int DISPLAY_SCREEN_TRACKCHANGES = 11;
   static final int OPTIONS_VERB = 12;
   static final int IMAGE_SCREEN_ZOOMIN = 13;
   static final int IMAGE_SCREEN_ZOOMOUT = 14;
   static final int IMAGE_SCREEN_ROTATE = 15;
   static final int IMAGE_SCREEN_ZOOMALL = 16;
   static final int IMAGE_SCREEN_ZOOM1TO1 = 17;
   static final int DISPLAY_SCREEN_SAVE = 18;
   static final int IMAGE_SCREEN_VIEWENLARGEALL = 19;
   static final int IMAGE_SCREEN_ENLARGEALL = 20;
   static final int TOC_SCREEN_MORE = 21;
   static final int TEXT_SCREEN_RETRIEVESLIDE = 22;
   static final int DISPLAY_SCREEN_RETRIEVEBLOCK = 23;
   static final int DISPLAY_SCREEN_EMBRETRIEVELARGECHUNK = 24;
   static final int DISPLAY_SCREEN_EMBRETRIEVEDEFCHUNK = 25;
   static final int IMAGE_SCREEN_GETDETAILVIEW = 26;
   static final int IMAGE_SCREEN_MORE = 27;
   static final int TEXT_SCREEN_VIEWPAGES = 28;
   static final int TEXT_SCREEN_VIEWTEXT = 29;
   static final int TEXT_SCREEN_VIEWBOTH = 30;
   static final int TEXT_SCREEN_PLAY = 31;
   static final int IMAGE_SCREEN_SHOWORIGINAL = 32;
   static final int DISPLAY_SCREEN_DOCINFO = 33;

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
