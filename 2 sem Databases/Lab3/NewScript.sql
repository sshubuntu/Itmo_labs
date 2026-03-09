DROP TABLE IF EXISTS ActionScene;
DROP TABLE IF EXISTS ActionToType;
DROP TABLE IF EXISTS Actions;
DROP TABLE IF EXISTS ActionDescription;
DROP TABLE IF EXISTS Human;
DROP TABLE IF EXISTS Animal;
DROP TABLE IF EXISTS Stats;
DROP TABLE IF EXISTS Place;
DROP TABLE IF EXISTS Objects;

BEGIN;
SET timezone = 'Europe/Moscow';

CREATE TABLE Place(
    Id SERIAL PRIMARY KEY,
    PlaceName VARCHAR(100) UNIQUE not NULL
);

CREATE Table Objects(
    Id SERIAL PRIMARY KEY,
    ObjectType varchar(100) not null,
    Characteristic varchar(1000)
);

CREATE TABLE Actions(
    Id SERIAL PRIMARY KEY,
    ActionName VARCHAR(100) not NULL,
    DirectionId INT REFERENCES Place(ID),
    Purpose TEXT
);

CREATE Table ActionDescription(
    Id SERIAL PRIMARY KEY,
    Description TEXT
);

CREATE TABLE ActionToType(
    ActionId INT REFERENCES Actions(id) not null,
    ActionDescribeId INT REFERENCES ActionDescription(id) not null,
    PRIMARY KEY (ActionId, ActionDescribeId)
);

CREATE TABLE ActionScene(
    Id SERIAL PRIMARY KEY,
    PlaceId INT REFERENCES Place(id) not NULL,
    ObjectId INT REFERENCES Objects(id) not NULL,
    ActionId INT REFERENCES Actions(id) not NULL,
    StartTime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    EndTime TIMESTAMPTZ NOT NULL DEFAULT NOW() + INTERVAL '2 seconds'
);

CREATE Table Animal(
    Id SERIAL PRIMARY KEY,
    ObjectId int REFERENCES objects(id) not null,
    Nickname varchar(100) not null,
    Age SMALLINT CHECK (Age>=0 AND Age <=100),
    AnimalSize VARCHAR(100)
);

CREATE Table Human(
    Id SERIAL PRIMARY KEY,
    ObjectId int REFERENCES objects(id),
    HumanName varchar(100),
    Age SMALLINT CHECK (Age>=0 AND Age <=110),
    Gender varchar(10)
);

CREATE TABLE Stats(
    Id SERIAL PRIMARY KEY,
    ObjectId INT REFERENCES Objects(Id),
    PlaceId INT REFERENCES Place(Id),
    Fraction TEXT,
    UNIQUE (ObjectId, PlaceId)
);

INSERT INTO Place(PlaceName)
VALUES  ('Вперед'),
        ('Назад'),
        ('Центр территории'),
        ('Наутек'),
        ('Рядом'),
        ('Лес'),
        ('Набок'),
        ('Территория парка');

INSERT INTO Objects(ObjectType, Characteristic)
VALUES  ('Человек', 'Девочка, имеет имя, пол и возраст'),
        ('Человек', 'Охотник со стажем, имеет имя, пол и возраст'),
        ('Животное', 'Раптор, имеет кличку, размер и возраст'),
        ('Животное', 'Малыш, имеет кличку, размер и возраст');

INSERT INTO Human(ObjectId, HumanName, Age, Gender)
VALUES  (1, 'Элли', 16, 'Женский'),
        (2, 'Охотник', 35, 'Мужской');

INSERT INTO Animal(ObjectId, Nickname, Age, AnimalSize)
VALUES(3, 'Раптор', 25, 'Огромный'),
      (4, 'Детеныш раптора', 5, 'Маленький');

INSERT INTO Actions(ActionName, DirectionId, Purpose)
VALUES  ('дернуться головой', null, 'любопытство'),
        ('склониться', 7, 'любопытство'),
        ('податься', 1, 'посмотреть, что там такое'),
        ('чирикнуть', null, 'испуг'),
        ('пуститься', 4, 'убежать'),
        ('оставаться', null, null),
        ('повернуться', 2, 'изменить направление'),
        ('отправиться', 3, 'изменить направление'),
        ('убежать', 6, 'Спастись от охотника'),
        ('стрелять', null, 'Получить наживу');

INSERT INTO ActionDescription(Description)
VALUES ('слегка'),
       ('с любопытством'),
       ('радостно'),
       ('Какое то время'),
       ('желая');

INSERT INTO ActionToType(ActionId, ActionDescribeId)
VALUES(1, 1),
      (1, 2),
      (2, 2),
      (3, 5),
      (4, 3),
      (6, 4);

INSERT INTO ActionScene(PlaceId, ObjectId, ActionId, StartTime, EndTime)
VALUES  (8, 3, 1, now(), now()+ interval '10 seconds'),
        (8, 3, 2, now()+ interval '14 seconds', now()+ interval '53 seconds'),
        (8, 3, 3, now()+ interval '14 seconds', now()+ interval '23 seconds'),
        (8, 4, 4, now()+ interval '23 seconds', now()+ interval '32 seconds'),
        (8, 4, 5, now()+ interval '9 seconds', now()+ interval '43 seconds'),
        (5, 3, 6, now()+ interval '0 minute', now()+ interval '5 minute'),
        (5, 1, 6, now()+ interval '45 seconds', now()+ interval '54 seconds'),
        (5, 3, 7, now()+ interval '76 seconds', now()+ interval '96 seconds'),
        (5, 3, 8, now()+ interval '77 seconds', now()+ interval '656 seconds'),
        (5, 4, 9, now()+ interval '5 seconds', now()+ interval '56 seconds');

COMMIT;

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