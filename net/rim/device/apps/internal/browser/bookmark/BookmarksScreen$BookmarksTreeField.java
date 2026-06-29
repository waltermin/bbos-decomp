package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.ui.component.TreeFieldCallback;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.quickcontact.QuickContactIcons;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.im.InputMethodRequests;

final class BookmarksScreen$BookmarksTreeField extends TreeField {
   private Image _imageUpDown;
   private Field _imRequest;
   private final BookmarksScreen this$0;

   BookmarksScreen$BookmarksTreeField(BookmarksScreen _1, TreeFieldCallback treeFieldCallback, long style, Field imRequest) {
      super(treeFieldCallback, style);
      this.this$0 = _1;
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

   @Override
   public final int getIndentForNode(int node) {
      return Math.min(super.getIndentForNode(node), this.getContentWidth() >> 1);
   }

   private final boolean expandNode(Object cookie) {
      boolean expand = cookie instanceof ProvisionedBookmarksFolder && ((ProvisionedBookmarksFolder)cookie).expandByDefault();
      if (!(cookie instanceof Object)) {
         return expand;
      }

      long folderID = ((Folder)cookie).getLUID();
      long[] collapsedFolders = null;
      Object obj = this.this$0.retrievePersistentObject();
      if (obj instanceof BookmarksTreeState) {
         collapsedFolders = ((BookmarksTreeState)obj).getCollpasedFolders();
      }

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

      Object cookie2 = this.getCookie(newNode);
      this.onNodeAdded(newNode, cookie2);
      return newNode;
   }

   @Override
   public final int addSiblingNode(int previousSibling, Object cookie) {
      int newNode = super.addSiblingNode(previousSibling, cookie);
      if (newNode > 0 && this.expandNode(cookie)) {
         this.setExpanded(newNode, true);
      }

      Object cookie2 = this.getCookie(newNode);
      this.onNodeAdded(newNode, cookie2);
      return newNode;
   }

   private final void onNodeAdded(int node, Object cookie) {
      Object obj = this.this$0.retrievePersistentObject();
      if (obj instanceof BookmarksTreeState) {
         BookmarksTreeState bm = (BookmarksTreeState)obj;
         if (bm.getSelectedNode() instanceof Object && cookie instanceof Object) {
            if (((Folder)cookie).getLUID() == ((Folder)bm.getSelectedNode()).getLUID()) {
               this.this$0._selectedNode = node;
               return;
            }
         } else if (bm.getSelectedNode() instanceof BrowserPageModel && cookie instanceof BrowserPageModel) {
            BrowserPageModel bpm = (BrowserPageModel)bm.getSelectedNode();
            BrowserPageModel bCookie = (BrowserPageModel)cookie;
            if (node > 1) {
               if (this.getParent(node) == 0) {
                  return;
               }

               Object parentCookie = this.getCookie(this.getParent(node));
               if (parentCookie instanceof ProvisionedBookmarksFolder && bpm.getTitle().equals(bCookie.getTitle())) {
                  this.this$0._selectedNode = node;
                  return;
               }
            }

            if (bpm.getUID() == bCookie.getUID()) {
               this.this$0._selectedNode = node;
               return;
            }
         } else if (bm.getSelectedNode() instanceof DummyPersistable && cookie instanceof ProvisionedBookmarksFolder) {
            DummyPersistable dp = (DummyPersistable)bm.getSelectedNode();
            if (dp.getLuid() == ((ProvisionedBookmarksFolder)cookie).getLUID()) {
               this.this$0._selectedNode = node;
            }
         }
      }
   }

   private final boolean currentBookmarkHasSibling() {
      int currentNode = this.getCurrentNode();
      if (currentNode > 0 && !(this.getCookie(currentNode) instanceof Object)) {
         int siblingNode = this.getNextSibling(currentNode);
         if (siblingNode > 0) {
            return true;
         }

         siblingNode = this.getPreviousSibling(currentNode);
         if (siblingNode > 0 && !(this.getCookie(siblingNode) instanceof Object)) {
            return true;
         }
      }

      return false;
   }

   private final int getCurrentBookmarkPositionInFolder() {
      int position = -1;

      for (int currentNode = this.getCurrentNode();
         currentNode > 0 && !(this.getCookie(currentNode) instanceof Object);
         currentNode = this.getPreviousSibling(currentNode)
      ) {
         position++;
      }

      return position;
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      if (this.this$0._bookmarkToMove == null) {
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
            this.setCookie(nextNode, this.this$0._bookmarkToMove);
            this.invalidateNode(currentNode);
            this.invalidateNode(nextNode);
            currentNode = nextNode;
         } else {
            if (((Folder)nextCookie).getLUID() == BrowserFolders.BROWSER_CHANNELS_FOLDER_ID) {
               if (amount >= 0) {
                  break;
               }

               nextNode = this.getPreviousSibling(nextNode);
               if (nextNode <= 0) {
                  break;
               }

               nextCookie = this.getCookie(nextNode);
            }

            if (this.this$0._bookmarkToMove instanceof PageModel && ((PageModel)this.this$0._bookmarkToMove).isHomePage()
               || this.this$0._folderBeforeMove.getLUID() == BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
               || nextCookie instanceof ProvisionedBookmarksFolder) {
               break;
            }

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
                     currentNode = this.addSiblingNode(nextNode, this.this$0._bookmarkToMove);
                     break;
                  }

                  nextNode = nextSibling;
               }
            } else {
               currentNode = this.addChildNode(nextNode, this.this$0._bookmarkToMove);
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
      if (this.this$0._bookmarkToMove == null) {
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
   protected final boolean keyChar(char character, int status, int time) {
      if (character == ' ' && this.this$0._bookmarkToMove == null && BookmarksScreen.access$1000(this.this$0).length() == 0) {
         int currentNode = this.getCurrentNode();
         if (currentNode > 0 && !(this.getCookie(currentNode) instanceof Object)) {
            Screen screen = this.getScreen();
            if (screen != null) {
               screen.scroll(512);
               return true;
            }
         }
      }

      return super.keyChar(character, status, time);
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      if ((status & 1) != 0 && this.this$0._bookmarkToMove == null) {
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
