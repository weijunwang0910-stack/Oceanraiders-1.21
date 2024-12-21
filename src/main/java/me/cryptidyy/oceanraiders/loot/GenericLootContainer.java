package me.cryptidyy.oceanraiders.loot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GenericLootContainer {

    private Inventory inv;
    private Location containerLoc;
    private LootTable lootTable;

    private Container container;
    private static ItemStack[] invContents;

    //Precondition: containerLoc is a chest or barrel
    public GenericLootContainer(Location containerLoc, LootTable table, int generateTimes)
    {
        this.container = (Container) containerLoc.getBlock().getState();
        this.containerLoc = containerLoc.clone().add(containerLoc.getX() > 0 ? 0.5 : 0.5, -1.0, containerLoc.getZ() > 0 ? 0.5 : 0.5);
        this.lootTable = table;
        inv = container.getInventory();

        generateInv(generateTimes);
    }

    public void generateInv(int amount)
    {
        if(inv == null)
            Bukkit.broadcastMessage("inv is null!");

        for(int i = 0; i < amount; i++)
        {
            inv.addItem(lootTable.getRandom());
        }

        //Convert array to arraylist
        invContents = Arrays
                .stream(inv.getContents())
                .filter(item -> item != null)
                .toArray(ItemStack[]::new);

        for(int i = 0; i < invContents.length; i++)
        {
            if(invContents[i] == null)
            {
                Bukkit.broadcastMessage("Null");
            }
            else
            {
                //Bukkit.broadcastMessage(invContents[i].getType().name() + invContents[i].getAmount());
            }
        }

        shuffleItems(invContents);
    }

    public void shuffleItems(ItemStack[] items)
    {
        inv.clear();
        //Bukkit.broadcastMessage(items.length + "");
        //Loop thru every itemstack
        for(int i = 0; i <= items.length - 1; i++)
        {
            ItemStack item = invContents[i];
            if(item == null) continue;

            //int divideAmount = (int) (item.getAmount() * ((double)items.length / (double)inv.getSize()));


            int totalItemsAmount = Arrays.stream(items).map(ItemStack::getAmount).mapToInt(Integer::intValue).sum();
            double divideAmount = ((double)item.getAmount() / (double)totalItemsAmount) * inv.getSize();

            if(divideAmount > item.getAmount() || divideAmount == 0)
            {
                divideAmount = item.getAmount();
            }

            int partialAmount = (int) (item.getAmount() / divideAmount);

            ItemStack partialItem = item;
            partialItem.setAmount(partialAmount);

            //Loop thru all the partial items
            for(int index = 1; index <= divideAmount; index++)
            {
                //Pick a random slot
                int randSlot = (int)(Math.random() * (inv.getSize() - 1));

                if(inv.getItem(randSlot) == null)
                {
                    inv.setItem(randSlot, partialItem);
                }
                else if(inv.getItem(randSlot).getType().equals(item.getType()))
                {
                    inv.setItem(randSlot, partialItem);
                    inv.getItem(randSlot).setAmount(partialItem.getAmount() + inv.getItem(randSlot).getAmount());
                }
                else
                {
                    inv.addItem(partialItem);
                }

            }

        }

    }
}
