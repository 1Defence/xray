package com.example;

public class RenderTypes
{
    enum HighlightStyle { OUTLINE,CLICKBOX,HULL}
    private int flags = 0;

    public void setStyle(HighlightStyle style){
        flags |= 1 << style.ordinal();
    }

    public boolean render(HighlightStyle style) {
        if (style == null) return false;
        int mask = 1 << style.ordinal();
        return (flags & mask) != 0;
    }

    public boolean noRender(){
        return flags == 0;
    }

}
