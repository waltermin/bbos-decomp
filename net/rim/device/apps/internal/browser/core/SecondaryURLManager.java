package net.rim.device.apps.internal.browser.core;

import java.util.Hashtable;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.internal.browser.html.HTMLImageInputField;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.page.RendererImageContainer;
import net.rim.device.apps.internal.browser.ui.BrowserBitmapField;
import net.rim.device.apps.internal.browser.ui.BrowserLinkBitmapField;
import net.rim.device.apps.internal.browser.ui.BrowserTextFlowManager;
import net.rim.device.apps.internal.browser.ui.TextFlowManager;
import net.rim.device.apps.internal.browser.util.ImageConverter;
import net.rim.device.apps.internal.browser.util.ImageMap;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.apps.internal.browser.wml.WMLAnchorVerb;
import net.rim.device.apps.internal.browser.wml.WMLAnchoredBitmapField;
import net.rim.device.apps.internal.browser.wml.WMLTextField;
import net.rim.vm.Array;
import org.w3c.dom.html2.HTMLAnchorElement;
import org.w3c.dom.html2.HTMLInputElement;

public final class SecondaryURLManager {
   private int _count;
   private Hashtable _stateLookup;
   private SecondaryURLManager$StateInfo[] _stateNodes;
   private RenderingOptions _renderingOptions;
   private BrowserTextFlowManager _cachedManager;

   public SecondaryURLManager(RenderingOptions renderingOptions) {
      this._renderingOptions = renderingOptions;
      this._stateLookup = new Hashtable();
      this._stateNodes = new SecondaryURLManager$StateInfo[0];
   }

   public final boolean addSecondaryURL(String url, SecondaryURLNode node) {
      SecondaryURLManager$StateInfo info = (SecondaryURLManager$StateInfo)this._stateLookup.get(url);
      if (info != null) {
         Arrays.add(info._nodes, node);
         return true;
      } else {
         info = new SecondaryURLManager$StateInfo(url, node);
         this._stateLookup.put(url, info);
         Arrays.add(this._stateNodes, info);
         this._count++;
         return false;
      }
   }

   public final boolean removeInlineDataRef(String url) {
      SecondaryURLManager$StateInfo info = (SecondaryURLManager$StateInfo)this._stateLookup.get(url);
      if (info != null) {
         if (info._state != 0) {
            info._state = 0;
            this._count++;
         }

         return info._nodes.length == 0;
      } else {
         return true;
      }
   }

   public final void inlineDataRefReceived(String url) {
      SecondaryURLManager$StateInfo info = (SecondaryURLManager$StateInfo)this._stateLookup.get(url);
      if (info == null) {
         info = new SecondaryURLManager$StateInfo(url, 2);
         this._stateLookup.put(url, info);
         Arrays.add(this._stateNodes, info);
      }
   }

   public final void setSecondaryURLRequested(String url) {
      SecondaryURLManager$StateInfo info = (SecondaryURLManager$StateInfo)this._stateLookup.get(url);
      if (info != null && info._state == 0) {
         info._state = 1;
         this._count--;
      }
   }

   public final String[] getSecondaryURLs(int amount) {
      if (this._count <= 0) {
         return null;
      }

      if (amount == this._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 11, 10)) {
         amount = this._count;
      }

