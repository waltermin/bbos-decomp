package net.rim.device.apps.internal.docview.gui;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;
import net.rim.device.apps.internal.docview.core.ArznDocumentHeader;
import net.rim.device.apps.internal.docview.core.ArznImageData;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.ui.Edit$BidiLineRuns;
import net.rim.device.internal.ui.RichText;

final class AttachmentViewerFactory {
   static final int MIN_ASSGFONTSIZE = 7;
   static final int MIN_FONT_SIZE_UTYPE = 3;
   private static final byte FONTSIZE_REGIONS = 6;
   private static final int SCALED_HEIGHT_PORTRAIT = (int)(DocViewGUIInternalConstants.SCREEN_WIDTH * 4622382067542392832L / 4620974692658839552L);
   private static IntHashtable _paramsHash;
   private static final int[] AUDIO_CODECS = new int[]{11, 3, 7, -805044219, 1718183726, 10, -804651007, 51, -805043264, 944130375, 942393, 16187408};

   static final boolean isAudioStreamingSupported() {
      return InternalServices.isSoftwareCapable(2);
   }

   private static final int getDeviceCodecValue() {
      if (isAudioStreamingSupported()) {
         for (int i = 0; i < AUDIO_CODECS.length; i++) {
            if (Audio.isCodecSupported(AUDIO_CODECS[i])) {
               return AUDIO_CODECS[i];
            }
         }
      }

      return -1;
   }

   static final int getAttachmentServerCodecValue() {
      int deviceCodec = getDeviceCodecValue();
      if (deviceCodec != -1) {
         switch (deviceCodec) {
            case 3:
               return 85;
            case 7:
               return 0;
            case 11:
               return 49;
         }
      }

      return -1;
   }

   static final String getDocInfoDescription(ResourceBundle resources, int commandCode) {
      int resId = -1;
      switch (commandCode) {
         case 40:
            resId = 63;
            break;
         case 41:
         case 42:
            resId = 64;
            break;
         case 43:
         case 44:
            resId = 65;
            break;
         case 45:
            resId = 66;
            break;
         case 46:
            resId = 67;
            break;
         case 47:
         case 48:
            resId = 68;
            break;
         case 78:
            resId = 97;
            break;
         case 89:
            resId = 77;
            break;
         case 90:
            resId = 78;
            break;
         case 91:
            resId = 79;
            break;
         case 92:
            resId = 116;
            break;
         case 93:
            resId = 117;
      }

      if (resId != -1) {
         String retValue = resources.getString(resId);
         if (retValue != null && retValue.length() > 0) {
            return ((StringBuffer)(new Object())).append(retValue).append(": ").toString();
         }
      }

      return "";
   }

   static final byte getDesiredNumberOfChannels(int deviceCodec) {
      return (byte)(deviceCodec != -1 ? 1 : -1);
   }

   static final String processDocInfoTimeString(String inputDocInfoValue) {
      if (inputDocInfoValue != null) {
         int stringLength = inputDocInfoValue.length();
         if (stringLength > 0) {
            IntVector colonIndices = (IntVector)(new Object());
            int colonSearchIndex = inputDocInfoValue.indexOf(58);
            boolean firstOcc = true;

            while (colonSearchIndex != -1) {
               if (firstOcc) {
                  int digitIndex = colonSearchIndex - 1;
                  if (digitIndex >= 0 && Character.isDigit(inputDocInfoValue.charAt(digitIndex))) {
                     digitIndex--;
                     if (digitIndex < 0 || !Character.isDigit(inputDocInfoValue.charAt(digitIndex))) {
                        colonIndices.addElement(digitIndex + 1);
                     }
                  }

                  firstOcc = false;
               }

               int digitIndex = colonSearchIndex + 1;
               if (digitIndex < stringLength && Character.isDigit(inputDocInfoValue.charAt(digitIndex))) {
                  digitIndex++;
                  if (digitIndex == stringLength || !Character.isDigit(inputDocInfoValue.charAt(digitIndex))) {
                     colonIndices.addElement(digitIndex - 1);
                  }
               }

               colonSearchIndex = inputDocInfoValue.indexOf(58, colonSearchIndex + 1);
            }

            int size = colonIndices.size();
            if (size <= 0) {
               colonIndices = null;
               return inputDocInfoValue;
            }

            StringBuffer tempBuffer = (StringBuffer)(new Object(inputDocInfoValue));

            for (int i = size - 1; i >= 0; i--) {
               tempBuffer.insert(colonIndices.elementAt(i), 0);
            }

            colonIndices = null;
            return tempBuffer.toString();
         }
      }

      return "";
   }

