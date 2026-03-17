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

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup(XRayConfig.GROUP)
public interface XRayConfig extends Config
{
	String GROUP = "xray";

	enum HighlightStyle { OUTLINE,CLICKBOX,HULL}

	@ConfigSection(name="Outline Style", description="Outline settings", position=1, closedByDefault=false)
	String outlineSection = "outlineStyle";

	@ConfigSection(name="Clickbox Style", description="Clickbox settings", position=2, closedByDefault=true)
	String clickboxSection = "clickboxStyle";

	@ConfigSection(name="Hull Style", description="Hull settings", position=3, closedByDefault=true)
	String hullSection = "hullStyle";

	@ConfigItem(
			position = 0,
			keyName = "transparentNpcs",
			name = "Transparent Npcs",
			description = "Hidden npcs that will render as a highlight"
	)
	default String getOutlineNpcs()
	{
		return "";
	}

	@ConfigItem(
			position = 1,
			keyName = "highlightStyle",
			name = "Highlight Style",
			description = "How the highlight of hidden npcs should be rendered"
	)
	default HighlightStyle getHighlightStyle()
	{
		return HighlightStyle.OUTLINE;
	}

	@Alpha
	@ConfigItem(
			position = 2,
			keyName = "outlineColor",
			name = "Outline color",
			description = "Color of the npc highlight outline.",
			section = outlineSection
	)
	default Color outlineColor()
	{
		return new Color(0, 255, 255, 155);
	}

	@ConfigItem(
			position = 3,
			keyName = "borderWidth",
			name = "Border width",
			description = "Width of the highlighted npc border.",
			section = outlineSection
	)
	@Range(
			min = 1,
			max = 4
	)

	default int borderWidth()
	{
		return 2;
	}

	@ConfigItem(
			position = 4,
			keyName = "outlineFeather",
			name = "Outline feather",
			description = "Specify between 0-4 how much of the model outline should be faded.",
			section = outlineSection
	)
	@Range(
			min = 0,
			max = 4
	)
	default int outlineFeather()
	{
		return 0;
	}

	@Alpha
	@ConfigItem(
			position = 5,
			keyName = "clickboxColor",
			name = "Clickbox Color",
			description = "Color of the npc highlight clickbox.",
			section = clickboxSection
	)
	default Color clickboxColor()
	{
		return new Color(0, 255, 255, 155);
	}

	@ConfigItem(
			position = 6,
			keyName = "clickboxWidth",
			name = "Clickbox width",
			description = "Width of the clickbox line",
			section = clickboxSection
	)
	@Range(
			min = 1,
			max = 10
	)

	default int clickboxWidth()
	{
		return 1;
	}

	@Alpha
	@ConfigItem(
			position = 7,
			keyName = "hullColor",
			name = "Hull Color",
			description = "Color of the npc highlight hull.",
			section = hullSection
	)
	default Color hullColor()
	{
		return new Color(0, 255, 255, 155);
	}

	@ConfigItem(
			position = 8,
			keyName = "hullWidth",
			name = "Hull width",
			description = "Width of the hull line",
			section = hullSection
	)
	@Range(
			min = 1,
			max = 10
	)

	default int hullWidth()
	{
		return 1;
	}

}
