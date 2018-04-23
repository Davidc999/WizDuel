package com.company;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class Game {
    // Instantiate  player 1
    Player player1;
    List<PlayerMove> player1Moves;

    // Instantiate  player 2
    Player player2;
    List<PlayerMove> player2Moves;

    // Instantiate target list
    List<Entity> entities;

    // Instantiate global effects
    List<GlobalEffects> globalEffects;

    public Game()
    {
        // Instantiate  player 1
        player1 = new Player(0);

        // Instantiate  player 2
        player2 = new Player(1);

        //Instantiate target list
        entities = new ArrayList<>();
        entities.add(player1);
        entities.add(player2);

        // Instantiate global effects;
        globalEffects = new ArrayList<>(3);
    }

    public void run() {
        //TODO: Add ability to attack new monsters!
        //TODO: When player is confused and gets another enchantment on same turn, announce that both do not work! It could be easier to just check at the end for multiple conflicting statuses and remove them all
        //TODO: I think double confuse/amnesia/fear ETC should not cancel out, but be treated as one.
        //TODO: Counter-spell should act before remove enchantment and magic mirror! It cannot cancel finger of death nor dispel magic!
        //TODO: Make sure remove enchantment works after all enchantment spells...
        //TODO: Confusion, amnesia, paralysis effects on monsters!


        //TODO: I may have a HUUUGE enum problem with status effects. It seems that maybe they cannot each have an individual duration.... Try to paralyze both players for different duration to see!

        while (true) {

            // Reset global effects
            globalEffects.clear();

            // Update player status effects
            player1.updateStatusEffects();
            player2.updateStatusEffects();
//            printStatus();

            //Get player inputs
            player1Moves = player1.getPlayerMoves(entities);
            player1.commandMonsters(entities);

            player2Moves = player2.getPlayerMoves(entities);
            player2.commandMonsters(entities);

            //Handle confusion
            if (player1.hasEffect(StatusEffect.confusion))
                player1Moves = player1.handleConfusion(entities);
            if (player2.hasEffect(StatusEffect.confusion))
                player2Moves = player2.handleConfusion(entities);

            printStatus();
            resolvePlayerMoves();
            resolveMonsterMoves();

            checkDeaths();
        }
    }

    private void checkDeaths()
    {
        Entity entity;
        for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext();)
        {
            entity = iterator.next();
            if(entity.hp <= 0) //Entity has died
            {
                if(entity instanceof Player) //A player has died, game ends!
                {
                    System.out.println("Player " + entity.id + Main.ANSI_RED + " drops DEAD!" + Main.ANSI_RESET);
                    System.out.println("The game is over! Player " + (3 - entity.id) + " is the winner!");
                    System.exit(0);
                }
                else //Monster
                {
                    System.out.println(entity.name + Main.ANSI_RED + " dies!" + Main.ANSI_RESET);
                    iterator.remove();
                }
            }
            if(entity instanceof Monster && entity.hasEffect(StatusEffect.remove_enchantment))
            {
                System.out.println("The " + Main.ANSI_PURPLE + "enchantment" + Main.ANSI_RESET + " binding " + entity.name + " to this plane fails, and it disappears.");
                iterator.remove();
            }
        }
    }

    private void resolveMonsterMoves()
    {
        Monster currMonster;
        for(int i=2;i<entities.size();i++)
        {
            currMonster = (Monster)entities.get(i);
            if(currMonster.hasEffect(StatusEffect.paralyzed)) // paralyzed mosters can't attack
            {
                System.out.println(currMonster.name + " is " + Main.ANSI_PURPLE + "paralyzed" + Main.ANSI_RESET + " and cannot attack.");
            }
            else {
                System.out.println(currMonster.name + Main.ANSI_RED + " attacks " + Main.ANSI_RESET + entities.get(currMonster.target).name + "...");
                if (entities.get(currMonster.target).hasEffect(StatusEffect.shielded)) {
                    System.out.println(entities.get(currMonster.target).name + " is " + Main.ANSI_YELLOW + "shielded" + Main.ANSI_RESET + " and takes no damage.");
                } else {
                    System.out.println(entities.get(currMonster.target).name + " is " + Main.ANSI_RED + "hit for " + currMonster.attackDmg + " damage." + Main.ANSI_RESET);
                    entities.get(currMonster.target).dealDamage(currMonster.attackDmg);
                }
            }
        }

    }

    private void printStatus() {
        String leftAlignFormat = "| %-9s | %-3s | %-9s | %-3s |%n";

        System.out.format("+-----------+-----+-----------+-----+%n");
        for (int index = 0; index < 8; index++) {
            System.out.format(leftAlignFormat, player1.getStatusString(index), player1.getMoveString(index), player2.getStatusString(index), player2.getMoveString(index));
        }
        System.out.format("+-----------+-----+-----------+-----+%n");
    }

    private void resolvePlayerMoves() {
        //TODO: Since spells must be resolved in some odd order, might be best to add them into a priority queue at this point and then send them on to some other function for resolution.
        PriorityQueue<PlayerMove> spellsQueue = new PriorityQueue<>();
        PlayerMove currMove;

        //Add player 1 moves to queue
        spellsQueue.addAll(player1Moves);

        //Add player 2 moves to queue
        spellsQueue.addAll(player2Moves);

        //Resolve moves by priority
        while (!spellsQueue.isEmpty()) {
                //TODO: resolve new monster targets here. This relies on monsters being cast before other spells!
            currMove = spellsQueue.poll();
            if(checkTarget(currMove))  //If the target exists, attempt to cast the spell.
                castSpellatTarget(currMove);
        }
    }

    private void castSpellatTarget(PlayerMove playerMove) {
        boolean successfullyCast;

        describeSpell(playerMove);
        successfullyCast = checkObjections(playerMove);
        if (successfullyCast) {
            applySpell(playerMove);
        }
    }

    private boolean checkTarget(PlayerMove playerMove)
    {
        if(playerMove.spellTarget == -1) //target player1 New mosnter
        {
            if(player1.newMonster != null)
                playerMove.spellTarget = entities.lastIndexOf(player1.newMonster);
            else
                playerMove.spellTarget = -3;
        }

        if(playerMove.spellTarget == -2) //target player1 New mosnter
        {
            if(player2.newMonster != null)
                playerMove.spellTarget = entities.lastIndexOf(player2.newMonster);
            else
                playerMove.spellTarget = -3;
        }

        if(playerMove.spellTarget == -3) // target does not exist
        {
            //TODO: different 'fizzle' for stab!
            System.out.println(playerMove.moveMaker.name + " attempted to cast " +SpellLibrary.spellNames[playerMove.spellIndex] +" at a target that doesn't exist! The spell fizzles!");
            return false;
        }
        else
            return true;
    }

    private void describeSpell(PlayerMove playerMove) {
        switch (playerMove.spellIndex) {
            case -1:
                return;
                //TODO: Maybe introduce global effects such as storm, counter_spell magic, etc...
            case 0: /*Dispel Magic*/
            break;
            case 27: //anti-spell
                System.out.println(playerMove.moveMaker.name + " casts" + Main.ANSI_BLUE + " "+SpellLibrary.spellNames[playerMove.spellIndex] + Main.ANSI_RESET + " at " + entities.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand.");
                break;
            case 2: // Magic mirror
                System.out.println(playerMove.moveMaker.name + " casts a" + Main.ANSI_YELLOW + " magic mirror" + Main.ANSI_RESET + " with " + playerMove.hand + " hands...");
                break;
            case 3: /*Long lightning bolt*/
                System.out.println(playerMove.moveMaker.name + " fires a" + Main.ANSI_RED + " lightning bolt" + Main.ANSI_RESET + " at " + entities.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 4: /*cure heavy wounds*/  case 5: /*cure light wounds*/
                System.out.println(playerMove.moveMaker.name + " casts " + Main.ANSI_GREEN + SpellLibrary.spellNames[playerMove.spellIndex] + Main.ANSI_RESET + " at " + entities.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 6: /*Amnesia*/ case 7: /*Confusion*/ case 31: /*Fear*/ case 18: /*Remove enchantment */ case 13: /*Paralysis*/
                System.out.println(playerMove.moveMaker.name + " casts" + Main.ANSI_PURPLE + " "+SpellLibrary.spellNames[playerMove.spellIndex] + Main.ANSI_RESET + " at " + entities.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 16: // shield
                System.out.println(playerMove.moveMaker.name + " erects a" + Main.ANSI_YELLOW + " shield" + Main.ANSI_RESET + " with his " + playerMove.hand + " hand...");
                break;
            case 17: //surrender
                System.out.println(playerMove.moveMaker.name + Main.ANSI_GREEN + " Surrenders!" + Main.ANSI_RESET);
                System.out.println("The game is over! Player " + (2 - playerMove.moveMaker.id) + " is the winner!");
                System.exit(0);
                break;
            case 25: /*Missile */ case 15: /* Fireball */
                System.out.println(playerMove.moveMaker.name + " launches a " + Main.ANSI_RED + SpellLibrary.spellNames[playerMove.spellIndex] + Main.ANSI_RESET + " at " + entities.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 26: /*Summon goblin*/
                System.out.println(playerMove.moveMaker.name + " casts " + Main.ANSI_CYAN + SpellLibrary.spellNames[playerMove.spellIndex] + Main.ANSI_RESET + " at " + entities.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 33: /* short lightning bolt - cast only once!*/
                System.out.println(playerMove.moveMaker.name + " fires a" + Main.ANSI_RED + " lightning bolt" + Main.ANSI_RESET + " at " + entities.get(playerMove.spellTarget).name + " with a clap...");
                break;
            case 34: /*Cause light wounds*/ case 36: /*Cause heavy wounds*/
                System.out.println(playerMove.moveMaker.name + " casts " + Main.ANSI_RED + SpellLibrary.spellNames[playerMove.spellIndex] + Main.ANSI_RESET + " at " + entities.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 40: // Protection from evil
                System.out.println(playerMove.moveMaker.name + " casts" + Main.ANSI_YELLOW + " protection from evil" + Main.ANSI_RESET + " with his " + playerMove.hand + " hand...");
                break;
            case 41: /*counter-spell WWS*/ case 37: /* counter-spell WPP */
                System.out.println(playerMove.moveMaker.name  + " casts " + Main.ANSI_BLUE + SpellLibrary.spellNames[playerMove.spellIndex] + Main.ANSI_RESET + " at "+ entities.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 42: // stab
                System.out.println(playerMove.moveMaker.name + " attempts to" + Main.ANSI_RED + " stab" + Main.ANSI_RESET + " at " + entities.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            default:
                System.out.println(Main.ANSI_RED + "Spell not yet implemented" + Main.ANSI_RESET);
        }
    }

    private boolean checkObjections(PlayerMove playerMove) {
        Entity spellTarget = entities.get(playerMove.spellTarget);
        // Short lightning bolt - cast only once!
        if(playerMove.spellIndex == 33) {
            Player castingPlayer = (Player) entities.get(playerMove.moveMaker.id);
            if (!castingPlayer.castShortLightning) // If this version has not yet been cast
            {
                castingPlayer.castShortLightning = true;
                return true;
            }
            else {
                System.out.println("Player " + castingPlayer.id + " has already cast this spell. The spell fizzles uselessly.");
                return false;
            }
        }

        // check dispel magic
        if(globalEffects.contains(GlobalEffects.dispelMagic) && playerMove.spellIndex != 42) // stab cannot be countered
        {
            System.out.println(SpellLibrary.spellNames[playerMove.spellIndex] + Main.ANSI_BLUE + " fizzles" + Main.ANSI_RESET + " due to dispel Magic!");
            return false;
        }

        // check for counter_spell
        if(entities.get(0).hasEffect(StatusEffect.counter_spell) && playerMove.spellIndex != 0 && playerMove.spellIndex != 42) //Dispel & stab cannot be countered
        {
            System.out.println(SpellLibrary.spellNames[playerMove.spellIndex] + " is " + Main.ANSI_BLUE + "dispelled" + Main.ANSI_RESET + " by counter-spell!");
            return false;
        }

        //Check for shield
        boolean shieldableSpell = (playerMove.spellIndex == 25) || (playerMove.spellIndex == 42);
        if(spellTarget.hasEffect(StatusEffect.shielded) &&(shieldableSpell) )
        {
            System.out.println(spellTarget.name + " is " + Main.ANSI_YELLOW + "shielded" + Main.ANSI_RESET + " and takes no damage.");
            return false;
        }

        //Check for magic mirror
        if(spellTarget.hasEffect(StatusEffect.magic_mirror) &&(SpellLibrary.reflectable[playerMove.spellIndex]))
        {
            System.out.println(SpellLibrary.spellNames[playerMove.spellIndex] + " is " + Main.ANSI_YELLOW + "reflected" + Main.ANSI_RESET + " by the magic mirror!");
            playerMove.spellTarget = playerMove.moveMaker.id;
            applySpell(playerMove);
            return false;
        }

        // Check for conflicting status: confusion, anmesia, charm person, charm monster, paralysis or fear
        if(SpellLibrary.isStatusEffectSpell(playerMove.spellIndex) && ((spellTarget.hasConflictingStatusEffect(SpellLibrary.getStatusEffectFromIndex(playerMove.spellIndex))) ))
        {
            System.out.println(SpellLibrary.spellNames[playerMove.spellIndex] + " is " + Main.ANSI_YELLOW + "cancelled out" + Main.ANSI_RESET + " by other " +Main.ANSI_PURPLE+"enchantments"+Main.ANSI_RESET);
            spellTarget.addStatusEffect(StatusEffect.conflicting_status);
            return false;
        }

        return true;
    }

    private void applySpell(PlayerMove playerMove){

        Monster summon;
        Monster ownerMonster;
        Player ownerPlayer;
        StatusEffect newEffect;
        Entity currEntity;
        int paralyzedHandIndex;

        switch (playerMove.spellIndex) {
            case -1:
                return;
            case 0: // Dispel Magic
                System.out.println(entities.get(playerMove.spellTarget).name + " is" + Main.ANSI_YELLOW + " shielded." + Main.ANSI_RESET +" All "+ Main.ANSI_PURPLE+"enchantments"+ Main.ANSI_RESET+" are removed, all further "+Main.ANSI_BLUE+"magic"+Main.ANSI_RESET+" spells fail!");
                //TODO: counter_spell monsters once they are implemented!
                for (Iterator<Entity> iter = entities.iterator();iter.hasNext();) {
                    currEntity = iter.next();
                    if(currEntity instanceof Monster){
                        System.out.println(currEntity.name + " is " + Main.ANSI_BLUE + "unsummoned!" + Main.ANSI_RESET);
                        iter.remove();
                    }
                    else{ //Player - remove enchantments
                        currEntity.clearStatusEffects();
                    }
                }
                entities.get(playerMove.spellTarget).addStatusEffect(StatusEffect.shielded);
                globalEffects.add(GlobalEffects.dispelMagic);
                break;
            case 2: // Magic mirror
                System.out.println(entities.get(playerMove.spellTarget).name + " is" + Main.ANSI_YELLOW + " reflecting magic." + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).addStatusEffect(StatusEffect.magic_mirror);
                break;
            case 3: // Long lightning bolt
                System.out.println(entities.get(playerMove.spellTarget).name + " is zapped for " + Main.ANSI_RED + "5 damage." + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).dealDamage(5);
                break;
            case 4: //cure heavy wounds
                System.out.println(entities.get(playerMove.spellTarget).name + " is healed for " + Main.ANSI_GREEN + "2 damage." + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).heal(2);
                break;
            case 5: //cure light wounds
                System.out.println(entities.get(playerMove.spellTarget).name + " is healed for " + Main.ANSI_GREEN + "1 damage." + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).heal(1);
                break;
            case 6: // Amnesia
                System.out.println(entities.get(playerMove.spellTarget).name+ " has" + Main.ANSI_PURPLE + " forgotten" + Main.ANSI_RESET + " the magic gestures!");
                entities.get(playerMove.spellTarget).addStatusEffect(StatusEffect.amnesia);
                break;
            case 7: // Confusion
                System.out.println(entities.get(playerMove.spellTarget).name+ " looks a bit" + Main.ANSI_PURPLE + " confused!" + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).addStatusEffect(StatusEffect.confusion);
                break;
            case 14: /*Summon troll*/
                if(entities.get(playerMove.newMonsterTarget) instanceof Player) // Put under comand of player
                {summon = new Monster(3,playerMove.moveMaker.id,playerMove.newMonsterTarget,"Troll");}
                else // Put under command of monster's owner;
                {
                    ownerMonster = (Monster)entities.get(playerMove.newMonsterTarget);
                    summon = new Monster(3, ownerMonster.owner, playerMove.newMonsterTarget, "Troll");
                }
                ownerPlayer = (Player)entities.get(summon.owner);
                ownerPlayer.newMonster = summon;
                entities.add(summon);
                System.out.println(summon.name+Main.ANSI_CYAN+" springs into existence."+Main.ANSI_RESET +" It obeys "+ entities.get(summon.owner).name + "'s commands.");
                break;
            case 13: //Paralysis
                //TODO: If already paralyzed, do not choose hand!
                //TODO: Describe the paralysis to all players...
                currEntity = entities.get(playerMove.spellTarget);
                newEffect = StatusEffect.paralyzed;
                if(currEntity instanceof Player) //If cast at a player, chose a hand
                {
                    paralyzedHandIndex = playerMove.moveMaker.getParalyzedHandIndexInput(currEntity.name);
                    newEffect.initParalysis(paralyzedHandIndex);
                    System.out.println(currEntity.name + "'s " + Hand.HANDS_INDEXED[paralyzedHandIndex] + " hand is " + Main.ANSI_PURPLE + "paralyzed!" + Main.ANSI_RESET);
                }
                else
                {
                    System.out.println(currEntity.name + " is " + Main.ANSI_PURPLE+ "paralyzed!" + Main.ANSI_RESET);
                }
                currEntity.addStatusEffect(newEffect);
                break;
            case 15: //fireball
                System.out.println(entities.get(playerMove.spellTarget).name + " is burned for " + Main.ANSI_RED + "5 damage." + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).dealDamage(5);
                break;
            case 16: // shield
                System.out.println(entities.get(playerMove.spellTarget).name + " is" + Main.ANSI_YELLOW + " shielded." + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).addStatusEffect(StatusEffect.shielded);
                break;
            case 17: //surrender
                break;
            case 18: //remove enchantment
                if(entities.get(playerMove.spellTarget) instanceof Player) // Only removes player enchants. Monsters will be destroyed after attacking.
                {
                    System.out.println("All " + Main.ANSI_PURPLE + "enchantments " + Main.ANSI_RESET + "on " + entities.get(playerMove.spellTarget).name + "are removed.");
                    entities.get(playerMove.spellTarget).clearStatusEffects();
                }
                entities.get(playerMove.spellTarget).addStatusEffect(StatusEffect.remove_enchantment);
                break;
            case 22: /*Summon Ogre*/
                if(entities.get(playerMove.newMonsterTarget) instanceof Player) // Put under comand of player
                {summon = new Monster(2,playerMove.moveMaker.id,playerMove.newMonsterTarget,"Ogre");}
                else // Put under command of monster's owner;
                {
                    ownerMonster = (Monster)entities.get(playerMove.newMonsterTarget);
                    summon = new Monster(2, ownerMonster.owner, playerMove.newMonsterTarget, "Ogre");
                }
                ownerPlayer = (Player)entities.get(summon.owner);
                ownerPlayer.newMonster = summon;
                entities.add(summon);
                System.out.println(summon.name+Main.ANSI_CYAN+" springs into existence."+Main.ANSI_RESET +" It obeys "+ entities.get(summon.owner).name + "'s commands.");
                break;
            case 25: //Missile
                System.out.println(entities.get(playerMove.spellTarget).name + " takes " + Main.ANSI_RED + "1 damage " + Main.ANSI_RESET + "from missile.");
                entities.get(playerMove.spellTarget).dealDamage(1);
                break;
            case 26: /*Summon goblin*/
                if(entities.get(playerMove.newMonsterTarget) instanceof Player) // Put under comand of player
                {summon = new Monster(1,playerMove.moveMaker.id,playerMove.newMonsterTarget,"Goblin");}
                else // Put under command of monster's owner;
                {
                    ownerMonster = (Monster)entities.get(playerMove.newMonsterTarget);
                    summon = new Monster(1, ownerMonster.owner, playerMove.newMonsterTarget, "Goblin");
                }
                ownerPlayer = (Player)entities.get(summon.owner);
                ownerPlayer.newMonster = summon;
                entities.add(summon);
                System.out.println(summon.name+Main.ANSI_CYAN+" springs into existence."+Main.ANSI_RESET +" It obeys "+ entities.get(summon.owner).name + "'s commands.");
                break;
            case 27: //anti-spell
                Player target = (Player)entities.get(3-playerMove.moveMaker.id);
                System.out.println("All of Player "+target.name + "'s previous gestures are " + Main.ANSI_BLUE + "nullified."+Main.ANSI_RESET);
                target.resetTrees();
                break;
            case 31: //Fear
                System.out.println(entities.get(playerMove.spellTarget).name+ " looks" + Main.ANSI_PURPLE + " terrified!" + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).addStatusEffect(StatusEffect.fear);
                break;
            case 33: // Short lightning bolt - cast only once!
                Player castingPlayer = (Player) entities.get(playerMove.moveMaker.id);
                System.out.println(entities.get(playerMove.spellTarget).name + " is zapped for " + Main.ANSI_RED + "5 damage." + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).dealDamage(5);
                System.out.println("This spell cannot be cast again by Player " + castingPlayer.id);
                castingPlayer.castShortLightning = true;
                break;
            case 34: //Cause light wounds
                System.out.println(entities.get(playerMove.spellTarget).name + " takes " + Main.ANSI_RED + "2 damage " + Main.ANSI_RESET + "from the spell.");
                entities.get(playerMove.spellTarget).dealDamage(2);
                break;
            case 35: /*Summon Giant*/
                if(entities.get(playerMove.newMonsterTarget) instanceof Player) // Put under comand of player
                {summon = new Monster(4,playerMove.moveMaker.id,playerMove.newMonsterTarget,"Giant");}
                else // Put under command of monster's owner;
                {
                    ownerMonster = (Monster)entities.get(playerMove.newMonsterTarget);
                    summon = new Monster(4, ownerMonster.owner, playerMove.newMonsterTarget, "Giant");
                }
                ownerPlayer = (Player)entities.get(summon.owner);
                ownerPlayer.newMonster = summon;
                entities.add(summon);
                System.out.println(summon.name+Main.ANSI_CYAN+" springs into existence."+Main.ANSI_RESET +" It obeys "+ entities.get(summon.owner).name + "'s commands.");
                break;
            case 36: //Cause heavy wounds
                System.out.println(entities.get(playerMove.spellTarget).name + " takes " + Main.ANSI_RED + "3 damage " + Main.ANSI_RESET + "from the spell.");
                entities.get(playerMove.spellTarget).dealDamage(3);
                break;
                //TODO: Check counter_spell magic and counter-spell interaction. Dispel Magic should still work
            case 37: // counter-spell WPP
                System.out.println(entities.get(playerMove.spellTarget).name + " is " + Main.ANSI_YELLOW + " shielded " + Main.ANSI_RESET +"from " + Main.ANSI_BLUE + "magic "+Main.ANSI_RESET +"and " +Main.ANSI_RED+"physical "+Main.ANSI_RESET + "attacks.");
                entities.get(playerMove.spellTarget).addStatusEffect(StatusEffect.counter_spell);
                entities.get(playerMove.spellTarget).addStatusEffect(StatusEffect.shielded);
                break;
            case 40: // protection from evil
                System.out.println(entities.get(playerMove.spellTarget).name + " is" + Main.ANSI_YELLOW + " shielded " + Main.ANSI_RESET + "for this turn, and the next 3.");
                newEffect = StatusEffect.shielded;
                newEffect.changeDuration(4);
                entities.get(playerMove.spellTarget).addStatusEffect(newEffect);
                break;
            case 41: // counter-spell WWS
                System.out.println(entities.get(playerMove.spellTarget).name + " is " + Main.ANSI_YELLOW + " shielded " + Main.ANSI_RESET +"from " + Main.ANSI_BLUE + "magic "+Main.ANSI_RESET +"and " +Main.ANSI_RED+"physical "+Main.ANSI_RESET + "attacks.");
                entities.get(playerMove.spellTarget).addStatusEffect(StatusEffect.counter_spell);
                entities.get(playerMove.spellTarget).addStatusEffect(StatusEffect.shielded);
                break;
            case 42: // stab
                System.out.println(entities.get(playerMove.spellTarget).name + " is " + Main.ANSI_RED + "stabbed for 1 damage." + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).dealDamage(1);
                break;
            default:
                break;
        }
    }
}
