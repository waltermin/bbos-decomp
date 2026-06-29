package net.rim.device.apps.internal.docview.gui;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.Memory;

final class DocViewImageDisplayField extends DocViewDisplayField {
   private Hashtable _displayHash = new Hashtable(1);
   private final int _flipValue;
   private final byte _presentationValue;
   private static final long IMAGEFIELDDISPLAY_STYLE = 18014398509482020L;

   DocViewImageDisplayField(
      DocViewDataProvider dataProvider,
      DocViewGUIProvider guiProvider,
      DocViewNotify notifyObject,
      DocViewParser docData,
      String currentItemDomID,
      int flipValue,
      byte presentationValue
   ) {
      super(dataProvider, guiProvider, notifyObject, docData, currentItemDomID, null, null, -1, -1, false);
      this._flipValue = flipValue;
      this._presentationValue = presentationValue;
   }

   protected final DocViewImageDisplayField$ImageState getCurrentImageState() {
      return (DocViewImageDisplayField$ImageState)this._displayHash.get(super._currentItemDomID);
   }

   @Override
   protected final int getCustomStringID(int menuStringID) {
      switch (menuStringID) {
         case 2:
            return super.getCustomStringID(menuStringID);
         case 3:
         default:
            return 102;
         case 4:
            return 103;
      }
   }

   @Override
   protected final boolean isCurrentDisplayItemComplete() {
      return true;
   }

   @Override
   protected final boolean selectItem(String itemDomID) {
      DocViewImageField fld = this.createFieldWithImage(super._parsingData, itemDomID, null, 18014398509482020L, this._flipValue);
      if (fld == null) {
         return false;
      }

      DocViewImageDisplayField$ImageState state = this.getStateWithDomID(itemDomID, true);
      DocViewImageDisplayField$ImageState activeState = this.getCurrentImageState();
      this.delete(activeState.getActiveImageField());
      if (state._mainImageField == null) {
         state._mainImageField = fld;
      }

      this.checkPersistedEnlargeAllAreas(state, itemDomID, false);
      this.add(state._mainImageField);
      return true;
   }

   @Override
   protected final void setExtremePosition(boolean top) {
      if (super._fullDocState) {
         Field fld = this.getCurrentImageState().getActiveImageField();
         if (fld instanceof DocViewImageField) {
            DocViewImageField imgField = (DocViewImageField)fld;
            if (top) {
               imgField.scrollToTop();
               return;
            }

            imgField.scrollToBottom();
         }
      }
   }

