package com.example.taskmaster;

public enum FontSize {
    // okay look
    // i know having two fields that are the same is completely redundant
    // they were different at one point in time - the itemSizeInSp was 2-4sp less than the groupSizeInSp
    // HOWEVER
    // i set them to the same size on a whim and i liked the way it looked better, and i'm too lazy to
    // change the implementation so it's gonna stay for now. sue me

    // repeat the above comment for cardSizeInSp
    // i'll kid myself into not changing it by saying that it'll allow for customization down the line

    SMALL(12, 12, 12),
    MEDIUM(16, 16, 16),
    LARGE(20, 20, 20),
    GRANDMA(24, 24, 24);

    private int groupSizeInSp;
    private int itemSizeInSp;
    private int cardSizeInSp;

    private FontSize(int groupSizeInSp, int itemSizeInSp, int cardSizeInSp) {
        this.groupSizeInSp = groupSizeInSp;
        this.itemSizeInSp = itemSizeInSp;
        this.cardSizeInSp = cardSizeInSp;
    }

    public int getGroupSizeInSp() {
        return groupSizeInSp;
    }

    public int getItemSizeInSp() {
        return itemSizeInSp;
    }

    public int getCardSizeInSp() {
        return cardSizeInSp;
    }

    public static String[] names() {
        FontSize[] values = values();
        String[] names = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }

        return names;
    }
}
