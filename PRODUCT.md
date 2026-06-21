# Product

## Register

product

## Users

Data engineering interviewers and recruiters viewing the project as a portfolio piece. Brandon opens this during technical screens or shares the URL to demonstrate the full-stack nature of the pipeline. Secondarily, Brandon uses it himself to monitor standings data. The viewer may not be technical, so the interface must communicate credibility at a glance without requiring them to read code.

## Product Purpose

A football standings dashboard that makes the output of a Java ETL pipeline visible and verifiable. The site shows live Premier League and World Cup 2026 standings pulled from a daily snapshot database, demonstrating that the pipeline actually runs. Success means an interviewer sees the URL, visits it, and immediately understands: "this person built and shipped a real data system."

## Brand Personality

Sharp, analytical, athletic. Not a sports entertainment site, not a generic data tool: the specific feeling of a football analyst who has spent serious time with the numbers.

## Anti-references

- Generic SaaS dashboards (Notion/Linear clones): sidebar nav, card grids, purple-and-gray palette, over-decorated buttons.
- AI-generated portfolio sites: gradient hero sections, glassmorphism cards, all-caps section eyebrows, hero metric blocks.
- Mainstream sports apps (ESPN, BBC Sport): busy info overlays, red/yellow accents, ad-heavy visual density.

## Design Principles

1. **Data is the hero.** Every design choice serves legibility of the table. No chrome that competes with the numbers.
2. **Engineering is visible.** Snapshot date, source attribution, pipeline freshness — surface these. The infrastructure is part of what's being shown.
3. **Precision over decoration.** Energy comes from sharp type and deliberate color, not motion or ornament. A clean table that loads fast is more impressive than one with elaborate transitions.
4. **One surface, no detours.** The entire experience is a single focused view. No onboarding, no empty-state handholding beyond the first visit.

## Accessibility & Inclusion

WCAG AA. Body text minimum 4.5:1 contrast, large text 3:1. Keyboard-navigable tab switching. Explicit `prefers-reduced-motion` support on any transition. Semantic HTML throughout (tables use `<table>`, not divs).
