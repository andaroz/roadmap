package com.roadmap.utility;

public final class CommonConstants {
    public static final String PROPERTIES_FILE = "application.properties";
    public static final String PROPERTY_KEY_EUR_TO_GBP = "eurToGbp";
    public static final String PROPERTY_KEY_GBP_TO_EUR = "gbpToEur";
    public static final String DECIMAL_FOORMAT_PATTERN = "0.00";
    public static final String CURRENCY_EUR = "EUR";
    public static final String CURRENCY_GBP = "GBP";
    public static final String BASE_PATH = "http://localhost:8888";
    public static final String ESHOP_PATH = "/eShop";
    public static final String SHOPING_CART_PATH = "/cart";
    //    Common default exception error messages
    public static final String MESSAGE_BAD_REQUEST = "Mandatory input fields are missing, please check payload.";
    public static final String MESSAGE_NOT_FOUND = "Item not found in Database.";
    public static final String MESSAGE_MISSING_ITEMS_IN_ORDER = "There is no items in your basket, please add items in your basket.";
    public static final String MESSAGE_MISSING_IDENTITY_INFORMATION = "Please add identity information in your Checkout details.";
    public static final String MESSAGE_MISSING_SHIPPING_INFORMATION = "Please add shipping information in your Checkout details.";
    public static final String MESSAGE_MISSING_PAYMENT_INFORMATION = "Please add payment information in your Checkout details.";
}
