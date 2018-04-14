package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) {

        // Instantiate  player 1
        Player player1 = new Player(1);
        PlayerMove player1Moves[] = new PlayerMove[3];
        for (int i = 0; i < player1Moves.length; i++) {
            player1Moves[i] = new PlayerMove();
        }

        // Instantiate  player 2
        Player player2 = new Player(2);
        PlayerMove player2Moves[] = new PlayerMove[3];
        for (int i = 0; i < player2Moves.length; i++) {
            player2Moves[i] = new PlayerMove();
        }

        //Instantiate target list
        List<Entity> entities = new ArrayList<>();
        entities.add(player1);
        entities.add(player2);

        while (true) {
            player1.updateStatusEffects();
            player2.updateStatusEffects();
            printStatus(player1, player2);
            player1.getPlayerInput(player1Moves, entities);
            resolveMoves(player1Moves,null,entities);
        }

    }

    private static void printStatus(Player player1, Player player2) {
        String leftAlignFormat = "| %-9s | %-3s | %-9s | %-3s |%n";

        System.out.format("+-----------+-----+-----------+-----+%n");
        for (int index = 0; index < 8; index++) {
            System.out.format(leftAlignFormat, player1.getStatusString(index), player1.getMoveString(index), player2.getStatusString(index), player2.getMoveString(index));
        }
        System.out.format("+-----------+-----+-----------+-----+%n");
    }

    private static void resolveMoves(PlayerMove[] player1Moves, PlayerMove[] player2Moves, List<Entity> targets) {
        //TODO: Since spells must be resolved in some odd order, might be best to add them into a priority queue at this point and then send them on to some other function for resolution.
        PriorityQueue<PlayerMove> spellsQueue;
        final String[] handDescription = {"left hand", "Right hand", "Both hands"};

        //Resolve player 1 moves
        for (int hand = 0; hand < player1Moves.length; hand++) {
            castSpellatTarget(player1Moves[hand], hand, targets);
        }

        //Resolve player 2 moves
    }

    private static void castSpellatTarget(PlayerMove player1Move, int hand, List<Entity> targets) {
        switch (player1Move.spellIndex) {
            case -1:
                return;
            case 25: //Missile
                if(!targets.get(player1Move.spellTarget).hasEffect(StatusEffect.shielded)) { //unless shielded
                    System.out.println(targets.get(player1Move.spellTarget).name +" takes "+ANSI_RED+"1 damage " + ANSI_RESET + "from missile");
                    targets.get(player1Move.spellTarget).dealDamage(1);
                }
                else
                    System.out.println(targets.get(player1Move.spellTarget).name+" is "+ANSI_YELLOW+"shielded"+ANSI_RESET+" and takes no damage");
                break;
            case 16: // shield
                targets.get(player1Move.spellTarget).addEffect(StatusEffect.shielded);
                break;
            case 42: // stab
                if(!targets.get(player1Move.spellTarget).hasEffect(StatusEffect.shielded)) //unless shielded
                    targets.get(player1Move.spellTarget).dealDamage(1);
                else
                    System.out.println(targets.get(player1Move.spellTarget).name+" is "+ANSI_YELLOW+"shielded"+ANSI_RESET+" and takes no damage");
                break;
            default:
                System.out.println(ANSI_RED+"Spell not yet implemented"+ANSI_RESET);
        }
    }
}