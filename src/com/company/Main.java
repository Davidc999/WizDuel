package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {



    public static void main(String[] args) {

        // Instantiate  player 1
        Player player1 = new Player(1);
        int player1Spells[] = new int[3];

        // Instantiate  player 2
        Player player2 = new Player(2);
        int player2Spells[] = new int[3];

        //Instantiate target list
        List<Entity> entities = new ArrayList<>();
        entities.add(player1);
        entities.add(player2);

        while (true) {

            printStatus(player1, player2);
            player1.getPlayerInput(player1Spells,entities);
            resolveSpells(player1Spells,null);
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

    private static void resolveSpells(int[] player1Spells, int[] player2Spells)
    {
        final String[] handDescription ={"left hand", "Right hand", "Both hands"};

        for(int i=0; i < player1Spells.length; i++)
        {
            if(player1Spells[i] != -1)
                System.out.println("Player 1 casts " + SpellTree.spellNames[player1Spells[i]]+ " with " + handDescription[i]);
        }
    }

}
