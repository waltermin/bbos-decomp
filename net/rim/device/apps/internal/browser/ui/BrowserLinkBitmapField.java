package net.rim.device.apps.internal.browser.ui;

import java.util.Vector;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.browser.html.SimpleHTMLAnchorElement;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.util.ImageMap;
import net.rim.device.apps.internal.browser.util.ImageMap$ImageArea;
import net.rim.device.apps.internal.browser.util.LinkType;
import net.rim.device.internal.ui.Cursor;
import org.w3c.dom.html2.HTMLAnchorElement;
import org.w3c.dom.html2.HTMLElement;

public class BrowserLinkBitmapField extends BrowserBitmapField implements CookieProvider {
   private HTMLAnchorElement _link;
   private long _cookieID;
   private ImageMap _imageMap;
   private int _currentFocusArea;

   public BrowserLinkBitmapField(BrowserContentImpl browserContent, Bitmap bitmap, String imageUrl, long style, String link, ImageMap imgMap) {
      this(browserContent, bitmap, imageUrl, style, link != null ? new SimpleHTMLAnchorElement(link) : null, imgMap, -1, -1, 0, null, null);
   }

   public BrowserLinkBitmapField(BrowserContentImpl browserContent, Bitmap bitmap, String imageUrl, long style, HTMLAnchorElement link, ImageMap imgMap) {
      this(browserContent, bitmap, imageUrl, style, link, imgMap, -1, -1, 0, null, null);
   }

   public BrowserLinkBitmapField(
      BrowserContentImpl browserContent,
      Bitmap bitmap,
      String imageUrl,
      long style,
      HTMLAnchorElement link,
      ImageMap imgMap,
      int specifiedWidth,
      int specifiedHeight,
      int focusType,
      String altText,
      HTMLElement element
   ) {
      super(browserContent, bitmap, imageUrl, style, true, specifiedWidth, specifiedHeight, focusType, altText, element);
      this._imageMap = imgMap;
      this._link = link;
      this.determineCookieId();
   }

   private void determineCookieId() {
      if (this._link != null) {
         String href = this._link.getHref();
         if (href != null) {
            this._cookieID = LinkType.getLinkType(href);
         }
      }
   }

   public long getCookieID() {
      return this._cookieID;
   }

   @Override
   public Object getCookieWithFocus() {
      String href = this._link != null ? this._link.getHref() : null;
      if (href == null && this._imageMap == null) {
         return null;
      }

      HTMLElement linkElement = this._link;
      int focusAreas = this._imageMap != null ? this._imageMap.getNumAreas() : 0;
      if (this._imageMap == null || this._currentFocusArea >= 0 && this._currentFocusArea < focusAreas) {
         if (this._imageMap != null && this._imageMap.isScaled()) {
            ImageMap$ImageArea area = this._imageMap.getArea(this._currentFocusArea);
            href = area.getHref();
            linkElement = area.getAreaElement();
            this._cookieID = 5019899335844518230L;
         }

         ContextObject context = (ContextObject)(new Object());
         if (linkElement != null || super._element != null) {
            Vector linkVector = (Vector)(new Object(4));
            linkVector.addElement(href);
            linkVector.addElement(null);
            linkVector.addElement(linkElement);
            linkVector.addElement(super._element);
            context.put(249, linkVector);
         }

         context.put(253, href);
         context.put(-442409970680484936L, super._browserContent);
         return FactoryUtil.createInstance(this._cookieID, context);
      } else {
         return null;
      }
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (this.getManager().getLeafFieldWithFocus() == this) {
         Verb[] verbs = new Object[0];
         Verb defaultVerb = CookieProviderUtilities.getFocusVerbs(this, null, verbs);
         int count = verbs.length;

         for (int idx = 0; idx < count; idx++) {
            int priority = verbs[idx] == defaultVerb ? 10 : Integer.MAX_VALUE;
            VerbMenuItem menuItem = (VerbMenuItem)(new Object(null, verbs[idx].getOrdering(), priority, verbs[idx], new Object(61)));
            contextMenu.addItem(menuItem);
            if (verbs[idx] == defaultVerb) {
               contextMenu.setDefaultItem(menuItem);
            }
         }
      }
   }

