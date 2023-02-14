
--
-- PostgreSQL database dump
--

-- Dumped from database version 12.9
-- Dumped by pg_dump version 12.9

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;


--
-- Name: user_privilege; Type: TYPE; Schema: db_schema; Owner: wc_user
--

CREATE TYPE user_privilege AS ENUM (
    'None',
    'View',
    'Edit',
    'Admin'
    );


--
-- Name: user_role; Type: TYPE; Schema: db_schema; Owner: wc_user
--

CREATE TYPE user_role AS ENUM (
    'Admin',
    'Sales'
    );


--
-- Name: user_status; Type: TYPE; Schema: db_schema; Owner: wc_user
--

CREATE TYPE user_status AS ENUM (
    'New',
    'Active',
    'Expired',
    'Locked'
    );

CREATE TYPE company_type AS ENUM (
    'Corporate',
    'Individual'
    );


create type auth_log_type AS ENUM (
    'LoginCookie',
    'LoginPassword',
    'RootConsole'
    );


create type bucket_type as enum (
    'Test',
    'Production'
    );


create type attached_file as (
    id        bigint,
    file_name varchar(255),
    file_size bigint,
    bucket    bucket_type
    );

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: file_demo; Type: TABLE; Schema: db_schema; Owner: wc_user
--

CREATE TABLE file_demo
(
    id         bigserial,
    attachment attached_file NOT NULL
);

--
-- Name: auth_login; Type: TABLE; Schema: db_schema; Owner: wc_user
--

CREATE TABLE auth_login
(
    id             bigserial,
    closed         timestamp,
    txt_comment    character varying(255),
    ip             varchar                     NOT NULL,
    opened         timestamp NOT NULL,
    type           auth_log_type               NOT NULL,
    remember_me_id bigint,
    user_id        bigint
);


--
-- Name: company; Type: TABLE; Schema: db_schema; Owner: wc_user
--

CREATE TABLE company
(
    id                         bigserial,
    address                    character varying(255),
    auto_logout                boolean                     NOT NULL,
    auto_logout_time           bigint,
    city                       character varying(255),
    company_logo               bytea,
    created                    timestamp NOT NULL,
    enabled                    boolean                     NOT NULL,
    name                       character varying(255)      NOT NULL,
    phone                      character varying(255),
    suite_number               character varying(255),
    updated                    timestamp                   NOT NULL,
    version                    bigint                      NOT NULL,
    zip_postal                 character varying(255),
    auth_log_id                bigint                      NOT NULL,
    stateprovince_name         character varying(255),
    stateprovince_country_name character varying(255),
    type                       company_type                NOT NULL,
    parent_company             bigint
);


--
-- Name: contact; Type: TABLE; Schema: db_schema; Owner: wc_user
--

CREATE TABLE contact
(
    id                   bigserial,
    cell_number          character varying(255),
    contact_company_name character varying(255)      NOT NULL,
    attachment attached_file,
    created              timestamp NOT NULL,
    email                character varying(255)      NOT NULL,
    last_name            character varying(255)      NOT NULL,
    name                 character varying(255)      NOT NULL,
    office_number        character varying(255),
    office_number_ext    character varying(255),
    title                character varying(255)      NOT NULL,
    updated              timestamp NOT NULL,
    auth_log_id          bigint                      NOT NULL,
    companyid            bigint
);


--
-- Name: country; Type: TABLE; Schema: db_schema; Owner: wc_user
--

CREATE TABLE country
(
    name               character varying(255) NOT NULL,
    abbreviated        character varying(255) NOT NULL,
    phone_country_code character varying(255)
);


--
-- Name: email_change_request; Type: TABLE; Schema: db_schema; Owner: wc_user
--

CREATE TABLE email_change_request
(
    id          bigserial,
    created     timestamp NOT NULL,
    email_new   character varying(255)      NOT NULL,
    token       uuid                        NOT NULL,
    updated     timestamp NOT NULL,
    used        boolean                     NOT NULL,
    auth_log_id bigint                      NOT NULL
);


--
-- Name: monsolei_sequence; Type: SEQUENCE; Schema: db_schema; Owner: wc_user
--

