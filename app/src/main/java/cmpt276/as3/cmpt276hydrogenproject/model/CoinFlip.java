package cmpt276.as3.cmpt276hydrogenproject.model;

public class CoinFlip {
    private boolean isHeads;
    private Child winner;
    private Child childWhoChose;
    private Child otherChild;
    private boolean childChoseHeads;

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
    }

    public CoinFlip() {
        flipCoin();
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
}
