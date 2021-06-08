# CS-166-Project-Part-3

Commands: (Run From Code)

source postgresql/startPostgreSQL.sh
source postgresql/createPostgreDB.sh
cp data/*.csv /tmp/$USER/myDB/data/
chmod +x java/compile.sh
chmod +x java/run.sh
cd java/
./compile.sh