   private final String getNextDomIDIfNotRetrieved() {
      if (!this.hasMultipleItems()) {
         return null;
      } else {
         DocViewDisplayField$ItemInfo item = this.getPrevNextItem(super._currentItemDomID, true);
         if (item != null && item._arbDOMID != null) {
            return this.isDomIDRetrieved(item._arbDOMID, item._chunkHint) == 2 ? item._arbDOMID : null;
         } else {
            return null;
         }
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      DocViewImageDisplayField$ImageState state = this.getCurrentImageState();
      Field fld = state.getActiveImageField();
      boolean isMainImageField = ObjectUtilities.objEqual(fld, state._mainImageField);
      short flags = this.getMenuFlags();
      if (!isMainImageField) {
         this.processMenuFlag(false, (short)2);
      }

      boolean retValue = super.keyDown(keycode, time);
      this.setMenuFlags(flags);
      return retValue;
   }

   @Override
   protected final boolean allowDefaultAction() {
      return !super._fullDocState;
   }

   @Override
   public final void perform(int menuCode, Object cookie) {
      DocViewImageDisplayField$ImageState state = this.getCurrentImageState();
      Field fld = state.getActiveImageField();
      if (fld instanceof DocViewImageField) {
         DocViewImageField imgField = (DocViewImageField)fld;
         switch (menuCode) {
            case 19:
            case 20:
               this.requestEnlargedAllImage(imgField, state, super._currentItemDomID);
               return;
            case 26:
               this.requestEnlargedImage(imgField, state, super._currentItemDomID);
               return;
            case 27:
               this.requestImageMore();
               return;
            case 32:
               showOriginalImpl(imgField);
               return;
         }
      }

      super.perform(menuCode, cookie);
   }

   private static final void showOriginalImpl(DocViewImageField imgField) {
      if (!imgField.isInOriginalState()) {
         switch (imgField.getRotationValue()) {
            case 90:
               imgField.rotate(270);
               break;
            case 180:
               imgField.rotate(180);
               break;
            case 270:
               imgField.rotate(90);
         }

         if (!imgField.isInOriginalState()) {
            imgField.zoomOriginal();
         }
      }
   }

   private final void requestImageMore() {
      if (this.isMoreSupported()) {
         String nextDomIDToRetrieve = this.getNextDomIDIfNotRetrieved();
         if (nextDomIDToRetrieve != null) {
            int startBlockIndex = this.getStartBlockIndexToRetrieve(nextDomIDToRetrieve);
            if (startBlockIndex > -2) {
               super._descriptor.reset();
               super._descriptor._arbDomID = nextDomIDToRetrieve;
               super._descriptor._chunkHint = startBlockIndex;
               this.executeMore(super._descriptor, false, true);
               return;
            }
         }
      } else {
         this.executeMore(null, true, true);
      }
   }

   protected final void gotEnlargedArea(DocViewImageDisplayField$ImageState state, XYRect enlargedArea) {
      DocViewParsingData parsingData = state.getEnlargedImageData(enlargedArea);
      if (parsingData != null) {
         DocViewImageField imgFld = this.createFieldWithImage(parsingData, null, enlargedArea, 18014398509482020L, this._flipValue);
         if (imgFld != null) {
            if (state._rotationValue != 0) {
               imgFld.rotate(state._rotationValue);
            }

            super._application.invokeLater(new DocViewImageDisplayField$1(this, state, imgFld));
         }
      }
   }

   protected final DocViewImageDisplayField$ImageState getStateWithDomID(String imDomID, boolean createNew) {
      String arbDomID = imDomID != null ? imDomID : DocViewDisplayField.getFirstArbitraryDomID(super._parsingData);
      if (arbDomID != null) {
         synchronized (this._displayHash) {
            if (this._displayHash.containsKey(arbDomID)) {
               return (DocViewImageDisplayField$ImageState)this._displayHash.get(arbDomID);
            } else if (createNew) {
               DocViewImageDisplayField$ImageState state = new DocViewImageDisplayField$ImageState();
               this._displayHash.put(arbDomID, state);
               return state;
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   protected final synchronized void resetEnlargeSentState(int partIndex, String arbDomID) {
      if (arbDomID != null) {
         DocViewImageDisplayField$ImageState chosenState = this.getStateWithDomID(arbDomID, false);
         if (chosenState != null) {
            if (partIndex == 1002 && chosenState._enlargeRequestSent) {
               chosenState._enlargeRequestSent = false;
               chosenState._rotationValue = 0;
               return;
            }

            if (partIndex == 1003 && chosenState._enlargeAllRequestSent) {
               chosenState._enlargeAllRequestSent = false;
               return;
            }
         }
      }

      Enumeration e = this._displayHash.elements();

      while (e.hasMoreElements()) {
         DocViewImageDisplayField$ImageState state = (DocViewImageDisplayField$ImageState)e.nextElement();
         if (partIndex == 1002 && state._enlargeRequestSent) {
            state._enlargeRequestSent = false;
            state._rotationValue = 0;
            return;
         }

         if (partIndex == 1003 && state._enlargeAllRequestSent) {
            state._enlargeAllRequestSent = false;
            return;
         }
      }
   }

   @Override
   protected final boolean doProcessMoreData() {
      return false;
   }

   @Override
   protected final boolean init() {
      if (!super.init()) {
         return false;
      }

      if (super._currentItemDomID == null) {
         super._currentItemDomID = DocViewDisplayField.getFirstArbitraryDomID(super._parsingData);
      }

      if (super._currentItemDomID == null) {
         return false;
      }

      DocViewImageDisplayField$ImageState state = this.getStateWithDomID(super._currentItemDomID, true);
      if (state == null) {
         return false;
      }

      if (state._mainImageField == null) {
         state._mainImageField = this.createFieldWithImage(super._parsingData, super._currentItemDomID, null, 18014398509482020L, this._flipValue);
         if (state._mainImageField != null) {
            state._mainImageField.setName(this.getFileName());
            this.checkPersistedEnlargeAllAreas(state, super._currentItemDomID, true);
            synchronized (super._application.getAppEventLock()) {
               this.add(state._mainImageField);
            }
         }
      }

      if (state._mainImageField == null) {
         super._application.invokeLater(new DocViewImageDisplayField$2(this));
         return false;
      } else {
         return true;
      }
   }

   @Override
   protected final boolean canClose(boolean executeClose) {
      if (!super.canClose(executeClose)) {
         return false;
      }

      DocViewImageDisplayField$ImageState state = this.getCurrentImageState();
      Field activeField = state.getActiveImageField();
      if (activeField == null || activeField == state._mainImageField) {
         return true;
      }

      if (executeClose) {
         synchronized (super._application.getAppEventLock()) {
            this.delete(activeField);
            this.add(state._mainImageField);
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   protected final void addCustomMenuVerbs(Menu menu, int instance) {
      DocViewImageDisplayField$ImageState state = this.getCurrentImageState();
      Field fld = state.getActiveImageField();
      boolean isMainImageField = ObjectUtilities.objEqual(fld, state._mainImageField);
      short flags = this.getMenuFlags();
      if (!isMainImageField) {
         this.processMenuFlag(false, (short)2);
      }

      super.addCustomMenuVerbs(menu, instance);
      this.setMenuFlags(flags);
      if (super._fullDocState && fld instanceof DocViewImageField) {
         if (isMainImageField && this.isMoreAvailable() && this.getNextDomIDIfNotRetrieved() != null) {
            menu.add(new VerbMenuItem(new DocViewGuiVerb(27, 344064, EmailResources.getResourceBundle(), 80, this), 0));
         }

         DocViewImageField imgField = (DocViewImageField)fld;
         if (isMainImageField) {
            int fullImageMenuID = this.getFullImageMenuItemID();
            if (state.isEnlargeAllAreaRetrieved()) {
               menu.add(new VerbMenuItem(new DocViewGuiVerb(19, 65552, DocViewDisplayField._resources, fullImageMenuID, this), 0));
            } else if (!state._enlargeAllRequestSent && this.isMoreSupported()) {
               XYRect cropRect = imgField.getCropRect();
               if (cropRect != null
                  && (cropRect.width > DocViewGUIInternalConstants.SCREEN_WIDTH || cropRect.height > DocViewGUIInternalConstants.SCREEN_HEIGHT)) {
                  menu.add(new VerbMenuItem(new DocViewGuiVerb(20, 65552, DocViewDisplayField._resources, fullImageMenuID, this), 0));
               }
            }
         }

         DocViewImageDisplayField$DocViewSaveImageVerb saveImageVb = new DocViewImageDisplayField$DocViewSaveImageVerb(
            this, this.getFileName(), imgField.getEncodedImage(), imgField.isProtected()
         );
         menu.add(new VerbMenuItem(saveImageVb, saveImageVb.getOrdering()));
         Verb[] verbs = VerbRepository.getVerbRepository(-2843135760572915788L).getVerbs(-753816125826020042L);
         if (verbs != null && verbs.length > 0) {
            ContextObject context = ContextObject.castOrCreate(null);
            byte[] imageData = imgField.getOriginalImageData();
            if (imageData != null) {
               context.put(8849067667159082262L, imageData);

               for (int idx = 0; idx < verbs.length; idx++) {
                  Verb verb = verbs[idx];
                  VerbMenuItem item = new VerbMenuItem(null, verb.getOrdering(), 500, verb, context);
                  menu.add(item);
               }
            }
         }

         if (!state._enlargeRequestSent && imgField.hasRectForDetail() && this.isMoreSupported()) {
            menu.add(new VerbMenuItem(new DocViewGuiVerb(26, 65552, DocViewDisplayField._resources, 98, this), 0));
         }

         if (!imgField.isInOriginalState()) {
            menu.add(
               new VerbMenuItem(
                  new DocViewGuiVerb(32, 131072, ResourceBundle.getBundle(2545338480386147321L, "net.rim.device.apps.internal.resource.Ui"), 111, this), 0
               )
            );
         }
      }
   }

   private final int getFullImageMenuItemID() {
      int retValue = 105;
      switch (this._presentationValue) {
         case 1:
         default:
            return 122;
         case 2:
            retValue = 123;
         case 0:
            return retValue;
      }
   }

   private final void requestEnlargedAllImage(DocViewImageField imgFld, DocViewImageDisplayField$ImageState state, String arbDomID) {
      if (state._enlargeAllImageField == null && state.isEnlargeAllAreaRetrieved()) {
         DocViewParsingData parsingData = state.getEnlargedAllImageData();
         if (parsingData != null) {
            state._enlargeAllImageField = this.createFieldWithImage(parsingData, null, imgFld.getCropRect(), 18014398509482020L, this._flipValue);
         }
      }

      if (state._enlargeAllImageField != null && state._enlargeAllImageField != imgFld) {
         synchronized (super._application.getAppEventLock()) {
            this.delete(imgFld);
            this.add(state._enlargeAllImageField);
         }
      } else {
         if (!state._enlargeAllRequestSent && this.isMoreSupported()) {
            super._descriptor.reset();
            super._descriptor._rectToEnlarge = imgFld.getCropRect();
            super._descriptor._partIndex = 1003;
            super._descriptor._chunkSize = 64000;
            super._descriptor._arbDomID = arbDomID;
            int maxWidth = Math.min(super._descriptor._rectToEnlarge.width, DocViewGUIInternalConstants.SCREEN_WIDTH * 8 / 3);
            int maxHeight = Math.min(super._descriptor._rectToEnlarge.height, DocViewGUIInternalConstants.SCREEN_HEIGHT * 8 / 3);
            super._descriptor._reqImageWidth = maxWidth;
            super._descriptor._reqImageHeight = maxHeight;
            if (maxWidth > DocViewGUIInternalConstants.SCREEN_WIDTH && maxHeight > DocViewGUIInternalConstants.SCREEN_HEIGHT) {
               if (DocViewGUIInternalConstants.SCREEN_WIDTH >= DocViewGUIInternalConstants.SCREEN_HEIGHT) {
                  if (super._descriptor._rectToEnlarge.width < super._descriptor._rectToEnlarge.height) {
                     super._descriptor._reqImageWidth = maxHeight;
                     super._descriptor._reqImageHeight = maxWidth;
                  }
               } else if (super._descriptor._rectToEnlarge.width >= super._descriptor._rectToEnlarge.height) {
                  super._descriptor._reqImageWidth = maxHeight;
                  super._descriptor._reqImageHeight = maxWidth;
               }
            }

            synchronized (this) {
               state._enlargeAllRequestSent = true;
            }

            if (!this.executeMore(super._descriptor, false, true)) {
               synchronized (this) {
                  state._enlargeAllRequestSent = false;
                  return;
               }
            }
         }
      }
   }

   private final void requestEnlargedImage(DocViewImageField imgFld, DocViewImageDisplayField$ImageState state, String arbDomID) {
      XYRect rectToEnlarge = imgFld.getRectForDetail();
      if (rectToEnlarge != null) {
         int rotValue = imgFld.getRotationValue();
         if (state._enlargedImageField != null && state._enlargedImageField != imgFld) {
            XYRect rect = state._enlargedImageField.getCropRect();
            if (rect != null && rect.equals(rectToEnlarge)) {
               synchronized (super._application.getAppEventLock()) {
                  showOriginalImpl(state._enlargedImageField);
                  if (rotValue > 0) {
                     state._enlargedImageField.rotate(rotValue);
                  }

                  this.delete(imgFld);
                  this.add(state._enlargedImageField);
                  return;
               }
            }
         }

         if (!state._enlargeRequestSent && this.isMoreSupported()) {
            super._descriptor.reset();
            super._descriptor._partIndex = 1002;
            super._descriptor._reqImageWidth = rotValue != 0 && rotValue != 180
               ? DocViewGUIInternalConstants.SCREEN_HEIGHT
               : DocViewGUIInternalConstants.SCREEN_WIDTH;
            super._descriptor._reqImageHeight = rotValue != 0 && rotValue != 180
               ? DocViewGUIInternalConstants.SCREEN_WIDTH
               : DocViewGUIInternalConstants.SCREEN_HEIGHT;
            super._descriptor._rectToEnlarge = rectToEnlarge;
            super._descriptor._arbDomID = arbDomID;
            synchronized (this) {
               state.discardCurrentEnlargeArea();
               state._enlargeRequestSent = true;
               state._rotationValue = rotValue;
            }

            if (!this.executeMore(super._descriptor, false, true)) {
               synchronized (this) {
                  state._enlargeRequestSent = false;
                  state._rotationValue = 0;
                  return;
               }
            }
         }
      }
   }

   private final DocViewImageField createFieldWithImage(DocViewParsingData parsingData, String arDomID, XYRect cropRect, long style, int flipValue) {
      DocViewImageField imgField = null;
      String arbDomID = arDomID == null ? DocViewDisplayField.getFirstArbitraryDomID(parsingData) : arDomID;
      Object obj = parsingData.getObjectWithDOMID(arbDomID);
      if (obj instanceof DocViewImageData) {
         DocViewImageData image = (DocViewImageData)obj;
         byte[] imgContents = image.getImageContents();
         if (imgContents != null && imgContents.length > 0) {
            try {
               XYRect origRect = cropRect;
               if (origRect == null) {
                  origRect = new XYRect(0, 0, image.getOriginalWidth(), image.getOriginalHeight());
               }

               imgField = new DocViewImageField(imgContents, origRect, style | 4503599627370496L, this._presentationValue == 1 || this._presentationValue == 2);
               imgField.setFlipValue(flipValue);
            } finally {
               return imgField;
            }
         }
      }

      return imgField;
   }

   private final void checkPersistedEnlargeAllAreas(DocViewImageDisplayField$ImageState state, String arbDomID, boolean firstImage) {
      if (state._enlargeAllImageField == null) {
         XYRect cropRect = state._mainImageField.getCropRect();
         if (cropRect != null && (cropRect.width > DocViewGUIInternalConstants.SCREEN_WIDTH || cropRect.height > DocViewGUIInternalConstants.SCREEN_HEIGHT)) {
            DocViewImageDisplayField$EnlargeAllPersistentQuery query = new DocViewImageDisplayField$EnlargeAllPersistentQuery();
            query._state = state;
            query._arbDomID = arbDomID;
            query._firstImage = firstImage;
            query._cropRect = cropRect;
            this.parseCustomData((byte)2, query);
         }
      }
   }

   protected final void gotEnlargedAllArea(DocViewImageDisplayField$ImageState state, XYRect enlargedArea) {
      if (state._enlargeAllImageField == null) {
         if (state.isEnlargeAllAreaRetrieved()) {
            DocViewParsingData parsingData = state.getEnlargedAllImageData();
            if (parsingData != null) {
               Memory.maximizeContiguousRAM();
               state._enlargeAllImageField = this.createFieldWithImage(parsingData, null, enlargedArea, 18014398509482020L, this._flipValue);
            }
         }

         if (state._enlargeAllImageField != null
            && state._enlargeAllImageField.getIndex() == -1
            && !ObjectUtilities.objEqual(state.getActiveImageField(), state._enlargeAllImageField)) {
            super._application.invokeLater(new DocViewImageDisplayField$3(this, state));
         }
      }
   }
}
