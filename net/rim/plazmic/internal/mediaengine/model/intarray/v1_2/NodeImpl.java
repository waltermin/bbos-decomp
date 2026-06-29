package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.ModelInteractor;
import net.rim.plazmic.internal.mediaengine.service.node.ContainerNode;
import net.rim.plazmic.internal.mediaengine.service.node.Node;

public class NodeImpl implements Node {
   ModelInteractorImpl _model;
   int _handle;

   public static void initNode(int handle, ModelInteractorImpl model) {
      model._nodes[handle + 3] = -1;
      model._nodes[handle + 4] = -1;
      model._nodes[handle + 5] = -1;
      model._nodes[handle + 6] = -1;
      model._nodes[handle + 7] = -1;
   }

   public int getHandle() {
      return this._handle;
   }

   public static int getType(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 1];
   }

   @Override
   public String getId() {
      return getId(this._handle, this._model);
   }

   public static String getId(int handle, ModelInteractorImpl model) {
      return model.getId(handle);
   }

   @Override
   public void setId(String id) {
      setId(id, this._handle, this._model);
   }

   @Override
   public ModelInteractor getModel() {
      return this._model;
   }

   public static void setId(String id, int handle, ModelInteractorImpl model) {
      model.setId(handle, id);
   }

   @Override
   public Node getParent() {
      return this._model.getNodeObject(getParent(this._handle, this._model));
   }

   public static int getParent(int nodeHandle, ModelInteractorImpl model) {
      return model._nodes[nodeHandle + 3];
   }

   static void setParent(int parentHandle, int nodeHandle, ModelInteractorImpl model) {
      if (nodeHandle >= 0) {
         model._nodes[nodeHandle + 3] = parentHandle;
      }
   }

   @Override
   public Node getNextSibling() {
      return this._model.getNodeObject(getNextSibling(this._handle, this._model));
   }

   public static int getNextSibling(int nodeHandle, ModelInteractorImpl model) {
      return model._nodes[nodeHandle + 4];
   }

   static void setNextSibling(int nextHandle, int nodeHandle, ModelInteractorImpl model) {
      if (nodeHandle >= 0) {
         model._nodes[nodeHandle + 4] = nextHandle;
      }
   }

   @Override
   public Node getPreviousSibling() {
      return this._model.getNodeObject(getPreviousSibling(this._handle, this._model));
   }

   public static int getPreviousSibling(int nodeHandle, ModelInteractorImpl model) {
      return model._nodes[nodeHandle + 5];
   }

   static void setPreviousSibling(int previousHandle, int nodeHandle, ModelInteractorImpl model) {
      if (nodeHandle >= 0) {
         model._nodes[nodeHandle + 5] = previousHandle;
      }
   }

   @Override
   public void insertNextSibling(Node nodeToInsert) {
      if (nodeToInsert != null) {
         insertNextSibling(((NodeImpl)nodeToInsert).getHandle(), this._handle, this._model);
      }
   }

   public static void insertNextSibling(int nextSibling, int nodeHandle, ModelInteractorImpl model) {
      if (!isInSceneGraph(nextSibling, model) && TextNodeImpl.checkTSpan(nextSibling, nodeHandle, model)) {
         int parent = getParent(nodeHandle, model);
         int oldNextSibling = getNextSibling(nodeHandle, model);
         setParent(parent, nextSibling, model);
         setNextSibling(oldNextSibling, nextSibling, model);
         setPreviousSibling(nodeHandle, nextSibling, model);
         setNextSibling(nextSibling, nodeHandle, model);
         setPreviousSibling(nextSibling, oldNextSibling, model);
         if (ContainerNodeImpl.isContainer(parent, model) && ContainerNodeImpl.getLastChild(parent, model) == nodeHandle) {
            ContainerNodeImpl.setLastChild(nextSibling, parent, model);
         } else if (getType(parent, model) == 50 && TextNodeImpl.getLastTSpan(parent, model) == nodeHandle) {
            TextNodeImpl.setLastTSpan(nextSibling, parent, model);
         }

         if (AnimationModel.isVisual(getType(nextSibling, model))) {
            setDirtyBits(nextSibling, model, -16777216);
            model._nodes[nextSibling + 15]++;
         }
      } else {
         throw new IllegalArgumentException("The node to be inserted already exists in the scene graph or only one of the nodes is a TSpan.");
      }
   }

   @Override
   public void insertPreviousSibling(Node nodeToInsert) {
      if (nodeToInsert != null) {
         insertPreviousSibling(((NodeImpl)nodeToInsert).getHandle(), this._handle, this._model);
      }
   }

   public static void insertPreviousSibling(int previousSibling, int nodeHandle, ModelInteractorImpl model) {
      if (!isInSceneGraph(previousSibling, model) && TextNodeImpl.checkTSpan(previousSibling, nodeHandle, model)) {
         int parent = getParent(nodeHandle, model);
         int oldPreviousSibling = getPreviousSibling(nodeHandle, model);
         setParent(parent, previousSibling, model);
         setNextSibling(nodeHandle, previousSibling, model);
         setPreviousSibling(oldPreviousSibling, previousSibling, model);
         setPreviousSibling(previousSibling, nodeHandle, model);
         setNextSibling(previousSibling, oldPreviousSibling, model);
         if (ContainerNodeImpl.isContainer(parent, model) && ContainerNodeImpl.getFirstChild(parent, model) == nodeHandle) {
            ContainerNodeImpl.setFirstChild(previousSibling, parent, model);
         } else if (getType(parent, model) == 50 && TextNodeImpl.getFirstTSpan(parent, model) == nodeHandle) {
            TextNodeImpl.setFirstTSpan(previousSibling, parent, model);
         }

         if (AnimationModel.isVisual(getType(previousSibling, model))) {
            setDirtyBits(previousSibling, model, -16777216);
            model._nodes[previousSibling + 15]++;
         }
      } else {
         throw new IllegalArgumentException("The node to be inserted already exists in the scene graph or only one of the nodes is a TSpan.");
      }
   }

   @Override
   public Node removeNode() {
      return this.removeNode(false);
   }

   @Override
   public Node removeNode(boolean leaveChildren) {
      return this._model.getNodeObject(removeNodeHandle(leaveChildren, this._handle, this._model));
   }

   public static int removeNodeHandle(int handle, ModelInteractorImpl model) {
      return removeNodeHandle(false, handle, model);
   }

   public static int removeNodeHandle(boolean leaveChildren, int handle, ModelInteractorImpl model) {
      if (!isInSceneGraph(handle, model)) {
         throw new IllegalArgumentException("The node to be removed does not exist in the scene graph.");
      }

      int parent = getParent(handle, model);
      int nextSibling = getNextSibling(handle, model);
      int prevSibling = getPreviousSibling(handle, model);
      if (!ContainerNodeImpl.isContainer(handle, model) || leaveChildren && ContainerNodeImpl.getFirstChild(handle, model) < 0) {
         leaveChildren = false;
      }

      if (leaveChildren) {
         int firstChild = ContainerNodeImpl.getFirstChild(handle, model);
         int lastChild = ContainerNodeImpl.getLastChild(handle, model);
         int currentChild = firstChild;

         while (currentChild >= 0) {
            int nextChild = getNextSibling(currentChild, model);
            setParent(parent, currentChild, model);
            currentChild = nextChild;
         }

         if (prevSibling >= 0) {
            setNextSibling(firstChild, prevSibling, model);
            setPreviousSibling(prevSibling, firstChild, model);
         } else if (parent >= 0) {
            ContainerNodeImpl.setFirstChild(firstChild, parent, model);
         }

         if (nextSibling >= 0) {
            setPreviousSibling(lastChild, nextSibling, model);
            setNextSibling(nextSibling, lastChild, model);
         } else if (parent >= 0) {
            ContainerNodeImpl.setLastChild(lastChild, parent, model);
         }

         ContainerNodeImpl.setFirstChild(-1, handle, model);
         ContainerNodeImpl.setLastChild(-1, handle, model);
      } else {
         if (prevSibling >= 0) {
            setNextSibling(nextSibling, prevSibling, model);
         } else {
            ContainerNodeImpl.setFirstChild(nextSibling, parent, model);
         }

         if (nextSibling >= 0) {
            setPreviousSibling(prevSibling, nextSibling, model);
         } else {
            ContainerNodeImpl.setLastChild(prevSibling, parent, model);
         }
      }

      initNode(handle, model);
      return nextSibling;
   }

   @Override
   public Node deleteNode() {
      return this.deleteNode(false);
   }

   @Override
   public Node deleteNode(boolean keepChildren) {
      if (this instanceof ContainerNode && !keepChildren) {
         ((ContainerNode)this).deleteAllChildren();
      }

      Node next = this._model.getNodeObject(deleteNodeHandle(keepChildren, this._handle, this._model));
      this._handle = Integer.MIN_VALUE;
      this._model = null;
      return next;
   }

   public static int deleteNodeHandle(int handle, ModelInteractorImpl model) {
      return deleteNodeHandle(false, handle, model);
   }

   public static int deleteNodeHandle(boolean keepChildren, int handle, ModelInteractorImpl model) {
      deleteTypedNode(handle, model);
      int next = getNextSibling(handle, model);
      if (ContainerNodeImpl.isContainer(handle, model) && !keepChildren) {
         ContainerNodeImpl.deleteAllChildren(handle, model);
      }

      if (isInSceneGraph(handle, model)) {
         removeNodeHandle(keepChildren, handle, model);
      }

      if (AnimationModel.isVisual(getType(handle, model))) {
         model.removeNodeInstance(model._nodes[handle + 9]);
      }

      model.deleteNode(handle);
      return next;
   }

   private static void deleteTypedNode(int handle, ModelInteractorImpl model) {
      int type = getType(handle, model);
      if (AnimationModel.isVisual(type)) {
         switch (type) {
            case 40:
               PathNodeImpl.delete(handle, model);
               break;
            case 42:
               ImageNodeImpl.delete(handle, model);
            case 44:
               if (type != 42) {
                  ForeignObjectNodeImpl.delete(handle, model);
               }
            case 46:
               ViewportNodeImpl.delete(handle, model);
               if (type != 46) {
                  break;
               }
            case 32:
            case 48:
            case 50:
               TextAttrNodeImpl.removeTextAttr(handle, model);
         }

         VisualNodeImpl.delete(handle, model);
      }
   }

   public static boolean isInSceneGraph(int handle, ModelInteractorImpl model) {
      return getParent(handle, model) >= 0 || model.getRootHandle() == handle;
   }

   static void setDirtyBits(int handle, ModelInteractorImpl model, int bits) {
      if (handle >= 0) {
         model._nodes[handle + 8] = model._nodes[handle + 8] | bits;
         model._nodes[handle + 16]++;
         if (getType(handle, model) == 32) {
            setDirtyBits(getNextSibling(handle, model), model, 67108864);
         }
      }
   }
}
