/*
 Alligators print out in the world as '#'. Alligators will choose to fight and will not run or walk away
    when they encounter a critter.

 During each time step, alligators will remain in place but will lose Params.rest_energy_cost of energy. Alligators will
    also reproduce, creating a new baby alligator during each time step.
 */

package assignment4;

public class Critter1 extends Critter {

    public void walk (int direction) {
        super.walk(direction);
    }

    public String toString() {
        return "1";
    }

    public boolean fight (String not_used) {
        return true;
    }

    public void doTimeStep() {
        Critter1 a = new Critter1();
        reproduce(a, 5); //diagonally left and down
    }

}
