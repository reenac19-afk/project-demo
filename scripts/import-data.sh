#!/bin/sh
set -e

DUMP_FILE="/data/dump.sql"

if [ ! -f "$DUMP_FILE" ]; then
    echo "dump.sql not found"
    exit 1
fi

echo "Extracting supplied patient and action data..."

awk '
/^COPY public\.patient / {copy=1}
copy {print}
/^\\\.$/ && copy {exit}
' "$DUMP_FILE" > /tmp/patient.sql

awk '
/^COPY public\.action / {copy=1}
copy {print}
/^\\\.$/ && copy {exit}
' "$DUMP_FILE" > /tmp/action.sql
echo "Waiting for Flyway schema..."

until psql \
    --host=postgres \
    --username=postgres \
    --dbname=patient_demo \
    -tAc "SELECT to_regclass('public.patient')" | grep -q patient &&
      psql \
    --host=postgres \
    --username=postgres \
    --dbname=patient_demo \
    -tAc "SELECT to_regclass('public.action')" | grep -q action
do
    sleep 2
done

echo "Flyway schema is ready."
{
    echo '\set ON_ERROR_STOP on'
    echo 'BEGIN;'
    echo 'TRUNCATE TABLE action, patient;'
    cat /tmp/patient.sql
    cat /tmp/action.sql
    echo 'COMMIT;'
} > /tmp/import.sql

psql \
    --host=postgres \
    --username=postgres \
    --dbname=patient_demo \
    --file=/tmp/import.sql

echo "Data import completed successfully."
