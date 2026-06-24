-- Schema Definition for Account Service (acct_db)
-- PostgreSQL dialect within schema 'ba0352'

CREATE SCHEMA IF NOT EXISTS ba0352;

-- Drop existing views and tables to start clean
DROP VIEW IF EXISTS ba0352.UCS_ORGTN_QUICK_SEARCH_VIEW CASCADE;
DROP TABLE IF EXISTS ba0352.ucs_li_of_bus_prodt_assn CASCADE;
DROP TABLE IF EXISTS ba0352.ucs_address CASCADE;
DROP TABLE IF EXISTS ba0352.ucs_cntprty_acct_role_assn CASCADE;
DROP TABLE IF EXISTS ba0352.ucs_li_of_bus_cd CASCADE;
DROP TABLE IF EXISTS ba0352.ucs_cntprty_acct CASCADE;
DROP TABLE IF EXISTS ba0352.ucs_orgtn CASCADE;
DROP TABLE IF EXISTS ba0352.ucs_cntprty_acct_st CASCADE;
DROP TABLE IF EXISTS ba0352.ucs_cntprty_acct_role_st CASCADE;
DROP TABLE IF EXISTS ba0352.ucs_orgtn_role CASCADE;
DROP TABLE IF EXISTS ba0352.ucs_prodt CASCADE;

-- Create ucs_orgtn table
CREATE TABLE ba0352.ucs_orgtn (
    id_orgtn INTEGER PRIMARY KEY,
    name_orgtn VARCHAR(255) NOT NULL
);

-- Create ucs_cntprty_acct table
CREATE TABLE ba0352.ucs_cntprty_acct (
    id_cntryprty_acct BIGSERIAL PRIMARY KEY,
    name_cntprty_acct VARCHAR(255) NOT NULL,
    id_orgtn INTEGER NOT NULL,
    ucs_cntprty_acct_role_sts VARCHAR(50) NOT NULL,
    CONSTRAINT fk_acct_org FOREIGN KEY (id_orgtn) REFERENCES ba0352.ucs_orgtn(id_orgtn)
);

-- Create ucs_li_of_bus_cd table
CREATE TABLE ba0352.ucs_li_of_bus_cd (
    id_li_of_bus_cd BIGSERIAL PRIMARY KEY,
    li_of_bus VARCHAR(100) NOT NULL
);

-- Create ucs_cntprty_acct_role_assn table
CREATE TABLE ba0352.ucs_cntprty_acct_role_assn (
    id_role_assn BIGSERIAL PRIMARY KEY,
    id_cntryprty_acct BIGINT NOT NULL,
    id_li_of_bus_cd BIGINT NOT NULL,
    CONSTRAINT fk_acct FOREIGN KEY (id_cntryprty_acct) REFERENCES ba0352.ucs_cntprty_acct(id_cntryprty_acct) ON DELETE CASCADE,
    CONSTRAINT fk_li_of_bus FOREIGN KEY (id_li_of_bus_cd) REFERENCES ba0352.ucs_li_of_bus_cd(id_li_of_bus_cd) ON DELETE CASCADE
);

-- Create ucs_address table
CREATE TABLE ba0352.ucs_address (
    id_address BIGSERIAL PRIMARY KEY,
    name_city VARCHAR(100),
    cd_st VARCHAR(100),
    id_cntryprty_acct BIGINT NOT NULL,
    CONSTRAINT fk_addr_acct FOREIGN KEY (id_cntryprty_acct) REFERENCES ba0352.ucs_cntprty_acct(id_cntryprty_acct) ON DELETE CASCADE
);

-- Create ucs_cntprty_acct_st table
CREATE TABLE ba0352.ucs_cntprty_acct_st (
    id_cntprty_acct_st BIGSERIAL PRIMARY KEY,
    status_name VARCHAR(100) NOT NULL
);

-- Create ucs_cntprty_acct_role_st table
CREATE TABLE ba0352.ucs_cntprty_acct_role_st (
    id_cntprty_acct_role_st BIGSERIAL PRIMARY KEY,
    role_status_name VARCHAR(100) NOT NULL
);

-- Create ucs_orgtn_role table
CREATE TABLE ba0352.ucs_orgtn_role (
    id_orgtn_role BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL
);

-- Create ucs_prodt table
CREATE TABLE ba0352.ucs_prodt (
    id_prodt BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL
);

-- Create ucs_li_of_bus_prodt_assn table
CREATE TABLE ba0352.ucs_li_of_bus_prodt_assn (
    id_li_of_bus_prodt_assn BIGSERIAL PRIMARY KEY,
    id_li_of_bus_cd BIGINT NOT NULL,
    id_prodt BIGINT NOT NULL,
    id_orgtn_role BIGINT NOT NULL,
    CONSTRAINT fk_assn_lob FOREIGN KEY (id_li_of_bus_cd) REFERENCES ba0352.ucs_li_of_bus_cd(id_li_of_bus_cd) ON DELETE CASCADE,
    CONSTRAINT fk_assn_prodt FOREIGN KEY (id_prodt) REFERENCES ba0352.ucs_prodt(id_prodt) ON DELETE CASCADE,
    CONSTRAINT fk_assn_role FOREIGN KEY (id_orgtn_role) REFERENCES ba0352.ucs_orgtn_role(id_orgtn_role) ON DELETE CASCADE
);

-- Create view ba0352.UCS_ORGTN_QUICK_SEARCH_VIEW
CREATE VIEW ba0352.UCS_ORGTN_QUICK_SEARCH_VIEW AS
SELECT
    ROW_NUMBER () OVER () AS ROW_ID,
    R1.*
FROM (SELECT
    T1.ID_ORGTN AS ORGANIZATION_ID,
    T1.NAME_ORGTN AS INSTITUTION_NAME,
    T2.ID_CNTRYPRTY_ACCT AS ACCOUNT_IDENTIFIER,
    T2.NAME_CNTPRTY_ACCT AS ACCOUNT_NAME,
    ADDR.NAME_CITY AS CITY,
    ADDR.CD_ST AS STATE,
    CONCAT (CAST (T1.ID_ORGTN AS VARCHAR (100)),
        CONCAT (' | ',
            CONCAT (COALESCE (T1.NAME_ORGTN,
                ''),
                CONCAT (' | ',
                    CONCAT (COALESCE (CAST (T2.ID_CNTRYPRTY_ACCT AS VARCHAR(100)),
                        ''),
                        CONCAT (' | ',
                            CONCAT (COALESCE (ADDR.NAME_CITY,
                                ''),
                                CONCAT (' | ',
                                    COALESCE (ADDR.CD_ST,
                                        ''))))))))) AS SEARCH_KEY
FROM ba0352.ucs_orgtn T1
JOIN ba0352.ucs_cntprty_acct T2 ON T2.ID_ORGTN = T1.ID_ORGTN
LEFT JOIN ba0352.ucs_address ADDR ON ADDR.ID_CNTRYPRTY_ACCT = T2.ID_CNTRYPRTY_ACCT
) R1;
