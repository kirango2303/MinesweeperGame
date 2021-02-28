public class CellHandling {

    // default state: covered (not flip up) and not flagged
    private boolean isCovered = true;
    private boolean isFlagged = false;

    // default cell type: Empty cell
    private CellType cellType = CellType.Empty;

    public CellHandling() {

    }

    public CellHandling(String isCovered, String isFlagged) {
        if (isCovered.equals("true")) {
            this.setCoveredSetter(true);
        } else {
            this.setCoveredSetter(false);
        }

        if (isFlagged.equals("true")) {
            this.isFlagged = true;
        } else {
            this.isFlagged = false;
        }

    }
    public boolean isCovered() {
        return isCovered();
    }
    
    public boolean isFlagged() {
        return isFlagged;
    }
    public CellType cellType() {
        return get();
        
    }

    // Method to flip up the cell
    public void reveal() {
        this.setCoveredSetter(false);
    }

    // Methods to check if cell is covered or flagged
    public boolean isCoveredCell() {
        return this.isCoveredGetter();
    }

    public boolean isFlaggedCell() {
        return this.isFlagged;
    }

    // Method to know what type is the cell
    public CellType getCellType() {
        return this.get();
    }

    // Method to flag/ remove flag for a cell
    public void changeFlagStatus() {
        this.isFlagged = !isFlagged;
    }

    // Method to return the image corresponding to the state of the cell
    public String getImageMeaning() {
        return ImageName.CoveredImage.toString();
    }

    // Method to count the number of bombs (to be displayed by the flag status)
    public void countAdjacentBomb() {
    }

    public CellType get() {
        return cellType;
    }

    public void set(CellType cellType) {
        this.cellType = cellType;
    }

    public boolean isCoveredGetter() {
        return isCovered;
    }

    public void setCoveredSetter(boolean isCovered) {
        this.isCovered = isCovered;
    }
}