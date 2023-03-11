package cz.zcu.kiv.nlp.ir.storage;

import java.util.Set;

import cz.zcu.kiv.nlp.ir.Indexable;

/**
 * Storage provides an interface to load indexable documents.
 */
public interface Storage {

  Set<Indexable> getEntries();
}
