package net.rim.device.apps.internal.browser.util;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.javascript.ESFrames;
import net.rim.vm.Array;

public final class Frame {
   private String _name;
   private String _url;
   private Frame[] _frames;
   private Frame _parent;
   private ESFrames _esFrames;
   private int _id = -1;
   public static int _counter = -1;

   public Frame(Frame parent, String name, String url) {
      this(parent, name, url, ++_counter);
   }

   public Frame(Frame parent, String name, String url, int id) {
      this._parent = parent;
      this._name = name != null ? name.trim() : null;
      this._url = url;
      this._id = id;
   }

   public final void setId(int id) {
      this._id = id;
   }

   public final int getId() {
      return this._id;
   }

   public final ESFrames getESFrames() {
      return this._esFrames;
   }

   public final void setESFrames(ESFrames frames) {
      this._esFrames = frames;
   }

   public final void addFrame(Frame child) {
      if (this._frames == null) {
         this._frames = new Frame[1];
      } else {
         Array.resize(this._frames, this._frames.length + 1);
      }

      this._frames[this._frames.length - 1] = child;
   }

   public final String getName() {
      return this._name;
   }

   public final String getUrl() {
      return this._url;
   }

   public final void setUrl(String url) {
      this._url = url;
   }

   public final Frame getParent() {
      return this._parent;
   }

   public final Frame getTop() {
      return this._parent != null ? this._parent.getTop() : this;
   }

   public final Frame find(String name) {
      if (this._frames != null) {
         Frame foundFrame = null;
         Frame currentChildFrame = null;
         int length = this._frames.length;

         for (int i = 0; i < length; i++) {
            currentChildFrame = this._frames[i];
            if (StringUtilities.strEqualIgnoreCase(name, currentChildFrame.getName(), 1701707776)) {
               return currentChildFrame;
            }

            foundFrame = currentChildFrame.find(name);
            if (foundFrame != null) {
               return foundFrame;
            }
         }
      }

      return null;
   }

   public static final Frame findFrameById(Frame frame, int id) {
      Frame returnFrame = null;
      if (frame.getId() == id) {
         return frame;
      }

      Frame[] children = frame.getChildren();
      if (children == null) {
         return null;
      }

      int size = children.length;

      for (int i = 0; i < size; i++) {
         returnFrame = findFrameById(children[i], id);
         if (returnFrame != null) {
            return returnFrame;
         }
      }

      return returnFrame;
   }

   public final Frame getChild(String name) {
      if (this._frames != null) {
         int length = this._frames.length;

         for (int i = 0; i < length; i++) {
            if (StringUtilities.strEqualIgnoreCase(name, this._frames[i].getName(), 1701707776)) {
               return this._frames[i];
            }
         }
      }

      return null;
   }

   public final void replaceChild(Frame newChild, int index) {
      this._frames[index] = newChild;
   }

   public final int length() {
      return this._frames != null ? this._frames.length : 0;
   }

   public final Frame[] getChildren() {
      return this._frames;
   }

   public final void removeChildren() {
      this._frames = null;
   }

   public static final Frame clone(Frame frameToClone, Frame clonedParent, boolean cloneESFrames) {
      Frame clone = new Frame(clonedParent, frameToClone.getName(), frameToClone.getUrl(), frameToClone.getId());
      if (cloneESFrames) {
         clone.setESFrames(frameToClone.getESFrames());
      }

      Frame[] children = frameToClone.getChildren();
      if (children == null) {
         return clone;
      }

      int size = children.length;

      for (int i = 0; i < size; i++) {
         Frame clonedChild = clone(children[i], clone, cloneESFrames);
         clone.addFrame(clonedChild);
      }

      return clone;
   }

   public static final Frame getClone(Frame frameToClone, boolean cloneESFrames) {
      if (frameToClone == null) {
         return null;
      }

      Frame topLevelClone = clone(frameToClone.getTop(), null, cloneESFrames);
      return findFrameById(topLevelClone, frameToClone.getId());
   }

   public final boolean removeChild(Frame frameToRemove) {
      boolean ret = false;
      Frame[] children = null;
      if (this._frames != null) {
         int size = this._frames.length;
         int id = frameToRemove.getId();
         children = new Frame[size - 1];

         for (int i = 0; i < size; i++) {
            if (this._frames[i].getId() == id) {
               ret = true;
            } else if (ret) {
               children[i - 1] = this._frames[i];
            } else {
               children[i] = this._frames[i];
            }
         }
      }

      if (ret) {
         this._frames = children;
      }

      return ret;
   }

   public final void setUserBaseActionAcrossFrames(boolean userBasedAction) {
      if (this._parent != null) {
         this._parent.setUserBaseActionAcrossFrames(userBasedAction);
      } else {
         if (this._esFrames != null) {
            this._esFrames.getWindow().getJavaScriptEngine().setUserBasedAction(userBasedAction);
         }

         if (this._frames != null) {
            int length = this._frames.length;

            for (int i = 0; i < length; i++) {
               if (this._frames[i].getESFrames() != null) {
                  this._frames[i].getESFrames().getWindow().getJavaScriptEngine().setUserBasedAction(userBasedAction);
               }
            }
         }
      }
   }
}
