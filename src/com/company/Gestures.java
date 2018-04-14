package com.company;

import java.util.EnumMap;

public enum Gestures {
    D(0,'D'),P(1,'P'),S(2,'S'),W(3,'W'),C(4,'C'),F(5,'F'),d(6,'d'),p(7,'p'),s(8,'s'),w(9,'w'),stab(10,'>'),nothing(11,'-');

    int gestureIndex;
    char gestureChar;
    public static int doubleOffset = 6;

    public static Gestures[] GESTURES_INDEXED = new Gestures[] { D,P,S,W,C,F,d,p,s,w,stab,nothing };

    public static boolean isBraveGesture(Gestures gesture)
    {
        return (gesture == D) || (gesture == C) || (gesture == F) || (gesture == S);
    }

    public static Gestures getGestureByChar(char character)
    {
        for(Gestures m: Gestures.values())
        {
            if(m.gestureChar == character)
            {
                return m;
            }
        }
        return null;
    }

    Gestures(int gestureIndex,char gestureChar){
        this.gestureChar = gestureChar;
        this.gestureIndex = gestureIndex;
    }
}
