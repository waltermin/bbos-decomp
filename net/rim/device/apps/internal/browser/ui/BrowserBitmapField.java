package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.ui.MIMEContentAnimatedBitmapField;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.browser.html.HTMLImg;
import net.rim.device.apps.internal.browser.img.SetAsHomescreenBackgroundMenuItem;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.verbs.FullImageVerb;
import net.rim.device.apps.internal.browser.verbs.LoadImageVerb;
import net.rim.device.apps.internal.browser.verbs.SaveImageVerb;
import net.rim.device.apps.internal.browser.verbs.ShowUrlVerb;
import net.rim.device.internal.ui.Edit$Helper;
import net.rim.device.internal.ui.RichText;
import org.w3c.dom.html2.HTMLElement;

public class BrowserBitmapField extends MIMEContentAnimatedBitmapField {
   protected int _focusX;
   protected int _focusY;
   protected int _focusWidth;
   protected int _focusHeight;
   private boolean _highlightImageOnFocus;
   private boolean _transcoded = true;
   private boolean _doScaling = true;
   private String _imageUrl;
   protected int _focusType;
   private boolean _acceptFocus;
   private int _preferredHeight;
   private int _preferredWidth;
   private String _fileSystemFilename;
   private String _altText;
   protected HTMLElement _element;
   private byte[] _textLengths;
   private short[] _textHeights;
   private boolean _limitVHSpace = true;
   private int _layoutGeneration = -1;
   protected BrowserContentBaseImpl _browserContent;
   public static final Bitmap _brokenImage = Bitmap.getBitmapResource("BrokenImage.gif");
   private static final Bitmap _loadingImage = Bitmap.getBitmapResource("LoadingImage.gif");
   protected static final int DEFAULT_FOCUS_HEIGHT;
   protected static final int DEFAULT_FOCUS_WIDTH;
   private static final int IMAGE_LOAD_PAD;
   public static final int SAFE_FOCUS_TYPE;
   public static final int TEXT_FLOW_FOCUS_TYPE;
   public static final int TEXT_FLOW_FOCUS_TYPE_WITH_FOCUS;
   private static final int MAX_VSPACE;
   private static final int MIN_VSPACE;
   private static final int MAX_HSPACE;
   private static final int MIN_HSPACE;

   public BrowserBitmapField(BrowserContentBaseImpl browserField, Bitmap bitmap, String imageUrl) {
      this(browserField, bitmap, imageUrl, 0, false, -1, -1, 0, null, null);
   }

   public BrowserBitmapField(BrowserContentBaseImpl browserField, Bitmap bitmap, String imageUrl, long style) {
      this(browserField, bitmap, imageUrl, style, false, -1, -1, 0, null, null);
   }

   public BrowserBitmapField(BrowserContentBaseImpl browserField, Bitmap bitmap, String imageUrl, long style, boolean highlightImageOnFocus) {
      this(browserField, bitmap, imageUrl, style, false, -1, -1, 0, null, null);
   }

