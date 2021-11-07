package cmpt276.as3.cmpt276hydrogenproject.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CoinFlip {
    private boolean isHeads;
    private boolean won;
    private Child choosingChild;
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

    public CoinFlip(Child childWhoChose, boolean childChoseHeads) {
        this.choosingChild = childWhoChose;
        this.childChoseHeads = childChoseHeads;
        flipCoin();
        if (childChoseHeads == isHeads) {
            won = true;
        } else {
            won = false;
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

    public boolean getWinStatus() {
        return won;
    }

    public Child getChoosingChild() {
        return choosingChild;
    }

    @Override
    public String toString() {
        String cfString = "";
        if (choosingChild != null) {
            cfString += choosingChild.getName() + " chose";

            if (childChoseHeads) {
                cfString += " heads\n";
            } else {
                cfString += " tails\n";
            }

            cfString += "Result: ";

            if (isHeads) {
                cfString += "Heads / ";
            } else {
                cfString += "Tails / ";
            }

            if (won) {
                cfString += "Win\n";
            } else {
                cfString += "Lose\n";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cfString += "Flipped on " + flipTime.format(formatter);
        } else {
            cfString += "Result: ";

            if (isHeads) {
                cfString += "heads";
            } else {
                cfString += "tails";
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cfString += " flipped on " + flipTime.format(formatter);
        }
        return cfString;
    }
}