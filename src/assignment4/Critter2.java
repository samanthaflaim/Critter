/*
 Snakes print out in the world as 'S'. Snakes will choose not to fight and will run down when they encounter a critter.

 During each time step, snakes walk in a random direction (and thus lose the designated amount of energy it takes to walk).
 */

package assignment4;

public class Critter2 extends Critter {

    public String toString() {
        return "2";
    }

    public boolean fight (String not_used) {
        run(6); //down
        return false;
    }

    public void doTimeStep() {
        int rand = Critter.getRandomInt(7);
        walk(rand);
    }

}
