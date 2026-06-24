# Database Scripts & Configurations

This directory contains the database initialization, schema definitions, and seed data scripts for all services in this application.

## Directory Structure

- `01_init_databases.sql`: Initializes the PostgreSQL databases (`auth_db` and `acct_db`).
- `02_auth_service_schema.sql`: Table structure (DDL) for User management & RBAC in `auth_db`.
- `03_auth_service_data.sql`: Core roles, admin/user records, and role associations (DML) in `auth_db`.
- `04_account_service_schema.sql`: Table structure and views (DDL) within the `ba0352` schema in `acct_db`.
- `05_account_service_data.sql`: Seed values for counterparty accounts, line-of-business, and addresses (DML) in `acct_db`.
- `06_history_service_schema.sql`: Table structure (DDL) for the event logs in IBM DB2 (`histdb`).
- `07_report_service_schema.sql`: Table structure (DDL) for the reports in Oracle DB (`FREEPDB1`).

---

## Service Database Configuration Reference

| Service | Database Name | Engine | Host / Port (Docker Compose) | Host / Port (Local Dev) | Credentials | Dialect |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `auth-service` | `auth_db` | PostgreSQL | `postgres:5432` | `localhost:5433` | `postgres` / `postgres` | `org.hibernate.dialect.PostgreSQLDialect` |
| `account-service` | `acct_db` | PostgreSQL | `postgres:5432` | `localhost:5433` | `postgres` / `postgres` | `org.hibernate.dialect.PostgreSQLDialect` |
| `history-service` | `histdb` | IBM DB2 | `db2:50000` | `localhost:50000` | `db2inst1` / `db2pwd` | `org.hibernate.dialect.DB2Dialect` |
| `report-service` | `FREEPDB1` | Oracle DB | `oracle-db:1521` | `localhost:1521` | `report_user` / `reportpwd` | `org.hibernate.dialect.OracleDialect` |
| `aggregator-service` | `aggdb` | H2 (In-memory) | N/A | N/A | `sa` / (No Password) | H2 (PostgreSQL Mode) |
| `audit-service` | `audit_db` | MongoDB (NoSQL)| `mongodb:27017` | `localhost:27017` | No auth configured | N/A |

---

## Execution Instructions

### 1. PostgreSQL (auth_db & acct_db)

If using the included Docker Compose configuration, the PostgreSQL container automatically runs `./init-scripts/init.sql` (which matches `01_init_databases.sql`) to bootstrap the databases. 

To manually load or recreate schemas and data:

```bash
# Connect to PostgreSQL container or instance and execute the files
psql -h localhost -p 5433 -U postgres -d postgres -f 01_init_databases.sql
psql -h localhost -p 5433 -U postgres -d auth_db -f 02_auth_service_schema.sql
psql -h localhost -p 5433 -U postgres -d auth_db -f 03_auth_service_data.sql
psql -h localhost -p 5433 -U postgres -d acct_db -f 04_account_service_schema.sql
psql -h localhost -p 5433 -U postgres -d acct_db -f 05_account_service_data.sql
```

### 2. IBM DB2 (histdb)

To run the DB2 schema configuration:

```bash
# Connect to the DB2 instance container
docker exec -it test_microservice_db2 bash

# Switch to the db2inst1 user and connect to histdb
su - db2inst1
db2 connect to histdb

# Run the DDL statements from 06_history_service_schema.sql
db2 -tvf 06_history_service_schema.sql
```

### 3. MongoDB (audit_db)
The audit service uses MongoDB. Because MongoDB is a schema-less NoSQL database, no SQL script is required. Documents are automatically created on first insertion inside the `audit_logs` collection.

### 4. Oracle DB (FREEPDB1)

To run the Oracle schema DDL:

```bash
# Execute DDL statements using SQL*Plus inside the Oracle container
docker exec -i test_microservice_oracle sqlplus report_user/reportpwd@localhost:1521/FREEPDB1 < 07_report_service_schema.sql
```
