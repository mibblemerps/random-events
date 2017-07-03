package net.mitchfizz05.randomevents.statuseffect;

/**
 * To be implemented by potion effects that are treatable with medicine.
 */
public interface ITreatableWithMedicine
{
    /**
     * Get the success chance when treated with standard medicine.
     * Note this chance may be further affected by other factors such as medicine quality.
     */
    float getTreatChance();
}
