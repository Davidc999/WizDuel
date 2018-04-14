package com.company;

public class SpellLibrary {
    public static String[] spellList = {"CDPW","CSWWS","Cw","DFFDD","DFPW","DFW","DPP","DSF","DSFFFC","DWFFd","DWSSSP","DWWFWC","DWWFWD","FFF","FPSFW","FSSDD","P","p","PDWP","PPws","PSDD",
            "PSDF","PSFW","PWPFSSSD","PWPWWC","SD","SFW","SPF","SPFPSDW","SPPC","SSFP","SWD","SWWC","WDDC","WFP","WFPSFW","WPFD","WPP","WSSC","WWFP","WWP","WWS",">"};
    public static String[] spellNames = {"Dispel magic", "Summon elemental", "Magic mirror", "Lightning bolt", "Cure heavy wounds", "Cure light wounds", "Amnesia", "Confusion", "Disease",
            "Blindness", "Delayed effect", "Raise dead", "Poison", "Paralysis", "Summon troll", "Fireball", "Shield", "Surrender", "Remove enchantment", "Invisibility", "Charm monster",
            "Charm person", "Summon ogre", "Finger of death", "Haste", "Missile", "Summon goblin", "Anti-spell", "Permanency", "Time stop", "Resist cold", "Fear", "Fire storm",
            "Lightning bolt", "Cause light wounds", "Summon giant", "Cause heavy wounds", "Counter-spell", "Ice storm", "Resist heat", "Protection from evil", "Counter-spell","stab"};
    //public static String[] spellList = {"P","p","DPP"};
    public static boolean[] isDoubleHandedSpell = {false, false, true, false, false, false, false, false, true, true, false, true, false, false, false, false, false, true, false, true,
            false, false, false, false, true, false, false, false, false, true, false, false, true, true, false, false, false, false, true, false, false, false,false};
    public static boolean[] requiresTarget = {false, true, true, true, true, true, true, true, true, true, false, true, true, true, true, true, true, false, true, true, true, false,
            true, true, true, true, true, false, false, true, true, true, false, true, true, true, true, true, false, true, true, true, true};
}
