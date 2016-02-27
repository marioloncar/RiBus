package com.jamaco.ribus.dynamic_listview_adapter;

public interface Section<T> {
    String getSectionTitleForItem(T instance);
}
