package com.example;

public class RenderTypes
{
    enum HighlightStyle { OUTLINE,CLICKBOX,HULL}

    private static final int HAS_OUTLINE     = 1 << 0;  // 0001 = 1
    private static final int HAS_CLICKBOX    = 1 << 1;  // 0010 = 2
    private static final int HAS_HULL  = 1 << 2;  // 0100 = 4

    private int flags = 0;

    public void setStyle(HighlightStyle style, boolean flagEnabled){
        int mask = 1 << style.ordinal();

        if (flagEnabled) {
            flags |= mask;
        } else {
            flags &= ~mask;
        }
    }

    public boolean hasStyle(HighlightStyle style) {
        if (style == null) return false;
        int mask = 1 << style.ordinal();
        return (flags & mask) != 0;
    }

}
