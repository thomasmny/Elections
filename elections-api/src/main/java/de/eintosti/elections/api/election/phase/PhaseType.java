package de.eintosti.elections.api.election.phase;

import de.eintosti.elections.api.election.Election;

/**
 * The phases of an election.
 */
public enum ElectionPhase {

    /**
     * Represents the setup {@link Phase} of an {@link Election}.
     *
     * <p>
     * The {@code SETUP} phase is the initial phase of an election. During this phase,
     * the necessary preparations are made before the election can proceed to the
     * next phase.
     * </p>
     */
    SETUP,

    /**
     * Represents the nomination {@link Phase} of an {@link Election}.
     *
     * <p>
     * During the {@code NOMINATION} phase, eligible candidates are nominated for the election.
     * Nominations can be made by the candidates themselves.
     * </p>
     */
    NOMINATION,

    /**
     * Represents the voting {@link Phase} of an {@link Election}.
     *
     * <p>
     * During the {@code VOTING} phase, eligible voters can cast their votes for the
     * candidates in the election. The votes are recorded and stored for later counting.
     * </p>
     */
    VOTING,

    /**
     * Represents the finished {@link Phase} of an {@link Election}.
     *
     * <p>
     * The {@code FINISHED} phase is the final phase of an election. During this phase,
     * all the votes are counted and the final results are generated. No further actions
     * can be taken in this phase.
     * </p>
     */
    FINISHED
}