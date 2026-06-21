package com.example.pharmacy.util;

import org.springframework.stereotype.Component;

@Component("storageZone")
public class StorageZoneFormatting {

    public String label(String code) {
        return StorageZoneLabels.label(code);
    }
}
