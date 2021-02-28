/**
 * One of the cell types, displayed using an image. 
 * The name of the image for empty cell is EmptyCell
 */

public class Empty extends CellHandling {

    // constructor invoking the constructor of CellHandling()
    public Empty() {
        super();
        this.set(CellType.Empty);
    }

    // constructor invoking the constructor of CellHandling(isCovered, isFlagged)
    public Empty(String isCovered, String isFlagged) {
        super(isCovered, isFlagged);
        this.set(CellType.Empty);
    }

    @Override
    public CellType getCellType() {
        return this.get();
    }

    @Override
    public String getImageMeaning() {
        return ImageName.EmptyImage.toString();
    }

}
