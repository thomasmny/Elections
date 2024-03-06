package com.eintosti.elections.inventory;

import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.candidate.Candidate;
import com.eintosti.elections.election.ElectionImpl;
import com.eintosti.elections.inventory.listener.TopFiveListener;
import com.eintosti.elections.util.InventoryUtils;
import com.eintosti.elections.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.LinkedList;

public class TopFiveInventory {

    private final ElectionImpl election;

    public TopFiveInventory(ElectionsPlugin plugin) {
        this.election = plugin.getElection();

        Bukkit.getPluginManager().registerEvents(new TopFiveListener(), plugin);
    }

    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, 36, Messages.getString("top5_title"));
        fillGuiWithGlass(inventory);

        int winnerSlot = 11;
        LinkedList<Candidate> top5 = election.getTop5();
        for (int rank = 1; rank <= top5.size(); rank++) {
            Candidate candidate = top5.get(rank - 1);
            InventoryUtils.addSkull(inventory, winnerSlot, "§a" + candidate.getName(), candidate.getName(), new ArrayList<>());
            addPositionSkull(inventory, winnerSlot + 9, rank);
            winnerSlot++;
        }

        return inventory;
    }

    private void addPositionSkull(Inventory inventory, int position, int rank) {
        String displayName = "-", url = "";

        switch (rank) {
            case 1:
                displayName = Messages.getString("top5_1");
                url = "af3034d24a85da31d67932c33e5f1821e219d5dcd9c2ba4f2559df48deea";
                break;
            case 2:
                displayName = Messages.getString("top5_2");
                url = "dc61b04e12a879767b3b72d69627f29a83bdeb6220f5dc7bea2eb2529d5b097";
                break;
            case 3:
                displayName = Messages.getString("top5_3");
                url = "f8ebab57b7614bb22a117be43e848bcd14daecb50e8f5d0926e4864dff470";
                break;
            case 4:
                displayName = Messages.getString("top5_4");
                url = "d2e78fb22424232dc27b81fbcb47fd24c1acf76098753f2d9c28598287db5";
                break;
            case 5:
                displayName = Messages.getString("top5_5");
                url = "6d57e3bc88a65730e31a14e3f41e038a5ecf0891a6c243643b8e5476ae2";
                break;
        }

        InventoryUtils.addUrlSkull(inventory, position, displayName, url);
    }

    private void fillGuiWithGlass(Inventory inventory) {
        for (int i = 0; i <= 10; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }
        for (int i = 16; i <= 19; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }
        for (int i = 25; i <= 35; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }
    }
}