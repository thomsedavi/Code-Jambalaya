CREATE OR REPLACE FUNCTION insert_update_student()
RETURNS trigger AS $$
DECLARE
  m record;
BEGIN
  SELECT * INTO m FROM major
  WHERE (mcode = NEW.mcode);
  IF m.mcode IS NULL THEN
    RAISE NOTICE 'major code % not found', NEW.mcode;
    RETURN NULL;
  ELSE
    RETURN NEW;
  END IF;
END;
$$ LANGUAGE PLpgSQL;

CREATE TRIGGER insert_update_student
BEFORE INSERT OR UPDATE ON student
FOR EACH ROW EXECUTE PROCEDURE insert_update_student();

CREATE OR REPLACE FUNCTION delete_major()
RETURNS trigger AS $$
DECLARE
  s record;
BEGIN
  SELECT * INTO s FROM student
  WHERE (mcode = OLD.mcode);
  IF s.mcode IS NULL THEN
    RETURN OLD;
  ELSE
    RAISE NOTICE 'student is still enrolled in major %', OLD.mcode;
    RETURN NULL;
  END IF;
END;
$$ LANGUAGE PLpgSQL;

CREATE TRIGGER delete_major
BEFORE DELETE ON major
FOR EACH ROW EXECUTE PROCEDURE delete_major();

CREATE OR REPLACE FUNCTION update_major()
RETURNS trigger AS $$
DECLARE
  s record;
BEGIN
  SELECT * INTO s FROM student
  WHERE (mcode = OLD.mcode);
  IF s.mcode IS NULL THEN
    RETURN NEW;
  ELSE
    RAISE NOTICE 'student is still enrolled in major %', OLD.mcode;
    RETURN NULL;
  END IF;
END;
$$ LANGUAGE PLpgSQL;

CREATE TRIGGER update_major
BEFORE UPDATE ON major
FOR EACH ROW EXECUTE PROCEDURE update_major();
