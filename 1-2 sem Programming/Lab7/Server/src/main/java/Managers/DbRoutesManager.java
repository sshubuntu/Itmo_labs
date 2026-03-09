package Managers;

import model.Coordinates;
import model.Location;
import model.Route;

import java.sql.*;
import java.util.ArrayList;

import static Managers.ConnectionManager.logger;
import static Managers.DbManager.connection;

/**
 * Manages database operations for routes, including loading, adding, updating, and deleting routes,
 * as well as handling associated coordinates, locations, and ownership information.
 *
 * @author sh_ub
 */
public class DbRoutesManager {

    /**
     * Loads all routes from the database, including their coordinates and locations.
     *
     * @return an {@link ArrayList} of {@link Route} objects loaded from the database
     * @author sh_ub
     */
    public ArrayList<Route> loadCollection() {
        ArrayList<Route> routes = new ArrayList<>();
        String sql = """
            SELECT
                r.id AS route_id, r.name AS route_name, r.creation_date, r.distance,
                c.id AS coord_id, c.x AS coord_x, c.y AS coord_y,
                lf.id AS from_id, lf.x AS from_x, lf.y AS from_y, lf.z AS from_z, lf.name AS from_name,
                lt.id AS to_id, lt.x AS to_x, lt.y AS to_y, lt.z AS to_z, lt.name AS to_name
            FROM route r
            LEFT JOIN coordinates c ON r.coordinates_id = c.id
            LEFT JOIN location lf ON r.from_id = lf.id
            LEFT JOIN location lt ON r.to_id = lt.id
        """;
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Coordinates coordinates = new Coordinates(
                        rs.getDouble("coord_x"),
                        rs.getFloat("coord_y")
                );

                Location from = new Location(
                        rs.getLong("from_x"),
                        rs.getLong("from_y"),
                        rs.getDouble("from_z"),
                        rs.getString("from_name")
                );

                Location to = new Location(
                        rs.getLong("to_x"),
                        rs.getLong("to_y"),
                        rs.getDouble("to_z"),
                        rs.getString("to_name")
                );

                Route route = new Route(
                        rs.getLong("route_id"),
                        rs.getString("route_name"),
                        coordinates,
                        rs.getTimestamp("creation_date").toInstant().atZone(java.time.ZoneId.systemDefault()),
                        from,
                        to,
                        rs.getInt("distance")
                );

                routes.add(route);
            }
            logger.info("Successfully loaded {} routes from the database", routes.size());
        } catch (SQLException e){
            logger.error("Failed to load routes from the database: {}", e.getMessage());
        }
        return routes;
    }

    /**
     * Adds a new route to the database, including its coordinates, locations, and owner information.
     *
     * @param route the {@link Route} to add
     * @return the generated ID of the new route, or {@code null} if an error occurs
     * @author sh_ub
     */
    public Long addRoute(Route route) {
        try {
            Long coordId = addCoordinates(route.getCoordinates());
            Long fromId = addLocation(route.getFrom());
            Long toId = addLocation(route.getTo());
            Timestamp creationDate = Timestamp.from(route.getCreationDate().toInstant());

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO route VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id",
                    Statement.RETURN_GENERATED_KEYS);

            if (route.getId() != null)
                statement.setLong(1, route.getId());

            statement.setString(2, route.getName());
            statement.setObject(3, coordId);
            statement.setTimestamp(4, creationDate);
            statement.setObject(5, fromId);
            statement.setObject(6, toId);
            statement.setDouble(7, route.getDistance());
            statement.execute();

            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                long newId = resultSet.getLong(1);
                logger.info("Successfully added route with ID {} and name {}", newId, route.getName());
                return newId;
            } else {
                logger.error("Failed to retrieve generated ID for route with name {}", route.getName());
                return null;
            }

        } catch (SQLException e) {
            logger.error("Failed to add route with name {}: {}", route.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * Adds coordinates to the database or retrieves an existing ID if they already exist.
     *
     * @param coordinates the {@link Coordinates} to add
     * @return the ID of the coordinates, or {@code null} if an error occurs
     * @author sh_ub
     */
    public Long addCoordinates(Coordinates coordinates) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO coordinates(X, Y) values(?, ?) ON CONFLICT (x, y) DO NOTHING RETURNING id");
            statement.setDouble(1, coordinates.getX());
            statement.setDouble(2, coordinates.getY());
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                logger.debug("Successfully added coordinates ({}, {}) with ID {}", coordinates.getX(), coordinates.getY(), result.getLong("id"));
                return result.getLong("id");
            }
            logger.debug("Coordinates ({}, {}) already exist, retrieving ID", coordinates.getX(), coordinates.getY());
            return GetCoordinatesId(coordinates);

        } catch (SQLException e) {
            logger.error("Failed to add coordinates ({}, {}): {}", coordinates.getX(), coordinates.getY(), e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves the ID of existing coordinates from the database.
     *
     * @param coordinates the {@link Coordinates} to find
     * @return the ID of the coordinates, or {@code null} if not found or an error occurs
     * @author sh_ub
     */
    public Long GetCoordinatesId(Coordinates coordinates) {
        String sql = "SELECT id FROM coordinates WHERE (x = ? AND y = ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDouble(1, coordinates.getX());
            statement.setDouble(2, coordinates.getY());
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                logger.debug("Found coordinates ID {} for ({}, {})", result.getLong("id"), coordinates.getX(), coordinates.getY());
                return result.getLong("id");
            }
            logger.debug("Coordinates ({}, {}) not found in the database", coordinates.getX(), coordinates.getY());
            return null;

        } catch (SQLException e) {
            logger.error("Failed to find ID for coordinates ({}, {}): {}", coordinates.getX(), coordinates.getY(), e.getMessage());
            return null;
        }
    }

    /**
     * Adds a location to the database or retrieves an existing ID if it already exists.
     *
     * @param location the {@link Location} to add
     * @return the ID of the location, or {@code null} if an error occurs
     * @author sh_ub
     */
    public Long addLocation(Location location) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO location(x, y, z, name) values(?, ?, ?, ?) ON CONFLICT (x, y, z, name) DO NOTHING RETURNING id");
            statement.setDouble(1, location.getX());
            statement.setDouble(2, location.getY());
            statement.setDouble(3, location.getZ());
            statement.setString(4, location.getName());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                logger.debug("Successfully added location {} with ID {}", location.getName(), rs.getLong("id"));
                return rs.getLong("id");
            }
            logger.debug("Location {} already exists, retrieving ID", location.getName());
            return GetLocationId(location);

        } catch (SQLException e) {
            logger.error("Failed to add location {}: {}", location.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves the ID of an existing location from the database.
     *
     * @param location the {@link Location} to find
     * @return the ID of the location, or {@code null} if not found or an error occurs
     * @author sh_ub
     */
    public Long GetLocationId(Location location) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM location WHERE (x = ? AND y = ? AND z = ? AND name = ?)");
            statement.setDouble(1, location.getX());
            statement.setDouble(2, location.getY());
            statement.setDouble(3, location.getZ());
            statement.setString(4, location.getName());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                logger.debug("Found location ID {} for {}", rs.getLong("id"), location.getName());
                return rs.getLong("id");
            }
            logger.debug("Location {} not found in the database", location.getName());
            return null;
        } catch (SQLException e) {
            logger.error("Failed to find ID for location {}: {}", location.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * Deletes a route from the database by its ID.
     *
     * @param id the ID of the route to delete
     * @author sh_ub
     */
    public void deleteRoute(Long id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM route WHERE id = ?");
            statement.setLong(1, id);
            statement.executeUpdate();
            logger.info("Successfully deleted route with ID {}", id);
        } catch (SQLException e){
            logger.error("Failed to delete route with ID {}: {}", id, e.getMessage());
        }
    }

    /**
     * Clears all data from the route, location, and coordinates tables in the database.
     *
     * @author sh_ub
     */
    public void clearTables(String ownerLogin){
        try {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM route where owner_login = ?")) {
                statement.setString(1, ownerLogin);
                statement.executeUpdate();
                connection.commit();
                logger.info("Successfully cleared tables: route, location, coordinates");
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Failed to clear tables due to database error: {}", e.getMessage());
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        }catch (SQLException e){
            logger.error("Failed to clear tables: {}", e.getMessage());
        }
    }

    /**
     * Updates a route in the database by deleting the existing one and adding a new one.
     *
     * @param route the {@link Route} to update
     * @author sh_ub
     */
    public void updateRoute(Route route) {
        deleteRoute(route.getId());
        Long newId = addRoute(route);
        if (newId != null) {
            logger.info("Successfully updated route with ID {} to new ID {}", route.getId(), newId);
        } else {
            logger.error("Failed to update route with ID {}", route.getId());
        }
    }

    /**
     * Sets the owner of a route in the database.
     *
     * @param ownerLogin the login of the owner to set
     * @param routeId the ID of the route
     * @author sh_ub
     */
    public void setOwner(String ownerLogin, Long routeId) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE route SET owner_login = ? WHERE id = ?");
            statement.setString(1, ownerLogin);
            statement.setLong(2, routeId);
            statement.executeUpdate();
            logger.info("Successfully set owner {} for route with ID {}", ownerLogin, routeId);
        } catch (SQLException e){
            logger.error("Failed to set owner {} for route with ID {}: {}", ownerLogin, routeId, e.getMessage());
        }
    }

    /**
     * Retrieves the owner of a route from the database.
     *
     * @param routeId the ID of the route
     * @return the login of the owner, or {@code null} if not found or an error occurs
     * @author sh_ub
     */
    public String getOwner(Long routeId) {
        try {
            PreparedStatement statement = connection.prepareStatement("Select owner_login from route where id = ?");
            statement.setLong(1, routeId);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String owner = rs.getString("owner_login");
                logger.debug("Found owner {} for route with ID {}", owner, routeId);
                return owner;
            }
            logger.debug("No owner found for route with ID {}", routeId);
            return null;
        } catch (SQLException e){
            logger.error("Failed to retrieve owner for route with ID {}: {}", routeId, e.getMessage());
            return null;
        }
    }
}