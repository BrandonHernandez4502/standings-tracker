--
-- PostgreSQL database dump
--

\restrict pXARA8UmQgQqJv6fIwR0897vWfCY0OeOEHgNSpgVDVZMNNhq4CebicYwZth6mEc

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: standing_snapshots; Type: TABLE; Schema: public; Owner: brandon
--

CREATE TABLE public.standing_snapshots (
    id bigint NOT NULL,
    snapshot_date date NOT NULL,
    league_id character varying(20) NOT NULL,
    team_id integer NOT NULL,
    team_name character varying(100) NOT NULL,
    "position" integer NOT NULL,
    played integer NOT NULL,
    won integer NOT NULL,
    drawn integer NOT NULL,
    lost integer NOT NULL,
    goals_for integer NOT NULL,
    goals_against integer NOT NULL,
    goal_difference integer NOT NULL,
    points integer NOT NULL,
    form character varying(20)
);


ALTER TABLE public.standing_snapshots OWNER TO brandon;

--
-- Name: standing_snapshots_id_seq; Type: SEQUENCE; Schema: public; Owner: brandon
--

CREATE SEQUENCE public.standing_snapshots_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.standing_snapshots_id_seq OWNER TO brandon;

--
-- Name: standing_snapshots_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: brandon
--

ALTER SEQUENCE public.standing_snapshots_id_seq OWNED BY public.standing_snapshots.id;


--
-- Name: standing_snapshots id; Type: DEFAULT; Schema: public; Owner: brandon
--

ALTER TABLE ONLY public.standing_snapshots ALTER COLUMN id SET DEFAULT nextval('public.standing_snapshots_id_seq'::regclass);


--
-- Data for Name: standing_snapshots; Type: TABLE DATA; Schema: public; Owner: brandon
--

