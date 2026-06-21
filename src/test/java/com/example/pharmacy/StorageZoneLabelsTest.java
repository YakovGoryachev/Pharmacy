package com.example.pharmacy;

import com.example.pharmacy.util.StorageZoneLabels;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StorageZoneLabelsTest {

    @Test
    void mapsKnownCodesToRussian() {
        assertEquals("Комнатная температура", StorageZoneLabels.label("ROOM_TEMP"));
        assertEquals("Холодильник (2–8°C)", StorageZoneLabels.label("FRIDGE"));
        assertEquals("Зал", StorageZoneLabels.label("Зал"));
    }

    @Test
    void unknownCodeReturnedAsIs() {
        assertEquals("CUSTOM", StorageZoneLabels.label("CUSTOM"));
    }
}
