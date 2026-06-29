package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.browser.field.Destroyable;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.Audio;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.browser.ui.SaveFileDialog;
import net.rim.device.internal.i18n.CommonResource;

final class DocViewSoundDisplayField extends DocViewDisplayField {
   private short _compression = -1;
   private int _codec = -1;
   private String _contentType;
   private DocViewSoundDisplayField$DocViewInputConnection _audioConnection;
   private Destroyable _destroyInterface;

   DocViewSoundDisplayField(DocViewDataProvider dataProvider, DocViewGUIProvider guiProvider, DocViewNotify notifyObject, DocViewParser docData) {
      super(dataProvider, guiProvider, notifyObject, docData, null, null, null, -1, -1, false);
   }

   @Override
   protected final boolean init() {
      boolean retValue = false;
      if (super.init() && AttachmentViewerFactory.isAudioStreamingSupported()) {
         Object obj = super._parsingData.getAudio()[0];
         if (obj instanceof DocViewAudioData) {
            DocViewAudioData audioData = (DocViewAudioData)obj;
            if (this.setAudioInfo(audioData.getAudioHeader()) && Audio.isCodecSupported(this._codec)) {
               this.setAudioCodec(this._compression);
               byte[] initialData = audioData.getAudioContents();
               int totalSize = audioData.getRiffSize();
               if (this._compression == 49) {
                  byte[] waveHeader = this.createWaveHeader(audioData.getAudioHeader(), audioData.getRiffSize());
                  if (waveHeader != null) {
                     totalSize += waveHeader.length;
                     if (initialData == null) {
                        initialData = waveHeader;
                     } else {
                        Arrays.append(waveHeader, initialData);
                        initialData = waveHeader;
                     }
                  }
               }

               this._audioConnection = new DocViewSoundDisplayField$DocViewInputConnection(this, initialData, totalSize);
               retValue = true;
            }

            Object var8 = null;
         }

         obj = null;
      }

      return retValue;
   }

   @Override
   protected final boolean autoRequestOnDisplay() {
      return false;
   }

   @Override
   protected final boolean doRunDefaultVerb(boolean defaultToSpace, int time) {
      return false;
   }

   @Override
   protected final boolean hasMultipleItems() {
      return false;
   }

