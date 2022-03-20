package com.sbt.pprb.qa.test_task.controller;

public final class EndpointPaths {

    public static final String INDEX = "/";
    public static final String REGISTER = "/registration";
    public static final String APP_INFO = "/api/v1/app-info";
    public static final String APP_INFO_DATABASE = "/database";
    public static final String APP_INFO_REST = "/rest";
    public static final String BEVERAGES = "/api/v1/beverages";
    public static final String BEVERAGES_VOLUMES = "/volumes";
    public static final String BEVERAGES_ID = "/{id}";
    public static final String ORDERS = "/api/v1/orders";
    public static final String ORDERS_ID = "/{id}";
    public static final String ORDERS_ID_BEVERAGES = "/{id}/beverages";
    public static final String ORDERS_BEVERAGES_ID = "/beverages/{id}";
    public static final String ORDERS_ID_BALANCE = "/{id}/balance";
    public static final String ORDERS_BEVERAGES_ID_ICE = "/beverages/{id}/ice";

    private EndpointPaths() {

    }
}
