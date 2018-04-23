package com.company;

public class SpellLibrary {
    public static String[] spellList = {"CDPW","CSWWS","Cw","DFFDD","DFPW","DFW","DPP","DSF","DSFFFC","DWFFd","DWSSSP","DWWFWC","DWWFWD","FFF","FPSFW","FSSDD","P","p","PDWP","PPws","PSDD",
            "PSDF","PSFW","PWPFSSSD","PWPWWC","SD","SFW","SPF","SPFPSDW","SPPC","SSFP","SWD","SWWC","WDDC","WFP","WFPSFW","WPFD","WPP","WSSC","WWFP","WWP","WWS",">"};

    public static String[] spellNames = {"Dispel magic", "Summon elemental", "Magic mirror", "Lightning bolt", "Cure heavy wounds", "Cure light wounds", "Amnesia", "Confusion", "Disease",
            "Blindness", "Delayed effect", "Raise dead", "Poison", "Paralysis", "Summon troll", "Fireball", "Shield", "Surrender", "Remove enchantment", "Invisibility", "Charm monster",
            "Charm person", "Summon ogre", "Finger of death", "Haste", "Missile", "Summon goblin", "Anti-spell", "Permanency", "Time stop", "Resist cold", "Fear", "Fire storm",
            "Lightning bolt", "Cause light wounds", "Summon giant", "Cause heavy wounds", "Counter-spell", "Ice storm", "Resist heat", "Protection from evil", "Counter-spell","stab"};
    //public static String[] spellList = {"Cw","WD"};
    public static boolean[] isDoubleHandedSpell = {false, false, true, false, false, false, false, false, true, true, false, true, false, false, false, false, false, true, false, true,
            false, false, false, false, true, false, false, false, false, true, false, false, true, true, false, false, false, false, true, false, false, false,false};
    public static boolean[] requiresTarget = {true, true, true, true, true, true, true, true, true, true, false, true, true, true, true, true, true, false, true, true, true, false,
            true, true, true, true, true, false, false, true, true, true, false, true, true, true, true, true, false, true, true, true, true};
    public static boolean[] reflectable = {false, false, false, true, false, false, true, true, true, true, false, false, true, true, false, true, false, false, false, false, false, true, true, false, false, false, true, false, false, false, false, true, false, true, true, false, true, false, false, false, false, false, false};
    public static int[] spellPriority = {3, 999, 4, 5, 5, 5, 5, 5, 999, 999, 999, 999, 999, 4, 2, 5, 4, 1, 999, 999, 999, 999, 2, 999, 999, 5, 2, 5, 999, 999, 999, 5, 999, 5, 5, 2, 5, 2, 999, 999, 4, 2, 5 };

    public static boolean isStatusEffectSpell(int spellindex)
    {
        return (spellindex == 7) || (spellindex == 6) || (spellindex == 20) || (spellindex == 21) || (spellindex == 13) || (spellindex ==31);
    }

    public static boolean isTargetableMonsterSpell(int spellindex)
    {
        return (spellindex == 14) || (spellindex == 22) || (spellindex == 26) || (spellindex == 35);
    }

    public static StatusEffect getStatusEffectFromIndex(int spellindex)
    {
        switch (spellindex) {
            case 6: return StatusEffect.amnesia;
            case 7: return StatusEffect.confusion;
            case 31: return StatusEffect.fear;
            case 13: return StatusEffect.paralyzed;
            case 20: return StatusEffect.charmed;
            case 21: return StatusEffect.charmed;
            default:
                return null;
        }
    }
}
