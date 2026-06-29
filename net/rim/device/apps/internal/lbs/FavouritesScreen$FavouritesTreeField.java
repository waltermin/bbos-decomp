package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.ui.component.TreeFieldCallback;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.quickcontact.QuickContactIcons;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.im.InputMethodRequests;

final class FavouritesScreen$FavouritesTreeField extends TreeField {
   private Image _imageUpDown;
   Field _imRequest;
   private final FavouritesScreen this$0;

   FavouritesScreen$FavouritesTreeField(FavouritesScreen this$0, TreeFieldCallback treeFieldCallback, long style, Field imRequest) {
      super(treeFieldCallback, style);
      this.this$0 = this$0;
      this._imRequest = imRequest;
   }

   @Override
   public final void dispatchEvent(Event rEvent) {
      if (rEvent.getID() == 1004) {
         rEvent.setSource(this._imRequest);
         this._imRequest.dispatchEvent(rEvent);
      } else {
         this._imRequest.dispatchEvent(rEvent);
      }
   }

   @Override
   public final InputMethodRequests getInputMethodRequests() {
      return this._imRequest.getInputMethodRequests();
   }

   private final boolean expandNode(Object cookie) {
      boolean expand = true;
      if (!(cookie instanceof Object)) {
         return expand;
      }

      long folderID = ((Folder)cookie).getLUID();
      long[] collapsedFolders = null;
      return collapsedFolders == null ? expand : !this.containsLong(collapsedFolders, folderID);
   }

   private final boolean containsLong(long[] array, long val) {
      for (int i = 0; i < array.length; i++) {
         if (array[i] == val) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final int addChildNode(int parent, Object cookie) {
      int newNode = super.addChildNode(parent, cookie);
      if (newNode > 0 && this.expandNode(cookie)) {
         this.setExpanded(newNode, true);
      }

      return newNode;
   }

   @Override
   public final int addSiblingNode(int previousSibling, Object cookie) {
      int newNode = super.addSiblingNode(previousSibling, cookie);
      if (newNode > 0 && this.expandNode(cookie)) {
         this.setExpanded(newNode, true);
      }

      return newNode;
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      if (this.this$0._favouriteToMove == null) {
         return super.moveFocus(amount, status, time);
      }

      int currentNode = this.getCurrentNode();
      if (currentNode <= 0) {
         return amount;
      }

      while (amount != 0) {
         int parentNode = this.getParent(currentNode);
         int nextNode;
         if (amount >= 0) {
            nextNode = this.getNextSibling(currentNode);
            if (nextNode <= 0) {
               if (parentNode <= 0) {
                  break;
               }

               nextNode = this.getNextSibling(parentNode);
               if (nextNode <= 0) {
                  nextNode = this.getParent(parentNode);
               } else {
                  while (true) {
                     int firstChild = this.getFirstChild(nextNode);
                     if (firstChild <= 0) {
                        firstChild = this.getParent(nextNode);
                        if (parentNode != firstChild && !(this.getCookie(nextNode) instanceof Object)) {
                           nextNode = firstChild;
                        }
                        break;
                     }

                     this.setExpanded(nextNode, true);
                     nextNode = firstChild;
                  }
               }
            }
         } else {
            nextNode = this.getPreviousSibling(currentNode);
            if (nextNode <= 0) {
               for (int nextParent = parentNode; nextParent > 0; nextParent = this.getParent(nextParent)) {
                  nextNode = this.getPreviousSibling(nextParent);
                  if (nextNode > 0) {
                     break;
                  }
               }
            }
         }

         if (nextNode <= 0) {
            break;
         }

         Object nextCookie = this.getCookie(nextNode);
         if (nextCookie instanceof Object) {
            this.setCookie(currentNode, nextCookie);
            this.setCookie(nextNode, this.this$0._favouriteToMove);
            this.invalidateNode(currentNode);
            this.invalidateNode(nextNode);
            currentNode = nextNode;
         } else {
            this.deleteSubtree(currentNode);
            if (parentNode > 0) {
               this.invalidateNode(parentNode);
            }

            parentNode = nextNode;
            int firstChild = this.getFirstChild(nextNode);
            if (firstChild > 0 && (amount < 0 || this.getCookie(firstChild) instanceof Object)) {
               nextNode = firstChild;

               while (true) {
                  int nextSibling = this.getNextSibling(nextNode);
                  if (nextSibling <= 0 || amount >= 0 && !(this.getCookie(nextSibling) instanceof Object)) {
                     currentNode = this.addSiblingNode(nextNode, this.this$0._favouriteToMove);
                     break;
                  }

                  nextNode = nextSibling;
               }
            } else {
               currentNode = this.addChildNode(nextNode, this.this$0._favouriteToMove);
            }

            this.invalidateNode(parentNode);
            this.setExpanded(parentNode, true);
         }

         if (amount >= 0) {
            amount--;
         } else {
            amount++;
         }
      }

      this.setCurrentNode(currentNode);
      return amount;
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      if (this.this$0._favouriteToMove == null) {
         super.drawFocus(graphics, on);
      } else {
         int rowHeight = this.getRowHeight();
         XYRect focus = (XYRect)(new Object());
         this.getFocusRect(focus);
         int width = Display.getWidth() - focus.x;
         graphics.pushContext(focus.x, focus.y, width, focus.height, 0, 0);
         graphics.clear();
         this.paint(graphics);
         if (on) {
            graphics.drawRect(focus.x, focus.y, width, focus.height);
            if (this._imageUpDown == null) {
               IconCollection iconCollection = QuickContactIcons.MOVE_UP_DOWN_ICONS;
               if (iconCollection != null) {
                  this._imageUpDown = iconCollection.getImage(0);
               }
            }

            if (this._imageUpDown != null) {
               this._imageUpDown.paint(graphics, focus.x + width - rowHeight, focus.y, rowHeight, rowHeight);
            }
         }

         graphics.popContext();
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      if ((status & 1) != 0 && this.this$0._favouriteToMove == null) {
         if (dy != 0 && Math.abs(dy) >= Math.abs(dx)) {
            Screen screen = this.getScreen();
            if (screen != null) {
               screen.scroll(dy > 0 ? 512 : 256);
               return true;
            }
         } else {
            int currentNode = this.getCurrentNode();
            if (currentNode > 0 && !(this.getCookie(currentNode) instanceof Object)) {
               int parentNode = this.getParent(currentNode);
               if (parentNode > 0) {
                  if (dx < 0) {
                     this.setCurrentNode(parentNode);
                     this.invalidateNode(currentNode);
                     this.invalidateNode(parentNode);
                     return true;
                  }

                  while (parentNode > 0) {
                     int nextSibling = this.getNextSibling(parentNode);
                     if (nextSibling > 0 && this.getCookie(nextSibling) instanceof Object) {
                        this.setCurrentNode(nextSibling);
                        this.invalidateNode(currentNode);
                        this.invalidateNode(nextSibling);
                        return true;
                     }

                     parentNode = this.getParent(parentNode);
                  }
               }

               return true;
            }
         }
      }

      return super.navigationMovement(dx, dy, status, time);
   }
}
