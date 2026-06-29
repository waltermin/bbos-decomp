package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.ActiveRichTextField$RegionQueue;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.vm.Array;

class DocViewRichTextField extends ActiveRichTextField {
   private Object[] _cookies;
   private int[] _backupOffsets;
   private byte[] _backupAttributes;

   DocViewRichTextField(int style) {
      super(null, style);
   }

   protected DocViewHyperlinkInfo getHyperlinkInfo(int region) {
      return this.getHyperlinkInfo(this.getObjRegionCookie(region));
   }

   private DocViewHyperlinkInfo getHyperlinkInfo(Object cookie) {
      if (!(cookie instanceof DocViewHyperlinkInfo)) {
         return !(cookie instanceof DocViewComplexRegionInfo) ? null : ((DocViewComplexRegionInfo)cookie)._hyperInfo;
      } else {
         return (DocViewHyperlinkInfo)cookie;
      }
   }

   final void setCustomText(String text, int[] offsets, byte[] attributes, Font[] fonts, Object[] cookies, int[] foregroundColors, int[] backgroundColors) {
      this._cookies = cookies;
      if (cookies != null) {
         this._backupOffsets = offsets;
         this._backupAttributes = attributes;
      }

      this.setText(text, offsets, attributes, fonts, foregroundColors, backgroundColors);
   }

   final void appendCustomText(String text, int[] offsets, byte[] attributes, Font[] fonts, Object[] cookies, int[] foregroundColors, int[] backgroundColors) {
      if (text != null && text.length() > 0) {
         int crtTextLength = this.getTextLength();
         if (fonts != null && fonts.length > 0) {
            this.appendFonts(fonts);
         }

         this.append(text, offsets, null, attributes);
         this.setAttributes(foregroundColors, backgroundColors);
         if (cookies != null) {
            this._backupOffsets = this.getOffsets();
            this._backupAttributes = this.getAttributes();
            if (this._backupAttributes != null && this._backupOffsets != null) {
               int attrLength = this._backupAttributes.length;
               if (this._cookies == null) {
                  this._cookies = new Object[attrLength];
               } else {
                  Array.resize(this._cookies, attrLength);
               }

               for (int i = cookies.length - 1; i >= 0; i--) {
                  if (this._backupOffsets[i] >= crtTextLength) {
                     this._cookies[i] = cookies[i];
                  }
               }
            }
         }

         this.customExecuteBgScan();
      }
   }

   protected void customExecuteBgScan() {
      this.executeBackgroundScan();
   }

   @Override
   protected void setText(String text, ActiveRichTextField$RegionQueue rq) {
      super.setText(text, rq);
      this.checkArrays();
   }

   private void checkArrays() {
      if (this._cookies != null && this._cookies.length > 0 && this._backupOffsets != null && this._backupAttributes != null) {
         int[] newOffsets = this.getOffsets();
         Object[] oldCookies = this._cookies;
         int attrLength = this.getAttributes().length;
         this._cookies = new Object[attrLength];

         for (int i = oldCookies.length - 1; i >= 0; i--) {
            Object obj = oldCookies[i];
            if (obj != null) {
               int newIndex = Arrays.binarySearch(newOffsets, this._backupOffsets[i], i, attrLength);
               if (newIndex >= 0) {
                  this._cookies[newIndex] = obj;

                  try {
                     this.setAttribute(newIndex, this._backupAttributes[i]);
                  } finally {
                     continue;
                  }
               }
            }
         }

         this._backupOffsets = null;
         this._backupAttributes = null;
         oldCookies = null;
      }
   }

   @Override
   protected MenuItem addCookieMenuItems(CookieProvider provider, int cookieId, ContextMenu contextMenu, Object context) {
      MenuItem defaultItem = super.addCookieMenuItems(provider, cookieId, contextMenu, context);
      if (this._cookies != null && cookieId < this._cookies.length && cookieId >= 0 && this._cookies[cookieId] != null) {
         DocViewHyperlinkInfo hyperInfo = this.getHyperlinkInfo(this._cookies[cookieId]);
         DocViewEmbHyperlinkInfo embTextInfo = null;
         if (hyperInfo instanceof DocViewEmbHyperlinkInfo && ((DocViewEmbHyperlinkInfo)hyperInfo)._linkType == 2) {
            embTextInfo = (DocViewEmbHyperlinkInfo)hyperInfo;
         }

         if (embTextInfo != null) {
            String strRegionCookie = null;
            if (super.isCookieValid(cookieId)) {
               strRegionCookie = this.getAttributedText().getText(this.getRunStart(), this.getRunEnd());
            }

            if (strRegionCookie == null || embTextInfo._linkTargetString.indexOf(strRegionCookie) == -1) {
               Verb[] verbs = new Verb[0];
               Verb defaultVerb = CookieProviderUtilities.getFocusVerbs(embTextInfo, null, verbs);
               int count = verbs.length;

               for (int idx = 0; idx < count; idx++) {
                  int priority = verbs[idx] == defaultVerb ? 10 : Integer.MAX_VALUE;
                  VerbMenuItem menuItem = new VerbMenuItem(verbs[idx], priority);
                  contextMenu.addItem(menuItem);
                  if (verbs[idx] == defaultVerb) {
                     defaultItem = menuItem;
                  }
               }
            }
         }

         return defaultItem;
      } else {
         return defaultItem;
      }
   }

   protected final Object getObjRegionCookie(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 0: aload 0
      // 1: getfield net/rim/device/apps/internal/docview/gui/DocViewRichTextField._cookies [Ljava/lang/Object;
      // 4: iload 1
      // 5: aaload
      // 6: areturn
      // 7: astore 2
      // 8: aconst_null
      // 9: areturn
      // a: astore 2
      // b: aconst_null
      // c: areturn
      // try (0 -> 4): 5 null
      // try (0 -> 4): 8 null
   }

   @Override
   public boolean isCookieValid(int id) {
      return super.isCookieValid(id) || this._cookies != null && id < this._cookies.length && id >= 0 && this._cookies[id] != null;
   }

   @Override
   public Object getCookie(int id) {
      return this._cookies != null && id < this._cookies.length && id >= 0 && this._cookies[id] != null ? this._cookies[id] : super.getCookie(id);
   }

   @Override
   public boolean regionsHaveSameCookie(int regionId1, int regionId2) {
      DocViewHyperlinkInfo crtInfo = this.getHyperlinkInfo(regionId1);
      return crtInfo == null ? false : crtInfo.identicalAndSuccesive(this.getHyperlinkInfo(regionId2), 1);
   }
}
