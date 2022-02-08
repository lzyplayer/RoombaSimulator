package main;

import world.Move;
import world.Roomba;

public class MyRoomba extends Roomba {
    public MyRoomba(int x, int y, int radius) {
        super(x, y, radius);
//        start_x = x;
//        start_y = y;
        sweepStatus = -1;
        turnCache = 0;
        forwardCache = 0;
        cornerGoDirFlag = 0;
        RandomCountSteps = 0;
        farInfraredPoint = 70;
        RandomCountthreshold = 1700;
        botjob = Job.OnCorner;


    }

    private boolean turning = false;
    private Direction newDir = Direction.NORTH;
//    private int start_x, start_y;
    private Move formerMovment;
    private Job botjob;
    private int cornerGoDirFlag;
    private int sweepStatus,formerSweepStatus;
    private int RandomCountSteps;
    private int RandomCountthreshold;
    private int farInfraredPoint;
    private int shiftDistance;
    private int turnCache;
    private int forwardCache;

    public Move getToCorner() {
        //	current north west corner
        Move currMov = Move.FORWARD;
        if (this.getDirection()==Direction.WEST && this.wallSensor && this.frontBumper){
            botjob = Job.OnSweep;
            sweepStatus = 0;
            formerSweepStatus = sweepStatus;
        }
        if (this.getDirection() == Direction.NORTH && frontBumper && formerMovment == Move.FORWARD) {
            cornerGoDirFlag = 0;
        }else if(this.getDirection() == Direction.WEST && frontBumper && formerMovment == Move.FORWARD){
            cornerGoDirFlag = 1;
        }
        if (cornerGoDirFlag ==0){
            currMov = turnToWest();
        }else {
            currMov = turnToNorth();
        }
        return currMov;
    }

    public Move horzionSweep() {
        Move currMov = Move.FORWARD;
        if (this.getDirection() == Direction.SOUTH && this.frontBumper && formerSweepStatus == 2) {
            //Sweep completed
            botjob = Job.OnRightSlide;
        }
        if (sweepStatus == 0 && (this.getDirection()!=Direction.EAST || !this.frontBumper))
            currMov = turnToEast();
        else if (sweepStatus == 0 && this.getDirection()==Direction.EAST && this.frontBumper) {
        	formerSweepStatus =sweepStatus;
            sweepStatus = 1;
            shiftDistance = 0;
        }

        if (sweepStatus == 1  && shiftDistance < 2 * this.getRadius()) {
            currMov = turnToSouth();
            if (currMov == Move.FORWARD)
                shiftDistance++;
        }else if(sweepStatus == 1 && (shiftDistance >= 2 * this.getRadius() )) {
        	if (formerSweepStatus==2)
				sweepStatus = 0;
        	else
        		sweepStatus = 2;
		}

		if (sweepStatus == 2 && (this.getDirection()!=Direction.WEST || !this.frontBumper))
			currMov = turnToWest();
		else if (sweepStatus == 2 && this.getDirection()==Direction.WEST && this.frontBumper) {
			formerSweepStatus =sweepStatus;
			sweepStatus = 1;
			shiftDistance = 0;
		}

        return currMov;
    }

    public Move rightSlideWalk(){
        //cache movement
        if (turnCache>0){
            turnCache--;
            forwardCache++;
            return Move.TURNCLOCKWISE;
        }else if(turnCache<0){
            turnCache++;
            forwardCache++;
            return Move.TURNCOUNTERCLOCKWISE;
        }
        if (forwardCache>0){
            forwardCache--;
            return Move.FORWARD;
        }
        Move currMov = Move.FORWARD;
        if (wallSensor){
            if (!frontBumper){
                currMov = Move.FORWARD;
            }else{
                currMov = Move.TURNCOUNTERCLOCKWISE;
                turnCache--;
            }
        }else {
            currMov = Move.TURNCLOCKWISE;
            turnCache++;
        }
        RandomCountSteps++;
        if (RandomCountSteps > RandomCountthreshold){
            botjob = Job.OnRandomWalk;
            RandomCountSteps = 0;
        }
        return currMov;
    }
    public Move randomWalk(){
        if (this.infraredSensor > farInfraredPoint){
            botjob = Job.OnSweep;
        }

        if(this.turning && this.getDirection() == newDir) {
			this.turning = false;
		}
		if(this.frontBumper && !this.turning) {
			turning = true;
			newDir = Direction.values()[(int)(Math.random()*Direction.values().length)];
			return Move.TURNCLOCKWISE;
		}

		if(this.turning) { return Move.TURNCLOCKWISE; }
        RandomCountSteps = RandomCountSteps + 2;
        if (RandomCountSteps > RandomCountthreshold){
            botjob = Job.OnSweep;
            RandomCountSteps = 0;
        }
		return Move.FORWARD;
    }

    @Override
    public Move makeMove() {
		System.out.println(this.getDirection());
		Move currMov = Move.FORWARD;
		switch (botjob){
            case OnCorner -> currMov = getToCorner();
            case OnSweep -> currMov = horzionSweep();
            case OnRightSlide -> currMov = rightSlideWalk();
            case OnRandomWalk -> currMov = randomWalk();
        }

        formerMovment = currMov;
        return currMov;
        /*TODO: Make this method better. Here's an example Roomba that always turns a random direction*/

//		System.out.println(this.getDirection());

    }

    public Move turnToWest() {
        if (this.getDirection() == Direction.NORTHWEST ||
                this.getDirection() == Direction.NORTH ||
                this.getDirection() == Direction.NORTHEAST ||
                this.getDirection() == Direction.EAST
        )
            return Move.TURNCOUNTERCLOCKWISE;
        if (this.getDirection() == Direction.SOUTHWEST ||
                this.getDirection() == Direction.SOUTH ||
                this.getDirection() == Direction.SOUTHEAST
        )
            return Move.TURNCLOCKWISE;
        return Move.FORWARD;

    }

    public Move turnToNorth() {
        if (this.getDirection() == Direction.SOUTHEAST ||
                this.getDirection() == Direction.SOUTH ||
                this.getDirection() == Direction.NORTHEAST ||
                this.getDirection() == Direction.EAST
        )
            return Move.TURNCOUNTERCLOCKWISE;
        if (this.getDirection() == Direction.WEST ||
                this.getDirection() == Direction.NORTHWEST ||
                this.getDirection() == Direction.SOUTHWEST
        )
            return Move.TURNCLOCKWISE;
        return Move.FORWARD;

    }

    public Move turnToEast() {
        if (this.getDirection() == Direction.SOUTHWEST ||
                this.getDirection() == Direction.SOUTHEAST ||
                this.getDirection() == Direction.SOUTH ||
                this.getDirection() == Direction.WEST
        ){
			return Move.TURNCOUNTERCLOCKWISE;
		}
        if (this.getDirection() == Direction.NORTHWEST ||
                this.getDirection() == Direction.NORTHEAST ||
                this.getDirection() == Direction.NORTH
        ){
            return Move.TURNCLOCKWISE;
		}
		turning =false;
		return Move.FORWARD;

    }

    public Move turnToSouth() {
        if (this.getDirection() == Direction.WEST ||
                this.getDirection() == Direction.SOUTHWEST ||
                this.getDirection() == Direction.NORTHWEST ||
                this.getDirection() == Direction.NORTH
        )
            return Move.TURNCOUNTERCLOCKWISE;
        if (this.getDirection() == Direction.EAST ||
                this.getDirection() == Direction.SOUTHEAST ||
                this.getDirection() == Direction.NORTHEAST
        )
            return Move.TURNCLOCKWISE;
        return Move.FORWARD;

    }
}
