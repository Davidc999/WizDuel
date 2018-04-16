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

    //Instantiate target list
    List<Entity> entities;

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
    }

    public void run() {
        //TODO: Add ability to attack new monsters!
        //TODO: When player is confused and gets another enchantment on same turn, announce that both do not work! It could be easier to just check at the end for multiple conflicting statuses and remove them all

        while (true) {

            player1.updateStatusEffects();
            player2.updateStatusEffects();
            printStatus();

            //Get player inputs
            player1Moves = player1.getPlayerInput(entities);
            player1.commandMonsters(entities);

            player2Moves = player2.getPlayerInput(entities);
            player2.commandMonsters(entities);

            //Handle confusion
            if (player1.hasEffect(StatusEffect.confusion))
                player1Moves = player1.handleConfusion(entities);
            if (player2.hasEffect(StatusEffect.confusion))
                player2Moves = player2.handleConfusion(entities);

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
        }
    }

    private void resolveMonsterMoves()
    {
        Monster currMonster;
        for(int i=2;i<entities.size();i++)
        {
            currMonster = (Monster)entities.get(i);
            System.out.println(currMonster.name+ Main.ANSI_RED+" attacks "+Main.ANSI_RESET+entities.get(currMonster.target).name+"...");
            if(entities.get(currMonster.target).hasEffect(StatusEffect.shielded))
            {
                System.out.println(entities.get(currMonster.target).name + " is " + Main.ANSI_YELLOW + "shielded" + Main.ANSI_RESET + " and takes no damage.");
            }
            else
            {
                System.out.println(entities.get(currMonster.target).name + " is " + Main.ANSI_RED + "hit for "+ currMonster.attackDmg +" damage." + Main.ANSI_RESET);
                entities.get(currMonster.target).dealDamage(currMonster.attackDmg);
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

        //Add player 1 moves to queue
        spellsQueue.addAll(player1Moves);

        //Add player 2 moves to queue
        spellsQueue.addAll(player2Moves);

        //Resolve moves by priority
        for (PlayerMove playerMove : spellsQueue) {
            //TODO: resolve new monster targets here
            castSpellatTarget(playerMove);
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

    private void describeSpell(PlayerMove playerMove) {
        switch (playerMove.spellIndex) {
            case -1:
                return;
            case 0: // Dispel Magic
                System.out.println(playerMove.moveMaker.name  + " casts " + Main.ANSI_BLUE + "dispel magic" + Main.ANSI_RESET + " with his " + playerMove.hand + " hand...");
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
            case 6: /*Amnesia*/ case 7: /*Confusion*/ case 31: /*Fear*/
                System.out.println(playerMove.moveMaker.name + " casts" + Main.ANSI_PURPLE + " "+SpellLibrary.spellNames[playerMove.spellIndex] + Main.ANSI_RESET + " at " + entities.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 16: // shield
                System.out.println(playerMove.moveMaker.name + " casts a" + Main.ANSI_YELLOW + " shield" + Main.ANSI_RESET + " with his " + playerMove.hand + " hand...");
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
            case 4: /*cure heavy wounds*/  case 5: /*cure light wounds*/
                System.out.println(playerMove.moveMaker.name + " casts " + Main.ANSI_GREEN + SpellLibrary.spellNames[playerMove.spellIndex] + Main.ANSI_RESET + " at " + entities.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
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

        // check for dispel magic
        if(entities.get(0).hasEffect(StatusEffect.dispel))
        {
            System.out.println(SpellLibrary.spellNames[playerMove.spellIndex] +" is dispelled!");
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
        if(SpellLibrary.isStatusEffectSpell(playerMove.spellIndex) && ((spellTarget.hasConflictingStatusEffect()) ))
        {
            System.out.println(SpellLibrary.spellNames[playerMove.spellIndex] + " is " + Main.ANSI_YELLOW + "cancelled out" + Main.ANSI_RESET + " by other " +Main.ANSI_PURPLE+"enchantments"+Main.ANSI_RESET);
            spellTarget.addEffect(StatusEffect.conflicting_status);
            return false;
        }

        return true;
    }

    private void applySpell(PlayerMove playerMove){

        switch (playerMove.spellIndex) {
            case -1:
                return;
            case 0: // Dispel Magic
                System.out.println(entities.get(playerMove.spellTarget).name + " is" + Main.ANSI_YELLOW + " shielded." + Main.ANSI_RESET +" All enchantments are removed, all magic spells fail!");
                //TODO: dispel monsters once they are implemented!
                for (Entity target1 : entities) {
                    target1.clearEffects();
                    target1.addEffect(StatusEffect.dispel);
                }
                entities.get(playerMove.spellTarget).addEffect(StatusEffect.shielded);
                break;
            case 2: // Magic mirror
                System.out.println(entities.get(playerMove.spellTarget).name + " is" + Main.ANSI_YELLOW + " reflecting magic." + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).addEffect(StatusEffect.magic_mirror);
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
                entities.get(playerMove.spellTarget).addEffect(StatusEffect.amnesia);
                break;
            case 7: // Confusion
                System.out.println(entities.get(playerMove.spellTarget).name+ " looks a bit" + Main.ANSI_PURPLE + " confused!" + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).addEffect(StatusEffect.confusion);
                break;
            case 15: //fireball
                System.out.println(entities.get(playerMove.spellTarget).name + " is burned for " + Main.ANSI_RED + "5 damage." + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).dealDamage(5);
                break;
            case 16: // shield
                System.out.println(entities.get(playerMove.spellTarget).name + " is" + Main.ANSI_YELLOW + " shielded." + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).addEffect(StatusEffect.shielded);
                break;
            case 17: //surrender
                break;
            case 25: //Missile
                System.out.println(entities.get(playerMove.spellTarget).name + " takes " + Main.ANSI_RED + "1 damage " + Main.ANSI_RESET + "from missile.");
                entities.get(playerMove.spellTarget).dealDamage(1);
                break;
            case 26: /*Summon goblin*/
                Monster goblin;
                Monster ownerMonster;
                Player ownerPlayer;
                if(entities.get(playerMove.newMonsterTarget) instanceof Player) // Put under comand of player
                {goblin = new Monster(1,playerMove.moveMaker.id,playerMove.newMonsterTarget,"Goblin");}
                else // Put under command of monster's owner;
                {
                    ownerMonster = (Monster)entities.get(playerMove.newMonsterTarget);
                    goblin = new Monster(1, ownerMonster.owner, playerMove.newMonsterTarget, "Goblin");
                }
                ownerPlayer = (Player)entities.get(goblin.owner);
                ownerPlayer.newMonster = goblin;
                entities.add(goblin);
                System.out.println(goblin.name+Main.ANSI_CYAN+" springs into existence."+Main.ANSI_RESET +" It obeys "+ entities.get(goblin.owner).name + "'s commands.");
                break;
            case 27: //anti-spell
                Player target = (Player)entities.get(3-playerMove.moveMaker.id);
                System.out.println("All of Player "+target.name + "'s previous gestures are " + Main.ANSI_BLUE + "nullified."+Main.ANSI_RESET);
                target.resetTrees();
                break;
            case 31: //Fear
                System.out.println(entities.get(playerMove.spellTarget).name+ " looks" + Main.ANSI_PURPLE + " terrified!" + Main.ANSI_RESET);
                entities.get(playerMove.spellTarget).addEffect(StatusEffect.fear);
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
            case 36: //Cause heavy wounds
                System.out.println(entities.get(playerMove.spellTarget).name + " takes " + Main.ANSI_RED + "3 damage " + Main.ANSI_RESET + "from the spell.");
                entities.get(playerMove.spellTarget).dealDamage(3);
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
