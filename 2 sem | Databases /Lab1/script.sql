DROP TABLE IF EXISTS ActionScene;
DROP TABLE IF EXISTS ActionToType;
DROP TABLE IF EXISTS Actions;
DROP TABLE IF EXISTS ActionDescription;
DROP TABLE IF EXISTS Human;
DROP TABLE IF EXISTS Animal;
DROP TABLE IF EXISTS Objects;
DROP TABLE IF EXISTS Place;

BEGIN;
SET timezone = 'Europe/Moscow';

CREATE TABLE If not EXISTS Place(
      Id SERIAL PRIMARY KEY,
      PlaceName VARCHAR(100) UNIQUE not NULL
);

CREATE Table If not EXISTS Objects(
      id SERIAL PRIMARY KEY,
      ObjectType varchar(100) not null,
      Characteristic varchar(1000)
);

CREATE TABLE If not EXISTS Actions(
      Id SERIAL PRIMARY KEY,
      ActionName VARCHAR(100) not NULL,
      DirectionId INT REFERENCES Place(ID),
      Purpose TEXT
);

CREATE Table If not EXISTS ActionDescription(
      Id SERIAL PRIMARY KEY,
      Description TEXT
);

CREATE TABLE If not EXISTS ActionToType(
      ActionId INT REFERENCES Actions(id) not null,
      ActionDescribeId INT REFERENCES ActionDescription(id) not null,
      PRIMARY KEY (ActionId, ActionDescribeId)
);

CREATE TABLE If not EXISTS ActionScene(
      Id SERIAL PRIMARY KEY,
      PlaceId INT REFERENCES Place(id) not NULL,
      ObjectId INT REFERENCES Objects(id) not NULL,
      ActionId INT REFERENCES Actions(id) not NULL,
      StartTime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
      EndTime TIMESTAMPTZ NOT NULL DEFAULT NOW() + INTERVAL '2 seconds'
);

CREATE Table if not EXISTS Animal(
      Id SERIAL PRIMARY KEY,
      ObjectId int REFERENCES objects(id) not null,
      Nickname varchar(100) not null,
      Age SMALLINT CHECK (Age>=0 AND Age <=100),
      AnimalSize VARCHAR(100)
);

CREATE Table if not EXISTS Human(
      Id SERIAL PRIMARY KEY,
      ObjectId int REFERENCES objects(id),
      HumanName varchar(100),
      Age SMALLINT CHECK (Age>=0 AND Age <=110),
      Gender varchar(10)
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
     VALUES (8, 3, 1, now(), now()+ interval '10 seconds'),
            (8, 3, 2, now()+ interval '14 seconds', now()+ interval '53 seconds'),
            (8, 3, 3, now()+ interval '14 seconds', now()+ interval '23 seconds'),
            (8, 4, 4, now()+ interval '23 seconds', now()+ interval '32 seconds'),
            (8, 4, 5, now()+ interval '9 seconds', now()+ interval '43 seconds'),
            (5, 3, 6, now()+ interval '1 minute', now()+ interval '98 seconds'),
            (5, 1, 6, now()+ interval '45 seconds', now()+ interval '54 seconds'),
            (5, 3, 7, now()+ interval '76 seconds', now()+ interval '96 seconds'),
            (5, 3, 8, now()+ interval '77 seconds', now()+ interval '656 seconds'),
            (5, 4, 9, now()+ interval '5 seconds', now()+ interval '56 seconds'),
            (5, 2, 9, now()+ interval '23 seconds', now()+ interval '65 seconds');
COMMIT ;
select * from  ActionScene;
select * from ActionToType;
select * from Actions;
select * from ActionDescription;
select * from Human;
select * from Animal;
select * from Objects;
select * from Place;
SELECT ActionScene.EndTime -
    ActionScene.StartTime
FROM ActionScene;
