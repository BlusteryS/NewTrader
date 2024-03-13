package ru.newplugin.newtrader.listeners;

import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.Material;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import ru.newplugin.newtrader.db.Base;

import java.util.LinkedList;
import java.util.List;

public class EventListener implements Listener {
	@EventHandler(priority = EventPriority.LOW)
	public void onVillagerSpawn(final CreatureSpawnEvent event) {
		if (event.getEntity() instanceof final AbstractVillager villager) {
			addCustomRecipes(villager);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onProfessionChange(final VillagerCareerChangeEvent event) {
		addCustomRecipes(event.getEntity());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onTrade(final PlayerTradeEvent event) {
		final ItemStack result = event.getTrade().getResult();

		if (result.getType() != Material.ENDER_PEARL) return;

		final Player player = event.getPlayer();
		final AbstractVillager villager = event.getVillager();
		final String uuid = villager.getUniqueId().toString();

		Base.contains(player.getName(), uuid).thenAccept(contains -> {
			if (!contains) {
				Base.update("INSERT INTO users (villager, nick) VALUES (?, ?)", uuid, player.getName());
				return;
			}

			Base.add(player.getName(), uuid, "count", result.getAmount());
		});
	}

	private void addCustomRecipes(final AbstractVillager villager) {
		final List<MerchantRecipe> merchantRecipes = new LinkedList<>(villager.getRecipes());

		final MerchantRecipe merchantRecipe = new MerchantRecipe(new ItemStack(Material.ENDER_PEARL), 3);
		merchantRecipe.setIngredients(List.of(new ItemStack(Material.GOLD_INGOT, 3)));
		merchantRecipe.setExperienceReward(true);
		merchantRecipe.setVillagerExperience(5);
		merchantRecipe.setMaxUses(3);
		merchantRecipes.add(merchantRecipe);

		villager.setRecipes(merchantRecipes);
	}
}