   static final String getErrorString(short errorTag) {
      int error = 3;
      switch (errorTag) {
         case 1:
            error = 17;
            break;
         case 2:
            error = 16;
            break;
         case 3:
            error = 11;
            break;
         case 4:
            error = 6;
            break;
         case 5:
            error = 1;
            break;
         case 246:
            error = 104;
            break;
         case 247:
            error = 74;
            break;
         case 249:
            error = 71;
            break;
         case 250:
            error = 12;
            break;
         case 252:
            error = 14;
            break;
         case 253:
            error = 10;
            break;
         case 255:
            error = 13;
            break;
         case 30000:
            error = 75;
      }

      return ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView").getString(error);
   }

   static final int convertFromParsedFontStyle(int parsedFontStyle) {
      if (parsedFontStyle == -1) {
         return 0;
      }

      int guiFontStyle = 0;
      if ((parsedFontStyle & 1) != 0) {
         guiFontStyle |= 2;
      }

      if ((parsedFontStyle & 2) != 0) {
         guiFontStyle |= 1;
      }

      if ((parsedFontStyle & 4) != 0) {
         guiFontStyle |= 4;
      }

      if ((parsedFontStyle & 8) != 0) {
         guiFontStyle |= 32;
      }

      return guiFontStyle;
   }

   static final Field getPreviewField(Object previewData, String domID, FontFactory fontFactory, int scale, Font font, int flipImage, boolean testAsBitmap) {
      Field previewField = null;
      if (!(previewData instanceof DocViewSheetData)) {
         if (previewData instanceof DocViewImageData) {
            try {
               DocViewImageData imgData = (DocViewImageData)previewData;
               byte[] binData = imgData.getImageContents();
               EncodedImage img = EncodedImage.createEncodedImage(binData, 0, binData.length);
               if (testAsBitmap) {
                  Bitmap testBmp = img.getBitmap();
                  if (testBmp != null) {
                     testBmp = null;
                  }
               }

               if (scale > 0 && scale != 1) {
                  int fpScale = Fixed32.toFP(scale);
                  img = img.scaleImage32(fpScale, fpScale);
               }

               previewField = flipImage == 0 ? new CustomBitmapField(img, 18014398509481988L) : new FlipBitmapField(img, 18014398509481988L, flipImage);
               ((CustomBitmapField)previewField).setDomID(domID);
            } finally {
               return previewField;
            }
         }
      } else {
         DocViewSheetData sheetData = (DocViewSheetData)previewData;
         if (sheetData.getNumberOfRows() > 0 && sheetData.getNumberOfCols() > 0) {
            previewField = new CustomListField(sheetData, fontFactory, (short)4096, (byte)(Graphics.isColor() ? 1 : 2), 36028797018963968L);
            sheetData.setColumnWidth((short)(DocViewGUIInternalConstants.SCREEN_WIDTH / sheetData.getNumberOfCols() - 3));
            if (font != null) {
               ((CustomListField)previewField)
                  .customSetFont(font, DocViewOptions.getOptions().getUseOriginalDocFont(), DocViewOptions.getOptions().getDocFontStyle());
            }

            ((CustomListField)previewField).initFocus();
         }
      }

      return previewField;
   }

   static final IntIntHashtable processFontSizes(int[] fontSizes) {
      int length = fontSizes.length;
      if (length == 0) {
         throw new Object("Invalid fonts sizes length.");
      }

      IntIntHashtable retValue = (IntIntHashtable)(new Object(length));
      if (length == 1) {
         retValue.put(fontSizes[0], Ui.convertSize(8, 3, 4194307));
         return retValue;
      }

      if (length <= 3) {
         for (int i = 0; i < length; i++) {
            int cpdValue = Ui.convertSize(7 + 2 * i, 3, 4194307);
            retValue.put(fontSizes[i], cpdValue);
         }
      } else if (length <= 6) {
         for (int i = 0; i < length; i++) {
            int cpdValue = Ui.convertSize(7 + i, 3, 4194307);
            retValue.put(fontSizes[i], cpdValue);
         }
      } else {
         for (int i = 0; i < 6; i++) {
            int cpdValue = Ui.convertSize(7 + i, 3, 4194307);
            retValue.put(fontSizes[i], cpdValue);
         }

         int maxValueCptd = retValue.get(fontSizes[5]);

         for (int i = 6; i < length; i++) {
            retValue.put(fontSizes[i], maxValueCptd);
         }
      }

      return retValue;
   }

