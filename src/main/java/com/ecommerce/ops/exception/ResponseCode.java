package com.ecommerce.ops.exception;

public enum ResponseCode {

    SUCCESS("00", "SUCCESS"),
    ORDER_NOT_FOUND("ERR_0001", "Order not found"),
    NO_USER_ID("ERR_0002", "Empty user id"),
    EMPTY_ORDER_ITEMS("ERR_0003", "Order items list is empty"),
    INVALID_ORDER_ITEM_LIST("ERR_0004", "Order items list have one / more invalid values"),
    INVALID_ORDER_ID_USER_ID_("ERR_0005", "OrderId and userId are mandatory"),
    EMPTY_ORDER_FOUND("ERR_0006", "No order details found for the given input"),
    INTERNAL_ERROR("ERR_500", "Internal server error");


    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
