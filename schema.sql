--
-- PostgreSQL database dump
--

\restrict aCRcTvEFwEMdrR7qFYawgYbnyqegX1BAQbBf9BQcVJOMlDMbHuiK03TvOQUxXD5

-- Dumped from database version 16.11 (Ubuntu 16.11-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.11 (Ubuntu 16.11-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: appointments; Type: TABLE; Schema: public; Owner: arify_app
--

CREATE TABLE public.appointments (
    phone text NOT NULL,
    doctor_id bigint NOT NULL,
    slot_id bigint NOT NULL,
    status text NOT NULL,
    order_id text,
    created_at timestamp with time zone DEFAULT now(),
    confirmed_at timestamp with time zone,
    CONSTRAINT appointments_status_check CHECK ((status = ANY (ARRAY['LOCKED'::text, 'PAID'::text, 'CANCELLED'::text])))
);


ALTER TABLE public.appointments OWNER TO arify_app;

--
-- Name: doctor_schedule; Type: TABLE; Schema: public; Owner: arify_app
--

CREATE TABLE public.doctor_schedule (
    id bigint NOT NULL,
    doctor_id bigint NOT NULL,
    day_of_week integer NOT NULL,
    start_time time without time zone NOT NULL,
    end_time time without time zone NOT NULL,
    slot_duration_minutes integer DEFAULT 15 NOT NULL,
    active boolean DEFAULT true,
    CONSTRAINT doctor_schedule_day_of_week_check CHECK (((day_of_week >= 0) AND (day_of_week <= 6)))
);


ALTER TABLE public.doctor_schedule OWNER TO arify_app;

--
-- Name: doctor_schedule_id_seq; Type: SEQUENCE; Schema: public; Owner: arify_app
--

CREATE SEQUENCE public.doctor_schedule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.doctor_schedule_id_seq OWNER TO arify_app;

--
-- Name: doctor_schedule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: arify_app
--

ALTER SEQUENCE public.doctor_schedule_id_seq OWNED BY public.doctor_schedule.id;


--
-- Name: doctors; Type: TABLE; Schema: public; Owner: arify_app
--

CREATE TABLE public.doctors (
    id bigint NOT NULL,
    name text NOT NULL,
    service_id bigint,
    phone text,
    active boolean DEFAULT true
);


ALTER TABLE public.doctors OWNER TO arify_app;

--
-- Name: doctors_id_seq; Type: SEQUENCE; Schema: public; Owner: arify_app
--

CREATE SEQUENCE public.doctors_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.doctors_id_seq OWNER TO arify_app;

--
-- Name: doctors_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: arify_app
--

ALTER SEQUENCE public.doctors_id_seq OWNED BY public.doctors.id;


--
-- Name: services; Type: TABLE; Schema: public; Owner: arify_app
--

CREATE TABLE public.services (
    id bigint NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.services OWNER TO arify_app;

--
-- Name: services_id_seq; Type: SEQUENCE; Schema: public; Owner: arify_app
--

CREATE SEQUENCE public.services_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.services_id_seq OWNER TO arify_app;

--
-- Name: services_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: arify_app
--

ALTER SEQUENCE public.services_id_seq OWNED BY public.services.id;


--
-- Name: slots; Type: TABLE; Schema: public; Owner: arify_app
--

CREATE TABLE public.slots (
    id bigint NOT NULL,
    doctor_id bigint NOT NULL,
    slot_time timestamp with time zone NOT NULL,
    status text NOT NULL,
    locked_until timestamp with time zone,
    CONSTRAINT slots_status_check CHECK ((status = ANY (ARRAY['AVAILABLE'::text, 'LOCKED'::text, 'BOOKED'::text])))
);


ALTER TABLE public.slots OWNER TO arify_app;

--
-- Name: slots_id_seq; Type: SEQUENCE; Schema: public; Owner: arify_app
--

CREATE SEQUENCE public.slots_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.slots_id_seq OWNER TO arify_app;

--
-- Name: slots_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: arify_app
--

ALTER SEQUENCE public.slots_id_seq OWNED BY public.slots.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: arify_app
--

CREATE TABLE public.users (
    phone text NOT NULL,
    name text NOT NULL,
    created_at timestamp with time zone DEFAULT now()
);


ALTER TABLE public.users OWNER TO arify_app;

--
-- Name: doctor_schedule id; Type: DEFAULT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.doctor_schedule ALTER COLUMN id SET DEFAULT nextval('public.doctor_schedule_id_seq'::regclass);


--
-- Name: doctors id; Type: DEFAULT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.doctors ALTER COLUMN id SET DEFAULT nextval('public.doctors_id_seq'::regclass);


--
-- Name: services id; Type: DEFAULT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.services ALTER COLUMN id SET DEFAULT nextval('public.services_id_seq'::regclass);


--
-- Name: slots id; Type: DEFAULT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.slots ALTER COLUMN id SET DEFAULT nextval('public.slots_id_seq'::regclass);


--
-- Name: appointments appointments_pkey; Type: CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT appointments_pkey PRIMARY KEY (phone);


--
-- Name: doctor_schedule doctor_schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.doctor_schedule
    ADD CONSTRAINT doctor_schedule_pkey PRIMARY KEY (id);


--
-- Name: doctors doctors_phone_key; Type: CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.doctors
    ADD CONSTRAINT doctors_phone_key UNIQUE (phone);


--
-- Name: doctors doctors_pkey; Type: CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.doctors
    ADD CONSTRAINT doctors_pkey PRIMARY KEY (id);


--
-- Name: services services_name_key; Type: CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.services
    ADD CONSTRAINT services_name_key UNIQUE (name);


--
-- Name: services services_pkey; Type: CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.services
    ADD CONSTRAINT services_pkey PRIMARY KEY (id);


--
-- Name: slots slots_pkey; Type: CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.slots
    ADD CONSTRAINT slots_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (phone);


--
-- Name: idx_appointments_slot; Type: INDEX; Schema: public; Owner: arify_app
--

CREATE UNIQUE INDEX idx_appointments_slot ON public.appointments USING btree (slot_id);


--
-- Name: idx_schedule_doctor; Type: INDEX; Schema: public; Owner: arify_app
--

CREATE INDEX idx_schedule_doctor ON public.doctor_schedule USING btree (doctor_id);


--
-- Name: idx_slots_doctor_time; Type: INDEX; Schema: public; Owner: arify_app
--

CREATE INDEX idx_slots_doctor_time ON public.slots USING btree (doctor_id, slot_time);


--
-- Name: idx_slots_status; Type: INDEX; Schema: public; Owner: arify_app
--

CREATE INDEX idx_slots_status ON public.slots USING btree (status);


--
-- Name: appointments appointments_doctor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT appointments_doctor_id_fkey FOREIGN KEY (doctor_id) REFERENCES public.doctors(id);


--
-- Name: appointments appointments_phone_fkey; Type: FK CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT appointments_phone_fkey FOREIGN KEY (phone) REFERENCES public.users(phone) ON DELETE CASCADE;


--
-- Name: appointments appointments_slot_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT appointments_slot_id_fkey FOREIGN KEY (slot_id) REFERENCES public.slots(id);


--
-- Name: doctor_schedule doctor_schedule_doctor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.doctor_schedule
    ADD CONSTRAINT doctor_schedule_doctor_id_fkey FOREIGN KEY (doctor_id) REFERENCES public.doctors(id) ON DELETE CASCADE;


--
-- Name: doctors doctors_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.doctors
    ADD CONSTRAINT doctors_service_id_fkey FOREIGN KEY (service_id) REFERENCES public.services(id) ON DELETE SET NULL;


--
-- Name: slots slots_doctor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: arify_app
--

ALTER TABLE ONLY public.slots
    ADD CONSTRAINT slots_doctor_id_fkey FOREIGN KEY (doctor_id) REFERENCES public.doctors(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict aCRcTvEFwEMdrR7qFYawgYbnyqegX1BAQbBf9BQcVJOMlDMbHuiK03TvOQUxXD5