   static final int convertFromParsedForeColor(int foreColor) {
      return foreColor == -1 ? -1 : foreColor;
   }

   static final int convertFromParsedBgColor(int bgColor) {
      return bgColor == -1 ? -1 : bgColor;
   }

   static final byte getPresentationValue(byte docType, byte docSubtype) {
      byte retValue = 0;
      if (docType == 0) {
         if (docSubtype == 3) {
            return 1;
         }

         retValue = 2;
      }

      return retValue;
   }

   static final boolean isAutoMoreEnabled() {
      return MessageListOptions.getOptions().getFlag(64);
   }

   static final boolean isMoreAllSupported(EmailMessageModel model) {
      return model != null ? EmailMoreVerb.isMoreAllAllowed(model) : false;
   }

   static final int getAttachmentIconIndex(byte type, byte subtype) {
      int iconIndex = -1;
      switch (type) {
         case -1:
         case 3:
         case 4:
            break;
         case 0:
         default:
            switch (subtype) {
               case 0:
                  return iconIndex;
               case 1:
               default:
                  return 65542;
               case 2:
                  return 65539;
               case 3:
                  return 65538;
               case 4:
                  return 65536;
               case 5:
                  return 65540;
               case 6:
                  return 65541;
            }
         case 1:
            return 65537;
         case 2:
            return 65543;
         case 5:
            iconIndex = 65544;
      }

      return iconIndex;
   }

   static final int getParsingErrorID(int parseStatus) {
      int errorID = -1;
      switch (parseStatus) {
         case 1:
         default:
            return 39;
         case 2:
            errorID = 40;
         case 0:
            return errorID;
      }
   }

   static final boolean isTypeRequestAllChunks(byte docType) {
      return docType == 2;
   }

   static final Object getDocData(
      int messageID, int morePartID, String archiveIndicator, String embeddedDOMId, boolean isRequestAllChunks, int startBlockIndex, int endBlockIndex
   ) {
      Object docData = null;
      DocViewAttachmentPersist persistInstance = DocViewAttachmentPersist.getInstance();
      if (isRequestAllChunks) {
         int ebIdx = endBlockIndex == -1 ? persistInstance.getAttachmentBlockCount(messageID, morePartID, archiveIndicator, embeddedDOMId) - 1 : endBlockIndex;

         for (int i = startBlockIndex; i <= ebIdx; i++) {
            byte[] chunkData = persistInstance.getUCSData(messageID, morePartID, archiveIndicator, embeddedDOMId, i);
            if (chunkData == null) {
               return null;
            }

            if (docData == null) {
               docData = new Object(ebIdx - startBlockIndex + 1);
            }

            ((Vector)docData).addElement(chunkData);
         }
      } else {
         docData = persistInstance.getUCSData(messageID, morePartID, archiveIndicator, embeddedDOMId, startBlockIndex);
      }

      return docData;
   }

   static final int getDesiredRenderedWidth(boolean isPresentation) {
      if (isPresentation) {
         return DocViewGUIInternalConstants.SCREEN_WIDTH < DocViewGUIInternalConstants.SCREEN_HEIGHT
            ? DocViewGUIInternalConstants.SCREEN_HEIGHT
            : DocViewGUIInternalConstants.SCREEN_WIDTH;
      } else {
         return DocViewGUIInternalConstants.SCREEN_WIDTH;
      }
   }

   static final int getDesiredRenderedHeight(boolean isPresentation) {
      if (isPresentation) {
         return DocViewGUIInternalConstants.SCREEN_WIDTH < DocViewGUIInternalConstants.SCREEN_HEIGHT
            ? DocViewGUIInternalConstants.SCREEN_WIDTH
            : DocViewGUIInternalConstants.SCREEN_HEIGHT;
      } else {
         return SCALED_HEIGHT_PORTRAIT;
      }
   }

   static final String constructCustomDomIDString(String embeddedDomID, String customInfoString) {
      return ((StringBuffer)(new Object()))
         .append(embeddedDomID != null ? ((StringBuffer)(new Object())).append(embeddedDomID).append("/").toString() : "")
         .append(customInfoString)
         .toString();
   }

