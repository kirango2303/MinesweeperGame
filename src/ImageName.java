

/**
 * This file holds an enumeration called ImageName, which is used to indicate
 * the image associated with each action of the user (click a cell/ flag/ remove
 * flag from a cell)
 */
public enum ImageName {
    EmptyImage, // uncovered - empty
    CoveredImage, // not yet clicked - initial state
    FlaggedImage, // flagged
    FlaggedButWrongImage, // after losing - reveal that the flag is wrong
    BombImage // after losing - reveal all the bombs
}
