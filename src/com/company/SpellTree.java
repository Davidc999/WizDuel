package com.company;

public class SpellTree {

    public static String[] spellList = {"CDPW","CSWWS","Cw","DFFDD","DFPW","DFW","DPP","DSF","DSFFFC","DWFFd","DWSSSP","DWWFWC","DWWFWD","FFF","FPSFW","FSSDD","P","p","PDWP","PPws","PSDD",
    "PSDF","PSFW","PWPFSSSD","PWPWWC","SD","SFW","SPF","SPFPSDW","SPPC","SSFP","SWD","SWWC","WDDC","WFP","WFPSFW","WPFD","WPP","WSSC","WWFP","WWP","WWS"};
    public static String[] spellNames = {"Dispel magic", "Summon elemental", "Magic mirror", "Lightning bolt", "Cure heavy wounds", "Cure light wounds", "Amnesia", "Confusion", "Disease",
            "Blindness", "Delayed effect", "Raise dead", "Poison", "Paralysis", "Summon troll", "Fireball", "Shield", "Surrender", "Remove enchantment", "Invisibility", "Charm monster",
            "Charm person", "Summon ogre", "Finger of death", "Haste", "Missile", "Summon goblin", "Anti-spell", "Permanency", "Time stop", "Resist cold", "Fear", "Fire storm",
            "Lightning bolt", "Cause light wounds", "Summon giant", "Cause heavy wounds", "Counter-spell", "Ice storm", "Resist heat", "Protection from evil", "Counter-spell"};
    //public static String[] spellList = {"P","p","DPP"};
    public static boolean[] isDoubleHandedSpell = {false, false, true, false, false, false, false, false, true, true, false, true, false, false, false, false, false, true, false, true,
            false, false, false, false, true, false, false, false, false, true, false, false, true, true, false, false, false, false, true, false, false, false};

    public int debug_node_number=0;

    public Node root, currLocation;

    //Moves: D(0,'D'),P(1,'P'),S(2,'S'),W(3,'W'),C(4,'C'),F(5,'F'),d(6,'d'),p(7,'p'),s(8,'s'),w(9,'w'),nothing(10,'-'),stab(11,'>') (8 unique, 12 with doubles)

    public SpellTree()
    {
        root = new Node("");
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
        for(int spellInd = 0; spellInd < spellList.length; spellInd++)
        {
            //Start casting a new spell from the root node
            tempNode = root;
            currSpell = spellList[spellInd];
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
        for(int spellInd = 0; spellInd < spellList.length; spellInd++)
        {
            if (root.name.length() >= spellList[spellInd].length())
            {
                if(root.name.substring(root.name.length() - spellList[spellInd].length()).equals(spellList[spellInd]))
                {
                    root.spellsCast.add(spellInd);
                }
            }
        }
        //Go down the tree and recourse
        Gestures gest;
        Node nodeToCheck;
        for(int gestInd = 0; gestInd < Gestures.GESTURES_INDEXED.length-2; gestInd++) {
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
        for(int gestInd = 0; gestInd < Gestures.GESTURES_INDEXED.length-2; gestInd++)
        {
            gest = Gestures.GESTURES_INDEXED[gestInd];
            nodeToCheck = root.getChild(gest);
            if(nodeToCheck != null)
            {
                addShortcutsToSubtree(nodeToCheck);
            }
            else
            {
                gestSequenceToCheck = (root.name + gest.gestureChar);
                shortcut = deepestChild(gestSequenceToCheck.substring(1)); //No need to check the full sequence, it's already null
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
        //TODO: Add clap verification!
        Node nodeToCheck;

        // Verify clap
        if((thisGesture == Gestures.C) && (otherGesture!=Gestures.C) )
        { thisGesture = Gestures.nothing; }

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

}
