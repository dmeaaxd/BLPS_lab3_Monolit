package org.example.blps_lab3_monolit.app.utils;



import org.example.blps_lab3_monolit.app.entity.Shop;

import java.util.Comparator;

public class ShopComparator implements Comparator<Shop> {

    @Override
    public int compare(Shop o1, Shop o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
