package me.cryptidyy.oceanraiders.customitems;

import java.util.List;

import me.cryptidyy.oceanraiders.state.GameManager;

public class TeleportTrident extends OceanItem {

	public TeleportTrident(List<String> lore, int slot)
	{
		super(OceanItemType.TELEPORT_TRIDENT, lore, slot);
	}

	@Override
	public void useItem(CustomItemUser user, GameManager manager)
	{
		
	}
}
