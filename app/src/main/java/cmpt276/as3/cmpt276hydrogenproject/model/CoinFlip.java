package cmpt276.as3.cmpt276hydrogenproject.model;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CoinFlip {
    private boolean isHeads;
    private boolean won;
    private Child choosingChild;
    private boolean childChoseHeads;
    private final LocalDateTime flipTime;
    private final double COIN_FLIP_THRESHOLD = 0.5;

    private void flipCoin() {
        //generates random number between 0 and 1.
        double numericalResult = (Math.random());
        //0 <= x <= 0.5 is heads. x > 0.5 is tails.
        this.isHeads = numericalResult <= COIN_FLIP_THRESHOLD;
    }

    public CoinFlip(Child childWhoChose, boolean childChoseHeads) {
        this.choosingChild = childWhoChose;
        this.childChoseHeads = childChoseHeads;
        flipCoin();
        won = childChoseHeads == isHeads;
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

    @NonNull
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            cfString += "Flipped on " + flipTime.format(formatter);
        } else {
            cfString += "No child selected\n";
            cfString += "Result: ";

            if (isHeads) {
                cfString += "Heads\n";
            } else {
                cfString += "Tails\n";
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            cfString += "Flipped on " + flipTime.format(formatter);
        }
        return cfString;
    }
}