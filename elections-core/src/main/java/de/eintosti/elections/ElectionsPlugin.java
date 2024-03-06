package com.eintosti.elections;

import com.eintosti.elections.api.Elections;
import com.eintosti.elections.api.ElectionsApi;
import com.eintosti.elections.command.ElectionsCommand;
import com.eintosti.elections.election.ElectionImpl;
import com.eintosti.elections.inventory.CreateInventory;
import com.eintosti.elections.inventory.NominationInventory;
import com.eintosti.elections.inventory.TimeInventory;
import com.eintosti.elections.inventory.TopFiveInventory;
import com.eintosti.elections.inventory.VoteInventory;
import com.eintosti.elections.util.Messages;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class ElectionsPlugin extends JavaPlugin {

    private ElectionImpl election;

    private CreateInventory createInventory;
    private NominationInventory runInventory;
    private TimeInventory timeInventory;
    private VoteInventory votingInventory;
    private TopFiveInventory topFiveInventory;

    @Override
    public void onLoad() {
        Messages.createMessageFile();
    }

    @Override
    public void onEnable() {
        Elections api = new ElectionsApi();
        getServer().getServicesManager().register(Elections.class, api, this, ServicePriority.Normal);
        ElectionsApi.register(api);

        this.election = new ElectionImpl(this);

        this.createInventory = new CreateInventory(this);
        this.runInventory = new NominationInventory(this);
        this.timeInventory = new TimeInventory(this);
        this.votingInventory = new VoteInventory(this);
        this.topFiveInventory = new TopFiveInventory(this);

        new ElectionsCommand(this);
    }

    @Override
    public void onDisable() {
        ElectionsApi.unregister();

        election.getPhase().finish();
    }

    public void resetElection() {
        election.getPhase().finish();
        this.election = new ElectionImpl(this);
    }

    public ElectionImpl getElection() {
        return election;
    }

    public CreateInventory getCreateInventory() {
        return createInventory;
    }

    public NominationInventory getNominationInventory() {
        return runInventory;
    }

    public TimeInventory getTimeInventory() {
        return timeInventory;
    }

    public VoteInventory getVoteInventory() {
        return votingInventory;
    }

    public TopFiveInventory getTopInventory() {
        return topFiveInventory;
    }
}