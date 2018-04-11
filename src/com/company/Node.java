package com.company;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Node {

    public String name;
    public List<Integer> spellsCast = new ArrayList<Integer>();
    private EnumMap<Gestures,Node> children;

    //Moves: D(0),P(1),S(2),W(3),C(4),F(5),-(6),>(7),d(8),p(9),s(10),w(11) (8 unique, 12 with doubles)

    public Node(String name)
    {
        children = new EnumMap<Gestures, Node>(Gestures.class);
        this.name = name;
    }

    public void addChild(Gestures gesture, String name)
    {
        children.put(gesture,new Node(name));
    }

    public void addChild(Gestures gesture, Node child)
    {
        children.put(gesture,child);
    }

    public Node getChild(Gestures gesture)
    {
        if(children.containsKey(gesture)) {
            return children.get(gesture);
        }

        return null;
    }

}
