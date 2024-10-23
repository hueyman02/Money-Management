// DataReadyCallback.java
package com.example.moneymanagement;

import java.util.Date;
import java.util.Map;

public interface DataReadyCallback {
    void onDataReady(Map<Date, Integer> data);


}


