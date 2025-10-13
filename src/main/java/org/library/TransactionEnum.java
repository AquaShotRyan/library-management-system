package org.library;

public enum TransactionEnum {
    CHECKED_OUT_BY_ANOTHER,
    ON_HOLD_BY_ANOTHER,
    CHECKED_OUT_BY_USER,
    ON_HOLD_BY_USER,
    AT_BORROWING_LIMIT,
    AT_HOLD_LIMIT,
    CAN_BORROW,
    CAN_HOLD
}
