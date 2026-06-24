-- Seed Data for Account Service (acct_db)
-- PostgreSQL dialect within schema 'ba0352'

SET search_path TO ba0352;

-- Insert Organizations
INSERT INTO ucs_orgtn (id_orgtn, name_orgtn) VALUES (1001, 'Freddie Mac Institutional');
INSERT INTO ucs_orgtn (id_orgtn, name_orgtn) VALUES (1002, 'Federal Housing Corp');
INSERT INTO ucs_orgtn (id_orgtn, name_orgtn) VALUES (1003, 'Global Treasury Bank');

-- Insert Line of Businesses
INSERT INTO ucs_li_of_bus_cd (li_of_bus) VALUES ('SingleFamily');
INSERT INTO ucs_li_of_bus_cd (li_of_bus) VALUES ('MultiFamily');
INSERT INTO ucs_li_of_bus_cd (li_of_bus) VALUES ('CapitalMarkets');

-- Insert Accounts
INSERT INTO ucs_cntprty_acct (name_cntprty_acct, id_orgtn, ucs_cntprty_acct_role_sts) VALUES ('Acme Mortgage Corp', 1001, 'Active');
INSERT INTO ucs_cntprty_acct (name_cntprty_acct, id_orgtn, ucs_cntprty_acct_role_sts) VALUES ('Apex Funding LLC', 1001, 'Pending');
INSERT INTO ucs_cntprty_acct (name_cntprty_acct, id_orgtn, ucs_cntprty_acct_role_sts) VALUES ('Summit Lending Group', 1002, 'Active');
INSERT INTO ucs_cntprty_acct (name_cntprty_acct, id_orgtn, ucs_cntprty_acct_role_sts) VALUES ('Beacon Securities', 1003, 'Inactive');

-- Insert Role Associations
INSERT INTO ucs_cntprty_acct_role_assn (id_cntryprty_acct, id_li_of_bus_cd) VALUES (1, 1); -- Acme -> SingleFamily
INSERT INTO ucs_cntprty_acct_role_assn (id_cntryprty_acct, id_li_of_bus_cd) VALUES (1, 2); -- Acme -> MultiFamily
INSERT INTO ucs_cntprty_acct_role_assn (id_cntryprty_acct, id_li_of_bus_cd) VALUES (2, 1); -- Apex -> SingleFamily
INSERT INTO ucs_cntprty_acct_role_assn (id_cntryprty_acct, id_li_of_bus_cd) VALUES (3, 2); -- Summit -> MultiFamily
INSERT INTO ucs_cntprty_acct_role_assn (id_cntryprty_acct, id_li_of_bus_cd) VALUES (4, 3); -- Beacon -> CapitalMarkets

-- Insert Addresses
INSERT INTO ucs_address (name_city, cd_st, id_cntryprty_acct) VALUES ('McLean', 'VA', 1);
INSERT INTO ucs_address (name_city, cd_st, id_cntryprty_acct) VALUES ('Dallas', 'TX', 2);
INSERT INTO ucs_address (name_city, cd_st, id_cntryprty_acct) VALUES ('Charlotte', 'NC', 3);
INSERT INTO ucs_address (name_city, cd_st, id_cntryprty_acct) VALUES ('New York', 'NY', 4);

-- Insert Account Statuses
INSERT INTO ucs_cntprty_acct_st (status_name) VALUES ('Active');
INSERT INTO ucs_cntprty_acct_st (status_name) VALUES ('Pending');
INSERT INTO ucs_cntprty_acct_st (status_name) VALUES ('Inactive');

-- Insert Account Role Statuses
INSERT INTO ucs_cntprty_acct_role_st (role_status_name) VALUES ('Active');
INSERT INTO ucs_cntprty_acct_role_st (role_status_name) VALUES ('Pending');
INSERT INTO ucs_cntprty_acct_role_st (role_status_name) VALUES ('Suspended');

-- Insert Organization Roles
INSERT INTO ucs_orgtn_role (role_name) VALUES ('Seller/Servicer');
INSERT INTO ucs_orgtn_role (role_name) VALUES ('Warehouse Lender');
INSERT INTO ucs_orgtn_role (role_name) VALUES ('Custodian');

-- Insert Products
INSERT INTO ucs_prodt (product_name) VALUES ('Mortgage-Backed Security');
INSERT INTO ucs_prodt (product_name) VALUES ('Whole Loan');
INSERT INTO ucs_prodt (product_name) VALUES ('Multi-Family Bond');

-- Insert Line of Business Product Association (LOB -> Product -> Org Role)
INSERT INTO ucs_li_of_bus_prodt_assn (id_li_of_bus_cd, id_prodt, id_orgtn_role) VALUES (1, 1, 1);
INSERT INTO ucs_li_of_bus_prodt_assn (id_li_of_bus_cd, id_prodt, id_orgtn_role) VALUES (1, 1, 2);
INSERT INTO ucs_li_of_bus_prodt_assn (id_li_of_bus_cd, id_prodt, id_orgtn_role) VALUES (1, 2, 2);
INSERT INTO ucs_li_of_bus_prodt_assn (id_li_of_bus_cd, id_prodt, id_orgtn_role) VALUES (1, 2, 3);
INSERT INTO ucs_li_of_bus_prodt_assn (id_li_of_bus_cd, id_prodt, id_orgtn_role) VALUES (2, 3, 1);
INSERT INTO ucs_li_of_bus_prodt_assn (id_li_of_bus_cd, id_prodt, id_orgtn_role) VALUES (2, 3, 3);
