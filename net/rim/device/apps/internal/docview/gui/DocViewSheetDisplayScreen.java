package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.util.IntHashtable;

final class DocViewSheetDisplayScreen extends DocViewDisplayScreen {
   DocViewSheetDisplayScreen(IntHashtable paramsHash) {
      super(paramsHash);
      super._displayField = new DocViewSheetDisplayField(
         this, this, null, super._docData, super._startArbDomID, super._themeScreenBgColor, super._themeScreenForeColor
      );
      super._optionsVerb = new OptionsVerb(DocViewDisplayScreen._resources.getString(18), this);
   }

   @Override
   protected final boolean reloadNullEmbeddedObject(boolean showTrackChanges) {
      DocViewDisplayScreen$EmbeddedObjectInfo embeddedObject = (DocViewDisplayScreen$EmbeddedObjectInfo)super._embeddedObjHash.get("CustomEmbeddedDomID");
      if (embeddedObject != null && embeddedObject._displayScreen != null) {
         embeddedObject._displayScreen.closeScreen();
         super._embeddedObjHash.remove("CustomEmbeddedDomID");
         super._docData.getParsingData().setTrackChangesOnStatus(showTrackChanges);
         super._displayField.perform(5, null);
         return true;
      } else {
         return false;
      }
   }
}