   public BrowserBitmapField(
      BrowserContentBaseImpl browserField,
      Bitmap bitmap,
      String imageUrl,
      long style,
      boolean highlightImageOnFocus,
      int specifiedWidth,
      int specifiedHeight,
      int focusType,
      String altText,
      HTMLElement element
   ) {
      super(bitmap, style | 18014398509481984L);
      this._focusType = focusType;
      this._highlightImageOnFocus = highlightImageOnFocus;
      this._imageUrl = imageUrl;
      this._acceptFocus = this.shouldAcceptFocus();
      this._browserContent = browserField;
      this._preferredWidth = specifiedWidth;
      this._preferredHeight = specifiedHeight;
      this._altText = altText;
      this._element = element;
      if (browserField != null) {
         RenderingOptions renderingOptions = browserField.getRenderingOptions();
         if (renderingOptions != null
            && (
               !renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 40, true)
                  || !renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 26, true)
            )) {
            this.setAddMIMEVerbs(false);
         }
      }
   }

   public void setLimitVHSpace(boolean limit) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      if (this._focusType != 1 && this._focusType != 2) {
         int fontHeight = Font.getDefault().getHeight() * 2;
         int pixels = amount * fontHeight;
         if (((status & 536870912) == 0 || (status & 65536) == 0) && ((status & 1073741824) == 0 || (status & 1) == 0)) {
            int newY = this._focusY + pixels;
            int max = this.getBitmapHeight() - this._focusHeight;
            if (newY < 0) {
               this._focusY = 0;
               return newY / fontHeight;
            } else if (newY > max) {
               this._focusY = max;
               return (newY - max) / fontHeight;
            } else {
               this._focusY = newY;
               return 0;
            }
         } else {
            this._focusX = MathUtilities.clamp(0, this._focusX + pixels, this.getBitmapWidth() - this._focusWidth);
            return 0;
         }
      } else {
         return super.moveFocus(amount, status, time);
      }
   }

   protected boolean shouldAcceptFocus() {
      return this._focusType != 1;
   }

   @Override
   public boolean isFocusable() {
      return this._acceptFocus;
   }

   @Override
   public void getFocusRect(XYRect rect) {
      if (this._focusType != 1 && this._focusType != 2) {
         rect.set(this._focusX, this._focusY, this._focusWidth, this._focusHeight);
      } else {
         super.getFocusRect(rect);
      }
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      this._focusX = 0;
      this._focusY = direction >= 0 ? 0 : this.getBitmapHeight() - this._focusHeight;
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (this._highlightImageOnFocus) {
         this.drawOutline(graphics, on);
      }
   }

   public void drawOutline(Graphics graphics, boolean on) {
      if (on) {
         graphics.setStipple(-1);
         graphics.setColor(15461355);
         int width = this.getContentWidth();
         int height = this.getContentHeight();
         graphics.drawRect(0, 0, width, height);
         graphics.setColor(1052688);
         graphics.setStipple(-252645136);
         graphics.drawRect(0, 0, width, height);
      } else {
         EncodedImage img = this.getImage();
         if (img != null && img.hasTransparency()) {
            this.invalidate();
         } else {
            graphics.clear();
            this.paint(graphics);
         }
      }
   }

   public final String getImageUrl() {
      return this._imageUrl;
   }

   public void setImageUrl(String imageUrl) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public EncodedImage getImage() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public void setImage(EncodedImage image) {
      this.setImage(image, 0, 0, -1, -1);
   }

   public void setImage(EncodedImage image, int hSpace, int vSpace, int preferredWidth, int preferredHeight) {
      if (this._limitVHSpace) {
         vSpace = MathUtilities.clamp(0, vSpace, 3);
         hSpace = MathUtilities.clamp(0, hSpace, 3);
      }

      super.setPadding(vSpace, hSpace, vSpace, hSpace);
      super.setImage(image);
      int oldPreferredWidth = this._preferredWidth;
      int oldPreferredHeight = this._preferredHeight;
      if (this._preferredWidth == -1 && this._preferredHeight == -1) {
         if (preferredWidth != -1 && preferredHeight != -1) {
            this._preferredWidth = preferredWidth;
            this._preferredHeight = preferredHeight;
         } else if (preferredWidth != -1) {
            this._preferredWidth = preferredWidth;
            this._preferredHeight = preferredWidth * image.getHeight() / image.getWidth();
         } else if (preferredHeight != -1) {
            this._preferredWidth = image.getWidth() * preferredHeight / image.getHeight();
            this._preferredHeight = preferredHeight;
         }
      } else if (this._preferredHeight == -1) {
         this._preferredHeight = this._preferredWidth * image.getHeight() / image.getWidth();
      } else if (this._preferredWidth == -1) {
         this._preferredWidth = this._preferredHeight * image.getWidth() / image.getHeight();
      }

      if (oldPreferredWidth != this._preferredWidth || oldPreferredHeight != this._preferredHeight) {
         this.updateLayout();
      }

      this._focusX = 0;
      this._focusY = 0;
      int iterations = 100;
      if (this._browserContent != null) {
         iterations = this._browserContent.getRenderingOptions().getPropertyWithIntValue(4550690918222697397L, 12, 10);
      }

      this.setMaximumLoopIterations(iterations);
      this.startAnimation();
      if (this._element instanceof HTMLImg) {
         ((HTMLImg)this._element).onLoad();
      }
   }

   public void setTranscoded(boolean transcoded, boolean doScaling) {
      this._transcoded = transcoded;
      this._doScaling = doScaling;
   }

   public boolean getTranscoded() {
      return this._transcoded;
   }

   public void setFileSystemFilename(String filename) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (this._fileSystemFilename != null) {
         if ((this._browserContent == null || this._browserContent.getRenderingOptions().getPropertyWithBooleanValue(4550690918222697397L, 40, true))
            && Graphics.isColor()) {
            EncodedImage img = this.getImage();
            if (img != null && img.getData() != null) {
               contextMenu.addItem(new SetAsHomescreenBackgroundMenuItem(this._fileSystemFilename));
               return;
            }
         }
      } else if (this._imageUrl != null && this._browserContent != null) {
         RenderingOptions renderingOptions = this._browserContent.getRenderingOptions();
         if (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 30, false)) {
            contextMenu.addItem((MenuItem)(new Object(new ShowUrlVerb(null, this._imageUrl, this._browserContent, 2), Integer.MAX_VALUE)));
         }

         if (this._transcoded && renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 38, false)) {
            VerbMenuItem menuItem = (VerbMenuItem)(new Object(new FullImageVerb(this._imageUrl, this._browserContent), 15));
            contextMenu.addItem(menuItem);
            contextMenu.setDefaultItem(menuItem);
         }

         if (Graphics.isColor() && (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 26, true) || this.isProtected())) {
            EncodedImage img = this.getImage();
            if (img != null && img.getData() != null) {
               contextMenu.addItem((MenuItem)(new Object(new SaveImageVerb(this._imageUrl, img, this.isProtected()), Integer.MAX_VALUE)));
            }
         }

         if (this._browserContent instanceof BrowserContentImpl && this.getImage() == null && this.getBitmap() == null) {
            VerbMenuItem menuItem = (VerbMenuItem)(new Object(new LoadImageVerb((BrowserContentImpl)this._browserContent, this._imageUrl), 15));
            contextMenu.addItem(menuItem);
            contextMenu.setDefaultItem(menuItem);
         }
      }
   }

   @Override
   protected void layout(int width, int height) {
      this._textLengths = null;
      this._textHeights = null;
      if (this._preferredWidth != -1 && this._preferredHeight != -1) {
         if (width < this._preferredWidth) {
            height = Fixed32.toInt(Fixed32.mul(Fixed32.div(Fixed32.toFP(width), Fixed32.toFP(this._preferredWidth)), Fixed32.toFP(this._preferredHeight)));
            this.setExtent(width, height);
         } else {
            this.setExtent(this._preferredWidth, this._preferredHeight);
         }
      }

      if (this.getImage() == null && this.getBitmap() == null) {
         if (this._preferredWidth == -1 || this._preferredHeight == -1) {
            int extraWidth = 0;
            if (this._altText != null) {
               extraWidth = this.getFont().measureText(this._altText, 0, this._altText.length(), null, null);
            }

            this.setExtent(_loadingImage.getWidth() + 8 + 1 + extraWidth, _loadingImage.getHeight() + 8);
         } else if (this._altText != null) {
            int offsetX = 4 + _loadingImage.getWidth() + 1;
            if (offsetX < this._preferredWidth) {
               Font[] fonts = new Object[]{this.getFont()};
               int[] indicies = new int[]{0, this._altText.length()};
               byte[] attribs = new byte[]{0, 0};
               Edit$Helper textHelper = RichText.calculateLengths(this._preferredWidth - offsetX, this._altText, indicies, attribs, fonts);
               this._textLengths = Arrays.copy(textHelper._lengths, 0, textHelper._lineCount);
               this._textHeights = Arrays.copy(textHelper._heights, 0, textHelper._lineCount);
               return;
            }
         }
      } else {
         if (!this._doScaling) {
            width = super.getPreferredWidth();
            height = super.getPreferredHeight();
         }

         try {
            super.layout(width, height);
         } finally {
            this.setExtent(_brokenImage.getWidth(), _brokenImage.getHeight());
            return;
         }
      }
   }

   @Override
   protected void paint(Graphics param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: bipush 32
      // 003: invokevirtual net/rim/device/api/ui/Graphics.isDrawingStyleSet (I)Z
      // 006: istore 2
      // 007: iload 2
      // 008: ifne 01f
      // 00b: aload 0
      // 00c: invokevirtual net/rim/device/apps/internal/browser/ui/BrowserBitmapField.getImage ()Lnet/rim/device/api/system/EncodedImage;
      // 00f: ifnull 015
      // 012: goto 1a5
      // 015: aload 0
      // 016: invokevirtual net/rim/device/api/ui/component/BitmapField.getBitmap ()Lnet/rim/device/api/system/Bitmap;
      // 019: ifnull 01f
      // 01c: goto 1a5
      // 01f: aload 0
      // 020: invokevirtual net/rim/device/api/ui/Field.getContentWidth ()I
      // 023: istore 3
      // 024: aload 0
      // 025: invokevirtual net/rim/device/api/ui/Field.getContentHeight ()I
      // 028: istore 4
      // 02a: aload 1
      // 02b: ldc_w 16777215
      // 02e: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 031: aload 1
      // 032: bipush 0
      // 033: bipush 0
      // 034: iload 3
      // 035: iload 4
      // 037: invokevirtual net/rim/device/api/ui/Graphics.fillRect (IIII)V
      // 03a: aload 1
      // 03b: bipush 0
      // 03c: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 03f: aload 1
      // 040: bipush 0
      // 041: bipush 0
      // 042: iload 3
      // 043: iload 4
      // 045: invokevirtual net/rim/device/api/ui/Graphics.drawRect (IIII)V
      // 048: iload 2
      // 049: ifeq 04f
      // 04c: goto 1a4
      // 04f: getstatic net/rim/device/apps/internal/browser/ui/BrowserBitmapField._loadingImage Lnet/rim/device/api/system/Bitmap;
      // 052: invokevirtual net/rim/device/api/system/Bitmap.getWidth ()I
      // 055: istore 5
      // 057: getstatic net/rim/device/apps/internal/browser/ui/BrowserBitmapField._loadingImage Lnet/rim/device/api/system/Bitmap;
      // 05a: invokevirtual net/rim/device/api/system/Bitmap.getHeight ()I
      // 05d: istore 6
      // 05f: aload 1
      // 060: bipush 4
      // 062: bipush 4
      // 064: iload 5
      // 066: iload 6
      // 068: getstatic net/rim/device/apps/internal/browser/ui/BrowserBitmapField._loadingImage Lnet/rim/device/api/system/Bitmap;
      // 06b: bipush 0
      // 06c: bipush 0
      // 06d: invokevirtual net/rim/device/api/ui/Graphics.drawBitmap (IIIILnet/rim/device/api/system/Bitmap;II)V
      // 070: bipush 4
      // 072: iload 5
      // 074: iadd
      // 075: bipush 1
      // 076: iadd
      // 077: istore 7
      // 079: aload 0
      // 07a: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._altText Ljava/lang/String;
      // 07d: ifnonnull 083
      // 080: goto 1a4
      // 083: aload 0
      // 084: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._textHeights [S
      // 087: ifnonnull 08d
      // 08a: goto 192
      // 08d: aload 1
      // 08e: invokevirtual net/rim/device/api/ui/Graphics.getClippingRect ()Lnet/rim/device/api/ui/XYRect;
      // 091: invokevirtual net/rim/device/api/ui/XYRect.Y2 ()I
      // 094: istore 8
      // 096: aload 0
      // 097: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._altText Ljava/lang/String;
      // 09a: invokevirtual java/lang/String.length ()I
      // 09d: istore 9
      // 09f: bipush 0
      // 0a0: istore 10
      // 0a2: bipush 4
      // 0a4: istore 11
      // 0a6: invokestatic net/rim/device/api/ui/Ui.getTmpDrawTextParam ()Lnet/rim/device/api/ui/DrawTextParam;
      // 0a9: astore 12
      // 0ab: bipush 0
      // 0ac: istore 13
      // 0ae: iload 13
      // 0b0: aload 0
      // 0b1: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._textHeights [S
      // 0b4: arraylength
      // 0b5: if_icmplt 0bb
      // 0b8: goto 18c
      // 0bb: iload 11
      // 0bd: iload 8
      // 0bf: if_icmple 0c5
      // 0c2: goto 18c
      // 0c5: iload 10
      // 0c7: aload 0
      // 0c8: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._textLengths [B
      // 0cb: iload 13
      // 0cd: baload
      // 0ce: iadd
      // 0cf: iload 9
      // 0d1: if_icmple 0d7
      // 0d4: goto 18c
      // 0d7: aload 0
      // 0d8: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._altText Ljava/lang/String;
      // 0db: iload 10
      // 0dd: aload 0
      // 0de: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._textLengths [B
      // 0e1: iload 13
      // 0e3: baload
      // 0e4: aconst_null
      // 0e5: bipush 0
      // 0e6: aconst_null
      // 0e7: invokestatic net/rim/device/internal/ui/RichText.getBidiOrder (Ljava/lang/String;II[BZ[I)Lnet/rim/device/internal/ui/Edit$BidiLineRuns;
      // 0ea: astore 14
      // 0ec: aload 14
      // 0ee: invokevirtual net/rim/device/internal/ui/Edit$BidiLineRuns.isIgnored ()Z
      // 0f1: ifeq 110
      // 0f4: aload 1
      // 0f5: aload 0
      // 0f6: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._altText Ljava/lang/String;
      // 0f9: iload 10
      // 0fb: aload 0
      // 0fc: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._textLengths [B
      // 0ff: iload 13
      // 101: baload
      // 102: iload 7
      // 104: iload 11
      // 106: aload 12
      // 108: aconst_null
      // 109: invokevirtual net/rim/device/api/ui/Graphics.drawText (Ljava/lang/String;IIIILnet/rim/device/api/ui/DrawTextParam;Lnet/rim/device/api/ui/TextMetrics;)I
      // 10c: pop
      // 10d: goto 16a
      // 110: iload 7
      // 112: istore 15
      // 114: bipush 0
      // 115: istore 16
      // 117: iload 16
      // 119: aload 14
      // 11b: getfield net/rim/device/internal/ui/Edit$BidiLineRuns._runs [I
      // 11e: arraylength
      // 11f: if_icmpge 16a
      // 122: aload 14
      // 124: getfield net/rim/device/internal/ui/Edit$BidiLineRuns._runs [I
      // 127: iload 16
      // 129: iinc 16 1
      // 12c: iaload
      // 12d: iload 10
      // 12f: iadd
      // 130: istore 17
      // 132: aload 14
      // 134: getfield net/rim/device/internal/ui/Edit$BidiLineRuns._runs [I
      // 137: iload 16
      // 139: iinc 16 1
      // 13c: iaload
      // 13d: istore 18
      // 13f: aload 12
      // 141: aload 14
      // 143: getfield net/rim/device/internal/ui/Edit$BidiLineRuns._runs [I
      // 146: iload 16
      // 148: iaload
      // 149: putfield net/rim/device/api/ui/DrawTextParam.iReverse I
      // 14c: iload 15
      // 14e: aload 1
      // 14f: aload 0
      // 150: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._altText Ljava/lang/String;
      // 153: iload 17
      // 155: iload 18
      // 157: iload 15
      // 159: iload 11
      // 15b: aload 12
      // 15d: aconst_null
      // 15e: invokevirtual net/rim/device/api/ui/Graphics.drawText (Ljava/lang/String;IIIILnet/rim/device/api/ui/DrawTextParam;Lnet/rim/device/api/ui/TextMetrics;)I
      // 161: iadd
      // 162: istore 15
      // 164: iinc 16 1
      // 167: goto 117
      // 16a: iload 11
      // 16c: aload 0
      // 16d: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._textHeights [S
      // 170: iload 13
      // 172: saload
      // 173: sipush 255
      // 176: iand
      // 177: iadd
      // 178: istore 11
      // 17a: iload 10
      // 17c: aload 0
      // 17d: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._textLengths [B
      // 180: iload 13
      // 182: baload
      // 183: iadd
      // 184: istore 10
      // 186: iinc 13 1
      // 189: goto 0ae
      // 18c: aload 12
      // 18e: invokestatic net/rim/device/api/ui/Ui.returnTmpDrawTextParam (Lnet/rim/device/api/ui/DrawTextParam;)V
      // 191: return
      // 192: aload 1
      // 193: aload 0
      // 194: getfield net/rim/device/apps/internal/browser/ui/BrowserBitmapField._altText Ljava/lang/String;
      // 197: iload 7
      // 199: bipush 4
      // 19b: iload 3
      // 19c: iload 7
      // 19e: isub
      // 19f: bipush 0
      // 1a0: bipush 0
      // 1a1: invokestatic net/rim/device/internal/ui/RichText.drawTextWithEllipses (Lnet/rim/device/api/ui/Graphics;Ljava/lang/String;IIIII)V
      // 1a4: return
      // 1a5: aload 0
      // 1a6: invokespecial net/rim/device/apps/internal/browser/ui/BrowserBitmapField.setImageScale ()V
      // 1a9: aload 0
      // 1aa: aload 1
      // 1ab: invokespecial net/rim/device/internal/ui/component/AnimatedBitmapField.paint (Lnet/rim/device/api/ui/Graphics;)V
      // 1ae: aload 0
      // 1af: invokespecial net/rim/device/apps/internal/browser/ui/BrowserBitmapField.resetImageScale ()V
      // 1b2: return
      // 1b3: astore 3
      // 1b4: aload 1
      // 1b5: bipush 0
      // 1b6: bipush 0
      // 1b7: getstatic net/rim/device/apps/internal/browser/ui/BrowserBitmapField._brokenImage Lnet/rim/device/api/system/Bitmap;
      // 1ba: invokevirtual net/rim/device/api/system/Bitmap.getWidth ()I
      // 1bd: getstatic net/rim/device/apps/internal/browser/ui/BrowserBitmapField._brokenImage Lnet/rim/device/api/system/Bitmap;
      // 1c0: invokevirtual net/rim/device/api/system/Bitmap.getHeight ()I
      // 1c3: getstatic net/rim/device/apps/internal/browser/ui/BrowserBitmapField._brokenImage Lnet/rim/device/api/system/Bitmap;
      // 1c6: bipush 0
      // 1c7: bipush 0
      // 1c8: invokevirtual net/rim/device/api/ui/Graphics.drawBitmap (IIIILnet/rim/device/api/system/Bitmap;II)V
      // 1cb: aload 0
      // 1cc: invokespecial net/rim/device/apps/internal/browser/ui/BrowserBitmapField.resetImageScale ()V
      // 1cf: return
      // 1d0: astore 3
      // 1d1: aload 1
      // 1d2: bipush 0
      // 1d3: bipush 0
      // 1d4: getstatic net/rim/device/apps/internal/browser/ui/BrowserBitmapField._brokenImage Lnet/rim/device/api/system/Bitmap;
      // 1d7: invokevirtual net/rim/device/api/system/Bitmap.getWidth ()I
      // 1da: getstatic net/rim/device/apps/internal/browser/ui/BrowserBitmapField._brokenImage Lnet/rim/device/api/system/Bitmap;
      // 1dd: invokevirtual net/rim/device/api/system/Bitmap.getHeight ()I
      // 1e0: getstatic net/rim/device/apps/internal/browser/ui/BrowserBitmapField._brokenImage Lnet/rim/device/api/system/Bitmap;
      // 1e3: bipush 0
      // 1e4: bipush 0
      // 1e5: invokevirtual net/rim/device/api/ui/Graphics.drawBitmap (IIIILnet/rim/device/api/system/Bitmap;II)V
      // 1e8: aload 0
      // 1e9: invokespecial net/rim/device/apps/internal/browser/ui/BrowserBitmapField.resetImageScale ()V
      // 1ec: return
      // 1ed: astore 19
      // 1ef: aload 0
      // 1f0: invokespecial net/rim/device/apps/internal/browser/ui/BrowserBitmapField.resetImageScale ()V
      // 1f3: aload 19
      // 1f5: athrow
      // try (212 -> 217): 220 null
      // try (212 -> 217): 235 null
      // try (212 -> 217): 250 null
      // try (220 -> 232): 250 null
      // try (235 -> 247): 250 null
      // try (250 -> 251): 250 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean animate() {
      this.setImageScale();
      boolean var4 = false /* VF: Semaphore variable */;

      boolean var1;
      try {
         var4 = true;
         var1 = super.animate();
         var4 = false;
      } finally {
         if (var4) {
            this.resetImageScale();
         }
      }

      this.resetImageScale();
      return var1;
   }

   private void setImageScale() {
      if (this._doScaling) {
         EncodedImage image = this.getImage();
         if (image != null) {
            int contentWidth = this.getContentWidth();
            int contentHeight = this.getContentHeight();
            if (contentWidth <= 0 || contentHeight <= 0) {
               return;
            }

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            int xScale = 65536;
            int yScale = 65536;
            if (this._preferredWidth > 0 && this._preferredHeight > 0) {
               int preferredWidthFP = Fixed32.toFP(this._preferredWidth);
               int preferredHeightFP = Fixed32.toFP(this._preferredHeight);
               if (this._preferredWidth <= contentWidth) {
                  xScale = Fixed32.div(Fixed32.toFP(imageWidth), preferredWidthFP);
                  yScale = Fixed32.div(Fixed32.toFP(imageHeight), preferredHeightFP);
               } else {
                  int contentWidthFP = Fixed32.toFP(contentWidth);
                  int preferredRatio = Fixed32.div(preferredHeightFP, preferredWidthFP);
                  xScale = Fixed32.div(Fixed32.toFP(imageWidth), contentWidthFP);
                  yScale = Fixed32.div(Fixed32.toFP(imageHeight), Fixed32.mul(contentWidthFP, preferredRatio));
               }
            } else if (Math.abs(imageWidth - contentWidth) > 10) {
               yScale = xScale = Fixed32.div(Fixed32.toFP(imageWidth), Fixed32.toFP(contentWidth));
            }

            image.setScaleX32(xScale);
            image.setScaleY32(yScale);
         }
      }
   }

   private void resetImageScale() {
      EncodedImage image = this.getImage();
      if (image != null) {
         image.setScaleX32(65536);
         image.setScaleY32(65536);
      }
   }

   @Override
   public int getPreferredWidth() {
      return this._preferredWidth == -1 ? super.getPreferredWidth() : this._preferredWidth + (this.getHSpace() << 1);
   }

   @Override
   public int getPreferredHeight() {
      return this._preferredHeight == -1 ? super.getPreferredHeight() : this._preferredHeight + (this.getVSpace() << 1);
   }

   public int getHSpace() {
      return this.getPaddingLeft();
   }

   public int getVSpace() {
      return this.getPaddingTop();
   }

   public String getAlt() {
      return this._altText;
   }

   public void invalidateField() {
      this.invalidate();
   }

   @Override
   public void getFocusRectPhantom(XYRect arg0) {
      int layoutGeneration = UiApplication.getUiApplication().getLayoutGeneration();
      if (layoutGeneration != this._layoutGeneration) {
         this._layoutGeneration = layoutGeneration;
         this._focusHeight = Math.min(this.getManager().getVisibleHeight(), this.getBitmapHeight());
         this._focusWidth = Math.min(this.getManager().getVisibleWidth(), this.getBitmapWidth());
      }

      super.getFocusRectPhantom(arg0);
   }
}
