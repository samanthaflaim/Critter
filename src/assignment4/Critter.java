package assignment4;
/* CRITTERS Critter.java
 * EE422C Project 4 submission by
 * Samantha Flaim
 * smf2728
 * 15460
 * Slip days used: <0>
 * Spring 2018
 */


import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.List;

/* see the PDF for descriptions of the methods and fields in this class
 * you may add fields, methods or inner classes to Critter ONLY if you make your additions private
 * no new public, protected or default-package code or data can be added to Critter
 */


public abstract class Critter {
	private static String myPackage;
	private	static List<Critter> population = new java.util.ArrayList<Critter>();
	private static List<Critter> babies = new java.util.ArrayList<Critter>();

	// Gets the package name.  This assumes that Critter and its subclasses are all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}
	
	private static java.util.Random rand = new java.util.Random();
	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}
	
	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}
	
	
	/* a one-character long string that visually depicts your critter in the ASCII interface */
	public String toString() {
		return "C";
	}
	
	private int energy = 0;
	protected int getEnergy() {
		return energy;
	}
	private void setEnergy (int inEnergy) {
		energy = inEnergy;
	}
	private void addEnergy (int inEnergy) {
		energy += inEnergy;
	}
	private void loseEnergy(int inEnergy) {
		energy -= inEnergy;
	}

	private int x_coord;
	private int y_coord;
	private void setX_coord (int inX) {
		x_coord = inX;
	}
	private void setY_coord (int inY) {
		y_coord = inY;
	}
	private int getX_coord () {
		return x_coord;
	}
	private int getY_coord () {
		return y_coord;
	}

	private boolean hasMoved;
	private void setHasMoved (boolean in) {
		hasMoved = in;
	}
	private boolean getHasMoved () {
		return hasMoved;
	}
	
	protected void walk(int direction) {
		if (!getHasMoved()) {
			switch (direction) {
				//right
				case 0:
					setX_coord(getX_coord() + 1);
					break;
				//diagonally right and up
				case 1:
					setX_coord(getX_coord() + 1);
					setY_coord(getY_coord() - 1);
					break;
				//up
				case 2:
					setY_coord(getY_coord() - 1);
					break;
				//diagonally left and up
				case 3:
					setX_coord(getX_coord() - 1);
					setY_coord(getY_coord() - 1);
					break;
				//left
				case 4:
					setX_coord(getX_coord() - 1);
					break;
				//diagonally left and down
				case 5:
					setX_coord(getX_coord() - 1);
					setY_coord(getY_coord() + 1);
					break;
				//down
				case 6:
					setY_coord(getY_coord() + 1);
					break;
				//diagonally right and down
				case 7:
					setX_coord(getX_coord() + 1);
					setY_coord(getY_coord() + 1);
					break;
			}
			if (getX_coord() < 0) {
				setX_coord(Params.world_width - 1);
			}
			else if (getX_coord() >= Params.world_width) {
				setX_coord(0);
			}

			if (getY_coord() < 0) {
				setY_coord(Params.world_height - 1);
			}
			else if (getY_coord() >= Params.world_height) {
				setY_coord(0);
			}
		}

		setEnergy(getEnergy() - Params.walk_energy_cost);
		setHasMoved(true);
	}
	
	protected final void run (int direction) {
		walk(direction);
		//set to false so that walk will occur again (will be set back to false after second walk)
		setHasMoved(false);
		walk(direction);
		//add back the energy walk deducted and then deduct run energy
		setEnergy(getEnergy() + (Params.walk_energy_cost * 2) - Params.run_energy_cost);
	}
	
	protected final void reproduce(Critter offspring, int direction) {
		if (this.getEnergy() >= Params.min_reproduce_energy) {
			//"parent" critter shouldn't be dead

			//set child's energy
			offspring.setEnergy(this.getEnergy() / 2); //fraction rounded down
			this.setEnergy((this.getEnergy() / 2) + (this.getEnergy() % 2)); //fraction rounded up

			//set child's position
			offspring.setX_coord(this.getX_coord());
			offspring.setY_coord(this.getY_coord());
			offspring.walk(direction);
			offspring.setHasMoved(false);

			//add baby to offspring
			babies.add(offspring);
		}
		else {
			//simply return if the parent doesn't have enough energy to reproduce
			return;
		}
	}

	public abstract void doTimeStep();
	public abstract boolean fight(String oponent);

	/**
	 * create and initialize a Critter subclass.
	 * critter_class_name must be the unqualified name of a concrete subclass of Critter, if not,
	 * an InvalidCritterException must be thrown.
	 * (Java weirdness: Exception throwing does not work properly if the parameter has lower-case instead of
	 * upper. For example, if craig is supplied instead of Craig, an error is thrown instead of
	 * an Exception.)
	 * @param critter_class_name
	 * @throws InvalidCritterException
	 */
	public static void makeCritter(String critter_class_name) throws InvalidCritterException {
		try {
			Critter addition = (Critter) Class.forName(myPackage + "." + critter_class_name).newInstance();

			//set location of addition
			addition.x_coord = Critter.getRandomInt(Params.world_width);
			addition.y_coord = Critter.getRandomInt(Params.world_height);

			//set energy of addition
			addition.energy = Params.start_energy;

			//mark addition as has not moved
			addition.hasMoved = false;

			//add addition to population
			Critter.population.add(addition);
		}
		//throw Invalid CritterException if something about inputted class name doesn't work
		catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			throw new InvalidCritterException(critter_class_name);
		}

	}
	
	/**
	 * Gets a list of critters of a specific type.
	 * @param critter_class_name What kind of Critter is to be listed.  Unqualified class name.
	 * @return List of Critters.
	 * @throws InvalidCritterException
	 */
	public static List<Critter> getInstances(String critter_class_name) {
		List<Critter> result = new java.util.ArrayList<Critter>();

		for (Critter critter : population) {
			if (critter.getClass().getSimpleName().equals(critter_class_name)) {
				result.add(critter);
			}
		}
	
		return result;
	}
	
	/**
	 * Prints out how many Critters of each type there are on the board.
	 * @param critters List of Critters.
	 */
	public static void runStats(List<Critter> critters) {
		System.out.print("" + critters.size() + " critters as follows -- ");
		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string,  1);
			} else {
				critter_count.put(crit_string, old_count.intValue() + 1);
			}
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			System.out.print(prefix + s + ":" + critter_count.get(s));
			prefix = ", ";
		}
		System.out.println();		
	}
	
	/* the TestCritter class allows some critters to "cheat". If you want to 
	 * create tests of your Critter model, you can create subclasses of this class
	 * and then use the setter functions contained here. 
	 * 
	 * NOTE: you must make sure that the setter functions work with your implementation
	 * of Critter. That means, if you're recording the positions of your critters
	 * using some sort of external grid or some other data structure in addition
	 * to the x_coord and y_coord functions, then you MUST update these setter functions
	 * so that they correctly update your grid/data structure.
	 */
	static abstract class TestCritter extends Critter {
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}
		
		protected void setX_coord(int new_x_coord) {
			super.x_coord = new_x_coord;
		}
		
		protected void setY_coord(int new_y_coord) {
			super.y_coord = new_y_coord;
		}
		
		protected int getX_coord() {
			return super.x_coord;
		}
		
		protected int getY_coord() {
			return super.y_coord;
		}
		

		/*
		 * This method getPopulation has to be modified by you if you are not using the population
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}
		
		/*
		 * This method getBabies has to be modified by you if you are not using the babies
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.  Babies should be added to the general population 
		 * at either the beginning OR the end of every timestep.
		 */
		protected static List<Critter> getBabies() {
			return babies;
		}
	}

	/**
	 * Clear the world of all critters, dead and alive
	 */
	public static void clearWorld() {
		//clear all critters - in population and in babies
		population.clear();
		babies.clear();
	}
	
	public static void worldTimeStep() {
		ArrayList<ArrayList<Critter>> encounters;
		ArrayList<Critter> toFight;
		Critter critterA;
		Critter critterB;
		boolean fightA, fightB;
		int randA;
		int randB;
		int[] locationBeforeFight = new int[2];

		//mark all critters as has not moved yet, as it is the beginning of the time step
		for (Critter critter : population) {
			critter.hasMoved = false;
		}

		//do the time step for each critter and deduct appropiate energy
		for (Critter critter : population) {
			critter.doTimeStep();
			critter.loseEnergy(Params.rest_energy_cost);
		}

		encounters = findAllEncounters();

		/*
		 * iterate through the array list of array lists, encounters
		 * if there is not encounter to take into account, size of the array list will be 1
		 * if size of array list is greater than 1, then there is a fight that needs to take place
		 * encounters' length will be number of critters in the world
		 */
		for (int enc = 0; enc < encounters.size(); enc++) {
			toFight = encounters.get(enc);
			critterA = toFight.get(0);
			if (toFight.size() > 1) {
				for (int B = 1; B < toFight.size(); B++) {
					critterB = toFight.get(B);

					//save prior location of critterA in case it can't move
					locationBeforeFight[0] = critterA.getX_coord();
					locationBeforeFight[1] = critterA.getY_coord();
					fightA = critterA.fight(critterB.toString());
					checkFightMove(critterA, locationBeforeFight);

					//save prior location of CritterB in case it can't move
					locationBeforeFight[0] = critterB.getX_coord();
					locationBeforeFight[1] = critterB.getY_coord();
					fightB = critterB.fight(critterB.toString());
					checkFightMove(critterB, locationBeforeFight);

					//if both critters are still in the same position and have the energy, they fight
					if ((critterA.getEnergy() > 0) && (critterB.getEnergy() > 0)) {
						if ((critterA.getX_coord() == critterB.getX_coord()) && (critterA.getY_coord() == critterB.getY_coord())) {
							if (fightA) {
								randA = getRandomInt(critterA.getEnergy() + 1);
							}
							else {
								randA = 0;
							}

							if (fightB) {
								randB = getRandomInt(critterB.getEnergy() + 1);
							}
							else {
								randB = 0;
							}

							if (randB > randA) { //B wins --> A dies
								critterB.addEnergy(critterA.getEnergy()/2);
								critterA.setEnergy(0);
								critterA = critterB;
							}
							else { //A wins or they tie --> B dies
								critterA.addEnergy(critterB.getEnergy()/2);
								critterB.setEnergy(0);
							}
						}
					}
				}
			}
		}

		//add newborn critters
		population.addAll(babies);

		getRidOfDeadCritters();
		refreshAlgae();
	}
	
	public static void displayWorld() {
		int printFlag = 0;

		for (int height = 0; height < (Params.world_height + 2); height++) {
			for (int width = 0; width < (Params.world_width + 2); width++) {
				if (((height == 0) && (width == 0))|| ((height == 0) && (width == (Params.world_width + 1))) ||
						((height == Params.world_height + 1) && (width == 0)) ||
						((height == (Params.world_height + 1)) && (width == (Params.world_width + 1)))) {
					System.out.print("+");
				}
				else if ((width == 0) || (width == (Params.world_width + 1))) {
					System.out.print("|");
				}
				else if ((height == 0) || (height == (Params.world_height + 1))) {
					System.out.print("-");
				}
				else {
					//find only one critter that is in this position; if there are multiple, it will just take the first one
					for (Critter critter : population) {
						if ((critter.getX_coord() == (width - 1)) && (critter.getY_coord() == (height - 1))) {
							System.out.print(critter.toString());
							printFlag = 1;
							//break in case there are other critters in the population who are in the same location after time step
							break;
						}
					}

					//no critter was found, so print a space
					if (printFlag == 0) {
						System.out.print(" ");
					}
					//reset flag for next time step
					printFlag = 0;
				}
			}
			//new line and now we go through locations at the next row
			System.out.println();
		}
	}

	/*
	 * Will create an array list of array list that is the size of the population
	 * each array list will only have 1 element if there is no one in their position or if the critter in their position is already
	 * 	added to the other critter's array list
	 * array lists of size greater than 1 include a list of critters that are all in the same position and thus must fight in time step
	 */
	public static ArrayList<ArrayList<Critter>> findAllEncounters () {
		ArrayList<ArrayList<Critter>> output = new ArrayList<ArrayList<Critter>>();
		ArrayList<Critter> tempList;
		ArrayList<Critter> alreadyFound = new ArrayList<Critter>(); //critter2's who have already been encountered

		for (int critter1 = 0; critter1 < population.size(); critter1++) {
			tempList = new ArrayList<Critter>();
			tempList.add(population.get(critter1));
			//go through all critters after critter1 in population
			for (int critter2 = (critter1 + 1); critter2 < population.size(); critter2++) {
				//if critter2 is already on critter1's 'fight list,' then no need to look at this pair
				if (!alreadyFound.contains(population.get(critter2))) {
					//double checek that critter1 and critter2 are in the same location
					if ((population.get(critter1).getX_coord() == population.get(critter2).getX_coord()) &&
							(population.get(critter1).getY_coord() == population.get(critter2).getY_coord())) {
						tempList.add(population.get(critter2));
						alreadyFound.add(population.get(critter2));
					}
				}
			}
			output.add(tempList);
		}

		return output;
	}

	/*
	 * helper function that will refreshAlgae at the end of each time step
	 * adds refresh_algae_count of algaea, all to random positions
	 */
	private static void refreshAlgae () {
		Algae newAlgae;
		int randPosition;

		for (int a = 0; a < Params.refresh_algae_count; a++) {
			newAlgae = new Algae();

			//set energy
			newAlgae.setEnergy(Params.start_energy);

			//set location
			randPosition = getRandomInt(Params.world_width);
			newAlgae.setX_coord(randPosition);
			randPosition = getRandomInt(Params.world_height);
			newAlgae.setY_coord(randPosition);

			//add to population
			population.add(newAlgae);
		}
	}

	/*
	 * if critterA and critterB moved into the same location during fight, set critterA (critter1) back to its location before
	 * the fight and leave critterB (critter2) at it's new location
	 */
	private static void checkFightMove (Critter inCritter, int[] before) {
		if ((inCritter.getX_coord() != before[0] || (inCritter.getY_coord() != before[1]))) {

			boolean moveBack = false;

			for (Critter critter : population) {
				//if there is already a critter in the locatoin that inCritter wants to move to, mark that they must move back
				if ((critter.getX_coord() == inCritter.getX_coord()) && (critter.getY_coord() == inCritter.getY_coord())
						&& (critter != inCritter)) {
					moveBack = true;
					break;
				}
			}

			//if inCritter was marked that it must move back, send it back to where it was before attempting to move
			if (moveBack) {
				inCritter.setX_coord(before[0]);
				inCritter.setY_coord(before[1]);
			}
		}
	}

	/*
	 * goes through population and gets rid of all dead critters
	 * dead critters = 0 energy
	 */
	private static void getRidOfDeadCritters () {
		List<Critter> deadCritters = new java.util.ArrayList<>();

		for (Critter critter : population) {
			//if critter has no energy save it in deadCritters
			if (critter.getEnergy() <= 0) {
				deadCritters.add(critter);
			}
		}

		//remove all critters in deadCritters from population
		for (Critter critter : deadCritters) {
			population.remove(critter);
		}
	}

}
