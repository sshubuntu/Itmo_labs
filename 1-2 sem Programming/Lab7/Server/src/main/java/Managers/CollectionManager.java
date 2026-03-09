package Managers;

import model.Route;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A class for managing a collection of Route objects.
 *
 * @author sh_ub
 */
public class CollectionManager {
    private static final Map<Long, Route> routes = new HashMap<>();
    static ArrayList<Route> collection = new ArrayList<>();
    private LocalDateTime lastInitTime;
    private final DbRoutesManager dbRoutesManager;

    /**
     * Constructor for CollectionManager.
     *
     * @param dbRoutesManager the database manager for routes
     */
    public CollectionManager(DbRoutesManager dbRoutesManager) {
        this.lastInitTime = null;
        this.dbRoutesManager = dbRoutesManager;
    }

    /**
     * Gets the last initialization time.
     *
     * @return the last initialization time
     */
    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    /**
     * Checks if the collection contains a specific route.
     *
     * @param route the route to check
     * @return true if the collection contains the route, false otherwise
     */
    public boolean isContain(Route route) {
        return routes.containsKey(route.getId());
    }

    /**
     * Adds a route to the collection.
     *
     * @param r the route to add
     * @return true if the route was added, false if it already exists
     */
    public boolean add(Route r) {
        if (isContain(r)) return false;
        routes.put(r.getId(), r);
        collection.add(r);
        sort();
        return true;
    }

    /**
     * Removes a route from the collection by its ID.
     *
     * @param id the ID of the route to remove
     * @return true if the route was removed, false if it does not exist
     */
    public boolean remove(Long id) {
        var r = routes.get(id);
        if (r == null) return false;
        routes.remove(r.getId());
        collection.remove(r);
        dbRoutesManager.deleteRoute(r.getId());
        sort();
        return true;
    }

    /**
     * Sorts the collection of routes.
     */
    public void sort(){
        Collections.sort(collection);
    }

    /**
     * Updates a route in the collection.
     *
     * @param r the route to update
     * @return true if the route was updated, false if it does not exist
     */
    public boolean update(Route r) {
        if (!isContain(r)) return false;
        collection.remove(routes.get(r.getId()));
        routes.put(r.getId(), r);
        collection.add(r);
        dbRoutesManager.updateRoute(r);
        sort();
        return true;
    }

    /**
     * Initializes the collection by loading routes from the database.
     */
    public synchronized void init()  {
        collection.clear();
        lastInitTime = LocalDateTime.now();
        collection = dbRoutesManager.loadCollection();
        collection.forEach(r -> routes.put(r.getId(), r));
        sort();
    }

    /**
     * Clears the collection for a specific owner.
     *
     * @param ownerId the ID of the owner
     */
    public void clear(String ownerId) {
        dbRoutesManager.clearTables(ownerId);
        collection = dbRoutesManager.loadCollection();
        collection.forEach(r -> routes.put(r.getId(), r));
    }

    /**
     * Gets the collection of routes.
     *
     * @return the collection of routes
     */
    public ArrayList<Route> getCollection() {
        return collection;
    }

    @Override
    public String toString() {
        return collection.isEmpty() ?  "" :  collection.stream().map(Route::toString).collect(Collectors.joining("\n"));
    }
}