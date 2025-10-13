package org.library;

public enum AvailabilityEnum {
    AVAILABLE("Available"),
    CHECKED_OUT("Checked Out"),
    ON_HOLD("On Hold");

    private final String displayStr;

    private AvailabilityEnum(String displayStr){
        this.displayStr = displayStr;
    }

    public final String getDisplayStr(){ return displayStr; }

    public static String getAvailableStr(){ return AVAILABLE.displayStr; }
}