COPY public.standing_snapshots (id, snapshot_date, league_id, team_id, team_name, "position", played, won, drawn, lost, goals_for, goals_against, goal_difference, points, form) FROM stdin;
1	2026-04-07	PL	57	Arsenal FC	1	31	21	7	3	61	22	39	70	\N
2	2026-04-07	PL	65	Manchester City FC	2	30	18	7	5	60	28	32	61	\N
3	2026-04-07	PL	66	Manchester United FC	3	31	15	10	6	56	43	13	55	\N
4	2026-04-07	PL	58	Aston Villa FC	4	31	16	6	9	42	37	5	54	\N
5	2026-04-07	PL	64	Liverpool FC	5	31	14	7	10	50	42	8	49	\N
6	2026-04-07	PL	61	Chelsea FC	6	31	13	9	9	53	38	15	48	\N
7	2026-04-07	PL	402	Brentford FC	7	31	13	7	11	46	42	4	46	\N
8	2026-04-07	PL	62	Everton FC	8	31	13	7	11	37	35	2	46	\N
9	2026-04-07	PL	63	Fulham FC	9	31	13	5	13	43	44	-1	44	\N
10	2026-04-07	PL	397	Brighton & Hove Albion FC	10	31	11	10	10	41	37	4	43	\N
11	2026-04-07	PL	71	Sunderland AFC	11	31	11	10	10	32	36	-4	43	\N
12	2026-04-07	PL	67	Newcastle United FC	12	31	12	6	13	44	45	-1	42	\N
13	2026-04-07	PL	1044	AFC Bournemouth	13	31	9	15	7	46	48	-2	42	\N
14	2026-04-07	PL	354	Crystal Palace FC	14	30	10	9	11	33	35	-2	39	\N
15	2026-04-07	PL	341	Leeds United FC	15	31	7	12	12	37	48	-11	33	\N
16	2026-04-07	PL	351	Nottingham Forest FC	16	31	8	8	15	31	43	-12	32	\N
17	2026-04-07	PL	73	Tottenham Hotspur FC	17	31	7	9	15	40	50	-10	30	\N
18	2026-04-07	PL	563	West Ham United FC	18	31	7	8	16	36	57	-21	29	\N
19	2026-04-07	PL	328	Burnley FC	19	31	4	8	19	33	61	-28	20	\N
20	2026-04-07	PL	76	Wolverhampton Wanderers FC	20	31	3	8	20	24	54	-30	17	\N
21	2026-04-08	PL	57	Arsenal FC	1	31	21	7	3	61	22	39	70	\N
22	2026-04-08	PL	65	Manchester City FC	2	30	18	7	5	60	28	32	61	\N
23	2026-04-08	PL	66	Manchester United FC	3	31	15	10	6	56	43	13	55	\N
24	2026-04-08	PL	58	Aston Villa FC	4	31	16	6	9	42	37	5	54	\N
25	2026-04-08	PL	64	Liverpool FC	5	31	14	7	10	50	42	8	49	\N
26	2026-04-08	PL	61	Chelsea FC	6	31	13	9	9	53	38	15	48	\N
27	2026-04-08	PL	402	Brentford FC	7	31	13	7	11	46	42	4	46	\N
28	2026-04-08	PL	62	Everton FC	8	31	13	7	11	37	35	2	46	\N
29	2026-04-08	PL	63	Fulham FC	9	31	13	5	13	43	44	-1	44	\N
30	2026-04-08	PL	397	Brighton & Hove Albion FC	10	31	11	10	10	41	37	4	43	\N
31	2026-04-08	PL	71	Sunderland AFC	11	31	11	10	10	32	36	-4	43	\N
32	2026-04-08	PL	67	Newcastle United FC	12	31	12	6	13	44	45	-1	42	\N
33	2026-04-08	PL	1044	AFC Bournemouth	13	31	9	15	7	46	48	-2	42	\N
34	2026-04-08	PL	354	Crystal Palace FC	14	30	10	9	11	33	35	-2	39	\N
35	2026-04-08	PL	341	Leeds United FC	15	31	7	12	12	37	48	-11	33	\N
36	2026-04-08	PL	351	Nottingham Forest FC	16	31	8	8	15	31	43	-12	32	\N
37	2026-04-08	PL	73	Tottenham Hotspur FC	17	31	7	9	15	40	50	-10	30	\N
38	2026-04-08	PL	563	West Ham United FC	18	31	7	8	16	36	57	-21	29	\N
39	2026-04-08	PL	328	Burnley FC	19	31	4	8	19	33	61	-28	20	\N
40	2026-04-08	PL	76	Wolverhampton Wanderers FC	20	31	3	8	20	24	54	-30	17	\N
41	2026-04-12	PL	57	Arsenal FC	1	32	21	7	4	62	24	38	70	\N
42	2026-04-12	PL	65	Manchester City FC	2	31	19	7	5	63	28	35	64	\N
43	2026-04-12	PL	66	Manchester United FC	3	31	15	10	6	56	43	13	55	\N
44	2026-04-12	PL	58	Aston Villa FC	4	32	16	7	9	43	38	5	55	\N
45	2026-04-12	PL	64	Liverpool FC	5	32	15	7	10	52	42	10	52	\N
46	2026-04-12	PL	61	Chelsea FC	6	32	13	9	10	53	41	12	48	\N
47	2026-04-12	PL	402	Brentford FC	7	32	13	8	11	48	44	4	47	\N
48	2026-04-12	PL	62	Everton FC	8	32	13	8	11	39	37	2	47	\N
49	2026-04-12	PL	397	Brighton & Hove Albion FC	9	32	12	10	10	43	37	6	46	\N
50	2026-04-12	PL	71	Sunderland AFC	10	32	12	10	10	33	36	-3	46	\N
51	2026-04-12	PL	1044	AFC Bournemouth	11	32	10	15	7	48	49	-1	45	\N
52	2026-04-12	PL	63	Fulham FC	12	32	13	5	14	43	46	-3	44	\N
53	2026-04-12	PL	354	Crystal Palace FC	13	31	11	9	11	35	36	-1	42	\N
54	2026-04-12	PL	67	Newcastle United FC	14	32	12	6	14	45	47	-2	42	\N
55	2026-04-12	PL	341	Leeds United FC	15	31	7	12	12	37	48	-11	33	\N
56	2026-04-12	PL	351	Nottingham Forest FC	16	32	8	9	15	32	44	-12	33	\N
57	2026-04-12	PL	563	West Ham United FC	17	32	8	8	16	40	57	-17	32	\N
58	2026-04-12	PL	73	Tottenham Hotspur FC	18	32	7	9	16	40	51	-11	30	\N
59	2026-04-12	PL	328	Burnley FC	19	32	4	8	20	33	63	-30	20	\N
60	2026-04-12	PL	76	Wolverhampton Wanderers FC	20	32	3	8	21	24	58	-34	17	\N
181	2026-04-13	PL	57	Arsenal FC	1	32	21	7	4	62	24	38	70	\N
182	2026-04-13	PL	65	Manchester City FC	2	31	19	7	5	63	28	35	64	\N
183	2026-04-13	PL	66	Manchester United FC	3	32	15	10	7	57	45	12	55	\N
184	2026-04-13	PL	58	Aston Villa FC	4	32	16	7	9	43	38	5	55	\N
185	2026-04-13	PL	64	Liverpool FC	5	32	15	7	10	52	42	10	52	\N
186	2026-04-13	PL	61	Chelsea FC	6	32	13	9	10	53	41	12	48	\N
187	2026-04-13	PL	402	Brentford FC	7	32	13	8	11	48	44	4	47	\N
188	2026-04-13	PL	62	Everton FC	8	32	13	8	11	39	37	2	47	\N
189	2026-04-13	PL	397	Brighton & Hove Albion FC	9	32	12	10	10	43	37	6	46	\N
190	2026-04-13	PL	71	Sunderland AFC	10	32	12	10	10	33	36	-3	46	\N
191	2026-04-13	PL	1044	AFC Bournemouth	11	32	10	15	7	48	49	-1	45	\N
192	2026-04-13	PL	63	Fulham FC	12	32	13	5	14	43	46	-3	44	\N
193	2026-04-13	PL	354	Crystal Palace FC	13	31	11	9	11	35	36	-1	42	\N
194	2026-04-13	PL	67	Newcastle United FC	14	32	12	6	14	45	47	-2	42	\N
195	2026-04-13	PL	341	Leeds United FC	15	32	8	12	12	39	49	-10	36	\N
196	2026-04-13	PL	351	Nottingham Forest FC	16	32	8	9	15	32	44	-12	33	\N
197	2026-04-13	PL	563	West Ham United FC	17	32	8	8	16	40	57	-17	32	\N
198	2026-04-13	PL	73	Tottenham Hotspur FC	18	32	7	9	16	40	51	-11	30	\N
199	2026-04-13	PL	328	Burnley FC	19	32	4	8	20	33	63	-30	20	\N
200	2026-04-13	PL	76	Wolverhampton Wanderers FC	20	32	3	8	21	24	58	-34	17	\N
201	2026-04-22	PL	65	Manchester City FC	1	33	21	7	5	66	29	37	70	\N
202	2026-04-22	PL	57	Arsenal FC	2	33	21	7	5	63	26	37	70	\N
203	2026-04-22	PL	66	Manchester United FC	3	33	16	10	7	58	45	13	58	\N
204	2026-04-22	PL	58	Aston Villa FC	4	33	17	7	9	47	41	6	58	\N
205	2026-04-22	PL	64	Liverpool FC	5	33	16	7	10	54	43	11	55	\N
206	2026-04-22	PL	397	Brighton & Hove Albion FC	6	34	13	11	10	48	39	9	50	\N
207	2026-04-22	PL	1044	AFC Bournemouth	7	34	11	16	7	52	52	0	49	\N
208	2026-04-22	PL	61	Chelsea FC	8	34	13	9	12	53	45	8	48	\N
209	2026-04-22	PL	402	Brentford FC	9	33	13	9	11	48	44	4	48	\N
210	2026-04-22	PL	62	Everton FC	10	33	13	8	12	40	39	1	47	\N
211	2026-04-22	PL	71	Sunderland AFC	11	33	12	10	11	36	40	-4	46	\N
212	2026-04-22	PL	63	Fulham FC	12	33	13	6	14	43	46	-3	45	\N
213	2026-04-22	PL	354	Crystal Palace FC	13	32	11	10	11	35	36	-1	43	\N
214	2026-04-22	PL	67	Newcastle United FC	14	33	12	6	15	46	49	-3	42	\N
215	2026-04-22	PL	341	Leeds United FC	15	34	9	13	12	44	51	-7	40	\N
216	2026-04-22	PL	351	Nottingham Forest FC	16	33	9	9	15	36	45	-9	36	\N
217	2026-04-22	PL	563	West Ham United FC	17	33	8	9	16	40	57	-17	33	\N
218	2026-04-22	PL	73	Tottenham Hotspur FC	18	33	7	10	16	42	53	-11	31	\N
219	2026-04-22	PL	328	Burnley FC	19	34	4	8	22	34	68	-34	20	\N
220	2026-04-22	PL	76	Wolverhampton Wanderers FC	20	33	3	8	22	24	61	-37	17	\N
221	2026-05-17	PL	57	Arsenal FC	1	36	24	7	5	68	26	42	79	\N
222	2026-05-17	PL	65	Manchester City FC	2	36	23	8	5	75	32	43	77	\N
223	2026-05-17	PL	66	Manchester United FC	3	37	19	11	7	66	50	16	68	\N
224	2026-05-17	PL	58	Aston Villa FC	4	37	18	8	11	54	48	6	62	\N
225	2026-05-17	PL	64	Liverpool FC	5	37	17	8	12	62	52	10	59	\N
226	2026-05-17	PL	1044	AFC Bournemouth	6	36	13	16	7	56	52	4	55	\N
227	2026-05-17	PL	397	Brighton & Hove Albion FC	7	37	14	11	12	52	43	9	53	\N
228	2026-05-17	PL	402	Brentford FC	8	37	14	10	13	54	51	3	52	\N
229	2026-05-17	PL	71	Sunderland AFC	9	37	13	12	12	40	47	-7	51	\N
230	2026-05-17	PL	61	Chelsea FC	10	36	13	10	13	55	49	6	49	\N
231	2026-05-17	PL	67	Newcastle United FC	11	37	14	7	16	53	53	0	49	\N
232	2026-05-17	PL	62	Everton FC	12	37	13	10	14	47	49	-2	49	\N
233	2026-05-17	PL	63	Fulham FC	13	37	14	7	16	45	51	-6	49	\N
234	2026-05-17	PL	341	Leeds United FC	14	37	11	14	12	49	53	-4	47	\N
235	2026-05-17	PL	354	Crystal Palace FC	15	37	11	12	14	40	49	-9	45	\N
236	2026-05-17	PL	351	Nottingham Forest FC	16	37	11	10	16	47	50	-3	43	\N
237	2026-05-17	PL	73	Tottenham Hotspur FC	17	36	9	11	16	46	55	-9	38	\N
238	2026-05-17	PL	563	West Ham United FC	18	37	9	9	19	43	65	-22	36	\N
239	2026-05-17	PL	328	Burnley FC	19	36	4	9	23	37	73	-36	21	\N
240	2026-05-17	PL	76	Wolverhampton Wanderers FC	20	37	3	10	24	26	67	-41	19	\N
241	2026-05-18	PL	57	Arsenal FC	1	37	25	7	5	69	26	43	82	\N
242	2026-05-18	PL	65	Manchester City FC	2	36	23	8	5	75	32	43	77	\N
243	2026-05-18	PL	66	Manchester United FC	3	37	19	11	7	66	50	16	68	\N
244	2026-05-18	PL	58	Aston Villa FC	4	37	18	8	11	54	48	6	62	\N
245	2026-05-18	PL	64	Liverpool FC	5	37	17	8	12	62	52	10	59	\N
246	2026-05-18	PL	1044	AFC Bournemouth	6	36	13	16	7	56	52	4	55	\N
247	2026-05-18	PL	397	Brighton & Hove Albion FC	7	37	14	11	12	52	43	9	53	\N
248	2026-05-18	PL	402	Brentford FC	8	37	14	10	13	54	51	3	52	\N
249	2026-05-18	PL	71	Sunderland AFC	9	37	13	12	12	40	47	-7	51	\N
250	2026-05-18	PL	61	Chelsea FC	10	36	13	10	13	55	49	6	49	\N
251	2026-05-18	PL	67	Newcastle United FC	11	37	14	7	16	53	53	0	49	\N
252	2026-05-18	PL	62	Everton FC	12	37	13	10	14	47	49	-2	49	\N
253	2026-05-18	PL	63	Fulham FC	13	37	14	7	16	45	51	-6	49	\N
254	2026-05-18	PL	341	Leeds United FC	14	37	11	14	12	49	53	-4	47	\N
255	2026-05-18	PL	354	Crystal Palace FC	15	37	11	12	14	40	49	-9	45	\N
256	2026-05-18	PL	351	Nottingham Forest FC	16	37	11	10	16	47	50	-3	43	\N
257	2026-05-18	PL	73	Tottenham Hotspur FC	17	36	9	11	16	46	55	-9	38	\N
258	2026-05-18	PL	563	West Ham United FC	18	37	9	9	19	43	65	-22	36	\N
259	2026-05-18	PL	328	Burnley FC	19	37	4	9	24	37	74	-37	21	\N
260	2026-05-18	PL	76	Wolverhampton Wanderers FC	20	37	3	10	24	26	67	-41	19	\N
261	2026-05-19	PL	57	Arsenal FC	1	37	25	7	5	69	26	43	82	\N
262	2026-05-19	PL	65	Manchester City FC	2	37	23	9	5	76	33	43	78	\N
263	2026-05-19	PL	66	Manchester United FC	3	37	19	11	7	66	50	16	68	\N
264	2026-05-19	PL	58	Aston Villa FC	4	37	18	8	11	54	48	6	62	\N
265	2026-05-19	PL	64	Liverpool FC	5	37	17	8	12	62	52	10	59	\N
266	2026-05-19	PL	1044	AFC Bournemouth	6	37	13	17	7	57	53	4	56	\N
267	2026-05-19	PL	397	Brighton & Hove Albion FC	7	37	14	11	12	52	43	9	53	\N
268	2026-05-19	PL	61	Chelsea FC	8	37	14	10	13	57	50	7	52	\N
269	2026-05-19	PL	402	Brentford FC	9	37	14	10	13	54	51	3	52	\N
270	2026-05-19	PL	71	Sunderland AFC	10	37	13	12	12	40	47	-7	51	\N
271	2026-05-19	PL	67	Newcastle United FC	11	37	14	7	16	53	53	0	49	\N
272	2026-05-19	PL	62	Everton FC	12	37	13	10	14	47	49	-2	49	\N
273	2026-05-19	PL	63	Fulham FC	13	37	14	7	16	45	51	-6	49	\N
274	2026-05-19	PL	341	Leeds United FC	14	37	11	14	12	49	53	-4	47	\N
275	2026-05-19	PL	354	Crystal Palace FC	15	37	11	12	14	40	49	-9	45	\N
276	2026-05-19	PL	351	Nottingham Forest FC	16	37	11	10	16	47	50	-3	43	\N
277	2026-05-19	PL	73	Tottenham Hotspur FC	17	37	9	11	17	47	57	-10	38	\N
278	2026-05-19	PL	563	West Ham United FC	18	37	9	9	19	43	65	-22	36	\N
279	2026-05-19	PL	328	Burnley FC	19	37	4	9	24	37	74	-37	21	\N
280	2026-05-19	PL	76	Wolverhampton Wanderers FC	20	37	3	10	24	26	67	-41	19	\N
281	2026-05-20	PL	57	Arsenal FC	1	37	25	7	5	69	26	43	82	\N
282	2026-05-20	PL	65	Manchester City FC	2	37	23	9	5	76	33	43	78	\N
283	2026-05-20	PL	66	Manchester United FC	3	37	19	11	7	66	50	16	68	\N
284	2026-05-20	PL	58	Aston Villa FC	4	37	18	8	11	54	48	6	62	\N
285	2026-05-20	PL	64	Liverpool FC	5	37	17	8	12	62	52	10	59	\N
286	2026-05-20	PL	1044	AFC Bournemouth	6	37	13	17	7	57	53	4	56	\N
287	2026-05-20	PL	397	Brighton & Hove Albion FC	7	37	14	11	12	52	43	9	53	\N
288	2026-05-20	PL	61	Chelsea FC	8	37	14	10	13	57	50	7	52	\N
289	2026-05-20	PL	402	Brentford FC	9	37	14	10	13	54	51	3	52	\N
290	2026-05-20	PL	71	Sunderland AFC	10	37	13	12	12	40	47	-7	51	\N
291	2026-05-20	PL	67	Newcastle United FC	11	37	14	7	16	53	53	0	49	\N
292	2026-05-20	PL	62	Everton FC	12	37	13	10	14	47	49	-2	49	\N
293	2026-05-20	PL	63	Fulham FC	13	37	14	7	16	45	51	-6	49	\N
294	2026-05-20	PL	341	Leeds United FC	14	37	11	14	12	49	53	-4	47	\N
295	2026-05-20	PL	354	Crystal Palace FC	15	37	11	12	14	40	49	-9	45	\N
296	2026-05-20	PL	351	Nottingham Forest FC	16	37	11	10	16	47	50	-3	43	\N
297	2026-05-20	PL	73	Tottenham Hotspur FC	17	37	9	11	17	47	57	-10	38	\N
298	2026-05-20	PL	563	West Ham United FC	18	37	9	9	19	43	65	-22	36	\N
299	2026-05-20	PL	328	Burnley FC	19	37	4	9	24	37	74	-37	21	\N
300	2026-05-20	PL	76	Wolverhampton Wanderers FC	20	37	3	10	24	26	67	-41	19	\N
\.


--
-- Name: standing_snapshots_id_seq; Type: SEQUENCE SET; Schema: public; Owner: brandon
--

SELECT pg_catalog.setval('public.standing_snapshots_id_seq', 300, true);


--
-- Name: standing_snapshots standing_snapshots_pkey; Type: CONSTRAINT; Schema: public; Owner: brandon
--

ALTER TABLE ONLY public.standing_snapshots
    ADD CONSTRAINT standing_snapshots_pkey PRIMARY KEY (id);


--
-- Name: standing_snapshots standing_snapshots_snapshot_date_league_id_team_id_key; Type: CONSTRAINT; Schema: public; Owner: brandon
--

ALTER TABLE ONLY public.standing_snapshots
    ADD CONSTRAINT standing_snapshots_snapshot_date_league_id_team_id_key UNIQUE (snapshot_date, league_id, team_id);


--
-- PostgreSQL database dump complete
--

\unrestrict pXARA8UmQgQqJv6fIwR0897vWfCY0OeOEHgNSpgVDVZMNNhq4CebicYwZth6mEc

