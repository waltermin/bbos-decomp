package net.rim.device.apps.internal.browser.webfeed;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.apps.internal.browser.model.HTTPAddressModel;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.ui.BrowserTextFlowManager;
import net.rim.device.apps.internal.browser.verbs.FollowLinkVerb;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.ui.Border;
import net.rim.device.internal.ui.BorderRounded;

final class WebFeedItemManager extends VerticalFieldManager implements RenderingApplication {
   private BrowserContent _descriptionContent;
   private WebFeedItem _item;
   private WebFeedField _parent;
   private boolean _keyHandledInRepeat;
   private static final Border SOLID_BORDER = new BorderRounded(2, 2, 2, 2, 0);

   public WebFeedItemManager(WebFeedField parent, WebFeedItem item) {
      this._parent = parent;
      this._item = item;
   }

   final WebFeedField getParent() {
      return this._parent;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      if (instance == 0) {
         if (this._item.getEnclosureLink() != null) {
            String type = this._item.getEnclosureType();
            if (type != null
               && SerializationManager.getConverter(MIMETypeAssociations.getNormalizedType(type), "net.rim.device.apps.internal.rendering") != null) {
               menu.add(new WebFeedItemManager$1(this, getLabel(type, this._item.getEnclosureSize()), 341248, 0));
            }
         }

         if (this._item.getLink() != null) {
            menu.add(new WebFeedItemManager$2(this, BrowserResources.getString(749), 341248, 2));
         }

         if (this._item.getDescription() != null) {
            if (this.getFieldCount() == 1) {
               menu.add(new WebFeedItemManager$3(this, BrowserResources.getString(752), 341248, 1));
            } else {
               menu.add(new WebFeedItemManager$4(this, BrowserResources.getString(753), 341248, 3));
            }
         }

         if (this._item.getStatus() == 1) {
            menu.add(new WebFeedItemManager$5(this, CommonResources.getResourceBundle(), 1352, 602448, 4));
         } else {
            menu.add(new WebFeedItemManager$6(this, CommonResources.getResourceBundle(), 1353, 602450, 4));
         }
      } else {
         if (instance == 65536) {
            MenuItem[] menuItems = this.makeMenuInternal();

            for (int i = 0; i < menuItems.length; i++) {
               menu.add(menuItems[i]);
            }
         }
      }
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu, int instance) {
      super.makeContextMenu(contextMenu, instance);
      MenuItem[] menuItems = this.makeMenuInternal();

      for (int i = 0; i < menuItems.length; i++) {
         contextMenu.addItem(menuItems[i]);
      }
   }

   private final MenuItem[] makeMenuInternal() {
      MenuItem[] items = new MenuItem[0];
      Field field = this.getScreen().getLeafFieldWithFocus();
      field.getCookie();
      MenuItem toggleDescriptionMenuItem = null;
      if (this._item.getDescription() != null) {
         toggleDescriptionMenuItem = new WebFeedItemManager$7(this, BrowserResources.getString(this.isDescriptionOpen() ? 753 : 752), 2, 1);
      }

      MenuItem readStoryMenuItem = null;
      if (this._item.getLink() != null) {
         readStoryMenuItem = new WebFeedItemManager$8(this, BrowserResources.getString(749), 3, 3);
      }

      if (this.isDescriptionOpen() && this.isOnLink()) {
         BrowserContentImpl bci = this._parent.getBrowserContent() instanceof BrowserContentImpl ? (BrowserContentImpl)this._parent.getBrowserContent() : null;
         if (bci != null) {
            Object model = null;
            Field fieldWithFocus = bci.getDisplayableContent().getLeafFieldWithFocus();
            Manager manager = fieldWithFocus.getManager();
            if (manager instanceof BrowserTextFlowManager) {
               BrowserTextFlowManager tfm = (BrowserTextFlowManager)manager;
               model = CookieProviderUtilities.getDefaultCookie(tfm.getCookieWithFocus());
               if (model instanceof HTTPAddressModel) {
                  HTTPAddressModel addressModel = (HTTPAddressModel)model;
                  String url = addressModel.getURL();
                  FollowLinkVerb verb = new FollowLinkVerb(url, false, null);
                  Arrays.add(items, new VerbMenuItem(verb, 0));
               }
            }
         }
      }

      if (readStoryMenuItem != null) {
         Arrays.add(items, readStoryMenuItem);
      }

      if (toggleDescriptionMenuItem != null) {
         Arrays.add(items, toggleDescriptionMenuItem);
      }

      return items;
   }

   @Override
   public final ContextMenu getContextMenu(int instance) {
      ContextMenu cm = ContextMenu.getInstance();
      cm.setTarget(this);
      this.makeContextMenu(cm, instance);
      return cm;
   }