CREATE SEQUENCE quickstep_sequence
    START WITH 1
    INCREMENT BY 100
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: password_reset_request; Type: TABLE; Schema: db_schema; Owner: wc_user
--

CREATE TABLE password_reset_request
(
    id             bigserial,
    created        timestamp NOT NULL,
    ip             varchar                     NOT NULL,
    token          uuid                        NOT NULL,
    updated        timestamp NOT NULL,
    used           boolean                     NOT NULL,
    portal_user_id bigint
);


--
-- Name: pending_user_signup; Type: TABLE; Schema: db_schema; Owner: wc_user
--

CREATE TABLE pending_user_signup
(
    id                bigserial,
    confirmation_code uuid                        NOT NULL,
    created           timestamp NOT NULL,
    email             character varying(255)      NOT NULL,
    ip                varchar                     NOT NULL,
    name              character varying(255)      NOT NULL,
    password          character varying(255)      NOT NULL,
    phone             character varying(255)      NOT NULL,
    title             character varying(255)      NOT NULL,
    updated           timestamp NOT NULL,
    used              boolean                     NOT NULL
);


--
-- Name: portal_user; Type: TABLE; Schema: db_schema; Owner: wc_user
--

CREATE TABLE portal_user
(
    id                         bigserial,
    address                    character varying(255),
    city                       character varying(255),
    created                    timestamp NOT NULL,
    email                      character varying(255)      NOT NULL UNIQUE,              -- Nemanja: Added unique constraint
    enabled                    boolean                     NOT NULL default false,
    invite_sent                boolean,
    language                   character varying(255),
    name                       character varying(255)      NOT NULL,
    password                   character varying(255)      NOT NULL,
    phone                      character varying(255),
    photo                      bytea,
    promo_code                 character varying(255),
    suite_number               character varying(255),
    time_zone                  character varying(255),
    title                      character varying(255),
    updated                    timestamp NOT NULL,
    user_role                  user_role                   NOT NULL,
    user_status                user_status                 NOT NULL,
    version                    bigint,
    zip_postal                 character varying(255),
    auth_log_id                bigint,
    companyid                  bigint,
    stateprovince_name         character varying(255),
    stateprovince_country_name character varying(255)
);


--
-- Name: remember_me; Type: TABLE; Schema: db_schema; Owner: wc_user
--

CREATE TABLE remember_me
(
    id                      bigserial,
    created                 timestamp NOT NULL,
    token                   uuid                        NOT NULL,
    updated                 timestamp NOT NULL,
    used                    boolean                     NOT NULL,
    previous_remember_me_id bigint,
    portal_user_id          bigint                      NOT NULL
);


--
-- Name: state_province; Type: TABLE; Schema: db_schema; Owner: wc_user
--

CREATE TABLE state_province
(
    name         character varying(255) NOT NULL,
    country_name character varying(255) NOT NULL,
    abbreviation character varying(255) NOT NULL
);

CREATE TYPE application_status AS ENUM ('Pending','Approved','Rejected');

CREATE TABLE application
(
    id                  bigserial,
    amount              numeric(19, 2)     not null,
    invoice             varchar(25)        not null,
    loan                bigint,
    name                varchar(255),
    phone               varchar(20)        not null,
    email               varchar(255),
    status              application_status not null,
    sent_on             timestamp,
    created             timestamp          not null,
    updated             timestamp          not null,
    company_id          bigint             not null,
    auth_log_id         bigint             NOT NULL,
    token               uuid,
    task_suite_response text,
    application_id      varchar(255),
    loan_id             varchar(255)
);

--
-- Data for Name: auth_login; Type: TABLE DATA; Schema: db_schema; Owner: wc_user
--

INSERT INTO auth_login (closed, txt_comment, ip, opened, type, remember_me_id, user_id)
VALUES (NULL, 'DbBootStrapper', '0.0.0.0', '2022-01-24 16:43:27.573376', 'RootConsole', NULL, NULL);


--
-- Data for Name: company; Type: TABLE DATA; Schema: db_schema; Owner: wc_user
--

INSERT INTO company (id, address, auto_logout, auto_logout_time, city, company_logo, created, enabled, name, phone,
                     suite_number, updated, version, zip_postal, auth_log_id, stateprovince_name,
                     stateprovince_country_name, type)
