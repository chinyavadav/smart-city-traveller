package com.smartcitytraveller.mobile.ui.dashboard;

public enum MapOptions {
    placeMap("Find Place", "Enter place or address"),
    directionsMap("Directions", "Enter your destination"),
    searchMap("Search Map", "Enter a place (restaurant, school etc)"),
    viewMap("View Place", "Enter name of area"),
    refresh("Refresh", "Refreshing...");

    private String display;
    private String caption;

    MapOptions(String display, String caption) {
        this.display = display;
        this.caption = caption;
    }

    public String getDisplay() {
        return display;
    }

    public String getCaption() {
        return caption;
    }

    public static MapOptions getView(String display) {
        for (MapOptions option : MapOptions.values()) {
            if (option.display.equals(display)) {
                return option;
            }
        }
        return null;
    }
}