   private final boolean isOnLink() {
      if (this.isDescriptionOpen()) {
         BrowserContentImpl bci = this._parent.getBrowserContent() instanceof BrowserContentImpl ? (BrowserContentImpl)this._parent.getBrowserContent() : null;
         if (bci == null) {
            return false;
         }

         Object model = null;
         Field fieldWithFocus = bci.getDisplayableContent().getLeafFieldWithFocus();
         Manager manager = fieldWithFocus.getManager();
         if (manager instanceof BrowserTextFlowManager) {
            BrowserTextFlowManager tfm = (BrowserTextFlowManager)manager;
            model = CookieProviderUtilities.getDefaultCookie(tfm.getCookieWithFocus());
            if (model instanceof HTTPAddressModel) {
               HTTPAddressModel addressModel = (HTTPAddressModel)model;
               String url = addressModel.getURL();
               if (url != null && url.length() > 0) {
                  return true;
               }

               return false;
            }
         }
      }

      return false;
   }

   private final boolean isDescriptionOpen() {
      return this._item.getDescription() != null && this.getFieldCount() > 1;
   }

   private static final String getLabel(String mimeType, int size) {
      StringBuffer label = new StringBuffer();
      if (mimeType != null && mimeType.length() > 5) {
         switch (mimeType.charAt(0)) {
            case 'A':
            case 'a':
               if (StringUtilities.startsWithIgnoreCase(mimeType, "audio/", 1701707776)) {
                  label.append(BrowserResources.getString(855));
               }
               break;
            case 'I':
            case 'i':
               if (StringUtilities.startsWithIgnoreCase(mimeType, "image/", 1701707776)) {
                  label.append(BrowserResources.getString(712));
               }
               break;
            case 'V':
            case 'v':
               if (StringUtilities.startsWithIgnoreCase(mimeType, "video/", 1701707776)) {
                  label.append(BrowserResources.getString(856));
               }
         }
      }

      if (label.length() == 0) {
         label.append(BrowserResources.getString(857));
      }

      if (size > 0) {
         label.append(' ');
         label.append('(');
         label.append(FileUtilities.sizeToString(size));
         label.append(')');
      }

      return label.toString();
   }

