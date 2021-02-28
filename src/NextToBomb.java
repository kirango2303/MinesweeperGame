

/**
 * One of the cell types, displayed using an image. 
 * The name of the image for bomb cell is BombImage.
 */

public class NextToBomb extends CellHandling {

    // Field: count the number on the cell (the number of surrounding bombs
    private int numberBomb;

    // constructor with no-argument in CellHandling()
    public NextToBomb() {
        super();
        this.set(CellType.NextToBomb);

    }

    // constructor with parameter in CellHandling(isCovered, isFlagged)
    public NextToBomb(String isCovered, String isFlagged, int numberBomb) {
        super(isCovered, isFlagged);
        this.set(CellType.NextToBomb);
        this.numberBomb = numberBomb;
    }

    @Override
    public CellType getCellType() {
        return this.get();
    }

    @Override
    public String getImageMeaning() {
        return Integer.toString(numberBomb);
    }

    @Override
    public void countAdjacentBomb() {
        this.numberBomb++;
    }

}
