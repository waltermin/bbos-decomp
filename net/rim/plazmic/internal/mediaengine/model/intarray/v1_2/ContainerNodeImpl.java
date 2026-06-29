package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

public class ContainerNodeImpl extends NodeImpl {
   public static int getFirstChild(int containerHandle, ModelInteractorImpl model) {
      return model._nodes[containerHandle + 6];
   }

   static void setFirstChild(int firstHandle, int containerHandle, ModelInteractorImpl model) {
      if (containerHandle >= 0) {
         model._nodes[containerHandle + 6] = firstHandle;
      }
   }

   public static int getLastChild(int containerHandle, ModelInteractorImpl model) {
      return model._nodes[containerHandle + 7];
   }

   protected static void setLastChild(int lastHandle, int containerHandle, ModelInteractorImpl model) {
      if (containerHandle >= 0) {
         model._nodes[containerHandle + 7] = lastHandle;
      }
   }

   private static void setOnlyChild(int childHandle, int containerHandle, ModelInteractorImpl model) {
      setLastChild(childHandle, containerHandle, model);
      setFirstChild(childHandle, containerHandle, model);
      NodeImpl.setParent(containerHandle, childHandle, model);
   }

   public static void insertFirstChild(int firstChild, int containerHandle, ModelInteractorImpl model) {
      if (!NodeImpl.isInSceneGraph(firstChild, model) && isContainer(containerHandle, model) && NodeImpl.getType(firstChild, model) != 32) {
         if (getLastChild(containerHandle, model) < 0) {
            setOnlyChild(firstChild, containerHandle, model);
         } else {
            NodeImpl.insertPreviousSibling(firstChild, getFirstChild(containerHandle, model), model);
         }

         NodeImpl.setDirtyBits(firstChild, model, -16777216);
      } else {
         throw new Object("The node to be inserted already exists in the scene graph or is a TSpan node, or the parent is not a container.");
      }
   }

   public static void insertLastChild(int lastChild, int containerHandle, ModelInteractorImpl model) {
      if (!NodeImpl.isInSceneGraph(lastChild, model) && isContainer(containerHandle, model) && NodeImpl.getType(lastChild, model) != 32) {
         if (getFirstChild(containerHandle, model) < 0) {
            setOnlyChild(lastChild, containerHandle, model);
         } else {
            NodeImpl.insertNextSibling(lastChild, getLastChild(containerHandle, model), model);
         }

         NodeImpl.setDirtyBits(lastChild, model, -16777216);
      } else {
         throw new Object("The node to be inserted already exists in the scene graph or is a TSpan node, or the parent is not a container.");
      }
   }

   public static void deleteAllChildren(int containerHandle, ModelInteractorImpl model) {
      int currentChild = getFirstChild(containerHandle, model);

      while (currentChild >= 0) {
         currentChild = NodeImpl.deleteNodeHandle(currentChild, model);
      }
   }

   public static boolean isContainer(int handle, ModelInteractorImpl model) {
      return NodeImpl.getType(handle, model) == 48 || NodeImpl.getType(handle, model) == 46;
   }
}