VALUES (1, 'SYSTEM', false, NULL, 'SYSTEM', NULL, '2022-01-24 16:43:27.926439', true, 'Service Experts', '', 'SYSTEM',
        '2022-01-24 16:43:27.926448', 0, 'SYSTEM', 1, 'Alberta', 'Canada', 'Individual'),
        (2, 'BestBuy', false, NULL, 'SYSTEM', NULL, '2022-01-24 16:43:27.926439', true, 'BestBuy', '', 'SYSTEM',
        '2022-01-24 16:43:27.926448', 0, 'SYSTEM', 1, 'Alberta', 'Canada', 'Corporate');

INSERT INTO company (id, address, auto_logout, auto_logout_time, city, company_logo, created, enabled, name, phone,
                     suite_number, updated, version, zip_postal, auth_log_id, stateprovince_name,
                     stateprovince_country_name, type, parent_company)
VALUES (3, 'SYSTEM', false, NULL, 'SYSTEM', NULL, '2022-01-24 16:43:27.926439', true, 'BestBuy Location 1', '', 'SYSTEM',
        '2022-01-24 16:43:27.926448', 0, 'SYSTEM', 1, 'Alberta', 'Canada', 'Individual', 2);

--
-- Data for Name: country; Type: TABLE DATA; Schema: db_schema; Owner: wc_user
--

INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Andorra', 'AD', '+376');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('United Arab Emirates', 'AE', '+971');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Afghanistan', 'AF', '+93');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Antigua & Barbuda', 'AG', '+1-268');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Anguilla', 'AI', '+1-264');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Albania', 'AL', '+355');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Armenia', 'AM', '+374');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Angola', 'AO', '+244');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Antarctica', 'AQ', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Argentina', 'AR', '+54');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('American Samoa', 'AS', '+1-684');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Austria', 'AT', '+43');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Australia', 'AU', '+61');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Aruba', 'AW', '+297');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Åland Islands', 'AX', '+358-18');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Azerbaijan', 'AZ', '+994');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Bosnia & Herzegovina', 'BA', '+387');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Barbados', 'BB', '+1-246');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Bangladesh', 'BD', '+880');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Belgium', 'BE', '+32');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Burkina Faso', 'BF', '+226');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Bulgaria', 'BG', '+359');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Bahrain', 'BH', '+973');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Burundi', 'BI', '+257');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Benin', 'BJ', '+229');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('St. Barthélemy', 'BL', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Bermuda', 'BM', '+1-441');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Brunei', 'BN', '+673');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Bolivia', 'BO', '+591');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Caribbean Netherlands', 'BQ', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Brazil', 'BR', '+55');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Bahamas', 'BS', '+1-242');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Bhutan', 'BT', '+975');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Bouvet Island', 'BV', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Botswana', 'BW', '+267');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Belarus', 'BY', '+375');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Belize', 'BZ', '+501');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Canada', 'CA', '+1');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Cocos (Keeling) Islands', 'CC', '+61');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Congo - Kinshasa', 'CD', '+243');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Central African Republic', 'CF', '+236');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Congo - Brazzaville', 'CG', '+242');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Switzerland', 'CH', '+41');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Côte d’Ivoire', 'CI', '+225');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Cook Islands', 'CK', '+682');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Chile', 'CL', '+56');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Cameroon', 'CM', '+237');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('China', 'CN', '+86');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Colombia', 'CO', '+57');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Costa Rica', 'CR', '+506');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Cuba', 'CU', '+53');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Cape Verde', 'CV', '+238');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Curaçao', 'CW', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Christmas Island', 'CX', '+61');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Cyprus', 'CY', '+357');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Czechia', 'CZ', '+420');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Germany', 'DE', '+49');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Djibouti', 'DJ', '+253');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Denmark', 'DK', '+45');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Dominica', 'DM', '+1-767');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Dominican Republic', 'DO', '+1-809and1-829');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Algeria', 'DZ', '+213');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Ecuador', 'EC', '+593');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Estonia', 'EE', '+372');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Egypt', 'EG', '+20');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Western Sahara', 'EH', '+212');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Eritrea', 'ER', '+291');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Spain', 'ES', '+34');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Ethiopia', 'ET', '+251');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Finland', 'FI', '+358');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Fiji', 'FJ', '+679');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Falkland Islands', 'FK', '+500');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Micronesia', 'FM', '+691');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Faroe Islands', 'FO', '+298');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('France', 'FR', '+33');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Gabon', 'GA', '+241');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('United Kingdom', 'GB', '+44');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Grenada', 'GD', '+1-473');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Georgia', 'GE', '+995');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('French Guiana', 'GF', '+594');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Guernsey', 'GG', '+44');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Ghana', 'GH', '+233');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Gibraltar', 'GI', '+350');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Greenland', 'GL', '+299');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Gambia', 'GM', '+220');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Guinea', 'GN', '+224');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Guadeloupe', 'GP', '+590');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Equatorial Guinea', 'GQ', '+240');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Greece', 'GR', '+30');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('South Georgia & South Sandwich Islands', 'GS', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Guatemala', 'GT', '+502');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Guam', 'GU', '+1-671');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Guinea-Bissau', 'GW', '+245');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Guyana', 'GY', '+592');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Hong Kong SAR China', 'HK', '+852');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Heard & McDonald Islands', 'HM', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Honduras', 'HN', '+504');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Croatia', 'HR', '+385');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Haiti', 'HT', '+509');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Hungary', 'HU', '+36');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Indonesia', 'ID', '+62');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Ireland', 'IE', '+353');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Israel', 'IL', '+972');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Isle of Man', 'IM', '+44');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('India', 'IN', '+91');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('British Indian Ocean Territory', 'IO', '+246');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Iraq', 'IQ', '+964');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Iran', 'IR', '+98');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Iceland', 'IS', '+354');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Italy', 'IT', '+39');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Jersey', 'JE', '+44');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Jamaica', 'JM', '+1-876');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Jordan', 'JO', '+962');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Japan', 'JP', '+81');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Kenya', 'KE', '+254');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Kyrgyzstan', 'KG', '+996');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Cambodia', 'KH', '+855');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Kiribati', 'KI', '+686');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Comoros', 'KM', '+269');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('St. Kitts & Nevis', 'KN', '+1-869');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('North Korea', 'KP', '+850');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('South Korea', 'KR', '+82');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Kuwait', 'KW', '+965');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Cayman Islands', 'KY', '+1-345');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Kazakhstan', 'KZ', '+7');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Laos', 'LA', '+856');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Lebanon', 'LB', '+961');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('St. Lucia', 'LC', '+1-758');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Liechtenstein', 'LI', '+423');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Sri Lanka', 'LK', '+94');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Liberia', 'LR', '+231');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Lesotho', 'LS', '+266');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Lithuania', 'LT', '+370');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Luxembourg', 'LU', '+352');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Latvia', 'LV', '+371');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Libya', 'LY', '+218');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Morocco', 'MA', '+212');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Monaco', 'MC', '+377');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Moldova', 'MD', '+373');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Montenegro', 'ME', '+382');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('St. Martin', 'MF', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Madagascar', 'MG', '+261');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Marshall Islands', 'MH', '+692');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('North Macedonia', 'MK', '+389');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Mali', 'ML', '+223');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Myanmar (Burma)', 'MM', '+95');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Mongolia', 'MN', '+976');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Macao SAR China', 'MO', '+853');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Northern Mariana Islands', 'MP', '+1-670');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Martinique', 'MQ', '+596');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Mauritania', 'MR', '+222');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Montserrat', 'MS', '+1-664');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Malta', 'MT', '+356');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Mauritius', 'MU', '+230');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Maldives', 'MV', '+960');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Malawi', 'MW', '+265');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Mexico', 'MX', '+52');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Malaysia', 'MY', '+60');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Mozambique', 'MZ', '+258');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Namibia', 'NA', '+264');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('New Caledonia', 'NC', '+687');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Niger', 'NE', '+227');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Norfolk Island', 'NF', '+672');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Nigeria', 'NG', '+234');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Nicaragua', 'NI', '+505');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Netherlands', 'NL', '+31');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Norway', 'NO', '+47');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Nepal', 'NP', '+977');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Nauru', 'NR', '+674');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Niue', 'NU', '+683');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('New Zealand', 'NZ', '+64');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Oman', 'OM', '+968');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Panama', 'PA', '+507');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Peru', 'PE', '+51');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('French Polynesia', 'PF', '+689');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Papua New Guinea', 'PG', '+675');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Philippines', 'PH', '+63');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Pakistan', 'PK', '+92');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Poland', 'PL', '+48');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('St. Pierre & Miquelon', 'PM', '+508');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Pitcairn Islands', 'PN', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Puerto Rico', 'PR', '+1-787and1-939');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Palestinian Territories', 'PS', '+970');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Portugal', 'PT', '+351');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Palau', 'PW', '+680');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Paraguay', 'PY', '+595');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Qatar', 'QA', '+974');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Réunion', 'RE', '+262');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Romania', 'RO', '+40');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Serbia', 'RS', '+381');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Russia', 'RU', '+7');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Rwanda', 'RW', '+250');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Saudi Arabia', 'SA', '+966');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Solomon Islands', 'SB', '+677');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Seychelles', 'SC', '+248');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Sudan', 'SD', '+249');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Sweden', 'SE', '+46');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Singapore', 'SG', '+65');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('St. Helena', 'SH', '+290');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Slovenia', 'SI', '+386');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Svalbard & Jan Mayen', 'SJ', '+47');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Slovakia', 'SK', '+421');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Sierra Leone', 'SL', '+232');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('San Marino', 'SM', '+378');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Senegal', 'SN', '+221');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Somalia', 'SO', '+252');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Suriname', 'SR', '+597');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('South Sudan', 'SS', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('São Tomé & Príncipe', 'ST', '+239');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('El Salvador', 'SV', '+503');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Sint Maarten', 'SX', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Syria', 'SY', '+963');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Eswatini', 'SZ', '+268');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Turks & Caicos Islands', 'TC', '+1-649');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Chad', 'TD', '+235');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('French Southern Territories', 'TF', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Togo', 'TG', '+228');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Thailand', 'TH', '+66');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Tajikistan', 'TJ', '+992');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Tokelau', 'TK', '+690');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Timor-Leste', 'TL', '+670');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Turkmenistan', 'TM', '+993');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Tunisia', 'TN', '+216');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Tonga', 'TO', '+676');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Turkey', 'TR', '+90');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Trinidad & Tobago', 'TT', '+1-868');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Tuvalu', 'TV', '+688');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Taiwan', 'TW', '+886');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Tanzania', 'TZ', '+255');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Ukraine', 'UA', '+380');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Uganda', 'UG', '+256');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('U.S. Outlying Islands', 'UM', NULL);
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('United States', 'US', '+1');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Uruguay', 'UY', '+598');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Uzbekistan', 'UZ', '+998');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Vatican City', 'VA', '+379');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('St. Vincent & Grenadines', 'VC', '+1-784');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Venezuela', 'VE', '+58');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('British Virgin Islands', 'VG', '+1-284');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('U.S. Virgin Islands', 'VI', '+1-340');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Vietnam', 'VN', '+84');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Vanuatu', 'VU', '+678');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Wallis & Futuna', 'WF', '+681');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Samoa', 'WS', '+685');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Yemen', 'YE', '+967');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Mayotte', 'YT', '+262');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('South Africa', 'ZA', '+27');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Zambia', 'ZM', '+260');
INSERT INTO country (name, abbreviated, phone_country_code)
VALUES ('Zimbabwe', 'ZW', '+263');


