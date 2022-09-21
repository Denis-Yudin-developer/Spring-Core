package com.edu.ulab.app.utils;

import java.util.UUID;

public class GeneratorUtils {
    public static Long nextID(){
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}
