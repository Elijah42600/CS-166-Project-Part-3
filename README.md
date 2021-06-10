# CS-166-Project-Part-3

Commands: (Run From Code)

- source postgresql/startPostgreSQL.sh
- source postgresql/createPostgreDB.sh
- cp data/*.csv /tmp/$USER/myDB/data/
- chmod +x java/compile.sh
- chmod +x java/run.sh
- psql -h localhost -p $PGPORT $USER"_DB" < sql/create.sql
- cd java/
- ./compile.sh
- ./run.sh

Assumptions:
1) Doctor ID will be assigned by system, and cannot be passed in.
 - Department ID must already exist as a department.
2) Patient ID will be assigned by system, and cannot be passed in.
 - Patient must be created before assigning appointments, so num_appts starts at 0.
3) Appointment time_slot, date, and status are all assumed to be valid.
4) Appointment ID and Doctor ID must already exist.
- Appointment must already belong to doctor in has_appointment
5) Assumes date range is inclusive. 
 - Date must be in YYYY/MM/DD format.
 - Assumes Doctor exists.
6) Does not discriminate between Departments with the same name. 
 - Assumes Department Name is valid.
 - Date must be in YYYY/MM/DD format.
7) Assumes "total number of different types of appointments" means amount of different statuses belonging to doctor.
 - Does not output doctors with no appointments.
8) Patient will not have more than one appointment of the same type.
