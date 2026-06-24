-- DDL for tracking BPEL Process Instances in Oracle Database
CREATE TABLE bpel_process_instances (
    process_id VARCHAR2(100) NOT NULL,
    process_name VARCHAR2(100) NOT NULL,
    status VARCHAR2(50) NOT NULL,
    payload CLOB,
    response CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_bpel_process_id PRIMARY KEY (process_id)
);
