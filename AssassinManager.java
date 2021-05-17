// Austin Faux
// CSE 143: Ken Aragon
// 4/25/19

// Generates one game of Assassin tracking a list of alive players and a list of dead players
// printing off who each player is stalking and who each player is killed by. 
// Reports that there was a winner to AssassinMain. 

import java.util.*;

public class AssassinManager {
   private AssassinNode front; // front of the kill Ring
   private AssassinNode graveyard; // list of killed players
   
   // Pre: list must not be empty (throws IllegalArgumentException if not)
   // Post: creates a list of players and stores their defualt killer in each node. 
   public AssassinManager(List<String> names) {
      if (names.isEmpty()) {
         throw new IllegalArgumentException();
      } 
      front = new AssassinNode(names.get(names.size() - 1)); // gets last player in list
      for (int i = names.size() - 2; i >= 0; i--) { 
         front = new AssassinNode(names.get(i), front);
         front.next.killer = front.name; 
      }
      AssassinNode curr = front;
      curr = moveForw(curr);
      front.killer = curr.name;
   }
   
   // Post: starting from the front, prints the players 
   //       and who each player is stalking with a 4 space indent. 
   public void printKillRing() {
      AssassinNode curr = front;
      while (curr.next != null) {
         System.out.println("    " + curr.name + " is stalking " + curr.next.name);
         curr = curr.next;
      }
      System.out.println("    " + curr.name + " is stalking " + front.name);
   }
   
   // Post: prints the list of players who have been killed and who they
   //       were killed by. With a 4 space indent. 
   public void printGraveyard() {
      AssassinNode curr = graveyard;
      while (curr != null) {
         System.out.println("    " + curr.name + " was killed by " + curr.killer); 
         curr = curr.next;
      } 
   }
   
   // Post: returns true if the passed in name is a player 
   //       and has not been killed and false otherwise.
   public boolean killRingContains(String name) {
      AssassinNode curr = front;
      while (curr != null) {
         if (name.equalsIgnoreCase(curr.name)) {
            return true;
         }
         curr = curr.next;
      }
      return false; 
   }
   
   // Post: returns true if the passed in name is a player
   //       that has been killed, false otherwise.
   public boolean graveyardContains(String name) {
      AssassinNode curr = graveyard;
      while (curr != null) {
         if (name.equalsIgnoreCase(curr.name)) {
            return true;
         }     
         curr = curr.next;
      }  
      return false;
   }
   
   // Post: returns true if the game is over. 
   public boolean gameOver() {
      return front.next == null;
   }
   
   // Post: returns the name of the winner. 
   public String winner() {
      if (front.next == null) {
         return front.name;
      } else {
         return null;
      }
   }
   
   // Pre: game must still be going and the name should not be in the list of alive players
   //      (throw IllegalArgumentException if not)
   // Post: Removes killed player from alive list and moves them to the graveyard. 
   //       Sets new target for the person that was stalking killed player.  
   public void kill(String name) {
      if (!killRingContains(name) || gameOver()) {
         throw new IllegalArgumentException();
      }
      AssassinNode curr = front; 
      AssassinNode temp = null;
      if (name.equalsIgnoreCase(curr.name)) {
         temp = curr;    
         front = front.next;
         curr = moveForw(curr);
      } else {
         while (curr.next != null && temp == null) {
            if (name.equalsIgnoreCase(curr.next.name)) {
               temp = curr.next;
               curr.next = curr.next.next;
            } else {
               curr = curr.next;
            }
         }
      }
      temp.killer = curr.name;
      temp.next = graveyard;
      graveyard = temp;   
   }
   
   // Post: moves curr to the correct position
   private AssassinNode moveForw(AssassinNode curr) {
      while (curr.next != null) {
         curr = curr.next;
      }
      return curr;
   }
}
