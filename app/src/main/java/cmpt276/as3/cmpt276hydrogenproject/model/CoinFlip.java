package cmpt276.as3.cmpt276hydrogenproject.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CoinFlip {
    private boolean isHeads;
    private Child winner;
    private Child childWhoChose;
    private Child otherChild;
    private boolean childChoseHeads;
    private LocalDateTime flipTime;

    private void flipCoin() {
        //generates random number between 0 and 1.
        double numericalResult = (Math.random());
        //0 <= x <= 0.5 is heads. x > 0.5 is tails.
        if(numericalResult <= 0.5) {
            this.isHeads = true;
        } else {
            this.isHeads = false;
        }
    }

    public CoinFlip(Child childWhoChose, Child otherChild, boolean childChoseHeads) {
        this.childWhoChose = childWhoChose;
        this.otherChild = otherChild;
        this.childChoseHeads = childChoseHeads;
        flipCoin();
        if (childChoseHeads == isHeads) {
            winner = childWhoChose;
        } else {
            winner = otherChild;
        }
        this.flipTime = LocalDateTime.now();
    }

    public CoinFlip() {
        flipCoin();
        this.flipTime = LocalDateTime.now();
    }

    public boolean getResult() {
        return isHeads;
    }

    public Child getWinner() {
        return winner;
    }

    public Child getChildWhoChose() {
        return childWhoChose;
    }

    @Override
    public String toString() {
        String cfString = "";
        if (childWhoChose != null) {
            cfString += childWhoChose.getName();
            if (childChoseHeads) {
                cfString += " (heads)";
            } else {
                cfString += " (tails)";
            }
            cfString += " vs. " + otherChild.getName();
            if (childChoseHeads) {
                cfString += " (tails)";
            } else {
                cfString += " (heads)";
            }
            cfString += " - " + winner.getName() + " wins!";
            cfString += "\nResult: ";
            if (isHeads) {
                cfString += "heads";
            } else {
                cfString += "tails";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cfString += ", flipped on " + flipTime.format(formatter);
        } else {
            cfString += "Result: ";
            if (isHeads) {
                cfString += "heads";
            } else {
                cfString += "tails";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cfString += ", flipped on " + flipTime.format(formatter);
        }
        return cfString;
    }
}