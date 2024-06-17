package me.cryptidyy.oceanraiders.shop;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class EnchantFamily {

	private Enchantment ench;
	private int level = 1;
	private int maxLevel;
	
	private int price;
	
	private final int oldLevel;
	private final int oldPrice;
	
	public EnchantFamily(Enchantment ench, int maxLevel, int price)
	{
		this.ench = ench;
		this.maxLevel = maxLevel;
		this.price = price;
		
		this.oldLevel = 1;
		this.oldPrice = price;
	}
	
	public EnchantFamily(Optional<EnchantFamily> optionalFamily, int currentLevel)
	{
		this.ench = optionalFamily.get().getEnchant();
		this.maxLevel = optionalFamily.get().getMaxLevel();
		this.level = currentLevel;
		
		this.oldLevel = 1;
		this.oldPrice = price;
	}
	
	/*public List<Enchantment> getEnchantments()
	{
		List<Enchantment> enchantments = new ArrayList<>();
		
		if(item.getType().toString().contains("ARMOR"))
		{
			enchantments.clear();
			enchantments.add(Enchantment.PROTECTION_ENVIRONMENTAL);
			return enchantments;
		}
		else if(item.getType().toString().contains("SWORD"))
		{
			enchantments.clear();
			enchantments.add(Enchantment.DAMAGE_ALL);
			return enchantments;
		}
		
		return this.enchantments;
		
	}*/
	
	public Enchantment getEnchant()
	{
		return this.ench;
	}
	
	public int getCurrentLevel()
	{
		return this.level;
	}
	public int getMaxLevel()
	{
		return this.maxLevel;
	}

	public void upgrade()
	{
		if(level == -1) return;
		if(level >= maxLevel)
		{
			level = -1;
			return;
		}
			
		level++;
		price += price;
	}
	
	public void reset()
	{
		level = oldLevel;
		price = oldPrice;
	}
	
	public ItemStack toBook()
	{
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(ench, level, true);
        meta.setLore(Arrays.asList(ChatColor.BLUE + "Price: " + price + " lapides"));

		if(price == 1)
			meta.setLore(Arrays.asList(ChatColor.BLUE + "Price: 1 lapis"));

        book.setItemMeta(meta);
        return book;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

}