   @Override
   protected boolean shouldAcceptFocus() {
      return true;
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (this._imageMap != null && this._imageMap.isScaled()) {
         if (!on) {
            graphics.pushContext(this.getPaddingLeft(), this.getPaddingTop(), this.getBitmapWidth(), this.getBitmapHeight(), 0, 0);
            graphics.clear();
            this.paint(graphics);
            graphics.popContext();
         } else {
            graphics.setStipple(-1);
            graphics.setColor(15461355);
            if (this._currentFocusArea >= 0 && this._currentFocusArea < this._imageMap.getNumAreas()) {
               ImageMap$ImageArea area = this._imageMap.getArea(this._currentFocusArea);
               int[] xCoords = area.getXCoords();
               int[] yCoords = area.getYCoords();
               switch (area.getShape()) {
                  case -1:
                     break;
                  case 0:
                  case 3:
                  default: {
                     int x1 = xCoords[0];
                     int y1 = yCoords[0];
                     int width = xCoords[1] - xCoords[0];
                     int height = yCoords[1] - yCoords[0];
                     graphics.drawRect(x1, y1, width, height);
                     graphics.setColor(1052688);
                     graphics.setStipple(-858993460);
                     graphics.drawRect(x1, y1, width, height);
                     return;
                  }
                  case 1: {
                     int radius = area.getRadius();
                     int size = radius << 1;
                     int x1 = xCoords[0] - radius;
                     int y1 = yCoords[0] - radius;
                     graphics.drawArc(x1, y1, size, size, 0, 360);
                     graphics.setColor(1052688);
                     graphics.setStipple(-858993460);
                     graphics.drawArc(x1, y1, size, size, 0, 360);
                     return;
                  }
                  case 2:
                     this.invalidate();
                     int count = xCoords.length;
                     if (count > 1) {
                        graphics.drawPathOutline(xCoords, yCoords, null, null, true);
                     } else if (count == 1) {
                        graphics.drawPoint(xCoords[0], yCoords[0]);
                     }

                     graphics.setColor(1052688);
                     graphics.setStipple(-858993460);
                     if (count > 1) {
                        graphics.drawPathOutline(xCoords, yCoords, null, null, true);
                        return;
                     }

                     if (count == 1) {
                        graphics.drawPoint(xCoords[0], yCoords[0]);
                        return;
                     }
               }
            }
         }
      } else {
         super.drawFocus(graphics, on);
      }
   }

