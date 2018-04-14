package com.company;

public class SpellTree {

    public int debug_node_number=0;

    public Node root, currLocation, prevlocation;

    //Moves: D(0,'D'),P(1,'P'),S(2,'S'),W(3,'W'),C(4,'C'),F(5,'F'),d(6,'d'),p(7,'p'),s(8,'s'),w(9,'w'),stab(10,'>'),nothing(11,'-'); (8 unique, 12 with doubles)

    public SpellTree()
    {
        root = new Node("");
        currLocation = root;
        currLocation = root;
        initTree();
        addSubSpellsToSubtree(root);
        addShortcutsToSubtree(root);
    }

    private void initTree()
    {
        //Go over all spells to build the tree states
        String currSpell;
        Node tempNode = root;
        String currString;
        Gestures currGest;
        for(int spellInd = 0; spellInd < SpellLibrary.spellList.length; spellInd++)
        {
            //Start casting a new spell from the root node
            tempNode = root;
            currSpell = SpellLibrary.spellList[spellInd];
            for(int currCharInd =0; currCharInd < currSpell.length(); currCharInd++)
            {
                currString = currSpell.substring(0,currCharInd+1);
                currGest = Gestures.getGestureByChar(currString.charAt(currString.length()-1));
                if(tempNode.getChild(currGest) == null) //Need to add a new node!
                {
                    tempNode.addChild(currGest,new Node(currString));
                    debug_node_number++;
                }
                //Walk into the existing node
                    tempNode = tempNode.getChild(currGest);
            }

        }
    }

    private void addSubSpellsToSubtree(Node root)
    {
        //Walk along the entire tree and add subspells

        // Add all spells cast by this node
        for(int spellInd = 0; spellInd < SpellLibrary.spellList.length; spellInd++)
        {
            if (root.name.length() >= SpellLibrary.spellList[spellInd].length())
            {
                if(root.name.substring(root.name.length() - SpellLibrary.spellList[spellInd].length()).equals(SpellLibrary.spellList[spellInd]))
                {
                    root.spellsCast.add(spellInd);
                }
            }
        }
        //Go down the tree and recurse
        Gestures gest;
        Node nodeToCheck;
        for(int gestInd = 0; gestInd < Gestures.GESTURES_INDEXED.length-1; gestInd++) {
            gest = Gestures.GESTURES_INDEXED[gestInd];
            nodeToCheck = root.getChild(gest);
            if(nodeToCheck != null)
            {
                addSubSpellsToSubtree(nodeToCheck);
            }
        }

    }

    private void addShortcutsToSubtree(Node root)
    {
        //After all tree states have been initialized, add shortcuts between the existing states.
        //This is done by an exhaustive walk along the tree, trying every possible move from every node, and checking where the move will take us.
        Gestures gest;
        Node nodeToCheck;
        Node shortcut;
        String gestSequenceToCheck;
        for(int gestInd = 0; gestInd < Gestures.GESTURES_INDEXED.length-1; gestInd++)
        {
            gest = Gestures.GESTURES_INDEXED[gestInd];
            nodeToCheck = root.getChild(gest);
            if(nodeToCheck != null)
            {
                addShortcutsToSubtree(nodeToCheck);
            }
            else
            {
                gestSequenceToCheck = (root.name + gest.gestureChar).toUpperCase();
                shortcut = deepestChild(gestSequenceToCheck.substring(0)); //The full sequence may still be relevant now that it's all uppercase!
                if(shortcut != null)
                {
                    root.addChild(gest,shortcut);
                }
            }
        }
    }

    private Node deepestChild(String gestureSequence)
    {
        //This function attempts to find the deepest node within the tree for a given sequence of gestures
        Node currNode = root;
        String seqToCheck;
        for(int startInd=0; startInd < gestureSequence.length(); startInd++)
        {
            currNode = root;
            seqToCheck = gestureSequence.substring(startInd);
            for(int charInd = 0; charInd < seqToCheck.length(); charInd++)
            {
                currNode = currNode.getChild(Gestures.getGestureByChar(seqToCheck.charAt(charInd)));
                if(currNode == null)
                    break;
            }
            if(currNode != null)
            {
                return currNode;
            }
        }
        return null; // Should not happen
    }

    public void walkTree(Gestures thisGesture,Gestures otherGesture)
    {
        Node nodeToCheck;
        prevlocation = currLocation;

        // Verify clap
        if((thisGesture == Gestures.C) && (otherGesture!=Gestures.C) )
        {
            thisGesture = Gestures.nothing;
            System.err.println("A single-handed clap amounts to nothing");
        }

        // Handle double gestures
        if((thisGesture == otherGesture) && (thisGesture.gestureIndex <= 3))
        { //Try to walk to a double handed node
            nodeToCheck = currLocation.getChild(Gestures.GESTURES_INDEXED[thisGesture.gestureIndex + Gestures.doubleOffset]);
            if(nodeToCheck == null) //On fail, relax to single-handed node
            {
                nodeToCheck = currLocation.getChild(thisGesture);
            }
        }
        else
        {
            nodeToCheck = currLocation.getChild(thisGesture);
        }
        if(nodeToCheck == null)
        {
            currLocation = root;
        }
        else
        {
            currLocation = nodeToCheck;
        }
    }

    public void walkBack()
    {
        currLocation = prevlocation;
    }

    public void resetTree()
    {
        currLocation = root;
    }

}
