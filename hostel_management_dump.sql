--
-- PostgreSQL database dump
--

\restrict ZJhqGn2glXeb5ESaHSJdeDuW4iKA1x54PCJqE68kU9Lxy1GIvjGNQib6uJfhLAz

-- Dumped from database version 18.3 (Ubuntu 18.3-1.pgdg24.04+1)
-- Dumped by pg_dump version 18.3 (Ubuntu 18.3-1.pgdg24.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: hostel; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA hostel;


ALTER SCHEMA hostel OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: candidate_documents; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.candidate_documents (
    document_id integer NOT NULL,
    candidate_id integer,
    document_type character varying(50),
    document_number character varying(100),
    file_name character varying(255),
    file_path character varying(255),
    uploaded_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE hostel.candidate_documents OWNER TO postgres;

--
-- Name: candidate_documents_document_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.candidate_documents_document_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.candidate_documents_document_id_seq OWNER TO postgres;

--
-- Name: candidate_documents_document_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.candidate_documents_document_id_seq OWNED BY hostel.candidate_documents.document_id;


--
-- Name: candidate_images; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.candidate_images (
    image_id integer NOT NULL,
    candidate_id integer,
    image_type character varying(50),
    file_name character varying(255),
    file_path character varying(255),
    uploaded_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE hostel.candidate_images OWNER TO postgres;

--
-- Name: candidate_images_image_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.candidate_images_image_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.candidate_images_image_id_seq OWNER TO postgres;

--
-- Name: candidate_images_image_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.candidate_images_image_id_seq OWNED BY hostel.candidate_images.image_id;


--
-- Name: candidates; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.candidates (
    candidate_id integer NOT NULL,
    user_id integer,
    first_name character varying(120) NOT NULL,
    last_name character varying(120),
    gender character(1),
    date_of_birth date,
    email character varying(120),
    contact_number character varying(20),
    address text,
    city character varying(120),
    state character varying(120),
    pin_code character varying(20),
    applied_post character varying(120),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modified_at timestamp without time zone,
    created_by character varying(20) NOT NULL,
    modified_by character varying(20) NOT NULL,
    active_flag character(1) DEFAULT 'Y'::bpchar NOT NULL,
    candidate_code character varying(30) DEFAULT NULL::character varying NOT NULL
);


ALTER TABLE hostel.candidates OWNER TO postgres;

--
-- Name: candidates_candidate_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.candidates_candidate_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.candidates_candidate_id_seq OWNER TO postgres;

--
-- Name: candidates_candidate_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.candidates_candidate_id_seq OWNED BY hostel.candidates.candidate_id;


--
-- Name: guest_details; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.guest_details (
    guest_id integer NOT NULL,
    request_id integer,
    guest_name character varying(120),
    relation character varying(50),
    age integer,
    gender character(1)
);


ALTER TABLE hostel.guest_details OWNER TO postgres;

--
-- Name: guest_details_guest_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.guest_details_guest_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.guest_details_guest_id_seq OWNER TO postgres;

--
-- Name: guest_details_guest_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.guest_details_guest_id_seq OWNED BY hostel.guest_details.guest_id;


--
-- Name: guest_proofs; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.guest_proofs (
    proof_id integer NOT NULL,
    guest_id integer,
    proof_type character varying(50),
    file_name character varying(255),
    file_path character varying(255),
    uploaded_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE hostel.guest_proofs OWNER TO postgres;

--
-- Name: guest_proofs_proof_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.guest_proofs_proof_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.guest_proofs_proof_id_seq OWNER TO postgres;

--
-- Name: guest_proofs_proof_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.guest_proofs_proof_id_seq OWNED BY hostel.guest_proofs.proof_id;


--
-- Name: requests; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.requests (
    request_id integer NOT NULL,
    user_id integer NOT NULL,
    request_type character varying(30) NOT NULL,
    reason text,
    from_date date,
    to_date date,
    no_of_days integer,
    no_of_persons integer,
    status character varying(30) DEFAULT 'PENDING'::character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone,
    created_by character varying(20) NOT NULL,
    modified_by character varying(20) NOT NULL,
    active_flag character(1) DEFAULT 'Y'::bpchar NOT NULL,
    user_role character varying(30),
    modified_at timestamp without time zone
);


ALTER TABLE hostel.requests OWNER TO postgres;

--
-- Name: requests_request_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.requests_request_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.requests_request_id_seq OWNER TO postgres;

--
-- Name: requests_request_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.requests_request_id_seq OWNED BY hostel.requests.request_id;


--
-- Name: roles; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.roles (
    role_id integer NOT NULL,
    role_name character varying(50) NOT NULL,
    description character varying(150),
    active_flag character(1) DEFAULT 'Y'::bpchar,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE hostel.roles OWNER TO postgres;

--
-- Name: roles_role_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.roles_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.roles_role_id_seq OWNER TO postgres;

--
-- Name: roles_role_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.roles_role_id_seq OWNED BY hostel.roles.role_id;


--
-- Name: staff; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.staff (
    staff_id integer NOT NULL,
    user_id integer,
    employee_code character varying(30) NOT NULL,
    first_name character varying(120) NOT NULL,
    last_name character varying(120),
    gender character(1),
    date_of_birth date,
    contact_number character varying(20),
    email character varying(120),
    address text,
    designation character varying(120),
    department character varying(120),
    date_of_joining date,
    active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modified_at timestamp without time zone,
    created_by character varying(20) NOT NULL,
    modified_by character varying(20) NOT NULL,
    active_flag character(1) DEFAULT 'Y'::bpchar NOT NULL
);


ALTER TABLE hostel.staff OWNER TO postgres;

--
-- Name: staff_documents; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.staff_documents (
    document_id integer NOT NULL,
    staff_id integer,
    document_type character varying(50),
    document_number character varying(100),
    file_name character varying(255),
    file_path character varying(255),
    uploaded_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE hostel.staff_documents OWNER TO postgres;

--
-- Name: staff_documents_document_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.staff_documents_document_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.staff_documents_document_id_seq OWNER TO postgres;

--
-- Name: staff_documents_document_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.staff_documents_document_id_seq OWNED BY hostel.staff_documents.document_id;


--
-- Name: staff_images; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.staff_images (
    image_id integer NOT NULL,
    staff_id integer,
    image_type character varying(50),
    file_name character varying(255),
    file_path character varying(255),
    uploaded_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE hostel.staff_images OWNER TO postgres;

--
-- Name: staff_images_image_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.staff_images_image_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.staff_images_image_id_seq OWNER TO postgres;

--
-- Name: staff_images_image_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.staff_images_image_id_seq OWNED BY hostel.staff_images.image_id;


--
-- Name: staff_staff_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.staff_staff_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.staff_staff_id_seq OWNER TO postgres;

--
-- Name: staff_staff_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.staff_staff_id_seq OWNED BY hostel.staff.staff_id;


--
-- Name: student_documents; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.student_documents (
    document_id integer NOT NULL,
    student_id integer NOT NULL,
    document_type character varying(50) NOT NULL,
    document_number character varying(100),
    file_name character varying(255),
    file_path character varying(255),
    uploaded_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE hostel.student_documents OWNER TO postgres;

--
-- Name: student_documents_document_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.student_documents_document_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.student_documents_document_id_seq OWNER TO postgres;

--
-- Name: student_documents_document_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.student_documents_document_id_seq OWNED BY hostel.student_documents.document_id;


--
-- Name: student_images; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.student_images (
    image_id integer NOT NULL,
    student_id integer NOT NULL,
    image_type character varying(50),
    file_name character varying(255),
    file_path character varying(255),
    uploaded_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE hostel.student_images OWNER TO postgres;

--
-- Name: student_images_image_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.student_images_image_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.student_images_image_id_seq OWNER TO postgres;

--
-- Name: student_images_image_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.student_images_image_id_seq OWNED BY hostel.student_images.image_id;


--
-- Name: students; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.students (
    student_id integer NOT NULL,
    user_id integer,
    admission_number character varying(30) NOT NULL,
    first_name character varying(120) NOT NULL,
    last_name character varying(120),
    gender character(1),
    date_of_birth date,
    contact_number character varying(20),
    personal_email character varying(120),
    father_name character varying(120),
    mother_name character varying(120),
    address text,
    hostel_status boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modified_at timestamp without time zone,
    created_by character varying(20) NOT NULL,
    modified_by character varying(20) NOT NULL,
    active_flag character(1) DEFAULT 'Y'::bpchar NOT NULL
);


ALTER TABLE hostel.students OWNER TO postgres;

--
-- Name: students_student_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.students_student_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.students_student_id_seq OWNER TO postgres;

--
-- Name: students_student_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.students_student_id_seq OWNED BY hostel.students.student_id;


--
-- Name: users; Type: TABLE; Schema: hostel; Owner: postgres
--

CREATE TABLE hostel.users (
    user_id integer NOT NULL,
    username character varying(60) NOT NULL,
    password character varying(255) NOT NULL,
    role_id integer NOT NULL,
    account_type character varying(30),
    enabled boolean DEFAULT true,
    account_non_locked boolean DEFAULT true,
    account_non_expired boolean DEFAULT true,
    credentials_non_expired boolean DEFAULT true,
    failed_attempts integer DEFAULT 0,
    token_version integer DEFAULT 0,
    last_login_time timestamp without time zone,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modified_at timestamp without time zone,
    created_by character varying(20) NOT NULL,
    modified_by character varying(20) NOT NULL,
    active_flag character(1) DEFAULT 'Y'::bpchar NOT NULL
);


ALTER TABLE hostel.users OWNER TO postgres;

--
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: hostel; Owner: postgres
--

CREATE SEQUENCE hostel.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE hostel.users_user_id_seq OWNER TO postgres;

--
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: hostel; Owner: postgres
--

ALTER SEQUENCE hostel.users_user_id_seq OWNED BY hostel.users.user_id;


--
-- Name: candidate_documents document_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.candidate_documents ALTER COLUMN document_id SET DEFAULT nextval('hostel.candidate_documents_document_id_seq'::regclass);


--
-- Name: candidate_images image_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.candidate_images ALTER COLUMN image_id SET DEFAULT nextval('hostel.candidate_images_image_id_seq'::regclass);


--
-- Name: candidates candidate_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.candidates ALTER COLUMN candidate_id SET DEFAULT nextval('hostel.candidates_candidate_id_seq'::regclass);


--
-- Name: guest_details guest_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.guest_details ALTER COLUMN guest_id SET DEFAULT nextval('hostel.guest_details_guest_id_seq'::regclass);


--
-- Name: guest_proofs proof_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.guest_proofs ALTER COLUMN proof_id SET DEFAULT nextval('hostel.guest_proofs_proof_id_seq'::regclass);


--
-- Name: requests request_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.requests ALTER COLUMN request_id SET DEFAULT nextval('hostel.requests_request_id_seq'::regclass);


--
-- Name: roles role_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.roles ALTER COLUMN role_id SET DEFAULT nextval('hostel.roles_role_id_seq'::regclass);


--
-- Name: staff staff_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.staff ALTER COLUMN staff_id SET DEFAULT nextval('hostel.staff_staff_id_seq'::regclass);


--
-- Name: staff_documents document_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.staff_documents ALTER COLUMN document_id SET DEFAULT nextval('hostel.staff_documents_document_id_seq'::regclass);


--
-- Name: staff_images image_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.staff_images ALTER COLUMN image_id SET DEFAULT nextval('hostel.staff_images_image_id_seq'::regclass);


--
-- Name: student_documents document_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.student_documents ALTER COLUMN document_id SET DEFAULT nextval('hostel.student_documents_document_id_seq'::regclass);


--
-- Name: student_images image_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.student_images ALTER COLUMN image_id SET DEFAULT nextval('hostel.student_images_image_id_seq'::regclass);


--
-- Name: students student_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.students ALTER COLUMN student_id SET DEFAULT nextval('hostel.students_student_id_seq'::regclass);


--
-- Name: users user_id; Type: DEFAULT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.users ALTER COLUMN user_id SET DEFAULT nextval('hostel.users_user_id_seq'::regclass);


--
-- Data for Name: candidate_documents; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.candidate_documents (document_id, candidate_id, document_type, document_number, file_name, file_path, uploaded_at) FROM stdin;
\.


--
-- Data for Name: candidate_images; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.candidate_images (image_id, candidate_id, image_type, file_name, file_path, uploaded_at) FROM stdin;
\.


--
-- Data for Name: candidates; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.candidates (candidate_id, user_id, first_name, last_name, gender, date_of_birth, email, contact_number, address, city, state, pin_code, applied_post, created_at, modified_at, created_by, modified_by, active_flag, candidate_code) FROM stdin;
1	16	Rahul	Sharma	M	2001-05-12	rahul.sharma@example.com	9876543210	MG Road	Bangalore	Karnataka	560001	Interview	2026-03-12 21:47:56.038231	2026-03-12 21:47:56.038231	anonymousUser	anonymousUser	Y	CAND2026001
2	18	Rahul	Sharma 2	M	2001-05-12	rahul.sharma2@example.com	9876543210	MG Road	Bangalore	Karnataka	560001	Interview 2	2026-04-02 12:22:55.305728	2026-04-02 12:22:55.305728	anonymousUser	anonymousUser	Y	CAND2026002
3	19	Rahul	Sharma 2	M	2001-05-12	rahul.sharma2@example.com	9876543210	MG Road	Bangalore	Karnataka	560001	Interview 2	2026-04-02 12:24:14.394027	2026-04-02 12:24:14.394027	anonymousUser	anonymousUser	Y	CAND2026003
4	21	Rahul2	Sharma 3	M	2001-05-12	rahul.sharma2@example.com	9876543210	MG Road	Bangalore	Karnataka	560001	Interview 2	2026-04-12 13:43:51.276723	2026-04-12 13:43:51.276723	anonymousUser	anonymousUser	Y	CAND2026004
\.


--
-- Data for Name: guest_details; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.guest_details (guest_id, request_id, guest_name, relation, age, gender) FROM stdin;
\.


--
-- Data for Name: guest_proofs; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.guest_proofs (proof_id, guest_id, proof_type, file_name, file_path, uploaded_at) FROM stdin;
\.


--
-- Data for Name: requests; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.requests (request_id, user_id, request_type, reason, from_date, to_date, no_of_days, no_of_persons, status, created_at, updated_at, created_by, modified_by, active_flag, user_role, modified_at) FROM stdin;
1	17	Leave	Extended leave	2026-04-02	2026-04-03	4	1	Approved	2026-04-02 12:22:07.883268	\N	STU2026006	admin	Y	ROLE_STUDENT	2026-04-02 17:05:58.0223
2	19	Leave	Extended leave	2026-04-02	2026-04-03	4	1	Rejected	2026-04-02 12:27:35.883551	\N	CAND2026003	admin	Y	ROLE_CANDIDATE	2026-04-02 17:06:16.552654
3	20	Leave staff	Extended leave staff	2026-04-02	2026-04-03	4	1	Cancelled	2026-04-02 16:44:02.998521	\N	EMP2026004	admin	Y	ROLE_STAFF	2026-04-02 17:06:33.157334
4	20	Sick food staff	sick food staff	2026-04-02	2026-04-03	4	1	Cancelled	2026-04-02 17:07:51.709132	\N	EMP2026004	admin	Y	ROLE_STAFF	2026-04-02 17:08:10.323062
5	17	stay request	stay request	2026-04-02	2026-05-03	4	1	Pending	2026-04-12 13:46:07.856927	\N	STU2026006	STU2026006	Y	ROLE_STUDENT	2026-04-12 13:46:07.856927
\.


--
-- Data for Name: roles; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.roles (role_id, role_name, description, active_flag, created_at) FROM stdin;
1	ROLE_STUDENT	Student user	Y	2026-03-07 05:35:35.451833
2	ROLE_STAFF	Staff user	Y	2026-03-07 05:35:35.451833
3	ROLE_ADMIN	System administrator	Y	2026-03-07 05:35:35.451833
4	ROLE_CANDIDATE	External candidate	Y	2026-03-07 05:35:35.451833
\.


--
-- Data for Name: staff; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.staff (staff_id, user_id, employee_code, first_name, last_name, gender, date_of_birth, contact_number, email, address, designation, department, date_of_joining, active, created_at, modified_at, created_by, modified_by, active_flag) FROM stdin;
2	15	EMP2026002	Anita	Rao	F	1995-08-20	9123456789	anita.rao@example.com	Indiranagar	Hostel Manager	Administration	2024-06-01	t	2026-03-12 21:41:14.264694	2026-03-12 21:41:14.264694	anonymousUser	anonymousUser	Y
3	20	EMP2026004	Last Name	First Name	F	2000-08-20	9123456789	anita.rao@example.com	Indiranagar	Hostel Manager	Administration	2024-06-01	t	2026-04-02 16:41:03.051535	2026-04-02 16:41:03.051535	admin	admin	Y
\.


--
-- Data for Name: staff_documents; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.staff_documents (document_id, staff_id, document_type, document_number, file_name, file_path, uploaded_at) FROM stdin;
\.


--
-- Data for Name: staff_images; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.staff_images (image_id, staff_id, image_type, file_name, file_path, uploaded_at) FROM stdin;
\.


--
-- Data for Name: student_documents; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.student_documents (document_id, student_id, document_type, document_number, file_name, file_path, uploaded_at) FROM stdin;
\.


--
-- Data for Name: student_images; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.student_images (image_id, student_id, image_type, file_name, file_path, uploaded_at) FROM stdin;
\.


--
-- Data for Name: students; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.students (student_id, user_id, admission_number, first_name, last_name, gender, date_of_birth, contact_number, personal_email, father_name, mother_name, address, hostel_status, created_at, modified_at, created_by, modified_by, active_flag) FROM stdin;
4	10	STU2026002	Rahul	Sharma	M	2002-04-12	9876543210	rahul.sharma@gmail.com	Ramesh Sharma	Sunita Sharma	Bangalore, Karnataka	t	2026-03-09 14:11:42.553668	2026-03-09 14:11:42.553668	admin	admin	Y
5	11	STU2026003	Rahul	Sharma	M	2002-04-12	9876543210	rahul.sharma@gmail.com	Ramesh Sharma	Sunita Sharma	Bangalore, Karnataka	t	2026-03-09 21:53:53.067803	2026-03-09 21:53:53.067803	admin	admin	Y
6	12	STU2026004	first name	last Name	M	2002-01-01	9876543210	rahul.sharma@gmail.com	Ramesh Sharma	Sunita Sharma	Bangalore, Karnataka	t	2026-03-10 22:19:14.400381	2026-03-10 22:19:14.400381	STU2026003	STU2026003	Y
7	17	STU2026006	first name	last Name	M	2002-01-01	9876543212	rahul.sharma@gmail.com	Ramesh Sharma	Sunita Sharma	Bangalore, Karnataka	t	2026-04-02 11:44:15.417362	2026-04-02 11:44:15.417362	anonymousUser	anonymousUser	Y
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: hostel; Owner: postgres
--

COPY hostel.users (user_id, username, password, role_id, account_type, enabled, account_non_locked, account_non_expired, credentials_non_expired, failed_attempts, token_version, last_login_time, created_at, modified_at, created_by, modified_by, active_flag) FROM stdin;
7	STU2026001	$2a$10$d95/xKmPaVJFHLlF3GMt4.YO24.OtUq7jr4zcCT458UZ6k52lAUKm	1	\N	t	t	t	t	0	0	\N	2026-03-09 14:06:17.41893	2026-03-09 14:06:17.41893	admin	admin	Y
10	STU2026002	$2a$10$E3rW1Yn3wc1Yfq81t8zqI.Cy8QMLIbPuCWpHGDxXyq0R/a8YMStu2	1	\N	t	t	t	t	0	0	\N	2026-03-09 14:11:42.551286	2026-03-09 14:11:42.551286	admin	admin	Y
11	STU2026003	$2a$10$2v5muN4Ztt6FDwnEgstMreqV5S0awuZTWkunCcXBYz/MAkfxkjiIi	1	\N	t	t	t	t	0	0	\N	2026-03-09 21:53:53.054885	2026-03-09 21:53:53.054885	admin	admin	Y
12	STU2026004	$2a$10$GNACdHBTkMb5JxzuDfaJxuV6FRYGKhQrORq/HnhsCUZOC32A6NsYW	1	\N	t	t	t	t	0	0	\N	2026-03-10 22:19:14.387238	2026-03-10 22:19:14.387238	STU2026003	STU2026003	Y
13	EMP2026001	$2a$10$mI5dor1jfmIjw4TcfGG60O0mzI0c4OtAnizWGLynMbCLPbbhkEofO	2	\N	t	t	t	t	0	0	\N	2026-03-12 21:40:28.007777	2026-03-12 21:40:28.007777	anonymousUser	anonymousUser	Y
15	EMP2026002	$2a$10$kpksTJn8amqWB2Pf7Do86ulM3uIJgARraBIqC5Mag.u25RbMluuUu	2	\N	t	t	t	t	0	0	\N	2026-03-12 21:41:14.259679	2026-03-12 21:41:14.259679	anonymousUser	anonymousUser	Y
16	CAND2026001	$2a$10$XvQ25adbGW7YEBGu152UqexjXbE5UAYiZhlXTCB4F884iDWQ.zuDy	4	\N	t	t	t	t	0	0	\N	2026-03-12 21:47:56.012511	2026-03-12 21:47:56.012511	anonymousUser	anonymousUser	Y
1	admin	$2a$12$zw3RXbAG.bGq9qI/UQw6QO9SpGVdH4HBldOpD7EPlb5i0EFsiABhq	3	LOCAL	t	t	t	t	0	4	\N	2026-03-07 13:41:51.690307	2026-03-22 08:19:30.840221	SYSTEM	admin	Y
17	STU2026006	$2a$10$bouS.BIALkTVZecDJEm9CeTJNRrCWzYXmva3fLRZeRq0OLensTlTG	1	\N	t	t	t	t	0	0	\N	2026-04-02 11:44:15.352581	2026-04-02 11:44:15.352581	anonymousUser	anonymousUser	Y
18	CAND2026002	$2a$10$bZYaTu.fbUrcC261XavgbePB4d00LayT0xapnTW.XZzuKN2DqKNvy	4	\N	t	t	t	t	0	0	\N	2026-04-02 12:22:55.286867	2026-04-02 12:22:55.286867	anonymousUser	anonymousUser	Y
19	CAND2026003	$2a$10$VvKmiaBaOLtlF2j.Ina0.OuoKFcJSiNcimI/KTYGY20WQrf4AIOpS	4	\N	t	t	t	t	0	0	\N	2026-04-02 12:24:14.322493	2026-04-02 12:24:14.322493	anonymousUser	anonymousUser	Y
20	EMP2026004	$2a$10$Zzxn8ty2dtTR5Vv4.7P0Zue.fbCHKMBF6d4QixtHKsFkcqYwh9WVq	2	\N	t	t	t	t	0	0	\N	2026-04-02 16:41:03.016972	2026-04-02 16:41:03.016972	admin	admin	Y
21	CAND2026004	$2a$10$PTvXrwzG4uFtVTnOh0w.0eJj8wIzas2K7RQf9oVwJy2kUR5JLYjbm	4	\N	t	t	t	t	0	0	\N	2026-04-12 13:43:51.236833	2026-04-12 13:43:51.236833	anonymousUser	anonymousUser	Y
\.


--
-- Name: candidate_documents_document_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.candidate_documents_document_id_seq', 1, false);


--
-- Name: candidate_images_image_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.candidate_images_image_id_seq', 1, false);


--
-- Name: candidates_candidate_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.candidates_candidate_id_seq', 4, true);


--
-- Name: guest_details_guest_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.guest_details_guest_id_seq', 1, false);


--
-- Name: guest_proofs_proof_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.guest_proofs_proof_id_seq', 1, false);


--
-- Name: requests_request_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.requests_request_id_seq', 5, true);


--
-- Name: roles_role_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.roles_role_id_seq', 4, true);


--
-- Name: staff_documents_document_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.staff_documents_document_id_seq', 1, false);


--
-- Name: staff_images_image_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.staff_images_image_id_seq', 1, false);


--
-- Name: staff_staff_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.staff_staff_id_seq', 3, true);


--
-- Name: student_documents_document_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.student_documents_document_id_seq', 1, false);


--
-- Name: student_images_image_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.student_images_image_id_seq', 1, false);


--
-- Name: students_student_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.students_student_id_seq', 7, true);


--
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: hostel; Owner: postgres
--

SELECT pg_catalog.setval('hostel.users_user_id_seq', 21, true);


--
-- Name: candidate_documents candidate_documents_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.candidate_documents
    ADD CONSTRAINT candidate_documents_pkey PRIMARY KEY (document_id);


--
-- Name: candidate_images candidate_images_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.candidate_images
    ADD CONSTRAINT candidate_images_pkey PRIMARY KEY (image_id);


--
-- Name: candidates candidates_candidate_code_key; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.candidates
    ADD CONSTRAINT candidates_candidate_code_key UNIQUE (candidate_code);


--
-- Name: candidates candidates_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.candidates
    ADD CONSTRAINT candidates_pkey PRIMARY KEY (candidate_id);


--
-- Name: candidates candidates_user_id_key; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.candidates
    ADD CONSTRAINT candidates_user_id_key UNIQUE (user_id);


--
-- Name: guest_details guest_details_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.guest_details
    ADD CONSTRAINT guest_details_pkey PRIMARY KEY (guest_id);


--
-- Name: guest_proofs guest_proofs_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.guest_proofs
    ADD CONSTRAINT guest_proofs_pkey PRIMARY KEY (proof_id);


--
-- Name: requests requests_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.requests
    ADD CONSTRAINT requests_pkey PRIMARY KEY (request_id);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (role_id);


--
-- Name: roles roles_role_name_key; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.roles
    ADD CONSTRAINT roles_role_name_key UNIQUE (role_name);


--
-- Name: staff_documents staff_documents_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.staff_documents
    ADD CONSTRAINT staff_documents_pkey PRIMARY KEY (document_id);


--
-- Name: staff staff_employee_code_key; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.staff
    ADD CONSTRAINT staff_employee_code_key UNIQUE (employee_code);


--
-- Name: staff_images staff_images_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.staff_images
    ADD CONSTRAINT staff_images_pkey PRIMARY KEY (image_id);


--
-- Name: staff staff_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.staff
    ADD CONSTRAINT staff_pkey PRIMARY KEY (staff_id);


--
-- Name: staff staff_user_id_key; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.staff
    ADD CONSTRAINT staff_user_id_key UNIQUE (user_id);


--
-- Name: student_documents student_documents_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.student_documents
    ADD CONSTRAINT student_documents_pkey PRIMARY KEY (document_id);


--
-- Name: student_images student_images_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.student_images
    ADD CONSTRAINT student_images_pkey PRIMARY KEY (image_id);


--
-- Name: students students_admission_number_key; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.students
    ADD CONSTRAINT students_admission_number_key UNIQUE (admission_number);


--
-- Name: students students_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.students
    ADD CONSTRAINT students_pkey PRIMARY KEY (student_id);


--
-- Name: students students_user_id_key; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.students
    ADD CONSTRAINT students_user_id_key UNIQUE (user_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: candidate_documents candidate_documents_candidate_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.candidate_documents
    ADD CONSTRAINT candidate_documents_candidate_id_fkey FOREIGN KEY (candidate_id) REFERENCES hostel.candidates(candidate_id) ON DELETE CASCADE;


--
-- Name: candidate_images candidate_images_candidate_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.candidate_images
    ADD CONSTRAINT candidate_images_candidate_id_fkey FOREIGN KEY (candidate_id) REFERENCES hostel.candidates(candidate_id) ON DELETE CASCADE;


--
-- Name: candidates candidates_user_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.candidates
    ADD CONSTRAINT candidates_user_id_fkey FOREIGN KEY (user_id) REFERENCES hostel.users(user_id);


--
-- Name: guest_details guest_details_request_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.guest_details
    ADD CONSTRAINT guest_details_request_id_fkey FOREIGN KEY (request_id) REFERENCES hostel.requests(request_id) ON DELETE CASCADE;


--
-- Name: guest_proofs guest_proofs_guest_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.guest_proofs
    ADD CONSTRAINT guest_proofs_guest_id_fkey FOREIGN KEY (guest_id) REFERENCES hostel.guest_details(guest_id) ON DELETE CASCADE;


--
-- Name: requests requests_user_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.requests
    ADD CONSTRAINT requests_user_id_fkey FOREIGN KEY (user_id) REFERENCES hostel.users(user_id);


--
-- Name: staff_documents staff_documents_staff_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.staff_documents
    ADD CONSTRAINT staff_documents_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES hostel.staff(staff_id) ON DELETE CASCADE;


--
-- Name: staff_images staff_images_staff_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.staff_images
    ADD CONSTRAINT staff_images_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES hostel.staff(staff_id) ON DELETE CASCADE;


--
-- Name: staff staff_user_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.staff
    ADD CONSTRAINT staff_user_id_fkey FOREIGN KEY (user_id) REFERENCES hostel.users(user_id);


--
-- Name: student_documents student_documents_student_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.student_documents
    ADD CONSTRAINT student_documents_student_id_fkey FOREIGN KEY (student_id) REFERENCES hostel.students(student_id) ON DELETE CASCADE;


--
-- Name: student_images student_images_student_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.student_images
    ADD CONSTRAINT student_images_student_id_fkey FOREIGN KEY (student_id) REFERENCES hostel.students(student_id) ON DELETE CASCADE;


--
-- Name: students students_user_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.students
    ADD CONSTRAINT students_user_id_fkey FOREIGN KEY (user_id) REFERENCES hostel.users(user_id);


--
-- Name: users users_role_id_fkey; Type: FK CONSTRAINT; Schema: hostel; Owner: postgres
--

ALTER TABLE ONLY hostel.users
    ADD CONSTRAINT users_role_id_fkey FOREIGN KEY (role_id) REFERENCES hostel.roles(role_id);


--
-- PostgreSQL database dump complete
--

\unrestrict ZJhqGn2glXeb5ESaHSJdeDuW4iKA1x54PCJqE68kU9Lxy1GIvjGNQib6uJfhLAz