--
-- Data for Name: email_change_request; Type: TABLE DATA; Schema: db_schema; Owner: wc_user
--


--
-- Data for Name: password_reset_request; Type: TABLE DATA; Schema: db_schema; Owner: wc_user
--


--
-- Data for Name: pending_user_signup; Type: TABLE DATA; Schema: db_schema; Owner: wc_user
--


--
-- Data for Name: remember_me; Type: TABLE DATA; Schema: db_schema; Owner: wc_user
--


--
-- Data for Name: state_province; Type: TABLE DATA; Schema: db_schema; Owner: wc_user
--

INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Alberta', 'Canada', 'AB');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('British Columbia', 'Canada', 'BC');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Manitoba', 'Canada', 'MB');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('New Brunswick', 'Canada', 'NB');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Newfoundland and Labrador', 'Canada', 'NL');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Northwest Territories', 'Canada', 'NT');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Nova Scotia', 'Canada', 'NS');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Nunavut', 'Canada', 'NU');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Ontario', 'Canada', 'ON');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Prince Edward Island', 'Canada', 'PE');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Québec', 'Canada', 'QC');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Saskatchewan', 'Canada', 'SK');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Yukon Territory', 'Canada', 'YT');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Alaska', 'United States', 'AK');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Alabama', 'United States', 'AL');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Arizona', 'United States', 'AZ');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Arkansas', 'United States', 'AR');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Colorado', 'United States', 'CO');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Connecticut', 'United States', 'CT');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Delaware', 'United States', 'DE');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('District of Columbia', 'United States', 'DC');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Florida', 'United States', 'FL');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Georgia', 'United States', 'GA');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Hawaii', 'United States', 'HI');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Idaho', 'United States', 'ID');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Illinois', 'United States', 'IL');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Indiana', 'United States', 'IN');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Iowa', 'United States', 'IA');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Kansas', 'United States', 'KS');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Kentucky', 'United States', 'KY');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Louisiana', 'United States', 'LA');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Maine', 'United States', 'ME');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Maryland', 'United States', 'MD');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Massachusetts', 'United States', 'MA');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Michigan', 'United States', 'MI');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Minnesota', 'United States', 'MN');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Mississippi', 'United States', 'MS');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Missouri', 'United States', 'MO');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Montana', 'United States', 'MT');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Nebraska', 'United States', 'NE');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Nevada', 'United States', 'NV');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('New Hampshire', 'United States', 'NH');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('New Jersey', 'United States', 'NJ');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('New Mexico', 'United States', 'NM');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('New York', 'United States', 'NY');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('North Carolina', 'United States', 'NC');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('North Dakota', 'United States', 'ND');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Ohio', 'United States', 'OH');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Oklahoma', 'United States', 'OK');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Oregon', 'United States', 'OR');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Pennsylvania', 'United States', 'PA');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Puerto Rico', 'United States', 'PR');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Rhode Island', 'United States', 'RI');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('South Carolina', 'United States', 'SC');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('South Dakota', 'United States', 'SD');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Tennessee', 'United States', 'TN');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Texas', 'United States', 'TX');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Utah', 'United States', 'UT');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Vermont', 'United States', 'VT');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Virginia', 'United States', 'VA');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Washington', 'United States', 'WA');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('West Virginia', 'United States', 'WV');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Wisconsin', 'United States', 'WI');
INSERT INTO state_province (name, country_name, abbreviation)
VALUES ('Wyoming', 'United States', 'WY');




