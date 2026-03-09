CREATE OR REPLACE FUNCTION trigger_function_calculate_percentage_of_time()
    RETURNS TRIGGER AS
$$
DECLARE
    time_fraction NUMERIC := EXTRACT(EPOCH FROM NEW.EndTime - NEW.StartTime);
BEGIN
    IF (time_fraction <= 0) THEN
        RAISE EXCEPTION 'Действие должно длиться больше % секунд!', time_fraction;
    END IF;

    IF TG_OP = 'UPDATE' THEN    
        PERFORM calculate_percentage_of_time(roww.ObjectId, roww.PlaceId)
        FROM (SELECT * FROM Stats WHERE ObjectId = NEW.ObjectId OR PlaceId = NEW.PlaceId) roww;
    END IF;

    PERFORM calculate_percentage_of_time(NEW.ObjectId, NEW.PlaceId);

    PERFORM calculate_percentage_of_time(NEW.ObjectId, rec.PlaceId)
    FROM (SELECT DISTINCT PlaceId FROM Stats WHERE ObjectId = NEW.ObjectId) rec;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION calculate_percentage_of_time(
    Object INT,
    Place INT
) RETURNS VOID AS
$$
DECLARE
    total_time_seconds NUMERIC;
    place_time_seconds NUMERIC;
    percentage NUMERIC;
BEGIN
    SELECT EXTRACT(EPOCH FROM SUM(EndTime - StartTime))
    INTO total_time_seconds
    FROM ActionScene
    WHERE ObjectId = Object;

    SELECT EXTRACT(EPOCH FROM SUM(EndTime - StartTime))
    INTO place_time_seconds
    FROM ActionScene
    WHERE ObjectId = Object AND PlaceId = Place;

    percentage := (place_time_seconds / total_time_seconds) * 100;

    INSERT INTO Stats(ObjectId, PlaceId, Fraction)
    VALUES (
               Object,
               Place,
               substring((percentage::TEXT) FROM '\d+\.\d\d') || ' %'
           )
    ON CONFLICT (ObjectId, PlaceId)
        DO UPDATE SET Fraction = EXCLUDED.Fraction;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER calculate_percentage_of_time_trigger_on_insert
    AFTER INSERT ON ActionScene
    FOR EACH ROW
EXECUTE FUNCTION trigger_function_calculate_percentage_of_time();

CREATE OR REPLACE TRIGGER calculate_percentage_of_time_trigger_on_update
    AFTER UPDATE OF StartTime, EndTime, PlaceId, ObjectId ON ActionScene
    FOR EACH ROW
    WHEN (
        (NEW.EndTime != OLD.EndTime) OR
        (NEW.StartTime != OLD.StartTime) OR
        (NEW.PlaceId != OLD.PlaceId) OR
        (NEW.ObjectId != OLD.ObjectId)
        )
EXECUTE FUNCTION trigger_function_calculate_percentage_of_time();