   @Override
   public void setImage(EncodedImage image, int hSpace, int vSpace, int preferredWidth, int preferredHeight) {
      if (this._imageMap != null) {
         this._imageMap.setOriginalSize(preferredWidth, preferredHeight);
         super._focusType = 0;
      }

      super.setImage(image, hSpace, vSpace, preferredWidth, preferredHeight);
      if (this._imageMap != null) {
         this._imageMap.setLayoutSize(this.getContentWidth(), this.getContentHeight());
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      if (this._imageMap != null && this._imageMap.isScaled()) {
         int focusAreas = this._imageMap.getNumAreas();
         if ((status & 196608) != 0 && !TextFlowManager._oneDNavigationMode) {
            int currentFocusArea = this._currentFocusArea;
            ImageMap$ImageArea closestArea = null;
            int closestAreaIndex = -1;
            if (currentFocusArea >= 0 && currentFocusArea < focusAreas) {
               ImageMap$ImageArea currentArea = this._imageMap.getArea(currentFocusArea);
               int closestDistance = 0;

               for (int i = focusAreas - 1; i >= 0; i--) {
                  if (currentFocusArea != i) {
                     ImageMap$ImageArea area = this._imageMap.getArea(i);
                     int distance;
                     if ((status & 65536) != 0) {
                        if (area.getMinY() > currentArea.getMaxY() || area.getMaxY() < currentArea.getMinY()) {
                           continue;
                        }

                        distance = amount >= 0 ? area.getMinX() - currentArea.getMinX() : currentArea.getMinX() - area.getMinX();
                        if (distance == 0 && (amount >= 0 ? area.getMaxX() < currentArea.getMaxX() : area.getMaxX() >= currentArea.getMaxX())) {
                           continue;
                        }
                     } else {
                        distance = amount >= 0 ? area.getMinY() - currentArea.getMaxY() : currentArea.getMinY() - area.getMaxY();
                     }

                     if (distance >= 0) {
                        if (closestArea == null || distance < closestDistance) {
                           closestArea = area;
                           closestAreaIndex = i;
                           closestDistance = distance;
                        } else if (closestArea != null && distance == closestDistance) {
                           int overlap;
                           int closestAreaOverlap;
                           if ((status & 65536) != 0) {
                              overlap = getVerticalOverlap(area, currentArea);
                              closestAreaOverlap = getVerticalOverlap(closestArea, currentArea);
                           } else {
                              overlap = getHorizontalOverlap(area, currentArea);
                              closestAreaOverlap = getHorizontalOverlap(closestArea, currentArea);
                           }

                           if (overlap > closestAreaOverlap || overlap == closestAreaOverlap && closestAreaIndex > i && amount < 0) {
                              closestArea = area;
                              closestAreaIndex = i;
                           }
                        }
                     }
                  }
               }
            }

            if (closestArea != null) {
               this._currentFocusArea = closestAreaIndex;
               this.updateFocusRegion(closestArea);
               return 0;
            }

            if ((status & 65536) != 0) {
               return 0;
            }

            this.setNullFocus(amount >= 0);
            return amount;
         } else {
            if (amount >= 0) {
               this._currentFocusArea++;
            } else {
               this._currentFocusArea--;
            }

            if (this._currentFocusArea < 0) {
               this.setNullFocus(false);
               return amount;
            } else if (this._currentFocusArea >= focusAreas) {
               this.setNullFocus(true);
               return amount;
            } else {
               this.updateFocusRegion(this._imageMap.getArea(this._currentFocusArea));
               return 0;
            }
         }
      } else {
         return super.moveFocus(amount, status, time);
      }
   }

   private void setNullFocus(boolean movingOffTheBottom) {
      if (this._imageMap != null) {
         if (movingOffTheBottom) {
            super._focusY = this.getBitmapHeight() + 1;
            super._focusX = 0;
            super._focusWidth = 3;
            super._focusHeight = 3;
            this._currentFocusArea = this._imageMap.getNumAreas();
            return;
         }

         super._focusY = 0;
         super._focusX = 0;
         super._focusWidth = 3;
         super._focusHeight = 3;
         this._currentFocusArea = -1;
      }
   }

   private static int getVerticalOverlap(ImageMap$ImageArea area1, ImageMap$ImageArea area2) {
      return getOverlap(area1.getMinY(), area1.getMaxY(), area2.getMinY(), area2.getMaxY());
   }

   private static int getHorizontalOverlap(ImageMap$ImageArea area1, ImageMap$ImageArea area2) {
      return getOverlap(area1.getMinX(), area1.getMaxX(), area2.getMinX(), area2.getMaxX());
   }

   private static int getOverlap(int a1, int a2, int b1, int b2) {
      int c1 = MathUtilities.clamp(a1, b1, a2);
      int c2 = MathUtilities.clamp(a1, b2, a2);
      return c2 - c1 > 0 ? c2 - c1 : -Math.min(Math.abs(b1 - a2), Math.abs(b2 - a1));
   }

   private boolean setInitialFocusArea(boolean fromTheTop) {
      if (this._imageMap != null) {
         ImageMap$ImageArea initialArea = null;
         int initialAreaIndex;
         int bestY;
         if (fromTheTop) {
            initialAreaIndex = -1;
            bestY = Integer.MAX_VALUE;
         } else {
            initialAreaIndex = this._imageMap.getNumAreas();
            bestY = Integer.MIN_VALUE;
         }

         for (int i = this._imageMap.getNumAreas() - 1; i >= 0; i--) {
            ImageMap$ImageArea area = this._imageMap.getArea(i);
            if (fromTheTop) {
               int y = area.getMinY();
               if (y <= bestY) {
                  bestY = y;
                  initialAreaIndex = i;
                  initialArea = area;
               }
            } else {
               int y = area.getMaxY();
               if (y > bestY) {
                  bestY = y;
                  initialAreaIndex = i;
                  initialArea = area;
               }
            }
         }

         this._currentFocusArea = initialAreaIndex;
         if (initialArea != null) {
            this.updateFocusRegion(initialArea);
            return true;
         }

         this.setNullFocus(fromTheTop);
      }

      return false;
   }

   @Override
   protected void layout(int width, int height) {
      super.layout(width, height);
      if (this._imageMap != null) {
         this._imageMap.setLayoutSize(this.getContentWidth(), this.getContentHeight());
      }
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      if (this._imageMap != null && this._imageMap.isScaled()) {
         this.setInitialFocusArea(direction >= 0);
      }
   }

   @Override
   protected boolean onCursorHover(int x, int y) {
      if (this._imageMap != null) {
         int oldFocusRect = this._currentFocusArea;
         this.hitTest(x, y);
         if (oldFocusRect != this._currentFocusArea) {
            this.invalidate();
         }

         return true;
      } else {
         return super.onCursorHover(x, y);
      }
   }

   @Override
   public Cursor getFocusCursor() {
      if (this._imageMap != null) {
         return this._currentFocusArea != -1 ? Cursor.getPredefinedCursor(1) : Cursor.getPredefinedCursor(0);
      } else {
         return this._link != null ? Cursor.getPredefinedCursor(1) : super.getFocusCursor();
      }
   }

   @Override
   protected boolean stylusTap(int xPos, int yPos, int status, int time) {
      if (this._imageMap == null) {
         return false;
      }

      this.hitTest(xPos, yPos);
      return false;
   }

   @Override
   public void getFocusRectPhantom(XYRect rect) {
      if (this._imageMap != null) {
         this.getFocusRect(rect);
      } else {
         super.getFocusRectPhantom(rect);
      }
   }

   private void updateFocusRegion(ImageMap$ImageArea area) {
      if (area.getShape() == 1) {
         int radius = area.getRadius();
         super._focusX = area.getXCoords()[0] - radius;
         super._focusY = area.getYCoords()[0] - radius;
         super._focusHeight = radius * 2;
         super._focusWidth = super._focusHeight;
      } else {
         super._focusY = area.getMinY();
         super._focusHeight = Math.max(area.getMaxY() - super._focusY, 3);
         super._focusX = area.getMinX();
         super._focusWidth = Math.max(area.getMaxX() - super._focusX, 3);
      }
   }

   private void hitTest(int xPos, int yPos) {
      int numAreas = this._imageMap.getNumAreas();

      for (int i = 0; i < numAreas; i++) {
         ImageMap$ImageArea area = this._imageMap.getArea(i);
         int[] xCoords = area.getXCoords();
         int[] yCoords = area.getYCoords();
         int j;
         int k;
         boolean c;
         switch (area.getShape()) {
            case -1:
               continue;
            case 0:
            case 3:
            default:
               if (xCoords[0] <= xPos && yCoords[0] <= yPos && xCoords[1] >= xPos && yCoords[1] >= yPos) {
                  this.updateFocusRegion(area);
                  this._currentFocusArea = i;
                  return;
               }
               continue;
            case 1:
               j = xCoords[0] - xPos;
               k = yCoords[0] - yPos;
               if (Math.sqrt(j * j + k * k) <= area.getRadius()) {
                  this.updateFocusRegion(area);
                  this._currentFocusArea = i;
                  return;
               }
               continue;
            case 2:
               c = false;
               j = 0;
               k = xCoords.length - 1;
         }

         for (; j < xCoords.length; k = j++) {
            if ((yCoords[j] <= yPos && yPos < yCoords[k] || yCoords[k] <= yPos && yPos < yCoords[j])
               && xPos < (xCoords[k] - xCoords[j]) * (yPos - yCoords[j]) / (yCoords[k] - yCoords[j]) + xCoords[j]) {
               c = !c;
            }
         }

         if (c) {
            this.updateFocusRegion(area);
            this._currentFocusArea = i;
            return;
         }
      }

      super._focusY = xPos;
      super._focusX = yPos;
      super._focusWidth = 3;
      super._focusHeight = 3;
      this._currentFocusArea = -1;
   }
}
