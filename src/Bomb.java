

/**
 * One of the cell types, displayed using an image. 
 * The name of the image for bomb cell is BombImage
 */

public class Bomb extends CellHandling {

    // constructor with no-argument in CellHandling()
    public Bomb() {
        super();
        this.set(CellType.Bomb);
    }

    // constructor with parameter in CellHandling(isCovered, isFlagged)
    public Bomb(String isCovered, String isFlagged) {
        super(isCovered, isFlagged);
        this.set(CellType.Bomb);
    }

    @Override
    public CellType getCellType() {
        return this.get();
    }

    @Override
    public String getImageMeaning() {
        return ImageName.BombImage.toString();
    }
}
