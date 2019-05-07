package com.ljmu.andre.CBIDatabase;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public interface CBIObject {
    void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion);
}
