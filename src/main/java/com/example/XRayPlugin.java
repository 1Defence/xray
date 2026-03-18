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

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.RenderCallback;
import net.runelite.client.callback.RenderCallbackManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.RenderTypes.HighlightStyle;

@PluginDescriptor(
		name = "XRay",
		description = "Select npcs are transparent, but clickable",
		tags = {"xray", "invisible", "transparent"}
)
@Slf4j
public class XRayPlugin extends Plugin
{
	@Inject
	private XRayConfig config;

	@Inject
	private XRayOverlay XRayOverlay;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private RenderCallbackManager renderCallbackManager;


	private List<String> outlineNames = new ArrayList<>();
	private List<String> clickboxNames = new ArrayList<>();
	private List<String> hullNames = new ArrayList<>();

	public Map<NPC,RenderTypes> trackedNpcs = new HashMap<>();

	public Color outlineColor;
	public int outlineWidth;
	public int outlineFeather;

	public Color clickboxColor;
	public float clickboxWidth;
	public Color hullColor;
	public float hullWidth;

	private final Set<String> LIST_CONFIGS = Set.of("outlineNpcs","clickboxNpcs","hullNpcs");

	@Provides
	XRayConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(XRayConfig.class);
	}

	private final RenderCallback renderCallback = new RenderCallback()
	{
		@Override
		public boolean drawObject(Scene scene, TileObject object)
		{
			if (object instanceof GameObject)
			{
				Renderable renderable = ((GameObject) object).getRenderable();

				if (renderable instanceof NPC)
				{
					NPC npc = (NPC)renderable;
					if(trackedNpcs.containsKey(npc)){
						return false;
					}
				}
			}
			return true;
		}
	};

	@Override
	protected void startUp()
	{
		overlayManager.add(XRayOverlay);
		CacheConfigs();
		clientThread.invokeLater(() ->
		{
			renderCallbackManager.register(renderCallback);
		});
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(XRayOverlay);
		clientThread.invoke(() ->
		{
			renderCallbackManager.unregister(renderCallback);
		});
	}

	/**remove tracking for given npc*/
	@Subscribe
	public void onNpcSpawned(NpcSpawned e){
		trackNPC(e.getNpc());
	}

	/**attempt to track given npc*/
	@Subscribe
	public void onNpcDespawned(NpcDespawned e){
		NPC npc = e.getNpc();
		trackedNpcs.remove(npc);
	}

	/**
	 * Repopulate tracked npcs if eneded
	 */
	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(XRayConfig.GROUP))
		{
			return;
		}

		CacheConfigs();
		if(LIST_CONFIGS.contains(configChanged.getKey()))
		{
			clientThread.invokeLater(() ->
			{
				trackedNpcs.clear();
				for (NPC npc : client.getTopLevelWorldView().npcs())
				{
					if (npc == null)
						continue;
					trackNPC(npc);
				}
			});
		}

	}

	public List<String> parseNpcList(String npcList)
	{
		final String configNpcs = npcList.toLowerCase();

		if (configNpcs.isEmpty())
		{
			return Collections.emptyList();
		}

		return Text.fromCSV(configNpcs);
	}

	/**start tracking npc to later highlight in the overlay*/
	void trackNPC(NPC npc){
		if(trackedNpcs.containsKey(npc))
			return;
		RenderTypes renderTypes = new RenderTypes();

		if(matchContained(npc, outlineNames))
			renderTypes.setStyle(HighlightStyle.OUTLINE);
		if(matchContained(npc, clickboxNames))
			renderTypes.setStyle(HighlightStyle.CLICKBOX);
		if(matchContained(npc, hullNames))
			renderTypes.setStyle(HighlightStyle.HULL);

		if(renderTypes.noRender())
			return;

		trackedNpcs.put(npc,renderTypes);
	}

	/**iterate through search terms to see if npc is a valid highlight*/
	boolean matchContained(NPC npc, List<String> searchTerms){
		for (String searchTerm : searchTerms)
		{
			if(!match(npc,searchTerm))
				continue;
			return true;
		}
		return false;
	}

	/**check for exact and wildcard matches*/
	boolean match(NPC npc, String searchTerm){
		String name = npc.getName();
		if(name == null)
			return false;
		if(name.isEmpty())
			return false;
		name = name.toLowerCase();
		//search term starts with wildcard G[*iant rat]
		if(searchTerm.startsWith("*")){
			return name.endsWith(searchTerm.substring(1));
		}
		//search term ends with wild card [giant ra*]T
		if(searchTerm.endsWith("*")){
			return name.startsWith(searchTerm.substring(0,searchTerm.length()-1));
		}
		//exact match
		return name.equals(searchTerm);
	}

	/**cache config values*/
	public void CacheConfigs()
	{
		outlineNames = parseNpcList(config.outlineNpcs());
		clickboxNames = parseNpcList(config.clickboxNpcs());
		hullNames = parseNpcList(config.hullNpcs());

		outlineColor = config.outlineColor();
		outlineWidth = config.outlineWidth();
		outlineFeather = config.outlineFeather();
		clickboxColor = config.clickboxColor();
		hullColor = config.hullColor();

		clickboxWidth = config.clickboxWidth()*0.5f;//0.5-5 -- cant range a float/double
		hullWidth = config.hullWidth()*0.5f;//0.5-5 -- cant range a float/double
	}

}