   static final String constructCustomDomIDStringEx(String embeddedDomID, String arbDomID, String customInfoString) {
      return ((StringBuffer)(new Object()))
         .append(embeddedDomID != null ? ((StringBuffer)(new Object())).append(embeddedDomID).append("/").toString() : "")
         .append(arbDomID)
         .append("/")
         .append(customInfoString)
         .toString();
   }

   static final int getEndBlockIndexWithArbDomID(
      int messageID, int morePartID, String archiveIndicator, String embeddedDOMId, String itemDomID, int totalBlocks
   ) {
      int endBlockIndex = readInt(
         DocViewAttachmentPersist.getInstance()
            .getUCSData(messageID, morePartID, archiveIndicator, constructCustomDomIDStringEx(embeddedDOMId, itemDomID, "DomIDLimits"), 0),
         4
      );
      if (endBlockIndex == -2) {
         endBlockIndex = totalBlocks - 1;
      }

      return endBlockIndex;
   }

   static final int getStartBlockIndexWithArbDomID(int messageID, int morePartID, String archiveIndicator, String embeddedDOMId, String itemDomID) {
      return readInt(
         DocViewAttachmentPersist.getInstance()
            .getUCSData(messageID, morePartID, archiveIndicator, constructCustomDomIDStringEx(embeddedDOMId, itemDomID, "DomIDLimits"), 0),
         0
      );
   }

   static final int readInt(byte[] param0, int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 0: aload 0
      // 1: iload 1
      // 2: bipush 0
      // 3: invokestatic net/rim/device/apps/internal/docview/gui/DocViewUtilities.readInt ([BIZ)I
      // 6: ireturn
      // 7: astore 2
      // 8: bipush -1
      // a: ireturn
      // b: astore 2
      // c: bipush -1
      // e: ireturn
      // try (0 -> 4): 5 null
      // try (0 -> 4): 8 null
   }

   static final boolean writeInt(byte[] param0, int param1, int param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 0: aload 0
      // 1: iload 1
      // 2: iload 2
      // 3: bipush 0
      // 4: invokestatic net/rim/device/apps/internal/docview/gui/DocViewUtilities.writeInt ([BIIZ)V
      // 7: bipush 1
      // 8: ireturn
      // 9: astore 3
      // a: bipush 0
      // b: ireturn
      // c: astore 3
      // d: bipush 0
      // e: ireturn
      // try (0 -> 6): 7 null
      // try (0 -> 6): 10 null
   }

