package org.library;

public enum MenuEnum {
    BORROW(1, "Borrow a book"),
    RETURN(2, "Return a book"),
    LOGOUT(3, "Logout"),
    INVALID_INPUT(-1, "");

    private final int optionNum;
    private final String optionDesc;

    private MenuEnum(int optionNum, String optionDesc){
        this.optionNum = optionNum;
        this.optionDesc = optionDesc;
    }

    public static MenuEnum getOption(int optionNum) {
        if (optionNum == BORROW.optionNum)
            return BORROW;

        else if (optionNum == RETURN.optionNum)
            return RETURN;

        else if (optionNum == LOGOUT.optionNum)
            return LOGOUT;

        return INVALID_INPUT;
    }

    public String getFullOptionDesc(){
        return String.format("%d) %s", optionNum, optionDesc);
    }
}
