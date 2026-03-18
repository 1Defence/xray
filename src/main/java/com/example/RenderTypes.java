package com.example;

public class RenderTypes
{
    enum HighlightStyle { OUTLINE,CLICKBOX,HULL}
    private int flags = 0;

    public void setStyle(HighlightStyle style){
        flags |= 1 << style.ordinal();
    }

    public boolean render(HighlightStyle style) {
        return (flags & 1 << style.ordinal()) != 0;
    }

    public boolean noRender(){
        return flags == 0;
    }

}
