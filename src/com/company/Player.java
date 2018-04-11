package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Player {

    int id, hp;
    List<Gestures> lastGesturesLeft; // This should be up to length 8
    List<Gestures> lastGesturesRight; // This should be up to length 8

    public Player(int id)
    {
        this.id = id;
        hp = 15;
        lastGesturesLeft  = new ArrayList<Gestures>(8);
        lastGesturesRight = new ArrayList<Gestures>(8);
    }

    public void addGestures(Gestures left, Gestures right)
    {
        if(lastGesturesLeft.size() == 8)
            lastGesturesLeft.remove(0);
        lastGesturesLeft.add(left);

        if(lastGesturesRight.size() == 8)
            lastGesturesRight.remove(0);
        lastGesturesRight.add(right);
    }

    public String getMoveString(int Lineindex)
    {
        if(Lineindex < lastGesturesRight.size())
            return lastGesturesLeft.get(Lineindex).gestureChar + " " + lastGesturesRight.get(Lineindex).gestureChar;
        else
            return " ";
    }

    public String getStatusString(int Lineindex)
    {
        switch (Lineindex)
        {
            case 0:
                return "Player" + id + ":";
            case 1:
                return hp + " hp";
            default:
                return " ";
        }
    }
}