      if (amount > 0 && this._stateNodes.length != 0) {
         String[] array = new String[amount];
         int arrayIndex = 0;
         int numNodes = this._stateNodes.length;

         for (int i = 0; i < numNodes && arrayIndex < amount; i++) {
            if (this._stateNodes[i]._state == 0) {
               array[arrayIndex] = this._stateNodes[i]._url;
               arrayIndex++;
            }
         }

         if (arrayIndex == 0) {
            return null;
         }

         if (arrayIndex != amount) {
            Array.resize(array, arrayIndex);
         }

         return array;
      } else {
         return null;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void secondaryURLReady(RendererImageContainer container, BrowserContentImpl browserContent) {
      if (container != null && container._url != null && container._image != null) {
         synchronized (Application.getEventLock()) {
            synchronized (this) {
               boolean var11 = false /* VF: Semaphore variable */;

               try {
                  var11 = true;
                  this.handleImage(container, browserContent);
                  var11 = false;
               } finally {
                  if (var11) {
                     if (this._cachedManager != null) {
                        this._cachedManager.setLayoutActive(true);
                        this._cachedManager = null;
                     }
                  }
               }

               if (this._cachedManager != null) {
                  this._cachedManager.setLayoutActive(true);
                  this._cachedManager = null;
               }
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void secondaryURLReady(RequestedResource resource, BrowserContentImpl browserContent) {
      if (resource != null) {
         InputConnection connection = resource.getInputConnection();
         String url = resource.getUrl();
         if (connection != null && url != null) {
            String contentType = RendererControl.getContentType(connection);
            String size = null;
            if (connection instanceof HttpConnection) {
               label198:
               try {
                  size = ((HttpConnection)connection).getHeaderField("x-rim-image-original-size");
               } finally {
                  break label198;
               }
            }

            RendererImageContainer container = new RendererImageContainer();
            container._url = url;
            if (size != null) {
               StringTokenizer tokenizer = new StringTokenizer(size, ',');
               container._width = Integer.parseInt(tokenizer.nextToken());
               container._height = Integer.parseInt(tokenizer.nextToken());
            }

            boolean var24 = false /* VF: Semaphore variable */;

            try {
               var24 = true;
               container._image = ImageConverter.convert(connection.openInputStream(), contentType);
               if (size == null) {
                  if (container._image != null) {
                     container._width = container._image.getWidth();
                     container._height = container._image.getHeight();
                     var24 = false;
                  } else {
                     var24 = false;
                  }
               } else {
                  var24 = false;
               }
            } finally {
               if (var24) {
                  return;
               }
            }

            if (container._image != null) {
               synchronized (Application.getEventLock()) {
                  synchronized (this) {
                     boolean var18 = false /* VF: Semaphore variable */;

                     try {
                        var18 = true;
                        this.handleImage(container, browserContent);
                        var18 = false;
                     } finally {
                        if (var18) {
                           if (this._cachedManager != null) {
                              this._cachedManager.setLayoutActive(true);
                              this._cachedManager = null;
                           }
                        }
                     }

                     if (this._cachedManager != null) {
                        this._cachedManager.setLayoutActive(true);
                        this._cachedManager = null;
                     }
                  }
               }
            }
         }
      }
   }

   private final int getMaxWidth(BrowserContentImpl browserContent) {
      if (browserContent != null) {
         RenderingApplication app = browserContent.getRenderingApplication();
         if (app != null) {
            return Math.max(app.getAvailableWidth(browserContent), 10);
         }
      }

      return Display.getWidth();
   }

   private final void handleImage(RendererImageContainer container, BrowserContentImpl browserContent) {
      SecondaryURLManager$StateInfo info = (SecondaryURLManager$StateInfo)this._stateLookup.get(container._url);
      if (info != null) {
         if (info._state == 0) {
            this._count--;
         }

         SecondaryURLNode[] nodes = info._nodes;
         SecondaryURLNode node = null;
         Field field = null;
         Manager manager = null;
         int nodeIndex = -1;

         for (SecondaryURLNode var17 : nodes) {
            field = var17.getUIField();
            if (field != null) {
               HTMLAnchorElement link = var17.getLink();
               EncodedImage image = container._image;
               manager = field.getManager();
               if (manager instanceof BrowserTextFlowManager) {
                  BrowserTextFlowManager btfm = (BrowserTextFlowManager)manager;
                  boolean layoutActive = btfm.isLayoutActive();
                  if (this._cachedManager != null && this._cachedManager != btfm && layoutActive) {
                     this._cachedManager.setLayoutActive(true);
                  }

                  if (layoutActive) {
                     this._cachedManager = btfm;
                     btfm.setLayoutActive(false);
                  }
               }

               container._width = Math.max(container._width, var17.getWidth());
               container._height = Math.max(container._height, var17.getHeight());
               if (!(field instanceof BrowserBitmapField)) {
                  if (var17 instanceof HTMLInputElement) {
                     if (manager instanceof TextFlowManager) {
                        HTMLImageInputField newField = new HTMLImageInputField(browserContent, (HTMLInputElement)var17, null, container._url, var17.getStyle());
                        newField.setImage(image);
                        var17.setUIField(newField);
                        ((TextFlowManager)manager).replaceField(field, newField);
                     }
                  } else if (field instanceof TextFlowManager) {
                     Object objCookie = var17.getCookie();
                     if (objCookie != null) {
                        ((TextFlowManager)field).setBackgroundImage(objCookie, image);
                     } else {
                        browserContent.setIcon(image);
                     }
                  } else {
                     nodeIndex = field.getIndex();
                     manager = field.getManager();
                     BrowserBitmapField newField = null;
                     if (field instanceof WMLTextField) {
                        WMLAnchorVerb anchorVerb = (WMLAnchorVerb)var17.getCookie();
                        newField = anchorVerb != null
                           ? new WMLAnchoredBitmapField(browserContent, null, container._url, var17.getStyle(), anchorVerb)
                           : new BrowserBitmapField(browserContent, null, container._url, var17.getStyle());
                        newField.setLimitVHSpace(false);
                        newField.setImage(image, var17.getHspace(), var17.getVspace(), var17.getWidth(), var17.getHeight());
                     } else {
                        ImageMap imageMap = var17.getImageMap();
                        newField = link == null && imageMap == null
                           ? new BrowserBitmapField(browserContent, null, container._url, var17.getStyle())
                           : new BrowserLinkBitmapField(browserContent, null, container._url, var17.getStyle(), link, imageMap);
                        newField.setImage(image, var17.getHspace(), var17.getVspace(), container._width, container._height);
                     }

                     Manager mng = field.getManager();
                     Field f = mng.getLeafFieldWithFocus();
                     boolean hasFocus = f == field;
                     manager.deleteRange(nodeIndex, 1);
                     manager.insert(newField, nodeIndex);
                     if (hasFocus) {
                        newField.setFocus();
                     }
                  }
               } else {
                  ((BrowserBitmapField)field).setImage(image, var17.getHspace(), var17.getVspace(), container._width, container._height);
                  if (var17.getReplaceTag() != -1 && manager instanceof BrowserTextFlowManager) {
                     BrowserTextFlowManager btfm = (BrowserTextFlowManager)manager;
                     btfm.toggleOutOfLineFieldAlt(var17.getReplaceTag(), false);
                  }
               }
            }
         }

         this._stateLookup.remove(container._url);
         Arrays.remove(this._stateNodes, info);
      }
   }

   public final boolean hasUnrequestedImages() {
      return this._count > 0;
   }

   public final int getUnrequestedCount() {
      return this._count;
   }
}
