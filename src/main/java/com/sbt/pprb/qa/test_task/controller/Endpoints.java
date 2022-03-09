package com.sbt.pprb.qa.test_task.controller;

public final class Endpoints {

    public static final String APP_INFO = "/api/app-info";
    public static final String APP_INFO_DATABASE = "/database";
    public static final String APP_INFO_REST = "/rest";
    public static final String BEVERAGES = "/api/beverages";
    public static final String BEVERAGES_VOLUMES = "/volumes";
    public static final String BEVERAGES_ID = "/{id}";
    public static final String ORDERS = "/api/orders";
    public static final String ORDERS_ID = "/{id}";
    public static final String ORDERS_ID_BEVERAGES = "/{id}/beverages";
    public static final String ORDERS_BEVERAGES_ID = "/beverages/{id}";
    public static final String ORDERS_ID_SUBMIT = "/{id}/submit";
    public static final String ORDERS_ID_ADD_BALANCE = "/{id}/add-balance";
    public static final String ORDERS_ID_RESET_BALANCE = "/{id}/reset-balance";
    public static final String ORDERS_BEVERAGES_ID_SELECT_ICE = "/beverages/{id}/select-ice";

    private Endpoints() {

    }
}
