package com.example.praktikumprogmob;

public class Constant {
    public static final String URL="https://api.pemirahimanikaunud.web.id/";
    public static final String HOME=URL+"api";
    public static final String LOGIN=HOME+"/login";
    public static final String LOGOUT=HOME+"/logout";
    public static final String REGISTER=HOME+"/register";
    public static final String SAVE_USER_INFO=HOME+"/save_user_info";
    public static final String PENGOBATANS = HOME+"/reminder";
    public static final String ADD_PENGOBATANS = PENGOBATANS+"/create";
    public static final String USER_EDIT_PASS=HOME+"/user_edit_pass";
    public static final String EDIT_USER_INFO=HOME+"/edit_user_info";
    public static final String USER_INFO=HOME+"/user_info";
    public static final String UPDATE_PENGOBATANS = PENGOBATANS+"/update";
    public static final String DELETE_PENGOBATANS = PENGOBATANS+"/delete";
}
