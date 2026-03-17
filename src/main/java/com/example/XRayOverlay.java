/*
 * Copyright (c) 2026, Jamal <http://github.com/1Defence>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.example;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;

import java.awt.*;

import static com.example.XRayConfig.HighlightStyle;

public class XRayOverlay extends Overlay
{
    @Inject
    private Client client;

    @Inject
    private XRayPlugin plugin;

    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private NpcUtil npcUtil;

    @Inject
    private XRayOverlay()
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics)
    {

        for(NPC npc : plugin.trackedNpcs){
            if(npc == null)
                continue;

            switch (plugin.highlightStyle){
                case OUTLINE:
                    renderOutline(npc);
                    break;
                case CLICKBOX:
                    renderShape(graphics, npc, HighlightStyle.CLICKBOX, plugin.outlineColor);
                    break;
                case HULL:
                    renderShape(graphics, npc, HighlightStyle.HULL, plugin.hullColor);
                    break;
            }

        }

        return null;
    }


    public void renderOutline(NPC npc)
    {
        NPCComposition npcComposition = npc.getTransformedComposition();
        if (npcComposition != null)
        {
            modelOutlineRenderer.drawOutline(npc,plugin.outlineWidth,plugin.outlineColor,plugin.outlineFeather);
        }
    }

    public void renderShape(Graphics2D graphics, NPC npc, HighlightStyle style, Color color)
    {
        LocalPoint lp = npc.getLocalLocation();
        if(lp == null)
            return;

        Shape shape = null;
        float strokeWidth = 1f;

        switch (style){
            case CLICKBOX:
                shape = Perspective.getClickbox(client, npc.getWorldView(), npc.getModel(), npc.getCurrentOrientation(), lp.getX(), lp.getY(),
                        Perspective.getTileHeight(client, lp, npc.getWorldLocation().getPlane()));
                strokeWidth = plugin.clickboxWidth;
                break;
            case HULL:
                shape = npc.getConvexHull();
                strokeWidth = plugin.hullWidth;
                break;
        }

        if (shape != null)
        {
            graphics.setStroke(new BasicStroke(strokeWidth));
            graphics.setColor(color);
            graphics.draw(shape);
        }
    }
}