   private final void showDescriptionContent() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: invokestatic net/rim/device/apps/internal/browser/webfeed/WebFeedStatusStore.getInstance ()Lnet/rim/device/apps/internal/browser/webfeed/WebFeedStatusStore;
      // 003: aload 0
      // 004: getfield net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager._item Lnet/rim/device/apps/internal/browser/webfeed/WebFeedItem;
      // 007: bipush 1
      // 008: invokevirtual net/rim/device/apps/internal/browser/webfeed/WebFeedStatusStore.setItemStatus (Lnet/rim/device/apps/internal/browser/webfeed/WebFeedItem;Z)V
      // 00b: aload 0
      // 00c: getfield net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager._descriptionContent Lnet/rim/device/api/browser/field/BrowserContent;
      // 00f: ifnull 015
      // 012: goto 130
      // 015: new net/rim/device/api/io/http/HttpHeaders
      // 018: dup
      // 019: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 01c: astore 1
      // 01d: aload 1
      // 01e: ldc_w "content-type"
      // 021: ldc_w "text/html;charset=utf-8"
      // 024: invokevirtual net/rim/device/api/io/http/HttpHeaders.addProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 027: aload 0
      // 028: getfield net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager._parent Lnet/rim/device/apps/internal/browser/webfeed/WebFeedField;
      // 02b: invokevirtual net/rim/device/apps/internal/browser/webfeed/WebFeedField.getBrowserContent ()Lnet/rim/device/api/browser/field/BrowserContent;
      // 02e: astore 2
      // 02f: new net/rim/device/api/browser/util/StaticHttpConnection
      // 032: dup
      // 033: aload 0
      // 034: getfield net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager._item Lnet/rim/device/apps/internal/browser/webfeed/WebFeedItem;
      // 037: invokevirtual net/rim/device/apps/internal/browser/webfeed/WebFeedItem.getBaseUrl ()Ljava/lang/String;
      // 03a: aload 0
      // 03b: getfield net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager._item Lnet/rim/device/apps/internal/browser/webfeed/WebFeedItem;
      // 03e: invokevirtual net/rim/device/apps/internal/browser/webfeed/WebFeedItem.getDescription ()Ljava/lang/String;
      // 041: ldc_w "utf-8"
      // 044: invokevirtual java/lang/String.getBytes (Ljava/lang/String;)[B
      // 047: aload 1
      // 048: invokespecial net/rim/device/api/browser/util/StaticHttpConnection.<init> (Ljava/lang/String;[BLnet/rim/device/api/io/http/HttpHeaders;)V
      // 04b: astore 3
      // 04c: aload 0
      // 04d: getfield net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager._parent Lnet/rim/device/apps/internal/browser/webfeed/WebFeedField;
      // 050: invokevirtual net/rim/device/apps/internal/browser/webfeed/WebFeedField.getRenderingSession ()Lnet/rim/device/api/browser/field/RenderingSession;
      // 053: astore 4
      // 055: invokestatic net/rim/device/apps/internal/browser/core/BrowserSession.getCurrentSession ()Lnet/rim/device/apps/internal/browser/core/BrowserSession;
      // 058: astore 5
      // 05a: aload 5
      // 05c: ifnonnull 062
      // 05f: goto 0c8
      // 062: aload 5
      // 064: invokevirtual net/rim/device/apps/internal/browser/core/BrowserSession.getConfig ()Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 067: astore 6
      // 069: aload 6
      // 06b: ifnull 0c8
      // 06e: invokestatic net/rim/device/apps/internal/browser/core/BrowserDaemonRegistry.getInstance ()Lnet/rim/device/apps/internal/browser/core/BrowserImpl;
      // 071: astore 7
      // 073: invokestatic net/rim/device/api/browser/field/RenderingSession.getNewInstance ()Lnet/rim/device/api/browser/field/RenderingSession;
      // 076: astore 8
      // 078: aload 8
      // 07a: ifnull 0c8
      // 07d: aload 8
      // 07f: astore 4
      // 081: aload 4
      // 083: invokevirtual net/rim/device/api/browser/field/RenderingSession.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 086: astore 9
      // 088: aload 7
      // 08a: aload 9
      // 08c: aload 6
      // 08e: invokevirtual net/rim/device/apps/internal/browser/core/BrowserImpl.setRenderingOptions (Lnet/rim/device/api/browser/field/RenderingOptions;Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;)V
      // 091: aload 9
      // 093: ldc2_w 4550690918222697397
      // 096: bipush 38
      // 098: bipush 0
      // 099: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 09c: aload 9
      // 09e: ldc2_w 4550690918222697397
      // 0a1: bipush 26
      // 0a3: bipush 0
      // 0a4: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 0a7: aload 9
      // 0a9: ldc2_w 4550690918222697397
      // 0ac: bipush 44
      // 0ae: bipush 0
      // 0af: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JII)V
      // 0b2: aload 9
      // 0b4: ldc2_w 4550690918222697397
      // 0b7: bipush 47
      // 0b9: bipush 0
      // 0ba: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 0bd: aload 9
      // 0bf: ldc2_w 4550690918222697397
      // 0c2: bipush 39
      // 0c4: bipush 1
      // 0c5: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 0c8: aload 0
      // 0c9: aload 4
      // 0cb: aload 3
      // 0cc: aload 0
      // 0cd: aload 2
      // 0ce: invokeinterface net/rim/device/api/browser/field/BrowserContent.getRenderingFlags ()I 1
      // 0d3: bipush 16
      // 0d5: ior
      // 0d6: invokevirtual net/rim/device/api/browser/field/RenderingSession.getBrowserContent (Ljavax/microedition/io/HttpConnection;Lnet/rim/device/api/browser/field/RenderingApplication;I)Lnet/rim/device/api/browser/field/BrowserContent;
      // 0d9: putfield net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager._descriptionContent Lnet/rim/device/api/browser/field/BrowserContent;
      // 0dc: goto 0e4
      // 0df: astore 2
      // 0e0: goto 0e4
      // 0e3: astore 2
      // 0e4: aload 0
      // 0e5: getfield net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager._descriptionContent Lnet/rim/device/api/browser/field/BrowserContent;
      // 0e8: ifnull 13d
      // 0eb: aload 0
      // 0ec: getfield net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager._descriptionContent Lnet/rim/device/api/browser/field/BrowserContent;
      // 0ef: dup
      // 0f0: instanceof net/rim/device/apps/internal/browser/page/BrowserContentImpl
      // 0f3: ifne 0fa
      // 0f6: pop
      // 0f7: goto 103
      // 0fa: checkcast net/rim/device/apps/internal/browser/page/BrowserContentImpl
      // 0fd: ldc_w "_top"
      // 100: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.setBaseTarget (Ljava/lang/String;)V
      // 103: aload 0
      // 104: getfield net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager._descriptionContent Lnet/rim/device/api/browser/field/BrowserContent;
      // 107: invokeinterface net/rim/device/api/browser/field/BrowserContent.getDisplayableContent ()Lnet/rim/device/api/ui/Field; 1
      // 10c: astore 2
      // 10d: aload 2
      // 10e: getstatic net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager.SOLID_BORDER Lnet/rim/device/internal/ui/Border;
      // 111: invokevirtual net/rim/device/api/ui/Field.setBorder (Lnet/rim/device/internal/ui/Border;)V
      // 114: aload 2
      // 115: bipush 0
      // 116: bipush 0
      // 117: bipush 0
      // 118: bipush 8
      // 11a: invokevirtual net/rim/device/api/ui/Field.setPadding (IIII)V
      // 11d: aload 0
      // 11e: aload 2
      // 11f: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 122: new net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager$9
      // 125: dup
      // 126: aload 0
      // 127: invokespecial net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager$9.<init> (Lnet/rim/device/apps/internal/browser/webfeed/WebFeedItemManager;)V
      // 12a: astore 3
      // 12b: aload 3
      // 12c: invokevirtual java/lang/Thread.start ()V
      // 12f: return
      // 130: aload 0
      // 131: aload 0
      // 132: getfield net/rim/device/apps/internal/browser/webfeed/WebFeedItemManager._descriptionContent Lnet/rim/device/api/browser/field/BrowserContent;
      // 135: invokeinterface net/rim/device/api/browser/field/BrowserContent.getDisplayableContent ()Lnet/rim/device/api/ui/Field; 1
      // 13a: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 13d: return
      // try (17 -> 98): 99 null
      // try (17 -> 98): 101 null
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
      boolean result = super.keyChar(character, status, time);
      return !result && character == '\n' ? true : result;
   }

   @Override
   protected final boolean keyRepeat(int keycode, int time) {
      boolean result = super.keyUp(keycode, time);
      if (!result && Keypad.key(keycode) == 10) {
         if (!this._keyHandledInRepeat) {
            this._keyHandledInRepeat = true;
            String link = this._item.getLink();
            if (link != null) {
               this._parent.getBrowserContent().getRenderingApplication().eventOccurred(new UrlRequestedEvent(this, link, null, null, false, 0));
            }
         }

         return true;
      } else {
         return result;
      }
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      boolean result = super.keyUp(keycode, time);
      if (!result && Keypad.key(keycode) == 10) {
         if (!this._keyHandledInRepeat) {
            this.toggleDescription();
         }

         this._keyHandledInRepeat = false;
         return true;
      } else {
         return result;
      }
   }

   private final boolean toggleDescription() {
      if (this._item.getDescription() != null) {
         if (this.getFieldCount() == 1) {
            this.showDescriptionContent();
            return true;
         }

         if (this._descriptionContent != null) {
            this.delete(this._descriptionContent.getDisplayableContent());
            return true;
         }
      }

      return false;
   }

   @Override
   protected final boolean invokeAction(int action) {
      if (super.invokeAction(action)) {
         return true;
      }

      if (action == 1) {
         this.getScreen().onMenu(65536);
      }

      return false;
   }

   private final RenderingApplication getRenderingApplication() {
      return this._parent.getBrowserContent().getRenderingApplication();
   }

   @Override
   public final Object eventOccurred(Event event) {
      RenderingApplication app = this.getRenderingApplication();
      if (app == null) {
         return null;
      }

      int eventId = event.getUID();
      switch (eventId) {
         case 1:
         case 10003:
         case 10006:
         case 10008:
         case 10010:
         case 10011:
            return app.eventOccurred(event);
         default:
            return null;
      }
   }

   @Override
   public final int getAvailableHeight(BrowserContent browserContent) {
      if (browserContent == null) {
         return 0;
      }

      Manager manager = browserContent.getDisplayableContent().getManager();
      return manager.getHeight();
   }

   @Override
   public final int getAvailableWidth(BrowserContent browserContent) {
      if (browserContent == null) {
         return 0;
      }

      Manager manager = browserContent.getDisplayableContent().getManager();
      return manager.getWidth() - 4 - 8;
   }

   @Override
   public final int getHistoryPosition(BrowserContent browserContent) {
      return 0;
   }

   @Override
   public final String getHTTPCookie(String url) {
      RenderingApplication app = this.getRenderingApplication();
      return app != null ? app.getHTTPCookie(url) : null;
   }

   @Override
   public final HttpConnection getResource(RequestedResource resource, BrowserContent referrer) {
      RenderingApplication app = this.getRenderingApplication();
      return app != null ? app.getResource(resource, referrer) : null;
   }

   @Override
   public final void invokeRunnable(Runnable runnable) {
      RenderingApplication app = this.getRenderingApplication();
      if (app != null) {
         app.invokeRunnable(runnable);
      }
   }
}
