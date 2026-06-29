package net.rim.device.apps.internal.ribbon.skin.svg;

import com.sun.cldc.i18n.Helper;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.FontLogicHelper;
import net.rim.device.api.ui.TextGraphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.TextAttrNodeImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.VisualNodeImpl;
import net.rim.plazmic.internal.mediaengine.service.MediaService;
import net.rim.plazmic.internal.mediaengine.ui.PME12GraphicsImpl;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

class FontManagerForeignObject extends DefaultHandler implements MediaService, GlobalEventListener {
   private String[] _nodeIds;
   private int[] _nodeHandles;
   private String[] _origFontFamilies;
   private int[] _origFontWeights;
   private ModelInteractorImpl _model;
   private static TextGraphics _textGraphics = new TextGraphics("BBMillbank", 10);

   FontManagerForeignObject(String param1, SAXParser param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial org/xml/sax/helpers/DefaultHandler.<init> ()V
      // 04: aload 0
      // 05: bipush 0
      // 06: anewarray 30
      // 09: putfield net/rim/device/apps/internal/ribbon/skin/svg/FontManagerForeignObject._nodeIds [Ljava/lang/String;
      // 0c: aload 0
      // 0d: bipush 0
      // 0e: newarray 10
      // 10: putfield net/rim/device/apps/internal/ribbon/skin/svg/FontManagerForeignObject._nodeHandles [I
      // 13: aload 0
      // 14: bipush 0
      // 15: anewarray 44
      // 18: putfield net/rim/device/apps/internal/ribbon/skin/svg/FontManagerForeignObject._origFontFamilies [Ljava/lang/String;
      // 1b: aload 0
      // 1c: bipush 0
      // 1d: newarray 10
      // 1f: putfield net/rim/device/apps/internal/ribbon/skin/svg/FontManagerForeignObject._origFontWeights [I
      // 22: aload 2
      // 23: new java/io/ByteArrayInputStream
      // 26: dup
      // 27: aload 1
      // 28: ldc_w "UTF8"
      // 2b: invokevirtual java/lang/String.getBytes (Ljava/lang/String;)[B
      // 2e: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 31: aload 0
      // 32: invokevirtual net/rim/device/api/xml/parsers/SAXParser.parse (Ljava/io/InputStream;Lorg/xml/sax/helpers/DefaultHandler;)V
      // 35: return
      // 36: astore 3
      // 37: return
      // 38: astore 3
      // 39: return
      // try (18 -> 27): 28 null
      // try (18 -> 27): 30 null
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7464003439710973532L) {
         this.updateFont();
      }
   }

   private void updateFont() {
      int locale = Locale.getDefaultForSystem().getCode();
      if (FontLogicHelper.useAltFont(locale)) {
         this.setAltFont(locale);
      } else {
         this.setMainFont();
      }
   }

   private void setAltFont(int locale) {
      if (this._model != null) {
         String altFontFamily = Helper.getSuggestedTypeface(locale);
         _textGraphics.setTypefaceName(altFontFamily);

         for (int i = 0; i < this._nodeHandles.length; i++) {
            int handle = this._nodeHandles[i];
            TextAttrNodeImpl.setFontFamily(altFontFamily, handle, this._model);
            TextAttrNodeImpl.setFontWeight(this._origFontWeights[i], handle, this._model);
            int size = TextAttrNodeImpl.getResolvedFontSize(handle, this._model);
            int weight = TextAttrNodeImpl.getResolvedFontWeight(handle, this._model);
            int strokeColor = VisualNodeImpl.getResolvedStrokeColor(handle, this._model);
            _textGraphics.setHeightWithLeading(Fixed32.toInt(size));
            _textGraphics.setStyle(PME12GraphicsImpl.getFontWeight(weight));
            if (strokeColor != Integer.MIN_VALUE) {
               _textGraphics.setEffects(768);
            } else {
               _textGraphics.setEffects(0);
            }

            if (!FontLogicHelper.fontLegible(_textGraphics, locale)) {
               FontLogicHelper.getSuggestedFont(_textGraphics, locale, false);
               int suggestedWeight = TextAttrNodeImpl.teStyleToSVGWeight(_textGraphics.getStyle());
               if (suggestedWeight != weight) {
                  TextAttrNodeImpl.setFontWeight(suggestedWeight, handle, this._model);
               }
            }
         }
      }
   }

   private void setMainFont() {
      if (this._model != null) {
         for (int i = 0; i < this._nodeHandles.length; i++) {
            TextAttrNodeImpl.setFontFamily(this._origFontFamilies[i], this._nodeHandles[i], this._model);
            TextAttrNodeImpl.setFontWeight(this._origFontWeights[i], this._nodeHandles[i], this._model);
         }
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (localName.equalsIgnoreCase("items")) {
         String ids = attributes.getValue("ids");
         StringTokenizer tokenizer = new StringTokenizer(ids, ';');

         while (tokenizer.hasMoreTokens()) {
            Arrays.add(this._nodeIds, tokenizer.nextToken().trim());
         }
      }
   }

   @Override
   public void setMedia(Object media) {
      if (media != this._model) {
         if (media instanceof ModelInteractorImpl) {
            this._model = (ModelInteractorImpl)media;
            if (this._model == null || this._nodeIds == null) {
               return;
            }

            for (int i = 0; i < this._nodeIds.length; i++) {
               int handle = this._model.getHandle(this._nodeIds[i]);
               if (handle != -1) {
                  Arrays.add(this._nodeHandles, handle);
                  String origFontFamily = TextAttrNodeImpl.getFontFamily(handle, this._model);
                  int origWeight = TextAttrNodeImpl.getFontWeight(handle, this._model);
                  Arrays.add(this._origFontFamilies, origFontFamily);
                  Arrays.add(this._origFontWeights, origWeight);
               }
            }

            this._nodeIds = null;
            this.updateFont();
         }
      }
   }

   @Override
   public Object getMedia() {
      return this._model;
   }

   @Override
   public void dispose() {
   }

   @Override
   public void setServices(MediaServices services) {
   }
}
