package cmpt276.as3.cmpt276hydrogenproject.model;

public class CoinFlip {
    private boolean isHeads;
    private Child winner;
    private Child childWhoChose;

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

    public CoinFlip(boolean flipResult) {
        this.isHeads = flipResult;
    }

    public CoinFlip() {
        flipCoin();
    }

    public boolean getResult() {
        return isHeads;
    }

    public void setResult(boolean heads) {
        isHeads = heads;
    }

    public Child getWinner() {
        return winner;
    }

    public void setWinner(Child winner) {
        this.winner = winner;
    }

    public Child getChildWhoChose() {
        return childWhoChose;
    }

    public void setChildWhoChose(Child childWhoChose) {
        this.childWhoChose = childWhoChose;
    }
}
