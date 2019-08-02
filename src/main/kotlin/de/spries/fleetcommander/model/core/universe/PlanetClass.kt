package de.spries.fleetcommander.model.core.universe

enum class PlanetClass constructor(private val productivityFactor: Float) {
    A(1.2f),
    B(1f),
    C(0.9f),
    D(0.8f),
    E(0.7f),
    F(0.6f),
    G(0.5f),
    H(0.45f),
    I(0.4f),
    J(0.25f),
    K(0.3f),
    L(0.25f),
    M(0.2f),
    N(0.15f),
    O(0.1f),
    P(0.05f);

    fun getCreditsPerFactoryPerTurn() = (CREDITS_PRODUCED_BASELINE * productivityFactor).toInt()

    fun getShipsPerFactoryPerTurn() = SHIPS_PRODUCED_BASELINE * productivityFactor

    companion object {
        private const val CREDITS_PRODUCED_BASELINE = 40
        private const val SHIPS_PRODUCED_BASELINE = 0.7f
    }
}