SELECT pg_catalog.setval('quickstep_sequence', 601, true);


--
-- Name: auth_login auth_login_pkey; Type: CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY auth_login
    ADD CONSTRAINT auth_login_pkey PRIMARY KEY (id);


--
-- Name: company company_pkey; Type: CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY company
    ADD CONSTRAINT company_pkey PRIMARY KEY (id);


--
-- Name: contact contact_pkey; Type: CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY contact
    ADD CONSTRAINT contact_pkey PRIMARY KEY (id);


--
-- Name: country country_pkey; Type: CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY country
    ADD CONSTRAINT country_pkey PRIMARY KEY (name);


--
-- Name: email_change_request email_change_request_pkey; Type: CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY email_change_request
    ADD CONSTRAINT email_change_request_pkey PRIMARY KEY (id);


--
-- Name: password_reset_request password_reset_request_pkey; Type: CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY password_reset_request
    ADD CONSTRAINT password_reset_request_pkey PRIMARY KEY (id);


--
-- Name: pending_user_signup pending_user_signup_pkey; Type: CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY pending_user_signup
    ADD CONSTRAINT pending_user_signup_pkey PRIMARY KEY (id);


--
-- Name: portal_user portal_user_pkey; Type: CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY portal_user
    ADD CONSTRAINT portal_user_pkey PRIMARY KEY (id);


--
-- Name: remember_me remember_me_pkey; Type: CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY remember_me
    ADD CONSTRAINT remember_me_pkey PRIMARY KEY (id);


