CREATE OR REPLACE FUNCTION coursePass (In_sId int, In_cId char, In_year int,
  In_grade char, In_graduationDate date)
RETURNS char AS $$
DECLARE
  s record;
  c record;
  r record;
BEGIN
  SELECT * INTO s FROM student
  WHERE (sid = In_sId);
  IF NOT FOUND THEN
    RAISE EXCEPTION 'student % not found', In_sId;
  END IF;
  SELECT * INTO c FROM course
  WHERE (cid = In_cId);
  IF NOT FOUND THEN
    RAISE EXCEPTION 'course % not found', In_cId;
  END IF;
  SELECT * INTO r FROM result
  WHERE (sid = In_sId AND cid = In_cId AND year = In_year);
  IF FOUND THEN
    RAISE EXCEPTION 'result %, %, % found', In_sId, In_cId, In_year;
  END IF;
  SELECT * INTO r FROM result
  WHERE (sid = In_sId AND cid = In_cId AND grade != 'D');
  IF FOUND THEN
    INSERT INTO result VALUES(In_sId, In_cId, In_year, In_grade);
  ELSE
    INSERT INTO result VALUES(In_sId, In_cId, In_year, In_grade);
    IF (In_grade != 'D') THEN
      UPDATE student SET pointsearned = pointsearned + c.points
      WHERE sid = In_sId;
      IF (s.pointsearned < 360 AND s.pointsearned + c.points >= 360) THEN
        INSERT INTO graduate VALUES (In_sId, In_graduationDate);
        RETURN 'INSERT 2';
      END IF;
    END IF;
  END IF;
  RETURN 'INSERT 1';
END;
$$ LANGUAGE PLpgSQL;