package de.spries.fleetcommander.model.core.universe;

public enum PlanetClass {
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

    private static final int CREDITS_PRODUCED_BASELINE = 40;
    private static final float SHIPS_PRODUCED_BASELINE = 0.7f;

    private float productivityFactor;

    PlanetClass(float productivityFactor) {this.productivityFactor = productivityFactor;}

    public int getCreditsPerFactoryPerTurn() {
        return (int) (CREDITS_PRODUCED_BASELINE * productivityFactor);
    }

    public float getShipsPerFactoryPerTurn() {
        return SHIPS_PRODUCED_BASELINE * productivityFactor;
    }
}