   static final void constructAndShowNewDisplayScreen(
      int messageID,
      int morePartID,
      String archiveIndicator,
      String embeddedDOMId,
      boolean isRequestAllChunks,
      int startBlockIndex,
      int endBlockIndex,
      String name,
      Object context,
      int themeBgColor,
      int themeForeColor,
      boolean displayTrackChanges,
      int applicationID,
      boolean displayImageAsSlideshow,
      byte presentationValue
   ) {
      Object docData = getDocData(messageID, morePartID, archiveIndicator, embeddedDOMId, isRequestAllChunks, startBlockIndex, endBlockIndex);
      if (docData == null) {
         UiApplication.getUiApplication()
            .invokeLater(new ErrorDlg(ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView").getString(94)));

         try {
            DocViewDisplayScreenInstance.getForwardScreenInstance(messageID, applicationID).onDocDisplayEnd();
         } finally {
            return;
         }
      } else {
         label41:
         try {
            DocViewDisplayScreenInstance.getActivePartInstance(messageID, applicationID)._screen.releaseRefs();
         } finally {
            break label41;
         }

         DocViewAttachmentPersist persistInstance = DocViewAttachmentPersist.getInstance();
         processData(
            name,
            docData,
            context,
            startBlockIndex,
            persistInstance.getAttachmentBlockCount(messageID, morePartID, archiveIndicator, embeddedDOMId),
            persistInstance.getAttachmentRetrievedBlockCount(messageID, morePartID, archiveIndicator, embeddedDOMId),
            messageID,
            morePartID,
            themeBgColor,
            themeForeColor,
            displayTrackChanges,
            false,
            null,
            null,
            true,
            applicationID,
            displayImageAsSlideshow,
            presentationValue
         );
      }
   }

   static final DocViewParser simulateParsingDataForImage(EncodedImage image, int originalWidth, int originalHeight, String imageDomID) {
      DocViewParser coreData = new DocViewParser(false);
      DocViewParsingData parsingData = coreData.getParsingData();
      parsingData.reset(true, 0, false, true);
      ArznDocumentHeader docHeader = new ArznDocumentHeader();
      docHeader._byDocType = 2;
      docHeader._abyUCSVersion[0] = 1;
      docHeader._abyUCSVersion[1] = 0;
      parsingData.addDocumentHeader(docHeader);
      parsingData.addDOMIDHint(imageDomID);
      ArznImageData imgData = parsingData.addImage();
      imgData.setDimensions(originalWidth, originalHeight);
      imgData.addContents(image.getData());
      parsingData.endParsing((byte)0);
      return coreData;
   }

   static final boolean isFastAttachmentEnabled() {
      if (RadioInfo.isDataServiceOperational()) {
         switch (MessageListOptions.getOptions().getAutoDownloadAttachments()) {
            case 0:
               break;
            case 2:
            default:
               if (!isHomeNetwork()) {
                  return false;
               }
            case 1:
               if (MessageListOptions.getOptions().getHighSpeedNetworkOnlyForAutoDownloadAttachment() && !isFastNetwork()) {
                  return false;
               }

               return true;
         }
      }

      return false;
   }

   private static final boolean isHomeNetwork() {
      boolean result = false;
      int index = RadioInfo.getCurrentNetworkIndex();
      if (index >= 0) {
         result = (RadioInternal.getNetworkCategory(index) & 4) != 0;
      }

      return result;
   }

   private static final boolean isFastNetwork() {
      return (RadioInfo.getNetworkService() & 13312) != 0;
   }

   static final int getReqChunkSize(EmailMessageModel model, byte docType) {
      if (docType != 5 && !isTypeRequestAllChunks(docType)) {
         return getDefaultChunkSize(model);
      } else {
         return isMoreAllSupported(model) ? 64000 : -1;
      }
   }

   static final int getDefaultChunkSize(EmailMessageModel model) {
      if (isMoreAllSupported(model)) {
         return isFastAttachmentEnabled() ? 32000 : 16000;
      } else {
         return -1;
      }
   }

   static final void processData(
      String titleString,
      Object docData,
      Object context,
      int currentBlockIndex,
      int totalBlocks,
      int retrievedBlocks,
      int messageID,
      int morePartID,
      int themeScreenBgColor,
      int themeForeColor,
      boolean displayTrackChanges,
      boolean pausable,
      DocViewParseNotify notifyObj,
      DocViewNotify notifyObject,
      boolean displayProgressBar,
      int applicationID,
      boolean displayImageAsSlideshow,
      byte presentationValue
   ) {
      int consecutiveBlocksCount = 0;
      if (!(docData instanceof byte[]) && !(docData instanceof Object)) {
         if (!(docData instanceof Object)) {
            throw new Object("Unsupported input parameter for UCS data.");
         }

         consecutiveBlocksCount = ((Vector)docData).size();
      } else {
         consecutiveBlocksCount = 1;
      }

      DocViewParser coreData = new DocViewParser(displayTrackChanges);
      DocViewParserObj parserObj = new DocViewParserObj(notifyObj);
      parserObj.init(
         coreData,
         currentBlockIndex,
         context,
         retrievedBlocks,
         totalBlocks,
         messageID,
         morePartID,
         titleString,
         themeScreenBgColor,
         themeForeColor,
         applicationID,
         notifyObject,
         displayImageAsSlideshow,
         presentationValue
      );
      new AttachmentViewerFactory$ParseThread(
            docData,
            coreData,
            displayProgressBar
               ? new ParsingProgressThread(coreData, consecutiveBlocksCount, parserObj)
               : new BaseParsingThread(coreData, consecutiveBlocksCount, parserObj),
            currentBlockIndex,
            pausable,
            totalBlocks
         )
         .start();
   }

   private static final void fillParamsHash(
      DocViewParser coreData,
      String titleString,
      Object context,
      int currentBlockIndex,
      int totalBlocks,
      int morePartID,
      boolean isEmbScreen,
      long style,
      short flags,
      int themeBgColor,
      int themeForeColor,
      int imageFlipValue,
      int applicationID,
      byte presentationValue,
      boolean isSpecificBgDisplay
   ) {
      if (_paramsHash == null) {
         _paramsHash = (IntHashtable)(new Object(24));
      }

      if (coreData != null) {
         _paramsHash.put(3, coreData);
      }

      if (context != null) {
         _paramsHash.put(4, context);
      }

      _paramsHash.put(5, new Object(currentBlockIndex));
      _paramsHash.put(0, new Object(isEmbScreen));
      _paramsHash.put(7, new Object(morePartID));
      _paramsHash.put(1, new Object(style));
      _paramsHash.put(8, new Object(flags));
      if (titleString != null) {
         _paramsHash.put(2, titleString);
      }

      _paramsHash.put(6, new Object(totalBlocks));
      _paramsHash.put(9, new Object(themeBgColor));
      _paramsHash.put(11, new Object(themeForeColor));
      _paramsHash.put(10, new Object(imageFlipValue));
      _paramsHash.put(12, new Object(applicationID));
      _paramsHash.put(13, new Object(presentationValue));
      _paramsHash.put(14, new Object(isSpecificBgDisplay));
   }

   static final DocViewDisplayScreen getDisplayScreen(
      DocViewParser param0,
      String param1,
      Object param2,
      int param3,
      int param4,
      int param5,
      boolean param6,
      byte param7,
      int param8,
      int param9,
      int param10,
      int param11,
      byte param12,
      boolean param13
   ) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 36
      // 002: istore 14
      // 004: aconst_null
      // 005: astore 15
      // 007: iload 7
      // 009: tableswitch 43 -1 5 350 43 109 255 350 350 175
      // 034: iload 4
      // 036: ifle 06f
      // 039: aload 0
      // 03a: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParser.getParsingData ()Lnet/rim/device/apps/internal/docview/gui/DocViewParsingData;
      // 03d: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParsingData.isEmptyDoc ()Z
      // 040: ifne 06f
      // 043: aload 0
      // 044: aload 1
      // 045: aload 2
      // 046: iload 3
      // 047: iload 4
      // 049: iload 5
      // 04b: iload 6
      // 04d: ldc2_w 299067162755072
      // 050: bipush 0
      // 051: iload 8
      // 053: iload 9
      // 055: iload 10
      // 057: iload 11
      // 059: iload 12
      // 05b: iload 13
      // 05d: invokestatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory.fillParamsHash (Lnet/rim/device/apps/internal/docview/gui/DocViewParser;Ljava/lang/String;Ljava/lang/Object;IIIZJSIIIIBZ)V
      // 060: new net/rim/device/apps/internal/docview/gui/DocViewTextDisplayScreen
      // 063: dup
      // 064: getstatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory._paramsHash Lnet/rim/device/api/util/IntHashtable;
      // 067: invokespecial net/rim/device/apps/internal/docview/gui/DocViewTextDisplayScreen.<init> (Lnet/rim/device/api/util/IntHashtable;)V
      // 06a: astore 15
      // 06c: goto 167
      // 06f: bipush 47
      // 071: istore 14
      // 073: goto 167
      // 076: iload 4
      // 078: ifle 0b1
      // 07b: aload 0
      // 07c: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParser.getParsingData ()Lnet/rim/device/apps/internal/docview/gui/DocViewParsingData;
      // 07f: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParsingData.isEmptyDoc ()Z
      // 082: ifne 0b1
      // 085: aload 0
      // 086: aload 1
      // 087: aload 2
      // 088: iload 3
      // 089: iload 4
      // 08b: iload 5
      // 08d: iload 6
      // 08f: ldc2_w 299067162755072
      // 092: bipush 0
      // 093: iload 8
      // 095: iload 9
      // 097: iload 10
      // 099: iload 11
      // 09b: iload 12
      // 09d: iload 13
      // 09f: invokestatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory.fillParamsHash (Lnet/rim/device/apps/internal/docview/gui/DocViewParser;Ljava/lang/String;Ljava/lang/Object;IIIZJSIIIIBZ)V
      // 0a2: new net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayScreen
      // 0a5: dup
      // 0a6: getstatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory._paramsHash Lnet/rim/device/api/util/IntHashtable;
      // 0a9: invokespecial net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayScreen.<init> (Lnet/rim/device/api/util/IntHashtable;)V
      // 0ac: astore 15
      // 0ae: goto 167
      // 0b1: bipush 48
      // 0b3: istore 14
      // 0b5: goto 167
      // 0b8: invokestatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory.isAudioStreamingSupported ()Z
      // 0bb: bipush 1
      // 0bc: if_icmpne 101
      // 0bf: iload 4
      // 0c1: ifle 0fa
      // 0c4: aload 0
      // 0c5: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParser.getParsingData ()Lnet/rim/device/apps/internal/docview/gui/DocViewParsingData;
      // 0c8: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParsingData.isEmptyDoc ()Z
      // 0cb: ifne 0fa
      // 0ce: aload 0
      // 0cf: aload 1
      // 0d0: aload 2
      // 0d1: iload 3
      // 0d2: iload 4
      // 0d4: iload 5
      // 0d6: iload 6
      // 0d8: ldc2_w 2814749767106560
      // 0db: bipush 0
      // 0dc: iload 8
      // 0de: iload 9
      // 0e0: iload 10
      // 0e2: iload 11
      // 0e4: iload 12
      // 0e6: iload 13
      // 0e8: invokestatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory.fillParamsHash (Lnet/rim/device/apps/internal/docview/gui/DocViewParser;Ljava/lang/String;Ljava/lang/Object;IIIZJSIIIIBZ)V
      // 0eb: new net/rim/device/apps/internal/docview/gui/DocViewSoundDisplayScreen
      // 0ee: dup
      // 0ef: getstatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory._paramsHash Lnet/rim/device/api/util/IntHashtable;
      // 0f2: invokespecial net/rim/device/apps/internal/docview/gui/DocViewSoundDisplayScreen.<init> (Lnet/rim/device/api/util/IntHashtable;)V
      // 0f5: astore 15
      // 0f7: goto 167
      // 0fa: bipush 106
      // 0fc: istore 14
      // 0fe: goto 167
      // 101: bipush 75
      // 103: istore 14
      // 105: goto 167
      // 108: iload 4
      // 10a: ifle 163
      // 10d: aload 0
      // 10e: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParser.getParsingData ()Lnet/rim/device/apps/internal/docview/gui/DocViewParsingData;
      // 111: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParsingData.isEmptyDoc ()Z
      // 114: ifne 163
      // 117: aload 0
      // 118: aload 1
      // 119: aload 2
      // 11a: iload 3
      // 11b: iload 4
      // 11d: iload 5
      // 11f: iload 6
      // 121: ldc2_w 2814749767106560
      // 124: bipush 0
      // 125: iload 8
      // 127: iload 9
      // 129: iload 10
      // 12b: iload 11
      // 12d: iload 12
      // 12f: iload 13
      // 131: invokestatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory.fillParamsHash (Lnet/rim/device/apps/internal/docview/gui/DocViewParser;Ljava/lang/String;Ljava/lang/Object;IIIZJSIIIIBZ)V
      // 134: ldc_w "image-screen"
      // 137: ldc_w "imgfield"
      // 13a: invokestatic net/rim/device/apps/api/utility/serialization/SerializationManager.getConverter (Ljava/lang/String;Ljava/lang/Object;)Lnet/rim/device/apps/api/utility/serialization/Converter;
      // 13d: aconst_null
      // 13e: checkcast java/lang/Object
      // 141: getstatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory._paramsHash Lnet/rim/device/api/util/IntHashtable;
      // 144: invokeinterface net/rim/device/apps/api/utility/serialization/Converter.convert (Ljava/io/DataInput;Ljava/lang/Object;)Ljava/lang/Object; 3
      // 149: checkcast net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen
      // 14c: astore 15
      // 14e: goto 167
      // 151: astore 16
      // 153: bipush 89
      // 155: istore 14
      // 157: goto 167
      // 15a: astore 16
      // 15c: bipush 89
      // 15e: istore 14
      // 160: goto 167
      // 163: bipush 88
      // 165: istore 14
      // 167: getstatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory._paramsHash Lnet/rim/device/api/util/IntHashtable;
      // 16a: ifnull 173
      // 16d: getstatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory._paramsHash Lnet/rim/device/api/util/IntHashtable;
      // 170: invokevirtual net/rim/device/api/util/IntHashtable.clear ()V
      // 173: aload 15
      // 175: ifnull 17b
      // 178: aload 15
      // 17a: areturn
      // 17b: invokestatic net/rim/device/api/ui/UiApplication.getUiApplication ()Lnet/rim/device/api/ui/UiApplication;
      // 17e: new net/rim/device/apps/internal/docview/gui/ErrorDlg
      // 181: dup
      // 182: ldc2_w -4603212010799374808
      // 185: ldc_w "net.rim.device.apps.internal.resource.DocView"
      // 188: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (JLjava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 18b: iload 14
      // 18d: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 190: invokespecial net/rim/device/apps/internal/docview/gui/ErrorDlg.<init> (Ljava/lang/String;)V
      // 193: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 196: aconst_null
      // 197: areturn
      // try (127 -> 136): 137 null
      // try (127 -> 136): 141 null
   }

   static final boolean isAttachmentViewingEnabled() {
      byte docViewerPolicy = ITPolicy.getByte(23, 2, (byte)1);
      return docViewerPolicy != 0;
   }

   static final int drawTextWithEllipses(Graphics graphics, StringBuffer buf, int offset, int length, int x, int y, int width, int paragDirection, int flags) {
      if (graphics != null && buf != null && length > 0) {
         int retValue = 0;
         DrawTextParam param = Ui.getTmpDrawTextParam();
         param.iMaxAdvance = width;
         param.iDrawNonPrintableCharacters = false;
         Edit$BidiLineRuns bidiRuns = RichText.getBidiOrder(buf, offset, length, null, true, paragDirection, null, 0, 0);
         int bidiRunsCount = bidiRuns._runs.length;
         if (!bidiRuns.isIgnored() && bidiRunsCount > 3) {
            boolean fitsOrIsLTR = true;
            boolean lineIsRTL = paragDirection == 1 || paragDirection == 3;
            Font f = graphics.getFont();
            if (lineIsRTL) {
               int widthRequired = f.getBounds(buf, offset, length);
               if (widthRequired > width || (flags & 5) != 0) {
                  fitsOrIsLTR = false;
               }
            }

            if (fitsOrIsLTR) {
               int rightmostX = x + width;

               for (int i = 0; i < bidiRunsCount; i++) {
                  int runStart = bidiRuns._runs[i++];
                  int runLength = bidiRuns._runs[i++];
                  param.iReverse = bidiRuns._runs[i];
                  if (param.iReverse == 1) {
                     param.iTruncateWithEllipsis = 1;
                  } else {
                     param.iTruncateWithEllipsis = 2;
                  }

                  int xAdjust = graphics.drawText(buf, runStart, runLength, x, y, param, null);
                  retValue += xAdjust;
                  param.iMaxAdvance -= xAdjust;
                  x += xAdjust;
                  if (x > rightmostX) {
                     break;
                  }
               }
            } else {
               int originalX = x;
               x += width;

               for (int i = bidiRunsCount - 1; i >= 0; i--) {
                  param.iReverse = bidiRuns._runs[i--];
                  int runLength = bidiRuns._runs[i--];
                  int runStart = bidiRuns._runs[i];
                  if (param.iReverse == 1) {
                     param.iTruncateWithEllipsis = 2;
                  } else {
                     param.iTruncateWithEllipsis = 1;
                  }

                  int pieceWidth = f.measureText(buf, runStart, runLength, param, null);
                  boolean breakFromLoop = false;
                  if (x - pieceWidth < originalX) {
                     breakFromLoop = true;
                     pieceWidth = x - originalX;
                  }

                  graphics.drawText(buf, runStart, runLength, x - pieceWidth, y, param, null);
                  int xAdjust = breakFromLoop ? pieceWidth : f.measureText(buf, runStart, runLength, param, null);
                  retValue += xAdjust;
                  param.iMaxAdvance -= xAdjust;
                  x -= xAdjust;
                  if (breakFromLoop) {
                     break;
                  }
               }
            }

            Ui.returnTmpDrawTextParam(param);
            return retValue;
         } else {
            if (bidiRunsCount == 3) {
               param.iReverse = bidiRuns._runs[2];
            }

            param.iTruncateWithEllipsis = 2;
            param.iAlignment = flags & 7;
            return graphics.drawText(buf, offset, length, x, y, param, null);
         }
      } else {
         return 0;
      }
   }

   static final byte getLineDirection(StringBuffer buf, int offset, int len) {
      if (buf != null && buf.length() > 0) {
         int endIndex = offset + len;
         if (offset >= 0 && len >= 0 && endIndex <= buf.length()) {
            synchronized (buf) {
               for (int i = offset; i < endIndex; i++) {
                  char c = buf.charAt(i);
                  if (RichText.isRTL(c)) {
                     return 2;
                  }

                  if (!RichText.isNeutral(c)) {
                     return 0;
                  }
               }

               return 0;
            }
         } else {
            throw new Object();
         }
      } else {
         return 0;
      }
   }

   static final boolean isTextDocumentEmpty(DocViewTextContentHandler textHandler) {
      return textHandler.getTextContentLength(true) <= 0
         && (textHandler.getBreakVector() == null || textHandler.getBreakVector().length <= 0)
         && !textHandler.hasTrackChanges();
   }
}
