# CS-166-Project-Part-3

Commands: (Run From Code)

source postgresql/startPostgreSQL.sh
source postgresql/createPostgreDB.sh
cp data/*.csv /tmp/$USER/myDB/data/
chmod +x java/compile.sh
chmod +x java/run.sh
psql -h localhost -p $PGPORT $USER"_DB" < sql/create.sql
cd java/
./compile.sh
./run.sh

Assumptions:
1) Doctor ID will be assigned by system, and cannot be passed in.
 - Department ID must already exist as a department.
2) d
3) d
4) d
5) d
6) d
7) d
8) d
9) d