   @Override
   protected final void callOnceOnDisplay() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField._audioConnection Lnet/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField$DocViewInputConnection;
      // 04: ifnull 78
      // 07: invokestatic net/rim/device/api/browser/field/RenderingSession.getNewInstance ()Lnet/rim/device/api/browser/field/RenderingSession;
      // 0a: astore 1
      // 0b: aload 1
      // 0c: invokevirtual net/rim/device/api/browser/field/RenderingSession.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 0f: astore 2
      // 10: aload 2
      // 11: ldc2_w 9094571315961484757
      // 14: bipush 1
      // 15: bipush 5
      // 17: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JII)V
      // 1a: aload 2
      // 1b: ldc2_w 4550690918222697397
      // 1e: bipush 27
      // 20: bipush 0
      // 21: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 24: aload 1
      // 25: aload 0
      // 26: getfield net/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField._audioConnection Lnet/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField$DocViewInputConnection;
      // 29: aload 0
      // 2a: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayField.getFileName ()Ljava/lang/String;
      // 2d: new net/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField$DocViewRenderApp
      // 30: dup
      // 31: aload 0
      // 32: aconst_null
      // 33: invokespecial net/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField$DocViewRenderApp.<init> (Lnet/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField;Lnet/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField$1;)V
      // 36: bipush 0
      // 37: invokevirtual net/rim/device/api/browser/field/RenderingSession.getBrowserContent (Ljavax/microedition/io/InputConnection;Ljava/lang/String;Lnet/rim/device/api/browser/field/RenderingApplication;I)Lnet/rim/device/api/browser/field/BrowserContent;
      // 3a: astore 3
      // 3b: aload 3
      // 3c: invokeinterface net/rim/device/api/browser/field/BrowserContent.getDisplayableContent ()Lnet/rim/device/api/ui/Field; 1
      // 41: astore 4
      // 43: aload 0
      // 44: aload 4
      // 46: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 49: aload 4
      // 4b: instanceof net/rim/device/api/browser/field/Destroyable
      // 4e: ifeq 64
      // 51: aload 0
      // 52: aload 4
      // 54: checkcast net/rim/device/api/browser/field/Destroyable
      // 57: putfield net/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField._destroyInterface Lnet/rim/device/api/browser/field/Destroyable;
      // 5a: aload 0
      // 5b: getfield net/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField._destroyInterface Lnet/rim/device/api/browser/field/Destroyable;
      // 5e: bipush 1
      // 5f: invokeinterface net/rim/device/api/browser/field/Destroyable.setDestroyMethod (I)V 2
      // 64: new net/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField$1
      // 67: dup
      // 68: aload 0
      // 69: aload 3
      // 6a: invokespecial net/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField$1.<init> (Lnet/rim/device/apps/internal/docview/gui/DocViewSoundDisplayField;Lnet/rim/device/api/browser/field/BrowserContent;)V
      // 6d: invokevirtual java/lang/Thread.start ()V
      // 70: goto 78
      // 73: astore 1
      // 74: goto 78
      // 77: astore 1
      // 78: aload 0
      // 79: invokespecial net/rim/device/apps/internal/docview/gui/DocViewDisplayField.callOnceOnDisplay ()V
      // 7c: return
      // try (3 -> 54): 55 null
      // try (3 -> 54): 57 null
   }

   @Override
   protected final void onFinalRelease() {
      if (this._destroyInterface != null) {
         this._destroyInterface.destroy();
      }

      super.onFinalRelease();
   }

   @Override
   protected final void processNewData(int currentBlockIndex) {
      super.processNewData(currentBlockIndex);
      if (this._audioConnection != null) {
         try {
            this._audioConnection.addAudioData(super._parsingData.getAudio()[0].getAudioContents());
         } finally {
            return;
         }
      }
   }

   @Override
   protected final void addCustomMenuVerbs(Menu menu, int instance) {
      super.addCustomMenuVerbs(menu, instance);
      if (this.isMoreAvailable()) {
         menu.add(new VerbMenuItem(new DocViewGuiVerb(9, 344064, EmailResources.getResourceBundle(), 80, this), 0));
      } else {
         if (this._audioConnection != null && this._contentType != null) {
            menu.add(new VerbMenuItem(new DocViewGuiVerb(18, 332288, CommonResource.getBundle(), 18, this), 0));
         }
      }
   }

   @Override
   public final void perform(int menuCode, Object cookie) {
      switch (menuCode) {
         case 18:
            this.saveAudio();
            return;
         default:
            super.perform(menuCode, cookie);
      }
   }

   private final void saveAudio() {
      if (!this.isMoreAvailable() && this._audioConnection != null && this._contentType != null) {
         byte[] audioData = this._audioConnection.getAudioData();
         if (audioData != null) {
            SaveFileDialog.save(this.getFileName(), this._contentType, MIMETypeAssociations.getMediaTypeFromMIMEType(this._contentType), false, audioData);
            Object var2 = null;
         }
      }
   }

   private final boolean setAudioInfo(byte[] wavHeader) {
      try {
         this._compression = DocViewUtilities.readShort(wavHeader, 0, false);
         switch (this._compression) {
            case 0:
               this._codec = 7;
               this._contentType = "audio/amr";
               break;
            case 49:
               this._codec = 11;
               this._contentType = "audio/wav";
               break;
            case 85:
               this._codec = 3;
               this._contentType = "audio/mpeg";
         }

         return true;
      } finally {
         ;
      }
   }

   private final byte[] createWaveHeader(byte[] header, int totalRiffDataSize) {
      int totalSize = 12 + header.length + 4 + 4 + 4 + 4 + 4 + totalRiffDataSize;
      int totalHeaderSize = 8 + totalSize - totalRiffDataSize;
      byte[] waveHeader = new byte[totalHeaderSize];
      if (waveHeader != null) {
         int index = 0;
         waveHeader[index++] = 82;
         waveHeader[index++] = 73;
         waveHeader[index++] = 70;
         waveHeader[index++] = 70;
         DocViewUtilities.writeInt(waveHeader, index, totalSize, false);
         index += 4;
         waveHeader[index++] = 87;
         waveHeader[index++] = 65;
         waveHeader[index++] = 86;
         waveHeader[index++] = 69;
         waveHeader[index++] = 102;
         waveHeader[index++] = 109;
         waveHeader[index++] = 116;
         waveHeader[index++] = 32;
         DocViewUtilities.writeInt(waveHeader, index, header.length, false);
         index += 4;
         System.arraycopy(header, 0, waveHeader, index, header.length);
         index += header.length;
         waveHeader[index++] = 102;
         waveHeader[index++] = 97;
         waveHeader[index++] = 99;
         waveHeader[index++] = 116;
         DocViewUtilities.writeInt(waveHeader, index, 4, false);
         index += 4;
         int sampleLength = 0;
         if (header.length >= 12) {
            double samplesPerSec = DocViewUtilities.readInt(header, 4, false);
            double avgBytesPerSec = DocViewUtilities.readInt(header, 8, false);
            if (avgBytesPerSec > 0L) {
               sampleLength = (int)(totalRiffDataSize * (samplesPerSec / avgBytesPerSec));
            }
         }

         DocViewUtilities.writeInt(waveHeader, index, sampleLength, false);
         index += 4;
         waveHeader[index++] = 100;
         waveHeader[index++] = 97;
         waveHeader[index++] = 116;
         waveHeader[index++] = 97;
         DocViewUtilities.writeInt(waveHeader, index, totalRiffDataSize, false);
         index += 4;
      }

      return waveHeader;
   }
}
