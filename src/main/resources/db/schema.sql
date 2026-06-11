-- PharmaControl: логическая/физическая модель БД (PostgreSQL) для приложения к ВКР
-- В runtime используется Hibernate ddl-auto=update

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pharmacies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(100),
    city VARCHAR(100),
    street VARCHAR(255),
    house_number VARCHAR(20),
    apartment VARCHAR(20),
    zip_code VARCHAR(20),
    number_license INTEGER,
    phone_number VARCHAR(50),
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    email VARCHAR(100),
    phone_number VARCHAR(50),
    role_id BIGINT REFERENCES roles(id),
    pharmacy_id BIGINT REFERENCES pharmacies(id),
    failed_login_attempts INT DEFAULT 0,
    locked_until TIMESTAMP,
    created_at TIMESTAMP,
    last_login_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS atc_manual (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20),
    level INT,
    name VARCHAR(255),
    parent_id BIGINT,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    code VARCHAR(50),
    parent_id BIGINT,
    is_system BOOLEAN,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS nomenclature (
    id BIGSERIAL PRIMARY KEY,
    product_type VARCHAR(32) NOT NULL DEFAULT 'MEDICINE',
    mnn VARCHAR(255),
    brand_name VARCHAR(255),
    form_of_release VARCHAR(100),
    dosage INT,
    dosage_unit VARCHAR(20),
    quantity_in_pack INT,
    manufacturer VARCHAR(255),
    country VARCHAR(100),
    barcode VARCHAR(50),
    price INT,
    storage_temp_min INT,
    storage_temp_max INT,
    storage_humidity INT,
    storage_light_protected BOOLEAN,
    storage_notes VARCHAR(500),
    min_stock_level INT,
    receipt BOOLEAN,
    narcotic BOOLEAN,
    psychotropic BOOLEAN,
    marked BOOLEAN DEFAULT FALSE,
    atc_manual_id BIGINT REFERENCES atc_manual(id)
);

CREATE TABLE IF NOT EXISTS nomenclature_category (
    nomenclature_id BIGINT REFERENCES nomenclature(id),
    category_id BIGINT REFERENCES categories(id),
    PRIMARY KEY (nomenclature_id, category_id)
);

CREATE TABLE IF NOT EXISTS batch (
    id BIGSERIAL PRIMARY KEY,
    batch_number VARCHAR(100),
    expiry_date DATE,
    production_date DATE,
    received_date DATE,
    supplier VARCHAR(255),
    price INT,
    qty_received INT,
    qty_in_stock INT,
    storage_zone VARCHAR(100),
    written_off BOOLEAN,
    created_at TIMESTAMP,
    nomenclature_id BIGINT REFERENCES nomenclature(id)
);

CREATE TABLE IF NOT EXISTS stock (
    id BIGSERIAL PRIMARY KEY,
    pharmacy_id BIGINT NOT NULL REFERENCES pharmacies(id),
    batch_id BIGINT NOT NULL REFERENCES batch(id),
    quantity INT DEFAULT 0,
    reserved INT DEFAULT 0,
    last_updated TIMESTAMP,
    created_at TIMESTAMP,
    UNIQUE (pharmacy_id, batch_id)
);

CREATE TABLE IF NOT EXISTS cheques (
    id BIGSERIAL PRIMARY KEY,
    number_cheque VARCHAR(50),
    fiscal_number VARCHAR(100) UNIQUE,
    total_amount INT,
    payment_method VARCHAR(20),
    is_returned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP,
    pharmacy_id BIGINT REFERENCES pharmacies(id),
    user_id BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS cheque_positions (
    id BIGSERIAL PRIMARY KEY,
    quantity INT,
    cost INT,
    sum_of_position INT,
    created_at TIMESTAMP,
    cheque_id BIGINT REFERENCES cheques(id),
    nomenclature_id BIGINT REFERENCES nomenclature(id),
    batch_id BIGINT REFERENCES batch(id)
);

CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGSERIAL PRIMARY KEY,
    form_type VARCHAR(50),
    patient_name VARCHAR(255),
    patient_birth_date DATE,
    doctor_name VARCHAR(255),
    prescription_number VARCHAR(50),
    prescription_series VARCHAR(20),
    prescription_date DATE,
    lpu_code VARCHAR(50),
    icd10_code VARCHAR(20),
    citizen_category_code VARCHAR(20),
    status VARCHAR(20),
    valid_until DATE,
    prepared_by VARCHAR(100),
    checked_by VARCHAR(100),
    dispensed_by VARCHAR(100),
    created_at TIMESTAMP,
    cheque_position_id BIGINT UNIQUE NOT NULL REFERENCES cheque_positions(id)
);

CREATE TABLE IF NOT EXISTS marking_codes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    gtin VARCHAR(20),
    serial_number VARCHAR(50),
    expiry_date DATE,
    status VARCHAR(30),
    withdrawn_at TIMESTAMP,
    disposal_document_id BIGINT,
    mdlp_status VARCHAR(30),
    created_at TIMESTAMP,
    nomenclature_id BIGINT REFERENCES nomenclature(id),
    batch_id BIGINT REFERENCES batch(id),
    cheque_position_id BIGINT REFERENCES cheque_positions(id)
);

CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(50),
    entity_name VARCHAR(100),
    entity_id BIGINT,
    old_values TEXT,
    new_values TEXT,
    timestamp TIMESTAMP,
    user_id BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS request_reports (
    id BIGSERIAL PRIMARY KEY,
    report_type VARCHAR(50),
    filters TEXT,
    file_path VARCHAR(500),
    generated_at TIMESTAMP,
    user_id BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS stock_transfers (
    id BIGSERIAL PRIMARY KEY,
    quantity INT,
    waybill_number VARCHAR(100),
    transfer_date DATE,
    created_at TIMESTAMP,
    from_pharmacy_id BIGINT REFERENCES pharmacies(id),
    to_pharmacy_id BIGINT REFERENCES pharmacies(id),
    batch_id BIGINT REFERENCES batch(id),
    user_id BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS inventory_sessions (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(20),
    comment TEXT,
    created_at TIMESTAMP,
    completed_at TIMESTAMP,
    pharmacy_id BIGINT REFERENCES pharmacies(id),
    user_id BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS inventory_lines (
    id BIGSERIAL PRIMARY KEY,
    book_quantity INT,
    actual_quantity INT,
    session_id BIGINT REFERENCES inventory_sessions(id),
    stock_id BIGINT REFERENCES stock(id)
);

CREATE TABLE IF NOT EXISTS write_off_documents (
    id BIGSERIAL PRIMARY KEY,
    quantity INT,
    reason VARCHAR(30),
    comment TEXT,
    created_at TIMESTAMP,
    pharmacy_id BIGINT REFERENCES pharmacies(id),
    batch_id BIGINT REFERENCES batch(id),
    user_id BIGINT REFERENCES users(id)
);