--
-- Name: state_province state_province_pkey; Type: CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY state_province
    ADD CONSTRAINT state_province_pkey PRIMARY KEY (name, country_name);


--
-- Name: company fk_auth_log; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY company
    ADD CONSTRAINT fk_auth_log FOREIGN KEY (auth_log_id) REFERENCES auth_login(id);


ALTER TABLE ONLY company
    ADD CONSTRAINT fk_company_parent_company FOREIGN KEY (parent_company) REFERENCES company (id);


ALTER TABLE ONLY application
    ADD CONSTRAINT application_pkey PRIMARY KEY (id);


ALTER TABLE ONLY application
    ADD CONSTRAINT fk_application_company FOREIGN KEY (company_id) REFERENCES company (id);


ALTER TABLE ONLY application
    ADD CONSTRAINT fk_auth_log FOREIGN KEY (auth_log_id) REFERENCES auth_login (id);
--
-- Name: contact fk_auth_log; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY contact
    ADD CONSTRAINT fk_auth_log FOREIGN KEY (auth_log_id) REFERENCES auth_login (id);


--
-- Name: email_change_request fk_auth_log; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY email_change_request
    ADD CONSTRAINT fk_auth_log FOREIGN KEY (auth_log_id) REFERENCES auth_login (id);



--
-- Name: portal_user fk_auth_log; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY portal_user
    ADD CONSTRAINT fk_auth_log FOREIGN KEY (auth_log_id) REFERENCES auth_login (id);

--
-- Name: contact fk_company; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY contact
    ADD CONSTRAINT fk_company FOREIGN KEY (companyid) REFERENCES company (id);


--
-- Name: portal_user fk_company; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY portal_user
    ADD CONSTRAINT fk_company FOREIGN KEY (companyid) REFERENCES company (id);


--
-- Name: state_province fk_country; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY state_province
    ADD CONSTRAINT fk_country FOREIGN KEY (country_name) REFERENCES country (name);


--
-- Name: password_reset_request fk_portal_user; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY password_reset_request
    ADD CONSTRAINT fk_portal_user FOREIGN KEY (portal_user_id) REFERENCES portal_user (id);


--
-- Name: remember_me fk_portal_user; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY remember_me
    ADD CONSTRAINT fk_portal_user FOREIGN KEY (portal_user_id) REFERENCES portal_user (id);


--
-- Name: remember_me fk_previous_remember_me; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY remember_me
    ADD CONSTRAINT fk_previous_remember_me FOREIGN KEY (previous_remember_me_id) REFERENCES remember_me(id);

--
-- Name: auth_login fk_remember_me_id; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY auth_login
    ADD CONSTRAINT fk_remember_me_id FOREIGN KEY (remember_me_id) REFERENCES remember_me (id);


--
-- Name: company fk_state_province; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY company
    ADD CONSTRAINT fk_state_province FOREIGN KEY (stateprovince_name, stateprovince_country_name) REFERENCES state_province (name, country_name);


--
-- Name: portal_user fk_state_province; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY portal_user
    ADD CONSTRAINT fk_state_province FOREIGN KEY (stateprovince_name, stateprovince_country_name) REFERENCES state_province (name, country_name);



--
-- Name: auth_login fk_user_id; Type: FK CONSTRAINT; Schema: db_schema; Owner: wc_user
--

ALTER TABLE ONLY auth_login
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES portal_user (id);


-- DROP TYPE "contact_job_status";

CREATE TYPE contact_job_status AS ENUM (
    'Pending',
    'Started',
    'Succeeded',
    'Failed',
    'Ignored');


-- DROP TABLE sms_contact_job;

CREATE TABLE sms_contact_job (
    id int8 NOT NULL,
    created timestamp NOT NULL,
    from_phone_number varchar(255) NOT NULL,
    invisible_until timestamp NULL,
    message varchar(1024) NOT NULL,
    server_response text NULL,
    status contact_job_status NOT NULL,
    to_phone_number varchar(255) NOT NULL,
    updated timestamp NOT NULL,
    sid varchar(255) NULL,
    application_id bigint,
    CONSTRAINT sms_contact_job_pkey PRIMARY KEY (id),
    CONSTRAINT sms_contact_job_application FOREIGN KEY (application_id) references application(id)
);
