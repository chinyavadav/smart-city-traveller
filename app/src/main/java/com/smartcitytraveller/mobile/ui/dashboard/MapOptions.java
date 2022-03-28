package com.smartcitytraveller.mobile.ui.dashboard;

public enum MapOptions {
    placeMap("Find Place"),
    directionsMap("Directions"),
    searchMap("Search Map"),
    viewMap("View Place"),
    refresh("Refresh");

    private String display;

    MapOptions(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
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
